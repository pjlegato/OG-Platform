/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.core.change;

import java.io.Serializable;
import java.util.Map;

import javax.time.Instant;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.id.UniqueId;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.PublicSPI;

/**
 * A description of changes to an entity.
 * <p>
 * The description describes what happened, when, and to which entity.
 */
@PublicSPI
@BeanDefinition
public class ChangeEvent extends DirectBean implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The type of change that occurred.
   */
  @PropertyDefinition
  private ChangeType _type;
  /**
   * The unique identifier of the entity after the change.
   * It will be null when the entity was added.
   */
  @PropertyDefinition
  private UniqueId _beforeId;
  /**
   * The unique identifier of the entity after the change.
   * It will be null when the entity was removed.
   */
  @PropertyDefinition
  private UniqueId _afterId;
  /**
   * The instant at which the change is recorded as happening, not null.
   */
  @PropertyDefinition
  private Instant _versionInstant;

  /**
   * Creates an instance.
   */
  public ChangeEvent() {
  }

  /**
   * Creates an instance.
   * 
   * @param type  the type of change, not null
   * @param beforeId  the unique identifier of the entity before the change, may be null
   * @param afterId  the unique identifier of the entity after the change, may be null
   * @param versionInstant  the instant at which the change is recorded as happening, not null
   */
  public ChangeEvent(final ChangeType type, final UniqueId beforeId, final UniqueId afterId, final Instant versionInstant) {
    ArgumentChecker.notNull(type, "type");
    ArgumentChecker.isTrue(beforeId != null || afterId != null, "At least one id must be specified");
    ArgumentChecker.notNull(versionInstant, "versionInstant");
    setType(type);
    setBeforeId(beforeId);
    setAfterId(afterId);
    setVersionInstant(versionInstant);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ChangeEvent}.
   * @return the meta-bean, not null
   */
  public static ChangeEvent.Meta meta() {
    return ChangeEvent.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(ChangeEvent.Meta.INSTANCE);
  }

  @Override
  public ChangeEvent.Meta metaBean() {
    return ChangeEvent.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 3575610:  // type
        return getType();
      case 1466459386:  // beforeId
        return getBeforeId();
      case -1076033513:  // afterId
        return getAfterId();
      case 2084044265:  // versionInstant
        return getVersionInstant();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 3575610:  // type
        setType((ChangeType) newValue);
        return;
      case 1466459386:  // beforeId
        setBeforeId((UniqueId) newValue);
        return;
      case -1076033513:  // afterId
        setAfterId((UniqueId) newValue);
        return;
      case 2084044265:  // versionInstant
        setVersionInstant((Instant) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ChangeEvent other = (ChangeEvent) obj;
      return JodaBeanUtils.equal(getType(), other.getType()) &&
          JodaBeanUtils.equal(getBeforeId(), other.getBeforeId()) &&
          JodaBeanUtils.equal(getAfterId(), other.getAfterId()) &&
          JodaBeanUtils.equal(getVersionInstant(), other.getVersionInstant());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getType());
    hash += hash * 31 + JodaBeanUtils.hashCode(getBeforeId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getAfterId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getVersionInstant());
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the type of change that occurred.
   * @return the value of the property
   */
  public ChangeType getType() {
    return _type;
  }

  /**
   * Sets the type of change that occurred.
   * @param type  the new value of the property
   */
  public void setType(ChangeType type) {
    this._type = type;
  }

  /**
   * Gets the the {@code type} property.
   * @return the property, not null
   */
  public final Property<ChangeType> type() {
    return metaBean().type().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the unique identifier of the entity after the change.
   * It will be null when the entity was added.
   * @return the value of the property
   */
  public UniqueId getBeforeId() {
    return _beforeId;
  }

  /**
   * Sets the unique identifier of the entity after the change.
   * It will be null when the entity was added.
   * @param beforeId  the new value of the property
   */
  public void setBeforeId(UniqueId beforeId) {
    this._beforeId = beforeId;
  }

  /**
   * Gets the the {@code beforeId} property.
   * It will be null when the entity was added.
   * @return the property, not null
   */
  public final Property<UniqueId> beforeId() {
    return metaBean().beforeId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the unique identifier of the entity after the change.
   * It will be null when the entity was removed.
   * @return the value of the property
   */
  public UniqueId getAfterId() {
    return _afterId;
  }

  /**
   * Sets the unique identifier of the entity after the change.
   * It will be null when the entity was removed.
   * @param afterId  the new value of the property
   */
  public void setAfterId(UniqueId afterId) {
    this._afterId = afterId;
  }

  /**
   * Gets the the {@code afterId} property.
   * It will be null when the entity was removed.
   * @return the property, not null
   */
  public final Property<UniqueId> afterId() {
    return metaBean().afterId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the instant at which the change is recorded as happening, not null.
   * @return the value of the property
   */
  public Instant getVersionInstant() {
    return _versionInstant;
  }

  /**
   * Sets the instant at which the change is recorded as happening, not null.
   * @param versionInstant  the new value of the property
   */
  public void setVersionInstant(Instant versionInstant) {
    this._versionInstant = versionInstant;
  }

  /**
   * Gets the the {@code versionInstant} property.
   * @return the property, not null
   */
  public final Property<Instant> versionInstant() {
    return metaBean().versionInstant().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ChangeEvent}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code type} property.
     */
    private final MetaProperty<ChangeType> _type = DirectMetaProperty.ofReadWrite(
        this, "type", ChangeEvent.class, ChangeType.class);
    /**
     * The meta-property for the {@code beforeId} property.
     */
    private final MetaProperty<UniqueId> _beforeId = DirectMetaProperty.ofReadWrite(
        this, "beforeId", ChangeEvent.class, UniqueId.class);
    /**
     * The meta-property for the {@code afterId} property.
     */
    private final MetaProperty<UniqueId> _afterId = DirectMetaProperty.ofReadWrite(
        this, "afterId", ChangeEvent.class, UniqueId.class);
    /**
     * The meta-property for the {@code versionInstant} property.
     */
    private final MetaProperty<Instant> _versionInstant = DirectMetaProperty.ofReadWrite(
        this, "versionInstant", ChangeEvent.class, Instant.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "type",
        "beforeId",
        "afterId",
        "versionInstant");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3575610:  // type
          return _type;
        case 1466459386:  // beforeId
          return _beforeId;
        case -1076033513:  // afterId
          return _afterId;
        case 2084044265:  // versionInstant
          return _versionInstant;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends ChangeEvent> builder() {
      return new DirectBeanBuilder<ChangeEvent>(new ChangeEvent());
    }

    @Override
    public Class<? extends ChangeEvent> beanType() {
      return ChangeEvent.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code type} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ChangeType> type() {
      return _type;
    }

    /**
     * The meta-property for the {@code beforeId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueId> beforeId() {
      return _beforeId;
    }

    /**
     * The meta-property for the {@code afterId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueId> afterId() {
      return _afterId;
    }

    /**
     * The meta-property for the {@code versionInstant} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Instant> versionInstant() {
      return _versionInstant;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
