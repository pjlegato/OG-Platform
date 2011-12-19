/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.future.method;

import static org.testng.AssertJUnit.assertEquals;

import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.instrument.future.InterestRateFutureDefinition;
import com.opengamma.financial.instrument.index.IborIndex;
import com.opengamma.financial.interestrate.TestsDataSets;
import com.opengamma.financial.interestrate.YieldCurveBundle;
import com.opengamma.financial.interestrate.future.definition.InterestRateFuture;
import com.opengamma.financial.interestrate.future.definition.InterestRateFutureOptionMarginSecurity;
import com.opengamma.financial.model.option.definition.SABRInterestRateDataBundle;
import com.opengamma.financial.model.option.definition.SABRInterestRateParameters;
import com.opengamma.financial.model.option.pricing.analytic.formula.BlackFunctionData;
import com.opengamma.financial.model.option.pricing.analytic.formula.BlackPriceFunction;
import com.opengamma.financial.model.option.pricing.analytic.formula.EuropeanVanillaOption;
import com.opengamma.financial.schedule.ScheduleCalculator;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtils;

import javax.time.calendar.Period;
import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.Test;

/**
 * Tests the method for interest rate future option with SABR volatility parameter surfaces.
 */
public class InterestRateFutureOptionMarginSecuritySABRMethodTest {
  //EURIBOR 3M Index
  private static final Period TENOR = Period.ofMonths(3);
  private static final int SETTLEMENT_DAYS = 2;
  private static final Calendar CALENDAR = new MondayToFridayCalendar("A");
  private static final DayCount DAY_COUNT_INDEX = DayCountFactory.INSTANCE.getDayCount("Actual/360");
  private static final BusinessDayConvention BUSINESS_DAY = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Modified Following");
  private static final boolean IS_EOM = true;
  private static final Currency CUR = Currency.USD;
  private static final IborIndex IBOR_INDEX = new IborIndex(CUR, TENOR, SETTLEMENT_DAYS, CALENDAR, DAY_COUNT_INDEX, BUSINESS_DAY, IS_EOM);
  // Future
  private static final ZonedDateTime SPOT_LAST_TRADING_DATE = DateUtils.getUTCDate(2012, 9, 19);
  private static final ZonedDateTime LAST_TRADING_DATE = ScheduleCalculator.getAdjustedDate(SPOT_LAST_TRADING_DATE, -SETTLEMENT_DAYS, CALENDAR);
  private static final double NOTIONAL = 1000000.0; // 1m
  private static final double FUTURE_FACTOR = 0.25;
  private static final double REFERENCE_PRICE = 0.0;
  private static final String NAME = "EDU2";
  private static final InterestRateFutureDefinition EDU2_DEFINITION = new InterestRateFutureDefinition(LAST_TRADING_DATE, IBOR_INDEX, REFERENCE_PRICE, NOTIONAL, FUTURE_FACTOR, NAME);
  private static final ZonedDateTime REFERENCE_DATE = DateUtils.getUTCDate(2010, 8, 18);
  private static final String DISCOUNTING_CURVE_NAME = "Funding";
  private static final String FORWARD_CURVE_NAME = "Forward";
  private static final String[] CURVES = {DISCOUNTING_CURVE_NAME, FORWARD_CURVE_NAME };
  private static final InterestRateFuture EDU2 = EDU2_DEFINITION.toDerivative(REFERENCE_DATE, REFERENCE_PRICE, CURVES);
  // Option 
  private static final ZonedDateTime EXPIRATION_DATE = DateUtils.getUTCDate(2011, 9, 16);
  private static final DayCount ACT_ACT = DayCountFactory.INSTANCE.getDayCount("Actual/Actual ISDA");
  private static final double EXPIRATION_TIME = ACT_ACT.getDayCountFraction(REFERENCE_DATE, EXPIRATION_DATE);
  private static final double STRIKE = 0.9850;
  private static final boolean IS_CALL = true;
  private static final InterestRateFutureOptionMarginSecurity OPTION_EDU2 = new InterestRateFutureOptionMarginSecurity(EDU2, EXPIRATION_TIME, STRIKE, IS_CALL);
  private static final InterestRateFutureOptionMarginSecuritySABRMethod METHOD = InterestRateFutureOptionMarginSecuritySABRMethod.getInstance();

