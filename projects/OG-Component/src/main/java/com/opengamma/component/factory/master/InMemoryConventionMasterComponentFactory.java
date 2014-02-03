/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.master;

import static com.opengamma.component.factory.master.DBMasterComponentUtils.isValidJmsConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.component.ComponentInfo;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractComponentFactory;
import com.opengamma.component.factory.ComponentInfoAttributes;
import com.opengamma.core.change.JmsChangeManager;
import com.opengamma.financial.convention.initializer.DefaultConventionMasterInitializer;
import com.opengamma.master.convention.ConventionMaster;
import com.opengamma.master.convention.impl.DataConventionMasterResource;
import com.opengamma.master.convention.impl.InMemoryConventionMaster;
import com.opengamma.master.convention.impl.RemoteConventionMaster;
import com.opengamma.util.jms.JmsConnector;

/**
 * Component factory for an in-memory convention master.
 */
@BeanDefinition
public class InMemoryConventionMasterComponentFactory extends AbstractComponentFactory {

  /**
   * The classifier that the factory should publish under.
   */
  @PropertyDefinition(validate = "notNull")
  private String _classifier;
  
  /**
   * Whether to use change management. If true, requires jms settings to be non-null.
   */
  @PropertyDefinition
  private boolean _enableChangeManagement = true;

  /**
   * The flag determining whether the component should be published by REST (default true).
   */
  @PropertyDefinition
  private boolean _publishRest = true;
  /**
   * The JMS connector.
   */
  @PropertyDefinition
  private JmsConnector _jmsConnector;
  /**
   * The JMS change manager topic.
   */
  @PropertyDefinition
  private String _jmsChangeManagerTopic;
  /**
   * Whether to populate the master with default hard-coded conventions (default false).
   */
  @PropertyDefinition
  private boolean _populateDefaultConventions;

