package com.mirakl.hybris.core.product.services.impl;

import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.PRODUCTS_IMPORT_STATUSES_PAGESIZE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mci.domain.product.MiraklProductImportResult;
import com.mirakl.client.mci.domain.product.MiraklProductImportResults;
import com.mirakl.client.mci.domain.product.MiraklProductImportWithTransformationStatus;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mci.front.request.product.MiraklProductImportStatusesRequest;
import com.mirakl.hybris.core.enums.MiraklProductImportStatus;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMciProductFileDownloadServiceTest {


  private static final String SHOP_ID = "5021";
  private static final String PRODUCT_FILE_ID_1 = "1234";
  private static final String PRODUCT_FILE_ID_2 = "1456";
  private static final int IMPORT_FILE_COUNT = 350;
  private static final int PAGE_SIZE = 100;
  private static final int TOTAL_PAGE_COUNT = IMPORT_FILE_COUNT / PAGE_SIZE + 1;

  @InjectMocks
  private DefaultMciProductFileDownloadService testObj;

  @Mock
  private MiraklCatalogIntegrationFrontApi mciApi;
  @Mock
  private Date sinceDate;
  @Captor
  private ArgumentCaptor<MiraklProductImportStatusesRequest> requestCaptor;
  @Mock
  private MiraklProductImportResults fileIds;
  @Mock
  private MiraklProductImportResult productImportResult1, productImportResult2;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;

  @Before
  public void setUp() throws Exception {
    when(mciApi.getProductImportStatuses(any(MiraklProductImportStatusesRequest.class))).thenReturn(fileIds);
    when(fileIds.getProductImportResults()).thenReturn(asList(productImportResult1, productImportResult2));
    when(productImportResult1.getImportId()).thenReturn(PRODUCT_FILE_ID_1);
    when(productImportResult2.getImportId()).thenReturn(PRODUCT_FILE_ID_2);
    when(productImportResult1.getImportStatus()).thenReturn(MiraklProductImportWithTransformationStatus.SENT);
    when(productImportResult2.getImportStatus()).thenReturn(MiraklProductImportWithTransformationStatus.FAILED);
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getInt(eq(PRODUCTS_IMPORT_STATUSES_PAGESIZE), anyInt()))
        .thenReturn(PAGE_SIZE);
  }

  @Test
  public void getTransformedProductFileIds() throws Exception {
    List<String> output =
        testObj.getImportIds(sinceDate, SHOP_ID, asList(MiraklProductImportStatus.SENT, MiraklProductImportStatus.FAILED));

    verify(mciApi).getProductImportStatuses(requestCaptor.capture());
    MiraklProductImportStatusesRequest request = requestCaptor.getValue();
    assertThat(request.getShopId()).isEqualTo(SHOP_ID);
    assertThat(request.getHasTransformedFile()).isEqualTo(true);
    assertThat(request.getLastRequestDate()).isEqualTo(sinceDate);
    assertThat(request.getProductImportStatus()).isNull();
    assertThat(output).containsOnly(PRODUCT_FILE_ID_1, PRODUCT_FILE_ID_2);
  }

  @Test
  public void getSpecificStatusOnly() throws Exception {
    List<String> output = testObj.getImportIds(sinceDate, SHOP_ID, singletonList(MiraklProductImportStatus.SENT));

    verify(mciApi).getProductImportStatuses(requestCaptor.capture());
    MiraklProductImportStatusesRequest request = requestCaptor.getValue();
    assertThat(request.getProductImportStatus().toString()).isEqualTo(MiraklProductImportStatus.SENT.toString());
    assertThat(output).containsOnly(PRODUCT_FILE_ID_1);
  }

  @Test
  public void filterUnwantedStatuses() throws Exception {
    when(productImportResult1.getImportStatus()).thenReturn(MiraklProductImportWithTransformationStatus.COMPLETE);

    List<String> output =
        testObj.getImportIds(sinceDate, SHOP_ID, asList(MiraklProductImportStatus.SENT, MiraklProductImportStatus.FAILED));

    verify(mciApi).getProductImportStatuses(requestCaptor.capture());
    MiraklProductImportStatusesRequest request = requestCaptor.getValue();
    assertThat(request.getProductImportStatus()).isNull();
    assertThat(output).containsOnly(PRODUCT_FILE_ID_2);
  }

  @Test
  public void getMultiplePages() throws Exception {
    when(fileIds.getTotalCount()).thenReturn((long) IMPORT_FILE_COUNT);

    testObj.getImportIds(sinceDate, SHOP_ID, null);

    verify(mciApi, times(TOTAL_PAGE_COUNT)).getProductImportStatuses(requestCaptor.capture());
    for (int page = 0; page < TOTAL_PAGE_COUNT; page++) {
      assertThat(requestCaptor.getAllValues().get(page).getOffset()).isEqualTo(page * PAGE_SIZE);
    }
  }

}
