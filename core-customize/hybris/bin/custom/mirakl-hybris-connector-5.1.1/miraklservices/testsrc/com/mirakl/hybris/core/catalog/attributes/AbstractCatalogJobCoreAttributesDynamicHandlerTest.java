package com.mirakl.hybris.core.catalog.attributes;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;

import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.cronjob.model.CronJobModel;

@Ignore
public abstract class AbstractCatalogJobCoreAttributesDynamicHandlerTest<T extends CronJobModel> {

  @Mock
  protected MiraklCoreAttributeConfigurationModel configuration, emptyConfiguration;
  @Mock
  protected MiraklCoreAttributeModel coreAttribute1, coreAttribute2;

  Set<MiraklCoreAttributeModel> coreAttributes;

  protected AbstractCatalogJobCoreAttributesDynamicHandler<T> testObj;

  public void setUp(AbstractCatalogJobCoreAttributesDynamicHandler<T> testObj) throws Exception {
    this.testObj = testObj;
    coreAttributes = Sets.newSet(coreAttribute1, coreAttribute2);
    when(configuration.getCoreAttributes()).thenReturn(coreAttributes);
  }

  @Test
  public void getCoreAttributes() {
    Set<MiraklCoreAttributeModel> result = testObj.getCoreAttributes(configuration);

    assertThat(result).containsOnly(coreAttribute1, coreAttribute2);
  }

  @Test
  public void getCoreAttributeswhenConfigurationIsNull() {
    Set<MiraklCoreAttributeModel> result = testObj.getCoreAttributes(null);

    assertThat(result).isEmpty();
  }

  @Test
  public void setCoreAttributes() {
    Set<MiraklCoreAttributeModel> coreAttributes = Sets.newSet(coreAttribute1, coreAttribute2);

    testObj.setCoreAttributes(emptyConfiguration, coreAttributes);

    verify(emptyConfiguration).setCoreAttributes(coreAttributes);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void setCoreAttributesWhenConfigurationIsNullAndCoreAttributesWhereSpecified() {
    testObj.setCoreAttributes(null, Sets.newSet(coreAttribute1, coreAttribute2));
  }

  @Test // Nothing should happen...
  public void setCoreAttributesWhenConfigurationIsNull() {
    testObj.setCoreAttributes(null, null);
  }

}
