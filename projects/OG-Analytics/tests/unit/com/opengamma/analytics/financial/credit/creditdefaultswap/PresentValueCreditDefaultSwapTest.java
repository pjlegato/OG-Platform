/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.credit.creditdefaultswap;

import javax.time.calendar.LocalDate;
import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.Test;

import com.opengamma.analytics.financial.credit.BuySellProtection;
import com.opengamma.analytics.financial.credit.CalibrateHazardRate;
import com.opengamma.analytics.financial.credit.CreditRating;
import com.opengamma.analytics.financial.credit.DebtSeniority;
import com.opengamma.analytics.financial.credit.Region;
import com.opengamma.analytics.financial.credit.RestructuringClause;
import com.opengamma.analytics.financial.credit.Sector;
import com.opengamma.analytics.financial.credit.StubType;
import com.opengamma.analytics.financial.credit.SurvivalCurve;
import com.opengamma.analytics.financial.credit.creditdefaultswap.definition.CreditDefaultSwapDefinition;
import com.opengamma.analytics.financial.credit.creditdefaultswap.pricing.GenerateCreditDefaultSwapPremiumLegSchedule;
import com.opengamma.analytics.financial.credit.creditdefaultswap.pricing.PresentValueCreditDefaultSwap;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.analytics.math.curve.InterpolatedDoublesCurve;
import com.opengamma.analytics.math.interpolation.LinearInterpolator1D;
import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.convention.frequency.PeriodFrequency;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtils;

/**
 *  Test of the implementation of the valuation model for a CDS 
 */
public class PresentValueCreditDefaultSwapTest {

  // ----------------------------------------------------------------------------------

  // TODO : Add all the tests
  // TODO : Move the calendar into a seperate TestCalendar class
  // TODO : Fix the time decay test

  // ----------------------------------------------------------------------------------

  // CDS contract parameters

  private static final BuySellProtection buySellProtection = BuySellProtection.BUY;

  private static final String protectionBuyer = "ABC";
  private static final String protectionSeller = "XYZ";
  private static final String referenceEntityTicker = "MSFT";
  private static final String referenceEntityShortName = "Microsoft";
  private static final String referenceEntityREDCode = "ABC123";

  private static final Currency currency = Currency.USD;

  private static final DebtSeniority debtSeniority = DebtSeniority.SENIOR;
  private static final RestructuringClause restructuringClause = RestructuringClause.NORE;

  private static final CreditRating compositeRating = CreditRating.AA;
  private static final CreditRating impliedRating = CreditRating.A;

  private static final Sector sector = Sector.INDUSTRIALS;
  private static final Region region = Region.NORTHAMERICA;
  private static final String country = "United States";

  private static final Calendar calendar = new MyCalendar();

  private static final ZonedDateTime startDate = DateUtils.getUTCDate(2007, 10, 22);
  private static final ZonedDateTime effectiveDate = DateUtils.getUTCDate(2007, 10, 23);
  private static final ZonedDateTime maturityDate = DateUtils.getUTCDate(2012, 12, 20);
  private static final ZonedDateTime valuationDate = DateUtils.getUTCDate(2007, 10, 23);

  private static final StubType stubType = StubType.FRONTSHORT;
  private static final PeriodFrequency couponFrequency = PeriodFrequency.QUARTERLY;
  private static final DayCount daycountFractionConvention = DayCountFactory.INSTANCE.getDayCount("ACT/360");
  private static final BusinessDayConvention businessdayAdjustmentConvention = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Following");

  private static final boolean immAdjustMaturityDate = true;
  private static final boolean adjustEffectiveDate = true;
  private static final boolean adjustMaturityDate = true;

  private static final double notional = 10000000.0;
  private static final double premiumLegCoupon = 100.0;
  private static final double recoveryRate = 0.40;
  private static final boolean includeAccruedPremium = false;

  // Dummy yield curve
  private static final double interestRate = 0.0;
  private static final double[] TIME = new double[] {0, 3, 5, 10, 15, 40 };
  private static final double[] RATES = new double[] {interestRate, interestRate, interestRate, interestRate, interestRate, interestRate };
  private static final InterpolatedDoublesCurve R = InterpolatedDoublesCurve.from(TIME, RATES, new LinearInterpolator1D());
  private static final YieldCurve yieldCurve = YieldCurve.from(R);

  // Construct a survival curve based on a flat hazard rate term structure (for testing purposes only)
  private static final double hazardRate = (premiumLegCoupon / 10000.0) / (1 - recoveryRate);
  private static final double[] tenorsAsDoubles = new double[] {5 };
  private static final double[] hazardRates = new double[] {hazardRate };
  private static final SurvivalCurve flatSurvivalCurve = new SurvivalCurve(tenorsAsDoubles, hazardRates);

