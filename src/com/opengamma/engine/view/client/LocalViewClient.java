/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view.client;

import java.util.Set;
import java.util.TreeSet;

import com.opengamma.engine.position.Portfolio;
import com.opengamma.engine.security.Security;
import com.opengamma.engine.view.ComputationResultListener;
import com.opengamma.engine.view.DeltaComputationResultListener;
import com.opengamma.engine.view.View;
import com.opengamma.engine.view.ViewComputationResultModel;
import com.opengamma.livedata.msg.UserPrincipal;
import com.opengamma.util.ArgumentChecker;

/**
 * An implementation of {@link ViewClient} that just wraps a local instance.
 * 
 */
public class LocalViewClient implements ViewClient {
  private final View _view;
  private final UserPrincipal _user;
  
  public LocalViewClient(View view, UserPrincipal user) {
    ArgumentChecker.notNull(view, "View");
    ArgumentChecker.notNull(user, "User");
    _view = view;
    _user = user;
  }

  /**
   * @return the view
   */
  public View getView() {
    return _view;
  }
  
  @Override
  public UserPrincipal getUser() {
    return _user;
  }

  @Override
  public void addComputationResultListener(ComputationResultListener listener) {
    getView().addResultListener(listener);
  }

  @Override
  public void addDeltaResultListener(DeltaComputationResultListener listener) {
    getView().addDeltaResultListener(listener);
  }

  @Override
  public Set<String> getAllValueNames() {
    return getView().getDefinition().getAllValueRequirements();
  }

  @Override
  public Set<String> getAllSecurityTypes() {
    // REVIEW kirk 2010-03-02 -- What if this is called before resolution? Is that feasible/possible?
    // REVIEW kirk 2010-03-02 -- Cache this? Push down to the PEM?
    Set<String> securityTypes = new TreeSet<String>();
    for (Security security : getView().getPortfolioEvaluationModel().getSecurities()) {
      securityTypes.add(security.getSecurityType());
    }
    return securityTypes;
  }

  @Override
  public ViewComputationResultModel getMostRecentResult() {
    return getView().getMostRecentResult();
  }

  @Override
  public String getName() {
    return getView().getDefinition().getName();
  }

  @Override
  public Portfolio getPortfolio() {
    return (Portfolio) getView().getPortfolio();
  }

  @Override
  public Set<String> getRequirementNames(String securityType) {
    return null;
  }

  @Override
  public boolean isLiveComputationRunning() {
    return getView().isRunning();
  }

  @Override
  public boolean isResultAvailable() {
    return getView().getMostRecentResult() != null;
  }

  @Override
  public void performComputation() {
    if (getView().isRunning()) {
      throw new IllegalStateException("View is currently doing live computation.");
    }
    getView().runOneCycle();
  }
  
  @Override
  public void performComputation(long snapshotTime) {
    if (getView().isRunning()) {
      throw new IllegalStateException("View is currently doing live computation.");
    }
    getView().runOneCycle(snapshotTime);
  }

  @Override
  public void removeComputationResultListener(ComputationResultListener listener) {
    getView().removeResultListener(listener);
  }

  @Override
  public void removeDeltaResultListener(DeltaComputationResultListener listener) {
    getView().removeDeltaResultLister(listener);
  }

}
