/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.analytics;

import java.util.Collection;

import com.opengamma.engine.depgraph.DependencyNode;
import com.opengamma.engine.depgraph.DependencyNodeResolver;
import com.opengamma.engine.position.Position;

/**
 * The base class from which most {@link AnalyticFunctionDefinition} implementations
 * should inherit.
 *
 * @author kirk
 */
public abstract class AbstractAggregatePositionAnalyticFunction extends AbstractAnalyticFunction implements AggregatePositionAnalyticFunctionDefinition {
  @Override
  public DependencyNode buildSubGraph(
      Collection<Position> positions,
      AnalyticFunctionResolver functionResolver,
      DependencyNodeResolver dependencyNodeResolver) {
    return null;
  }
}
