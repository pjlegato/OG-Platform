/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.volatility.surface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.BasicImmutableBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.ImmutableList;
import com.opengamma.core.config.Config;
import com.opengamma.id.UniqueIdentifiable;

/**
 * Holds the range of X for a future price curve (to be used with volatility surfaces).
 * @param <X> Type of the x-data 
 */
@Config(description = "Future price curve definition")
@BeanDefinition
public final class FuturePriceCurveDefinition<X> implements ImmutableBean {

  /**
   * The definition name.
   */
  @PropertyDefinition(validate = "notNull")
  private final String _name;
  /**
   * The target.
   */
  @PropertyDefinition(validate = "notNull")
  private final UniqueIdentifiable _target;
  /**
   * The definition values.
   */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableList<X> _xs;

  public static <X> FuturePriceCurveDefinition<X> of(final String name, final UniqueIdentifiable target, final List<X> xs) {
    return new FuturePriceCurveDefinition<>(name, target, xs);
  }

  public static <X> FuturePriceCurveDefinition<X> of(final String name, final UniqueIdentifiable target, final X[] xs) {
    return new FuturePriceCurveDefinition<>(name, target, ImmutableList.copyOf(xs));
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FuturePriceCurveDefinition}.
   * @return the meta-bean, not null
   */
  @SuppressWarnings("rawtypes")
  public static FuturePriceCurveDefinition.Meta meta() {
    return FuturePriceCurveDefinition.Meta.INSTANCE;
  }

  /**
   * The meta-bean for {@code FuturePriceCurveDefinition}.
   * @param <R>  the bean's generic type
   * @param cls  the bean's generic type
   * @return the meta-bean, not null
   */
  @SuppressWarnings("unchecked")
  public static <R> FuturePriceCurveDefinition.Meta<R> metaFuturePriceCurveDefinition(Class<R> cls) {
    return FuturePriceCurveDefinition.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(FuturePriceCurveDefinition.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   *
   * @param <X>  the type
   * @return the builder, not null
   */
  public static <X> FuturePriceCurveDefinition.Builder<X> builder() {
    return new FuturePriceCurveDefinition.Builder<X>();
  }

  private FuturePriceCurveDefinition(
      String name,
      UniqueIdentifiable target,
      List<X> xs) {
    JodaBeanUtils.notNull(name, "name");
    JodaBeanUtils.notNull(target, "target");
    JodaBeanUtils.notNull(xs, "xs");
    this._name = name;
    this._target = target;
    this._xs = (xs != null ? ImmutableList.copyOf(xs) : null);
  }

  @SuppressWarnings("unchecked")
  @Override
  public FuturePriceCurveDefinition.Meta<X> metaBean() {
    return FuturePriceCurveDefinition.Meta.INSTANCE;
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
   * Gets the definition name.
   * @return the value of the property, not null
   */
  public String getName() {
    return _name;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the target.
   * @return the value of the property, not null
   */
  public UniqueIdentifiable getTarget() {
    return _target;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the definition values.
   * @return the value of the property, not null
   */
  public ImmutableList<X> getXs() {
    return _xs;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder<X> toBuilder() {
    return new Builder<X>(this);
  }

  @Override
  public FuturePriceCurveDefinition<X> clone() {
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FuturePriceCurveDefinition<?> other = (FuturePriceCurveDefinition<?>) obj;
      return JodaBeanUtils.equal(getName(), other.getName()) &&
          JodaBeanUtils.equal(getTarget(), other.getTarget()) &&
          JodaBeanUtils.equal(getXs(), other.getXs());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getName());
    hash += hash * 31 + JodaBeanUtils.hashCode(getTarget());
    hash += hash * 31 + JodaBeanUtils.hashCode(getXs());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("FuturePriceCurveDefinition{");
    buf.append("name").append('=').append(getName()).append(',').append(' ');
    buf.append("target").append('=').append(getTarget()).append(',').append(' ');
    buf.append("xs").append('=').append(getXs());
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FuturePriceCurveDefinition}.
   */
  public static final class Meta<X> extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    @SuppressWarnings("rawtypes")
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<String> _name = DirectMetaProperty.ofImmutable(
        this, "name", FuturePriceCurveDefinition.class, String.class);
    /**
     * The meta-property for the {@code target} property.
     */
    private final MetaProperty<UniqueIdentifiable> _target = DirectMetaProperty.ofImmutable(
        this, "target", FuturePriceCurveDefinition.class, UniqueIdentifiable.class);
    /**
     * The meta-property for the {@code xs} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableList<X>> _xs = DirectMetaProperty.ofImmutable(
        this, "xs", FuturePriceCurveDefinition.class, (Class) ImmutableList.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "name",
        "target",
        "xs");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return _name;
        case -880905839:  // target
          return _target;
        case 3835:  // xs
          return _xs;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public FuturePriceCurveDefinition.Builder<X> builder() {
      return new FuturePriceCurveDefinition.Builder<X>();
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    @Override
    public Class<? extends FuturePriceCurveDefinition<X>> beanType() {
      return (Class) FuturePriceCurveDefinition.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public MetaProperty<String> name() {
      return _name;
    }

    /**
     * The meta-property for the {@code target} property.
     * @return the meta-property, not null
     */
    public MetaProperty<UniqueIdentifiable> target() {
      return _target;
    }

    /**
     * The meta-property for the {@code xs} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableList<X>> xs() {
      return _xs;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return ((FuturePriceCurveDefinition<?>) bean).getName();
        case -880905839:  // target
          return ((FuturePriceCurveDefinition<?>) bean).getTarget();
        case 3835:  // xs
          return ((FuturePriceCurveDefinition<?>) bean).getXs();
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
   * The bean-builder for {@code FuturePriceCurveDefinition}.
   */
  public static final class Builder<X> extends BasicImmutableBeanBuilder<FuturePriceCurveDefinition<X>> {

    private String _name;
    private UniqueIdentifiable _target;
    private List<X> _xs = new ArrayList<X>();

    /**
     * Restricted constructor.
     */
    private Builder() {
      super(FuturePriceCurveDefinition.Meta.INSTANCE);
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(FuturePriceCurveDefinition<X> beanToCopy) {
      super(FuturePriceCurveDefinition.Meta.INSTANCE);
      this._name = beanToCopy.getName();
      this._target = beanToCopy.getTarget();
      this._xs = new ArrayList<X>(beanToCopy.getXs());
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public Builder<X> set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          this._name = (String) newValue;
          break;
        case -880905839:  // target
          this._target = (UniqueIdentifiable) newValue;
          break;
        case 3835:  // xs
          this._xs = (List<X>) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public FuturePriceCurveDefinition<X> build() {
      return new FuturePriceCurveDefinition<X>(
          _name,
          _target,
          _xs);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code name} property in the builder.
     * @param name  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder<X> name(String name) {
      JodaBeanUtils.notNull(name, "name");
      this._name = name;
      return this;
    }

    /**
     * Sets the {@code target} property in the builder.
     * @param target  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder<X> target(UniqueIdentifiable target) {
      JodaBeanUtils.notNull(target, "target");
      this._target = target;
      return this;
    }

    /**
     * Sets the {@code xs} property in the builder.
     * @param xs  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder<X> xs(List<X> xs) {
      JodaBeanUtils.notNull(xs, "xs");
      this._xs = xs;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("FuturePriceCurveDefinition.Builder{");
      buf.append("name").append('=').append(_name).append(',').append(' ');
      buf.append("target").append('=').append(_target).append(',').append(' ');
      buf.append("xs").append('=').append(_xs);
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
