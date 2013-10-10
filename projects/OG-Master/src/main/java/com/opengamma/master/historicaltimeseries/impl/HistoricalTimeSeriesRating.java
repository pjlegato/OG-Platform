/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.historicaltimeseries.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableConstructor;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.BasicImmutableBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.core.config.Config;
import com.opengamma.master.historicaltimeseries.ManageableHistoricalTimeSeriesInfo;
import com.opengamma.util.ArgumentChecker;

/**
 * A set of rating rules that allow a time-series to be rated.
 * <p>
 * This is stored as configuration to choose the best matching time-series.
 * <p>
 * This class is immutable and thread-safe.
 */
@Config(description = "Historical time-series rating")
@BeanDefinition
public final class HistoricalTimeSeriesRating implements ImmutableBean {

  /**
   * The set of rules.
   */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableSet<HistoricalTimeSeriesRatingRule> _rules;
  /**
   * The rules grouped by field type.
   */
  // not a property
  private final ImmutableMap<String, Map<String, Integer>> _rulesByFieldType;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance of {@code HistoricalTimeSeriesRating}.
   * 
   * @param rules  the rules, not null and not empty
   * @return the rating, not null
   */
  public static HistoricalTimeSeriesRating of(Collection<HistoricalTimeSeriesRatingRule> rules) {
    return new HistoricalTimeSeriesRating(rules);
  }

  //-------------------------------------------------------------------------
  /**
   * Creates an instance.
   * 
   * @param rules  the rules, not null and not empty
   */
  @ImmutableConstructor
  private HistoricalTimeSeriesRating(Collection<HistoricalTimeSeriesRatingRule> rules) {
    ArgumentChecker.notEmpty(rules, "rules");
    _rules = ImmutableSet.copyOf(rules);
    _rulesByFieldType = buildRuleDb();
  }

  private ImmutableMap<String, Map<String, Integer>> buildRuleDb() {
    Map<String, Map<String, Integer>> map = Maps.newHashMap();
    for (HistoricalTimeSeriesRatingRule rule : _rules) {
      String fieldName = rule.getFieldName();
      Map<String, Integer> ruleDb = map.get(fieldName);
      if (ruleDb == null) {
        ruleDb = new HashMap<String, Integer>();
        map.put(fieldName, ruleDb);
      }
      ruleDb.put(rule.getFieldValue(), rule.getRating());
    }
    return ImmutableMap.copyOf(map);
  }

  //-------------------------------------------------------------------------
  /**
   * Rates historical time-series info based on the stored rules.
   * 
   * @param series  the series to rate, not null
   * @return the rating
   */
  public int rate(ManageableHistoricalTimeSeriesInfo series) {
    String dataSource = series.getDataSource();
    Map<String, Integer> dataSourceMap = _rulesByFieldType.get(HistoricalTimeSeriesRatingFieldNames.DATA_SOURCE_NAME);
    Integer dsRating = dataSourceMap.get(dataSource);
    if (dsRating == null) {
      dsRating = dataSourceMap.get(HistoricalTimeSeriesRatingFieldNames.STAR_VALUE);
      if (dsRating == null) {
        throw new OpenGammaRuntimeException("There must be a star match if no match with given dataSource: " + dataSource);
      }
    }
    String dataProvider = series.getDataProvider();
    Map<String, Integer> dataProviderMap = _rulesByFieldType.get(HistoricalTimeSeriesRatingFieldNames.DATA_PROVIDER_NAME);
    Integer dpRating = dataProviderMap.get(dataProvider);
    if (dpRating == null) {
      dpRating = dataProviderMap.get(HistoricalTimeSeriesRatingFieldNames.STAR_VALUE);
      if (dpRating == null) {
        throw new OpenGammaRuntimeException("There must be a star match if no match with given dataProvider: " + dataProvider);
      }
    }
    return dsRating * dpRating;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code HistoricalTimeSeriesRating}.
   * @return the meta-bean, not null
   */
  public static HistoricalTimeSeriesRating.Meta meta() {
    return HistoricalTimeSeriesRating.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(HistoricalTimeSeriesRating.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   *
   * @return the builder, not null
   */
  public static HistoricalTimeSeriesRating.Builder builder() {
    return new HistoricalTimeSeriesRating.Builder();
  }

  @Override
  public HistoricalTimeSeriesRating.Meta metaBean() {
    return HistoricalTimeSeriesRating.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the set of rules.
   * @return the value of the property, not null
   */
  public ImmutableSet<HistoricalTimeSeriesRatingRule> getRules() {
    return _rules;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public HistoricalTimeSeriesRating clone() {
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      HistoricalTimeSeriesRating other = (HistoricalTimeSeriesRating) obj;
      return JodaBeanUtils.equal(getRules(), other.getRules());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getRules());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("HistoricalTimeSeriesRating{");
    buf.append("rules").append('=').append(getRules());
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code HistoricalTimeSeriesRating}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code rules} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableSet<HistoricalTimeSeriesRatingRule>> _rules = DirectMetaProperty.ofImmutable(
        this, "rules", HistoricalTimeSeriesRating.class, (Class) ImmutableSet.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "rules");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 108873975:  // rules
          return _rules;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public HistoricalTimeSeriesRating.Builder builder() {
      return new HistoricalTimeSeriesRating.Builder();
    }

    @Override
    public Class<? extends HistoricalTimeSeriesRating> beanType() {
      return HistoricalTimeSeriesRating.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code rules} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableSet<HistoricalTimeSeriesRatingRule>> rules() {
      return _rules;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 108873975:  // rules
          return ((HistoricalTimeSeriesRating) bean).getRules();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code HistoricalTimeSeriesRating}.
   */
  public static final class Builder extends BasicImmutableBeanBuilder<HistoricalTimeSeriesRating> {

    private Set<HistoricalTimeSeriesRatingRule> _rules = new HashSet<HistoricalTimeSeriesRatingRule>();

    /**
     * Restricted constructor.
     */
    private Builder() {
      super(HistoricalTimeSeriesRating.Meta.INSTANCE);
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(HistoricalTimeSeriesRating beanToCopy) {
      super(HistoricalTimeSeriesRating.Meta.INSTANCE);
      this._rules = new HashSet<HistoricalTimeSeriesRatingRule>(beanToCopy.getRules());
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 108873975:  // rules
          this._rules = (Set<HistoricalTimeSeriesRatingRule>) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public HistoricalTimeSeriesRating build() {
      return new HistoricalTimeSeriesRating(
          _rules);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code rules} property in the builder.
     * @param rules  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder rules(Set<HistoricalTimeSeriesRatingRule> rules) {
      JodaBeanUtils.notNull(rules, "rules");
      this._rules = rules;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(64);
      buf.append("HistoricalTimeSeriesRating.Builder{");
      buf.append("rules").append('=').append(_rules);
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