  // ----------------------------------------------------------------------------------

  // Construct a CDS contract 
  private static final CreditDefaultSwapDefinition cds = new CreditDefaultSwapDefinition(buySellProtection,
      protectionBuyer,
      protectionSeller,
      referenceEntityTicker,
      referenceEntityShortName,
      referenceEntityREDCode,
      currency,
      debtSeniority,
      restructuringClause,
      compositeRating,
      impliedRating,
      sector,
      region,
      country,
      calendar,
      startDate,
      effectiveDate,
      maturityDate,
      valuationDate,
      stubType,
      couponFrequency,
      daycountFractionConvention,
      businessdayAdjustmentConvention,
      immAdjustMaturityDate,
      adjustEffectiveDate,
      adjustMaturityDate,
      notional,
      premiumLegCoupon,
      recoveryRate,
      includeAccruedPremium);

  // -----------------------------------------------------------------------------------------------

  // Simple test to compute the PV of a CDS assuming a flat term structure of market observed CDS par spreads

  @Test
  public void testPresentValueCreditDefaultSwapFlatSurvivalCurve() {

    // -----------------------------------------------------------------------------------------------

    final boolean outputResults = false;

    double presentValue = 0.0;

    if (outputResults) {
      System.out.println("Running CDS PV test (with a simple flat survival curve) ...");
    }

    // -----------------------------------------------------------------------------------------------

    // Call the constructor to create a CDS whose PV we will compute
    final PresentValueCreditDefaultSwap creditDefaultSwap = new PresentValueCreditDefaultSwap();

    // Call the CDS PV calculator to get the current PV
    presentValue = creditDefaultSwap.getPresentValueCreditDefaultSwap(cds, yieldCurve, flatSurvivalCurve);

    if (outputResults) {
      System.out.println("CDS PV = " + presentValue);
    }

  }

  // -----------------------------------------------------------------------------------------------

  // Simple test to calibrate a single name CDS to a term structure of market observed par CDS spreads and compute the PV

  @Test
  public void testPresentValueCreditSwapCalibratedSurvivalCurve() {

    // -----------------------------------------------------------------------------------------------

    final boolean outputResults = false;

    double presentValue = 0.0;

    if (outputResults) {
      System.out.println("Running CDS PV test (with a calibrated survival curve) ...");
    }

    // -----------------------------------------------------------------------------------------------

    // Define the market data to calibrate to

    // The number of CDS instruments used to calibrate against
    int numberOfCalibrationCDS = 10;

    // The CDS tenors to calibrate to
    final ZonedDateTime[] tenors = new ZonedDateTime[numberOfCalibrationCDS];

    tenors[0] = DateUtils.getUTCDate(2008, 12, 20);
    tenors[1] = DateUtils.getUTCDate(2009, 6, 20);
    tenors[2] = DateUtils.getUTCDate(2010, 6, 20);
    tenors[3] = DateUtils.getUTCDate(2011, 6, 20);
    tenors[4] = DateUtils.getUTCDate(2012, 6, 20);
    tenors[5] = DateUtils.getUTCDate(2014, 6, 20);
    tenors[6] = DateUtils.getUTCDate(2017, 6, 20);
    tenors[7] = DateUtils.getUTCDate(2022, 6, 20);
    tenors[8] = DateUtils.getUTCDate(2030, 6, 20);
    tenors[9] = DateUtils.getUTCDate(2040, 6, 20);

    // The market observed par CDS spreads at these tenors
    final double[] marketSpreads = new double[numberOfCalibrationCDS];

    final double flatSpread = 100.0;

    marketSpreads[0] = flatSpread;
    marketSpreads[1] = flatSpread;
    marketSpreads[2] = flatSpread;
    marketSpreads[3] = flatSpread;
    marketSpreads[4] = flatSpread;
    marketSpreads[5] = flatSpread;
    marketSpreads[6] = flatSpread;
    marketSpreads[7] = flatSpread;
    marketSpreads[8] = flatSpread;
    marketSpreads[9] = flatSpread;

    // The recovery rate assumption used in the PV calculations when calibrating
    final double calibrationRecoveryRate = 0.40;

    // -------------------------------------------------------------------------------------

    // Calibrate the hazard rate term structure to the market observed par spreads

    // Create a calibration CDS (will be a modified version of the baseline CDS)
    CreditDefaultSwapDefinition calibrationCDS = cds;

    // Set the recovery rate of the calibration CDS used for the curve calibration (this appears in the calculation of the contingent leg)
    calibrationCDS = calibrationCDS.withRecoveryRate(calibrationRecoveryRate);

    // Create a calibrate survival curve object
    final CalibrateHazardRate hazardRateCurve = new CalibrateHazardRate();

    // Calibrate the survival curve to the market observed par CDS spreads (returns hazard rate term structure as a vector of doubles)
    double[] calibratedHazardRateTermStructure = hazardRateCurve.getCalibratedHazardRateTermStructure(calibrationCDS, tenors, marketSpreads, yieldCurve);

    // -------------------------------------------------------------------------------------

    // Now want to create a new CDS and price it using the calibrated survival curve

    // Create a cashflow schedule object (to facilitate the conversion of tenors into doubles)
    GenerateCreditDefaultSwapPremiumLegSchedule cashflowSchedule = new GenerateCreditDefaultSwapPremiumLegSchedule();

    // Convert the ZonedDateTime tenors into doubles (measured from valuationDate)
    double[] tenorsAsDoubles = cashflowSchedule.convertTenorsToDoubles(cds, tenors);

    // Build a survival curve using the input tenors (converted to doubles) and the previously calibrated hazard rates
    final SurvivalCurve survivalCurve = new SurvivalCurve(tenorsAsDoubles, calibratedHazardRateTermStructure);

    // Call the constructor to create a CDS whose PV we will compute
    final PresentValueCreditDefaultSwap creditDefaultSwap = new PresentValueCreditDefaultSwap();

    // Call the CDS PV calculator to get the current PV (should be equal to zero)
    presentValue = creditDefaultSwap.getPresentValueCreditDefaultSwap(cds, yieldCurve, survivalCurve);

    if (outputResults) {
      System.out.println("CDS PV = " + presentValue);
    }

    // -------------------------------------------------------------------------------------
  }

