package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.PRODUCTS_EXPORT_FILE_MAX_LINE_COUNT;
import static com.mirakl.hybris.core.enums.MiraklExportType.PRODUCT_EXPORT;
import static com.mirakl.hybris.core.util.PaginationUtils.getNumberOfPages;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.product.synchro.MiraklProductSynchroTracking;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.catalog.product.MiraklProductSynchroRequest;
import com.mirakl.hybris.core.catalog.strategies.MiraklExportHeaderResolverStrategy;
import com.mirakl.hybris.core.enums.MiraklProductExportHeader;
import com.mirakl.hybris.core.jobs.services.ExportJobReportService;
import com.mirakl.hybris.core.util.services.CsvService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMciProductExportStrategyTest {

  private static final int MAX_LINES_PER_FILE = 2;
  private static final String FILE_CONTENT = randomAlphanumeric(10);
  private static final String SYNC_JOB_ID = "syncJobId";
  private static final String SYNCHRONIZATION_FILE_NAME = "Synchronization file name";


  @InjectMocks
  private DefaultMciProductExportStrategy mciExportStrategy;

  @Mock
  private CategoryService categoryService;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;
  @Mock
  private CsvService csvService;
  @Mock
  private ExportJobReportService exportJobReportService;
  @Mock
  private SessionService sessionService;
  @Mock
  private MiraklMarketplacePlatformFrontApi mmpFrontApi;
  @Mock
  private MiraklProductSynchroTracking synchroTracking;
  @Mock
  private CategoryModel pcmCategory, brandCategory;
  @Mock
  private BaseSiteModel baseSite;
  @Mock
  private CatalogVersionModel catalogVersion;
  @Mock
  private ProductModel product1, product2, product3, product4, product5;
  @Mock
  private MiraklExportHeaderResolverStrategy miraklExportHeaderResolverStrategy;

  private List<ProductModel> products;

  @Captor
  private ArgumentCaptor<MiraklProductSynchroRequest> miraklProductSynchroRequestArgumentCaptor;

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() throws IOException {
    products = asList(product1, product2, product3, product4, product5);
    List<Map<String, String>> mappedContent = mockMappedContent(products.size());
    when(csvService.createCsvWithHeaders(
        eq(miraklExportHeaderResolverStrategy.getSupportedHeaders(MiraklProductExportHeader.class)), anyList()))
    .thenReturn(FILE_CONTENT);
    when(sessionService.executeInLocalViewWithParams(anyMapOf(String.class, Object.class), any(SessionExecutionBody.class)))
    .thenReturn(mappedContent);
    when(mmpFrontApi.synchronizeProducts(miraklProductSynchroRequestArgumentCaptor.capture())).thenReturn(synchroTracking);
    when(synchroTracking.getSynchroId()).thenReturn(SYNC_JOB_ID);
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getInt(PRODUCTS_EXPORT_FILE_MAX_LINE_COUNT)).thenReturn(MAX_LINES_PER_FILE);
  }

  @Test
  public void shouldExportProducts() throws IOException {
    int exportedProductsCount =
        mciExportStrategy.exportProducts(products, pcmCategory, brandCategory, baseSite, SYNCHRONIZATION_FILE_NAME);

    assertThat(exportedProductsCount).isEqualTo(products.size());
    int numberOfPages = getNumberOfPages(products.size(), MAX_LINES_PER_FILE);
    verify(mmpFrontApi, times(numberOfPages)).synchronizeProducts(miraklProductSynchroRequestArgumentCaptor.capture());
    MiraklProductSynchroRequest request = miraklProductSynchroRequestArgumentCaptor.getValue();
    assertThat(request).isNotNull();
    assertThat(request.getFilename()).isEqualTo(SYNCHRONIZATION_FILE_NAME);
    assertThat(IOUtils.toString(request.getInputStream())).isEqualTo(FILE_CONTENT);

    verify(exportJobReportService, times(numberOfPages)).createMiraklJobReport(SYNC_JOB_ID, PRODUCT_EXPORT);
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, String>> mockMappedContent(int size) {
    List<Map<String, String>> mappedContent = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      mappedContent.add(mock(Map.class));
    }
    return mappedContent;
  }
}
