package com.mirakl.hybris.core.catalog.attributes;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklExportCatalogCronJobModel;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ExportCatalogCoreAttributesDynamicHandlerTest
    extends AbstractCatalogJobCoreAttributesDynamicHandlerTest<MiraklExportCatalogCronJobModel> {

  @Mock
  private MiraklExportCatalogCronJobModel catalogExportJob;

  @InjectMocks
  private ExportCatalogCoreAttributesDynamicHandler testObj;

  @Before
  public void setUp() throws Exception {
    super.setUp(testObj);
    when(catalogExportJob.getCoreAttributeConfiguration()).thenReturn(configuration);
  }

  @Test
  public void get() {
    Set<MiraklCoreAttributeModel> coreAttributes = testObj.get(catalogExportJob);

    assertThat(coreAttributes).isNotEmpty();
    assertThat(coreAttributes).containsOnly(coreAttribute1, coreAttribute2);
  }

  @Test
  public void set() {
    Set<MiraklCoreAttributeModel> coreAttributes = Sets.newSet(coreAttribute1, coreAttribute2);
    testObj.set(catalogExportJob, coreAttributes);

    verify(configuration).setCoreAttributes(coreAttributes);
  }
}
