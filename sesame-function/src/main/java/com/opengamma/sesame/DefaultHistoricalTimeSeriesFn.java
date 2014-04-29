/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;

import com.google.common.collect.Lists;
import com.opengamma.core.convention.ConventionSource;
import com.opengamma.core.historicaltimeseries.HistoricalTimeSeries;
import com.opengamma.core.historicaltimeseries.HistoricalTimeSeriesSource;
import com.opengamma.core.value.MarketDataRequirementNames;
import com.opengamma.financial.analytics.ircurve.strips.CurveNodeWithIdentifier;
import com.opengamma.financial.analytics.ircurve.strips.PointsCurveNodeWithIdentifier;
import com.opengamma.financial.analytics.ircurve.strips.ZeroCouponInflationNode;
import com.opengamma.financial.analytics.timeseries.HistoricalTimeSeriesBundle;
import com.opengamma.financial.convention.InflationLegConvention;
import com.opengamma.financial.convention.PriceIndexConvention;
import com.opengamma.financial.currency.CurrencyPair;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.financial.security.FinancialSecurityVisitorAdapter;
import com.opengamma.financial.security.future.BondFutureSecurity;
import com.opengamma.financial.security.future.DeliverableSwapFutureSecurity;
import com.opengamma.financial.security.future.FederalFundsFutureSecurity;
import com.opengamma.financial.security.future.InterestRateFutureSecurity;
import com.opengamma.financial.security.irs.FloatingInterestRateSwapLeg;
import com.opengamma.financial.security.irs.InterestRateSwapSecurity;
import com.opengamma.financial.security.option.BondFutureOptionSecurity;
import com.opengamma.financial.security.option.IRFutureOptionSecurity;
import com.opengamma.financial.security.option.SwaptionSecurity;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.sesame.component.RetrievalPeriod;
import com.opengamma.sesame.marketdata.HistoricalMarketDataFn;
import com.opengamma.timeseries.date.localdate.LocalDateDoubleTimeSeries;
import com.opengamma.util.money.Currency;
import com.opengamma.util.result.FailureStatus;
import com.opengamma.util.result.Result;
import com.opengamma.util.time.LocalDateRange;

/**
 * Function implementation that provides a historical time-series bundle.
 */
public class DefaultHistoricalTimeSeriesFn implements HistoricalTimeSeriesFn {

  private static final Logger s_logger = LoggerFactory.getLogger(DefaultHistoricalTimeSeriesFn.class);

  private static final HistoricalTimeSeriesBundle EMPTY_TIME_SERIES_BUNDLE = new HistoricalTimeSeriesBundle();

  private final HistoricalTimeSeriesSource _htsSource;
  private final String _resolutionKey;
  private final ConventionSource _conventionSource;
  private final Period _htsRetrievalPeriod;
  private final HistoricalMarketDataFn _historicalMarketDataFn;

  public DefaultHistoricalTimeSeriesFn(HistoricalTimeSeriesSource htsSource,
                                       String resolutionKey,
                                       ConventionSource conventionSource,
                                       HistoricalMarketDataFn historicalMarketDataFn,
                                       RetrievalPeriod htsRetrievalPeriod) {
    _htsSource = htsSource;
    _resolutionKey = resolutionKey;
    _conventionSource = conventionSource;
    _htsRetrievalPeriod = htsRetrievalPeriod.getRetrievalPeriod();
    _historicalMarketDataFn = historicalMarketDataFn;
  }

  //-------------------------------------------------------------------------
  @Override
  public Result<LocalDateDoubleTimeSeries> getHtsForCurrencyPair(Environment env, CurrencyPair currencyPair, LocalDate endDate) {
    LocalDate startDate = endDate.minus(_htsRetrievalPeriod);
    LocalDateRange dateRange = LocalDateRange.of(startDate, endDate, true);
    return getHtsForCurrencyPair(env, currencyPair, dateRange);
  }


  public Result<HistoricalTimeSeriesBundle> getHtsForCurveNode(Environment env, CurveNodeWithIdentifier node, LocalDate endDate) {
    // For expediency we will mirror the current ways of working out dates which is
    // pretty much to take 1 year before the valuation date. This is blunt and
    // returns more data than is actually required
    // todo - could we manage HTS lookup in the same way as market data? i.e. request the values needed look them up so they are available next time
    
    final LocalDate startDate = endDate.minus(_htsRetrievalPeriod);
    return getHtsForCurveNode(env, node, LocalDateRange.of(startDate, endDate, true));

  }

