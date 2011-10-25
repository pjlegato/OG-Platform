/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.future.definition;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.convention.yield.YieldConvention;
import com.opengamma.financial.convention.yield.YieldConventionFactory;
import com.opengamma.financial.instrument.bond.BondFixedSecurityDefinition;
import com.opengamma.financial.instrument.future.BondFutureDefinition;
import com.opengamma.financial.schedule.ScheduleCalculator;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtils;

import javax.time.calendar.Period;
import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.Test;

/**
 * Tests related to bond futures transaction Derivative construction.
 */
public class BondFutureTransactionTest {
  // 5-Year U.S. Treasury Note Futures: FVU1
  private static final Currency CUR = Currency.USD;
  private static final Period PAYMENT_TENOR = Period.ofMonths(6);
  private static final Calendar CALENDAR = new MondayToFridayCalendar("A");
  private static final DayCount DAY_COUNT = DayCountFactory.INSTANCE.getDayCount("Actual/Actual ISDA");
  private static final BusinessDayConvention BUSINESS_DAY = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Following");
  private static final boolean IS_EOM = false;
  private static final int SETTLEMENT_DAYS = 1;
  private static final YieldConvention YIELD_CONVENTION = YieldConventionFactory.INSTANCE.getYieldConvention("STREET CONVENTION");
  private static final int NB_BOND = 7;
  private static final Period[] BOND_TENOR = new Period[] {Period.ofYears(5), Period.ofYears(5), Period.ofYears(5), Period.ofYears(8), Period.ofYears(5), Period.ofYears(5), Period.ofYears(5) };
  private static final ZonedDateTime[] START_ACCRUAL_DATE = new ZonedDateTime[] {DateUtils.getUTCDate(2010, 11, 30), DateUtils.getUTCDate(2010, 12, 31), DateUtils.getUTCDate(2011, 1, 31),
      DateUtils.getUTCDate(2008, 2, 29), DateUtils.getUTCDate(2011, 3, 31), DateUtils.getUTCDate(2011, 4, 30), DateUtils.getUTCDate(2011, 5, 31) };
  private static final double[] RATE = new double[] {0.01375, 0.02125, 0.0200, 0.02125, 0.0225, 0.0200, 0.0175 };
  private static final double[] CONVERSION_FACTOR = new double[] {.8317, .8565, .8493, .8516, .8540, .8417, .8292 };
  private static final ZonedDateTime[] MATURITY_DATE = new ZonedDateTime[NB_BOND];
  private static final BondFixedSecurityDefinition[] BASKET_DEFINITION = new BondFixedSecurityDefinition[NB_BOND];
  static {
    for (int loopbasket = 0; loopbasket < NB_BOND; loopbasket++) {
      MATURITY_DATE[loopbasket] = START_ACCRUAL_DATE[loopbasket].plus(BOND_TENOR[loopbasket]);
      BASKET_DEFINITION[loopbasket] = BondFixedSecurityDefinition.from(CUR, MATURITY_DATE[loopbasket], START_ACCRUAL_DATE[loopbasket], PAYMENT_TENOR, RATE[loopbasket], SETTLEMENT_DAYS, CALENDAR,
          DAY_COUNT, BUSINESS_DAY, YIELD_CONVENTION, IS_EOM);
    }
  }
  private static final ZonedDateTime LAST_TRADING_DATE = DateUtils.getUTCDate(2011, 9, 21);
  private static final ZonedDateTime FIRST_NOTICE_DATE = DateUtils.getUTCDate(2011, 8, 31);
  private static final ZonedDateTime LAST_NOTICE_DATE = DateUtils.getUTCDate(2011, 9, 29);
  private static final double NOTIONAL = 100000;
  private static final double REF_PRICE = 0.0;
  private static final BondFutureDefinition BOND_FUTURE_SECURITY_DEFINITION = new BondFutureDefinition(LAST_TRADING_DATE, FIRST_NOTICE_DATE, LAST_NOTICE_DATE, NOTIONAL,
      BASKET_DEFINITION, CONVERSION_FACTOR, REF_PRICE);
  private static final ZonedDateTime REFERENCE_DATE = DateUtils.getUTCDate(2011, 6, 20);
  private static final String CREDIT_CURVE_NAME = "Credit";
  private static final String REPO_CURVE_NAME = "Repo";
  private static final String[] CURVES_NAME = {CREDIT_CURVE_NAME, REPO_CURVE_NAME };
  private static final BondFuture BOND_FUTURE_SECURITY = BOND_FUTURE_SECURITY_DEFINITION.toDerivative(REFERENCE_DATE, CURVES_NAME);
  // Transaction
  private static final int QUANTITY = 4321;
  private static final double REFERENCE_PRICE = 1.0987;
  private static final BondFutureTransaction FUTURE_TRANSACTION = new BondFutureTransaction(BOND_FUTURE_SECURITY, QUANTITY, REFERENCE_PRICE);

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullUndelying() {
    new BondFutureTransaction(null, QUANTITY, REFERENCE_PRICE);
  }

  @Test
  /**
   * Tests the getter methods.
   */
  public void getter() {
    assertEquals("Bond future transaction derivative: underlying", BOND_FUTURE_SECURITY, FUTURE_TRANSACTION.getUnderlyingFuture());
    assertEquals("Bond future transaction derivative: quantity", QUANTITY, FUTURE_TRANSACTION.getQuantity());
    assertEquals("Bond future transaction derivative: reference price", REFERENCE_PRICE, FUTURE_TRANSACTION.getReferencePrice());
  }

  @Test
  /**
   * Tests the equal and hash code methods.
   */
  public void equalHash() {
    assertTrue(FUTURE_TRANSACTION.equals(FUTURE_TRANSACTION));
    BondFutureTransaction other = new BondFutureTransaction(BOND_FUTURE_SECURITY, QUANTITY, REFERENCE_PRICE);
    assertTrue(FUTURE_TRANSACTION.equals(other));
    assertTrue(FUTURE_TRANSACTION.hashCode() == other.hashCode());
    BondFutureTransaction modifiedFuture;
    modifiedFuture = new BondFutureTransaction(BOND_FUTURE_SECURITY, QUANTITY + 1, REFERENCE_PRICE);
    assertFalse(FUTURE_TRANSACTION.equals(modifiedFuture));
    modifiedFuture = new BondFutureTransaction(BOND_FUTURE_SECURITY, QUANTITY, REFERENCE_PRICE + 0.001);
    assertFalse(FUTURE_TRANSACTION.equals(modifiedFuture));
    BondFuture otherUnderlying = BOND_FUTURE_SECURITY_DEFINITION.toDerivative(ScheduleCalculator.getAdjustedDate(REFERENCE_DATE, CALENDAR, 1), CURVES_NAME);
    modifiedFuture = new BondFutureTransaction(otherUnderlying, QUANTITY, REFERENCE_PRICE);
    assertFalse(FUTURE_TRANSACTION.equals(modifiedFuture));
    assertFalse(FUTURE_TRANSACTION.equals(CUR));
    assertFalse(FUTURE_TRANSACTION.equals(null));
  }

}
