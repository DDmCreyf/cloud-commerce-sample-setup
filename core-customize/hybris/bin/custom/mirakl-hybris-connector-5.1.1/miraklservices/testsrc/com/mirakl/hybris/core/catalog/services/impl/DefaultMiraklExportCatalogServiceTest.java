package com.mirakl.hybris.core.catalog.services.impl;

import static com.mirakl.client.domain.common.MiraklProcessTrackingStatus.COMPLETE;
import static com.mirakl.client.domain.common.MiraklProcessTrackingStatus.WAITING;
import static com.mirakl.hybris.core.catalog.services.impl.DefaultMiraklExportCatalogService.DEFAULT_LOCALIZED_ATTRIBUTE_PATTERN;
import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertTrue;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mci.front.domain.attribute.MiraklAttributeImportTracking;
import com.mirakl.client.mci.front.domain.hierarchy.MiraklHierarchyImportResult;
import com.mirakl.client.mci.front.domain.hierarchy.MiraklHierarchyImportTracking;
import com.mirakl.client.mci.front.domain.value.list.MiraklValueListImportResult;
import com.mirakl.client.mci.front.domain.value.list.MiraklValueListImportTracking;
import com.mirakl.client.mci.front.request.attribute.MiraklAttributeImportRequest;
import com.mirakl.client.mci.front.request.hierarchy.MiraklHierarchyImportRequest;
import com.mirakl.client.mci.front.request.hierarchy.MiraklHierarchyImportStatusRequest;
import com.mirakl.client.mci.front.request.value.list.MiraklValueListImportRequest;
import com.mirakl.client.mci.front.request.value.list.MiraklValueListImportStatusRequest;
import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.events.ExportableCategoryEvent;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.strategies.DeleteCatalogEntriesStrategy;
import com.mirakl.hybris.core.catalog.strategies.ExportCoreAttributesStrategy;
import com.mirakl.hybris.core.catalog.strategies.PrepareCatalogExportStrategy;
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;
import com.mirakl.hybris.core.constants.MiraklservicesConstants;
import com.mirakl.hybris.core.jobs.services.ExportJobReportService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklExportCatalogServiceTest {

  private static final String HIERARCHY_IMPORT_TRACKING_ID = "4097";
  private static final String VALUE_LIST_IMPORT_TRACKING_ID = "4095";
  private static final String ATTRIBUTE_IMPORT_TRACKING_ID = "4012";
  private static final int IMPORT_TIMEOUT = 60;
  private static final int IMPORT_CHECK_INTERVAL = 1;
  private static final String VALUE_TO_LOCALIZE = "description";

  @InjectMocks
  private DefaultMiraklExportCatalogService exportCatalogService;

  @Mock
  private MiraklExportCatalogContext context;
  @Mock
  private MiraklExportCatalogConfig exportConfig;
  @Mock
  private CategoryModel rootCategory, category1, category2, category3;
  @Mock
  private EventService eventService;
  @Mock
  private ExportCoreAttributesStrategy exportCoreAttributesStrategy;
  @Mock
  private PrepareCatalogExportStrategy prepareCatalogExportStrategy;
  @Mock
  private DeleteCatalogEntriesStrategy deleteCatalogEntriesStrategy;
  @Mock
  private MiraklCatalogIntegrationFrontApi mciApi;
  @Mock
  private ExportCatalogWriter writer;
  @Mock
  private MiraklAttributeImportTracking attributeImportTracking;
  @Mock
  private MiraklHierarchyImportTracking hierarchyImportTracking;
  @Mock
  private MiraklValueListImportTracking valueListImportTracking;
  @Mock
  private MiraklHierarchyImportResult hierarchyImportResult;
  @Mock
  private MiraklValueListImportResult valueListImportResult;
  @Mock
  private ExportJobReportService exportJobReportService;
  @Mock
  private File file;
  @Mock
  private Converter<MiraklExportCatalogContext, MiraklExportCatalogContext> contextDuplicatorConverter;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Before
  public void setUp() throws Exception {
    when(context.getExportConfig()).thenReturn(exportConfig);
    when(context.getWriter()).thenReturn(writer);
    when(mciApi.importAttributes(any(MiraklAttributeImportRequest.class))).thenReturn(attributeImportTracking);
    when(mciApi.importHierarchies(any(MiraklHierarchyImportRequest.class))).thenReturn(hierarchyImportTracking);
    when(mciApi.importValueLists(any(MiraklValueListImportRequest.class))).thenReturn(valueListImportTracking);
    when(exportConfig.getRootCategory()).thenReturn(rootCategory);
    when(mciApi.getHierarchyImportResult(any(MiraklHierarchyImportStatusRequest.class))).thenReturn(hierarchyImportResult);
    when(mciApi.getValueListImportResult(any(MiraklValueListImportStatusRequest.class))).thenReturn(valueListImportResult);
    when(valueListImportResult.getImportStatus()).thenReturn(WAITING, WAITING, COMPLETE);
    when(hierarchyImportResult.getImportStatus()).thenReturn(COMPLETE);
    when(attributeImportTracking.getImportId()).thenReturn(ATTRIBUTE_IMPORT_TRACKING_ID);
    when(valueListImportTracking.getImportId()).thenReturn(VALUE_LIST_IMPORT_TRACKING_ID);
    when(hierarchyImportTracking.getImportId()).thenReturn(HIERARCHY_IMPORT_TRACKING_ID);
    when(writer.getCategoriesFile()).thenReturn(file);
    when(writer.getAttributesFile()).thenReturn(file);
    when(writer.getValueListsFile()).thenReturn(file);
    when(contextDuplicatorConverter.convert(context)).thenReturn(context);
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getString(MiraklservicesConstants.CATALOG_EXPORT_LOCALIZED_ATTRIBUTE_PATTERN,
        DEFAULT_LOCALIZED_ATTRIBUTE_PATTERN)).thenReturn(DEFAULT_LOCALIZED_ATTRIBUTE_PATTERN);
  }

  @Test
  public void shouldVisitExportableCategories() throws IOException {
    when(rootCategory.getCategories()).thenReturn(asList(category1, category2, category3));

    exportCatalogService.export(context);

    verify(eventService, times(4)).publishEvent(any(ExportableCategoryEvent.class));
  }

  @Test
  public void shouldNotVisitNotExportableCategories() throws IOException {
    when(rootCategory.getCategories()).thenReturn(asList(category1, category2));
    when(category2.isOperatorExclusive()).thenReturn(true);

    exportCatalogService.export(context);

    verify(eventService, times(2)).publishEvent(any(ExportableCategoryEvent.class));
  }

  @Test
  public void shouldSendFilesInRightOrder() throws IOException {
    when(exportConfig.isExportAttributes()).thenReturn(true);
    when(exportConfig.isExportCategories()).thenReturn(true);
    when(exportConfig.isExportValueLists()).thenReturn(true);

    exportCatalogService.export(context);

    InOrder inOrder = inOrder(mciApi);
    inOrder.verify(mciApi).importValueLists(any(MiraklValueListImportRequest.class));
    inOrder.verify(mciApi).importHierarchies(any(MiraklHierarchyImportRequest.class));
    inOrder.verify(mciApi).importAttributes(any(MiraklAttributeImportRequest.class));
  }

  @Test
  public void shouldWaitBeforeExportingAttributes() throws Exception {
    when(exportConfig.isExportAttributes()).thenReturn(true);
    when(exportConfig.isExportCategories()).thenReturn(true);
    when(exportConfig.isExportValueLists()).thenReturn(true);
    when(exportConfig.getImportTimeout()).thenReturn(IMPORT_TIMEOUT);
    when(exportConfig.getImportCheckInterval()).thenReturn(IMPORT_CHECK_INTERVAL);

    DateTime testStartDate = DateTime.now();
    exportCatalogService.export(context);

    assertTrue(testStartDate.plusSeconds(2 * IMPORT_CHECK_INTERVAL).isBeforeNow());
    verify(hierarchyImportResult, times(3)).getImportStatus();
    verify(valueListImportResult, times(3)).getImportStatus();
  }

  @Test
  public void shouldReturnLocalizedValue() throws Exception {
    String output = exportCatalogService.formatAttributeExportName(VALUE_TO_LOCALIZE, Locale.CANADA_FRENCH);

    assertThat(output).isEqualTo(String.format(DEFAULT_LOCALIZED_ATTRIBUTE_PATTERN, VALUE_TO_LOCALIZE, "fr"));
  }

  @Test
  public void shouldReturnOriginalValue() throws Exception {
    String output = exportCatalogService.formatAttributeExportName(VALUE_TO_LOCALIZE, null);

    assertThat(output).isEqualTo(VALUE_TO_LOCALIZE);
  }

}
