/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.util.timeseries.fudge;

import org.fudgemsg.mapping.FudgeBuilder;

import com.opengamma.util.timeseries.fast.DateTimeNumericEncoding;
import com.opengamma.util.timeseries.fast.longint.FastMapLongDoubleTimeSeries;

/**
 * 
 *
 * @author jim
 */
public class FastMapLongDoubleTimeSeriesBuilder extends FastLongDoubleTimeSeriesBuilder<FastMapLongDoubleTimeSeries> implements
    FudgeBuilder<FastMapLongDoubleTimeSeries> {

  @Override
  public FastMapLongDoubleTimeSeries makeSeries(DateTimeNumericEncoding encoding, long[] times, double[] values) {
    return new FastMapLongDoubleTimeSeries(encoding, times, values);
  }


}