  // -----------------------------------------------------------------------------------------------

  // Test to vary the valuationDate of a CDS from adjustedEffectiveDate to adjustedMaturityDate and compute PV

  @Test
  public void testPresentValueCreditDefaultSwapTimeDecay() {

    // -----------------------------------------------------------------------------------------------

    final boolean outputResults = false;

    double presentValue = 0.0;

    if (outputResults) {
      System.out.println("Running CDS PV time decay test ...");
    }

    // -----------------------------------------------------------------------------------------------

    // Create a valuation CDS whose valuationDate will vary (will be a modified version of the baseline CDS)
    CreditDefaultSwapDefinition valuationCDS = cds;

    // Call the constructor to create a CDS whose PV we will compute
    final PresentValueCreditDefaultSwap creditDefaultSwap = new PresentValueCreditDefaultSwap();

    // Call the CDS PV calculator to get the current PV
    presentValue = creditDefaultSwap.getPresentValueCreditDefaultSwap(cds, yieldCurve, flatSurvivalCurve);

    if (outputResults) {
      System.out.println(valuationDate + "\t" + presentValue);
    }

    // -----------------------------------------------------------------------------------------------

    // start at the initial valuation date
    ZonedDateTime rollingValuationDate = cds.getValuationDate();

    while (!rollingValuationDate.isAfter(cds.getMaturityDate().minusDays(10))) {

      // Roll the current valuation date
      rollingValuationDate = rollingValuationDate.plusDays(1);

      // Modify the CDS's valuation date
      valuationCDS = valuationCDS.withValuationDate(rollingValuationDate);

      // Calculate the CDS PV
      presentValue = creditDefaultSwap.getPresentValueCreditDefaultSwap(valuationCDS, yieldCurve, flatSurvivalCurve);

      if (outputResults) {
        System.out.println(rollingValuationDate + "\t" + presentValue);
      }
    }

    // -----------------------------------------------------------------------------------------------
  }

  //-----------------------------------------------------------------------------------------------

  // Bespoke calendar class (have made this public - may want to change this)
  public static class MyCalendar implements Calendar {

    private static final Calendar weekend = new MondayToFridayCalendar("GBP");

    @Override
    public boolean isWorkingDay(LocalDate date) {

      if (!weekend.isWorkingDay(date)) {
        return false;
      }

      /*
      // Custom bank holiday
      if (date.equals(LocalDate.of(2012, 8, 27))) {
        return false;
      }

      // Custom bank holiday
      if (date.equals(LocalDate.of(2012, 8, 28))) {
        return false;
      }

      // Custom bank holiday
      if (date.equals(LocalDate.of(2017, 8, 28))) {
        return false;
      }

      // Custom bank holiday
      if (date.equals(LocalDate.of(2017, 8, 29))) {
        return false;
      }
       */

      return true;
    }

    @Override
    public String getConventionName() {
      return "";
    }

  }

  // -----------------------------------------------------------------------------------------------
}

//-----------------------------------------------------------------------------------------------