  private void processResult(ExternalIdBundle id, String dataField, HistoricalTimeSeries timeSeries, final HistoricalTimeSeriesBundle bundle, List<Result<?>> failures) {
    if (timeSeries != null) {
      if (timeSeries.getTimeSeries().isEmpty()) {
        failures.add(Result.failure(FailureStatus.MISSING_DATA, "Time series for {} is empty", id));
      } else {
        bundle.add(dataField, id, timeSeries);
      }
    } else {
      failures.add(Result.failure(FailureStatus.MISSING_DATA, "Couldn't get time series for {}", id));
    }
  }

  @Override
  public Result<HistoricalTimeSeriesBundle> getFixingsForSecurity(Environment env, FinancialSecurity security) {
    HistoricalTimeSeriesBundle bundle;
    final FixingRetriever retriever = new FixingRetriever(_htsSource, env);
    try {
      bundle = security.accept(retriever);
      if (Result.allSuccessful(retriever.getResults())) {
        return Result.success(bundle);
      }
    } catch (Exception ex) {
      return Result.failure(ex);
    }
    return Result.failure(retriever.getResults());
  }
  
  private class FixingRetriever extends FinancialSecurityVisitorAdapter<HistoricalTimeSeriesBundle> {

    private final HistoricalTimeSeriesSource _htsSource;
    
    private final LocalDate _now;

    private final List<Result<?>> _results;

    public FixingRetriever(HistoricalTimeSeriesSource htsSource, Environment env) {
      _htsSource = htsSource;
      _now = env.getValuationDate();
      _results = new ArrayList<>();
    }

    private List<Result<?>> getResults() {
      return _results;
    }

    /**
     * Returns a time series bundle of the previous month's market values for the specified security.
     * @param security the security to retrieve the market values for.
     */
    private HistoricalTimeSeriesBundle getMarketValueTimeSeries(FinancialSecurity security) {
      final HistoricalTimeSeriesBundle bundle = new HistoricalTimeSeriesBundle();
      String field = MarketDataRequirementNames.MARKET_VALUE;
      ExternalIdBundle id = security.getExternalIdBundle();
      getPreviousMonthValues(field, id, bundle);
      return bundle;
    }
    
    /**
     * Returns a time series of the previous month's field values for the specified external id into the time series bundle.
     * @param field the name of the value used to lookup.
     * @param id the external id of used to lookup the field values.
     * @param bundle the time series bundle to hold the market values.
     */
    private void getPreviousMonthValues(String field, ExternalIdBundle id, HistoricalTimeSeriesBundle bundle) {
      getPreviousPeriodValues(field, id, Period.ofMonths(1), bundle);
    }

    /**
     * Returns a time series of the previous month's field values for the specified external id into the time series bundle.
     * @param field the name of the value used to lookup.
     * @param id the external id of used to lookup the field values.
     * @param length the length of time to get values for.
     * @param bundle the time series bundle to hold the market values.
     */
    private void getPreviousPeriodValues(String field, ExternalIdBundle id, Period length, HistoricalTimeSeriesBundle bundle) {
      final boolean includeStart = true;
      final boolean includeEnd = true;
      final LocalDate startDate = _now.minus(length);
      HistoricalTimeSeries series = _htsSource.getHistoricalTimeSeries(field, id, _resolutionKey, startDate, includeStart, _now, includeEnd);
      processResult(id, field, series, bundle, _results);
    }

    @Override
    public HistoricalTimeSeriesBundle visitFederalFundsFutureSecurity(FederalFundsFutureSecurity security) {
      final HistoricalTimeSeriesBundle bundle = getMarketValueTimeSeries(security);
      String field = MarketDataRequirementNames.MARKET_VALUE;
      final ExternalIdBundle underlyingId = security.getUnderlyingId().getExternalId().toBundle();
      getPreviousMonthValues(field, underlyingId, bundle);
      return bundle;
    }
    
    @Override
    public HistoricalTimeSeriesBundle visitInterestRateFutureSecurity(InterestRateFutureSecurity security) {
      return getMarketValueTimeSeries(security);
    }
    
    @Override
    public HistoricalTimeSeriesBundle visitIRFutureOptionSecurity(IRFutureOptionSecurity security) {
      return getMarketValueTimeSeries(security);
    }

    @Override
    public HistoricalTimeSeriesBundle visitSwaptionSecurity(SwaptionSecurity security) {
      if (security.getCurrency().equals(Currency.BRL)) {
        throw new UnsupportedOperationException("Fixing series for Brazilian swaptions not yet implemented");
      }
      return EMPTY_TIME_SERIES_BUNDLE;
    }