  final YieldCurveBundle CURVES_BUNDLE = TestsDataSets.createCurves1();
  final SABRInterestRateParameters SABR_PARAMETER = TestsDataSets.createSABR1();
  final SABRInterestRateDataBundle SABR_BUNDLE = new SABRInterestRateDataBundle(SABR_PARAMETER, CURVES_BUNDLE);

  @Test
  /**
   * Test the option price from the future price. Mid-curve one year option.
   */
  public void priceFromFuturePriceMidCurve() {
    final double priceFuture = 0.9905;
    final double priceOption = METHOD.optionPriceFromFuturePrice(OPTION_EDU2, SABR_BUNDLE, priceFuture);
    final double delay = EDU2.getLastTradingTime() - EXPIRATION_TIME;
    final double volatility = SABR_PARAMETER.getVolatility(EXPIRATION_TIME, delay, 1 - STRIKE, 1 - priceFuture);
    final BlackPriceFunction blackFunction = new BlackPriceFunction();
    final BlackFunctionData dataBlack = new BlackFunctionData(1 - priceFuture, 1.0, volatility);
    final EuropeanVanillaOption option = new EuropeanVanillaOption(1 - STRIKE, EXPIRATION_TIME, !IS_CALL);
    final double priceOptionExpected = blackFunction.getPriceFunction(option).evaluate(dataBlack);
    assertEquals("Future option with SABR volatilities: option price from future price", priceOptionExpected, priceOption);
  }

  @Test
  /**
   * Test the option price from the future price. Standard option.
   */
  public void priceFromFuturePriceStandard() {
    final double expirationTime = ACT_ACT.getDayCountFraction(REFERENCE_DATE, LAST_TRADING_DATE);
    final InterestRateFutureOptionMarginSecurity optionEDU2Standard = new InterestRateFutureOptionMarginSecurity(EDU2, expirationTime, STRIKE, IS_CALL);
    final double priceFuture = 0.9905;
    final double priceOption = METHOD.optionPriceFromFuturePrice(optionEDU2Standard, SABR_BUNDLE, priceFuture);
    final double delay = 0.0;
    final double volatility = SABR_PARAMETER.getVolatility(expirationTime, delay, 1 - STRIKE, 1 - priceFuture);
    final BlackPriceFunction blackFunction = new BlackPriceFunction();
    final BlackFunctionData dataBlack = new BlackFunctionData(1 - priceFuture, 1.0, volatility);
    final EuropeanVanillaOption option = new EuropeanVanillaOption(1 - STRIKE, expirationTime, !IS_CALL);
    final double priceOptionExpected = blackFunction.getPriceFunction(option).evaluate(dataBlack);
    assertEquals("Future option with SABR volatilities: option price from future price", priceOptionExpected, priceOption);
  }

  @Test
  /**
   * Test the option price from the future price. Standard option.
   */
  public void priceStandard() {
    final double expirationTime = ACT_ACT.getDayCountFraction(REFERENCE_DATE, LAST_TRADING_DATE);
    final InterestRateFutureOptionMarginSecurity optionEDU2Standard = new InterestRateFutureOptionMarginSecurity(EDU2, expirationTime, STRIKE, IS_CALL);
    final double priceOption = METHOD.optionPrice(optionEDU2Standard, SABR_BUNDLE);
    final InterestRateFutureDiscountingMethod methodFuture = InterestRateFutureDiscountingMethod.getInstance();
    final double priceFuture = methodFuture.price(EDU2, CURVES_BUNDLE);
    final double priceOptionExpected = METHOD.optionPriceFromFuturePrice(optionEDU2Standard, SABR_BUNDLE, priceFuture);
    assertEquals("Future option with SABR volatilities: option price", priceOptionExpected, priceOption);
  }

}