  @Override
  public void init(final ComponentRepository repo, final LinkedHashMap<String, String> configuration) {
    final ComponentInfo info = new ComponentInfo(ConventionMaster.class, getClassifier());
    
    // create
    final ConventionMaster master;
    if (isEnableChangeManagement() && isValidJmsConfiguration(getClassifier(), getClass(), getJmsConnector(), getJmsChangeManagerTopic())) {
      JmsChangeManager cm = new JmsChangeManager(getJmsConnector(), getJmsChangeManagerTopic());
      master = new InMemoryConventionMaster(cm);
      repo.registerLifecycle(cm);
      if (getJmsConnector().getClientBrokerUri() != null) {
        info.addAttribute(ComponentInfoAttributes.JMS_BROKER_URI, getJmsConnector().getClientBrokerUri().toString());
      }
      info.addAttribute(ComponentInfoAttributes.JMS_CHANGE_MANAGER_TOPIC, getJmsChangeManagerTopic());
    } else {
      master = new InMemoryConventionMaster();
    }
    if (isPopulateDefaultConventions()) {
      DefaultConventionMasterInitializer.INSTANCE.init(master);
    }
    
    // register
    info.addAttribute(ComponentInfoAttributes.LEVEL, 1);
    info.addAttribute(ComponentInfoAttributes.REMOTE_CLIENT_JAVA, RemoteConventionMaster.class);
    info.addAttribute(ComponentInfoAttributes.UNIQUE_ID_SCHEME, InMemoryConventionMaster.DEFAULT_OID_SCHEME);
    repo.registerComponent(info, master);
    
    // publish
    if (isPublishRest()) {
      repo.getRestComponents().publish(info, new DataConventionMasterResource(master));
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code InMemoryConventionMasterComponentFactory}.
   * @return the meta-bean, not null
   */
  public static InMemoryConventionMasterComponentFactory.Meta meta() {
    return InMemoryConventionMasterComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(InMemoryConventionMasterComponentFactory.Meta.INSTANCE);
  }

  @Override
  public InMemoryConventionMasterComponentFactory.Meta metaBean() {
    return InMemoryConventionMasterComponentFactory.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the classifier that the factory should publish under.
   * @return the value of the property, not null
   */
  public String getClassifier() {
    return _classifier;
  }

  /**
   * Sets the classifier that the factory should publish under.
   * @param classifier  the new value of the property, not null
   */
  public void setClassifier(String classifier) {
    JodaBeanUtils.notNull(classifier, "classifier");
    this._classifier = classifier;
  }

  /**
   * Gets the the {@code classifier} property.
   * @return the property, not null
   */
  public final Property<String> classifier() {
    return metaBean().classifier().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets whether to use change management. If true, requires jms settings to be non-null.
   * @return the value of the property
   */
  public boolean isEnableChangeManagement() {
    return _enableChangeManagement;
  }

  /**
   * Sets whether to use change management. If true, requires jms settings to be non-null.
   * @param enableChangeManagement  the new value of the property
   */
  public void setEnableChangeManagement(boolean enableChangeManagement) {
    this._enableChangeManagement = enableChangeManagement;
  }

  /**
   * Gets the the {@code enableChangeManagement} property.
   * @return the property, not null
   */
  public final Property<Boolean> enableChangeManagement() {
    return metaBean().enableChangeManagement().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag determining whether the component should be published by REST (default true).
   * @return the value of the property
   */
  public boolean isPublishRest() {
    return _publishRest;
  }

  /**
   * Sets the flag determining whether the component should be published by REST (default true).
   * @param publishRest  the new value of the property
   */
  public void setPublishRest(boolean publishRest) {
    this._publishRest = publishRest;
  }

  /**
   * Gets the the {@code publishRest} property.
   * @return the property, not null
   */
  public final Property<Boolean> publishRest() {
    return metaBean().publishRest().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the JMS connector.
   * @return the value of the property
   */
  public JmsConnector getJmsConnector() {
    return _jmsConnector;
  }

  /**
   * Sets the JMS connector.
   * @param jmsConnector  the new value of the property
   */
  public void setJmsConnector(JmsConnector jmsConnector) {
    this._jmsConnector = jmsConnector;
  }

  /**
   * Gets the the {@code jmsConnector} property.
   * @return the property, not null
   */
  public final Property<JmsConnector> jmsConnector() {
    return metaBean().jmsConnector().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the JMS change manager topic.
   * @return the value of the property
   */
  public String getJmsChangeManagerTopic() {
    return _jmsChangeManagerTopic;
  }

  /**
   * Sets the JMS change manager topic.
   * @param jmsChangeManagerTopic  the new value of the property
   */
  public void setJmsChangeManagerTopic(String jmsChangeManagerTopic) {
    this._jmsChangeManagerTopic = jmsChangeManagerTopic;
  }

  /**
   * Gets the the {@code jmsChangeManagerTopic} property.
   * @return the property, not null
   */
  public final Property<String> jmsChangeManagerTopic() {
    return metaBean().jmsChangeManagerTopic().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets whether to populate the master with default hard-coded conventions (default false).
   * @return the value of the property
   */
  public boolean isPopulateDefaultConventions() {
    return _populateDefaultConventions;
  }

  /**
   * Sets whether to populate the master with default hard-coded conventions (default false).
   * @param populateDefaultConventions  the new value of the property
   */
  public void setPopulateDefaultConventions(boolean populateDefaultConventions) {
    this._populateDefaultConventions = populateDefaultConventions;
  }

  /**
   * Gets the the {@code populateDefaultConventions} property.
   * @return the property, not null
   */
  public final Property<Boolean> populateDefaultConventions() {
    return metaBean().populateDefaultConventions().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public InMemoryConventionMasterComponentFactory clone() {
    return (InMemoryConventionMasterComponentFactory) super.clone();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      InMemoryConventionMasterComponentFactory other = (InMemoryConventionMasterComponentFactory) obj;
      return JodaBeanUtils.equal(getClassifier(), other.getClassifier()) &&
          (isEnableChangeManagement() == other.isEnableChangeManagement()) &&
          (isPublishRest() == other.isPublishRest()) &&
          JodaBeanUtils.equal(getJmsConnector(), other.getJmsConnector()) &&
          JodaBeanUtils.equal(getJmsChangeManagerTopic(), other.getJmsChangeManagerTopic()) &&
          (isPopulateDefaultConventions() == other.isPopulateDefaultConventions()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getClassifier());
    hash += hash * 31 + JodaBeanUtils.hashCode(isEnableChangeManagement());
    hash += hash * 31 + JodaBeanUtils.hashCode(isPublishRest());
    hash += hash * 31 + JodaBeanUtils.hashCode(getJmsConnector());
    hash += hash * 31 + JodaBeanUtils.hashCode(getJmsChangeManagerTopic());
    hash += hash * 31 + JodaBeanUtils.hashCode(isPopulateDefaultConventions());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(224);
    buf.append("InMemoryConventionMasterComponentFactory{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  @Override
  protected void toString(StringBuilder buf) {
    super.toString(buf);
    buf.append("classifier").append('=').append(JodaBeanUtils.toString(getClassifier())).append(',').append(' ');
    buf.append("enableChangeManagement").append('=').append(JodaBeanUtils.toString(isEnableChangeManagement())).append(',').append(' ');
    buf.append("publishRest").append('=').append(JodaBeanUtils.toString(isPublishRest())).append(',').append(' ');
    buf.append("jmsConnector").append('=').append(JodaBeanUtils.toString(getJmsConnector())).append(',').append(' ');
    buf.append("jmsChangeManagerTopic").append('=').append(JodaBeanUtils.toString(getJmsChangeManagerTopic())).append(',').append(' ');
    buf.append("populateDefaultConventions").append('=').append(JodaBeanUtils.toString(isPopulateDefaultConventions())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code InMemoryConventionMasterComponentFactory}.
   */
  public static class Meta extends AbstractComponentFactory.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code classifier} property.
     */
    private final MetaProperty<String> _classifier = DirectMetaProperty.ofReadWrite(
        this, "classifier", InMemoryConventionMasterComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code enableChangeManagement} property.
     */
    private final MetaProperty<Boolean> _enableChangeManagement = DirectMetaProperty.ofReadWrite(
        this, "enableChangeManagement", InMemoryConventionMasterComponentFactory.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code publishRest} property.
     */
    private final MetaProperty<Boolean> _publishRest = DirectMetaProperty.ofReadWrite(
        this, "publishRest", InMemoryConventionMasterComponentFactory.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code jmsConnector} property.
     */
    private final MetaProperty<JmsConnector> _jmsConnector = DirectMetaProperty.ofReadWrite(
        this, "jmsConnector", InMemoryConventionMasterComponentFactory.class, JmsConnector.class);
    /**
     * The meta-property for the {@code jmsChangeManagerTopic} property.
     */
    private final MetaProperty<String> _jmsChangeManagerTopic = DirectMetaProperty.ofReadWrite(
        this, "jmsChangeManagerTopic", InMemoryConventionMasterComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code populateDefaultConventions} property.
     */
    private final MetaProperty<Boolean> _populateDefaultConventions = DirectMetaProperty.ofReadWrite(
        this, "populateDefaultConventions", InMemoryConventionMasterComponentFactory.class, Boolean.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "classifier",
        "enableChangeManagement",
        "publishRest",
        "jmsConnector",
        "jmsChangeManagerTopic",
        "populateDefaultConventions");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return _classifier;
        case 981110710:  // enableChangeManagement
          return _enableChangeManagement;
        case -614707837:  // publishRest
          return _publishRest;
        case -1495762275:  // jmsConnector
          return _jmsConnector;
        case -758086398:  // jmsChangeManagerTopic
          return _jmsChangeManagerTopic;
        case 1366925483:  // populateDefaultConventions
          return _populateDefaultConventions;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends InMemoryConventionMasterComponentFactory> builder() {
      return new DirectBeanBuilder<InMemoryConventionMasterComponentFactory>(new InMemoryConventionMasterComponentFactory());
    }

    @Override
    public Class<? extends InMemoryConventionMasterComponentFactory> beanType() {
      return InMemoryConventionMasterComponentFactory.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code classifier} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> classifier() {
      return _classifier;
    }

    /**
     * The meta-property for the {@code enableChangeManagement} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> enableChangeManagement() {
      return _enableChangeManagement;
    }

    /**
     * The meta-property for the {@code publishRest} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> publishRest() {
      return _publishRest;
    }

    /**
     * The meta-property for the {@code jmsConnector} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<JmsConnector> jmsConnector() {
      return _jmsConnector;
    }

    /**
     * The meta-property for the {@code jmsChangeManagerTopic} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> jmsChangeManagerTopic() {
      return _jmsChangeManagerTopic;
    }

    /**
     * The meta-property for the {@code populateDefaultConventions} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> populateDefaultConventions() {
      return _populateDefaultConventions;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return ((InMemoryConventionMasterComponentFactory) bean).getClassifier();
        case 981110710:  // enableChangeManagement
          return ((InMemoryConventionMasterComponentFactory) bean).isEnableChangeManagement();
        case -614707837:  // publishRest
          return ((InMemoryConventionMasterComponentFactory) bean).isPublishRest();
        case -1495762275:  // jmsConnector
          return ((InMemoryConventionMasterComponentFactory) bean).getJmsConnector();
        case -758086398:  // jmsChangeManagerTopic
          return ((InMemoryConventionMasterComponentFactory) bean).getJmsChangeManagerTopic();
        case 1366925483:  // populateDefaultConventions
          return ((InMemoryConventionMasterComponentFactory) bean).isPopulateDefaultConventions();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          ((InMemoryConventionMasterComponentFactory) bean).setClassifier((String) newValue);
          return;
        case 981110710:  // enableChangeManagement
          ((InMemoryConventionMasterComponentFactory) bean).setEnableChangeManagement((Boolean) newValue);
          return;
        case -614707837:  // publishRest
          ((InMemoryConventionMasterComponentFactory) bean).setPublishRest((Boolean) newValue);
          return;
        case -1495762275:  // jmsConnector
          ((InMemoryConventionMasterComponentFactory) bean).setJmsConnector((JmsConnector) newValue);
          return;
        case -758086398:  // jmsChangeManagerTopic
          ((InMemoryConventionMasterComponentFactory) bean).setJmsChangeManagerTopic((String) newValue);
          return;
        case 1366925483:  // populateDefaultConventions
          ((InMemoryConventionMasterComponentFactory) bean).setPopulateDefaultConventions((Boolean) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((InMemoryConventionMasterComponentFactory) bean)._classifier, "classifier");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