    @Override
    public HistoricalTimeSeriesBundle visitBondFutureSecurity(BondFutureSecurity security) {
      return getMarketValueTimeSeries(security);
    }
    
    @Override
    public HistoricalTimeSeriesBundle visitDeliverableSwapFutureSecurity(DeliverableSwapFutureSecurity security) {
      return getMarketValueTimeSeries(security);
    }   
    
    @Override
    public HistoricalTimeSeriesBundle visitBondFutureOptionSecurity(BondFutureOptionSecurity security) {
      return getMarketValueTimeSeries(security);
    }

    @Override
    public HistoricalTimeSeriesBundle visitInterestRateSwapSecurity(final InterestRateSwapSecurity security) {
      final HistoricalTimeSeriesBundle bundle = new HistoricalTimeSeriesBundle();
      for (final FloatingInterestRateSwapLeg leg : security.getLegs(FloatingInterestRateSwapLeg.class)) {
        ExternalId id = leg.getFloatingReferenceRateId();
        getPreviousPeriodValues(MarketDataRequirementNames.MARKET_VALUE, id.toBundle(), Period.ofYears(1), bundle);
      }
      return bundle;
    }

  }

  @Override
  public Result<HistoricalTimeSeriesBundle> getHtsForCurveNode(Environment env, CurveNodeWithIdentifier node, LocalDateRange dateRange) {
    LocalDate startDate = dateRange.getStartDateInclusive();
    LocalDate endDate = dateRange.getEndDateInclusive();
    final boolean includeStart = true;
    final boolean includeEnd = true;

    
    List<Result<?>> failures = Lists.newArrayList();
    
    final HistoricalTimeSeriesBundle bundle = new HistoricalTimeSeriesBundle();
    
    ExternalIdBundle id = ExternalIdBundle.of(node.getIdentifier());
    String dataField = node.getDataField();
    // TODO use HistoricalMarketDataFn.getValues()?
    HistoricalTimeSeries timeSeries = _htsSource.getHistoricalTimeSeries(dataField, id, _resolutionKey, startDate,
                                                                         includeStart, endDate, includeEnd);
    processResult(id, dataField, timeSeries, bundle, failures);

    if (node instanceof PointsCurveNodeWithIdentifier) {
      final PointsCurveNodeWithIdentifier pointsNode = (PointsCurveNodeWithIdentifier) node;
      id = ExternalIdBundle.of(pointsNode.getUnderlyingIdentifier());
      dataField = pointsNode.getUnderlyingDataField();
      timeSeries = _htsSource.getHistoricalTimeSeries(dataField, id, _resolutionKey, startDate, includeStart,
                                                      endDate, includeEnd);
      
      processResult(id, dataField, timeSeries, bundle, failures);

    }

    if (node.getCurveNode() instanceof ZeroCouponInflationNode) {
      final ZeroCouponInflationNode inflationNode = (ZeroCouponInflationNode) node.getCurveNode();
      InflationLegConvention inflationLegConvention = _conventionSource.getSingle(inflationNode.getInflationLegConvention(),
                                                                                  InflationLegConvention.class);
      PriceIndexConvention priceIndexConvention = _conventionSource.getSingle(inflationLegConvention.getPriceIndexConvention(),
                                                                              PriceIndexConvention.class);
      final String priceIndexField = MarketDataRequirementNames.MARKET_VALUE; //TODO
      final ExternalIdBundle priceIndexId = ExternalIdBundle.of(priceIndexConvention.getPriceIndexId());
      final HistoricalTimeSeries priceIndexSeries = _htsSource.getHistoricalTimeSeries(priceIndexField,
                                                                                       priceIndexId,
                                                                                       _resolutionKey,
                                                                                       startDate,
                                                                                       includeStart,
                                                                                       endDate,
                                                                                       includeEnd);
      processResult(priceIndexId, priceIndexField, priceIndexSeries, bundle, failures);
    }
    
    if (Result.anyFailures(failures)) {
      return Result.failure(failures);
    }
    
    return Result.success(bundle);  
  }

  @Override
  public Result<LocalDateDoubleTimeSeries> getHtsForCurrencyPair(Environment env, CurrencyPair currencyPair, LocalDateRange dateRange) {
    return _historicalMarketDataFn.getFxRates(env, currencyPair, dateRange);
  }
}
