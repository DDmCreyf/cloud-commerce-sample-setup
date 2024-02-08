package com.mirakl.hybris.core.category.services.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.category.synchro.MiraklCategorySynchroTracking;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.catalog.category.MiraklCategorySynchroRequest;
import com.mirakl.hybris.core.category.services.CommissionCategoryService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCommissionCategoryExportServiceTest {

  private static final String CSV_CONTENT = "csvContent";
  private static final String SYNCHRONIZATION_FILE_NAME = "Synchronization file name";

  @InjectMocks
  private DefaultCommissionCategoryExportService testObj;

  @Mock
  private CommissionCategoryService commissionCategoryService;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklOperatorApi;
  @Mock
  private CategoryModel rootCategory, commissionCategory;
  @Mock
  private MiraklCategorySynchroTracking categorySynchroTracking;
  @Captor
  private ArgumentCaptor<MiraklCategorySynchroRequest> categorySynchroRequestArgumentCaptor;

  @Before
  public void setUp() throws IOException {
    when(commissionCategoryService.getCategories(rootCategory)).thenReturn(asList(rootCategory, commissionCategory));
    when(commissionCategoryService.getCategoryExportCsvContent(Locale.ENGLISH, singleton(Locale.ENGLISH),
        asList(rootCategory, commissionCategory))).thenReturn(CSV_CONTENT);
    when(commissionCategoryService.getCategoryExportCsvContent(Locale.ENGLISH, singleton(Locale.FRENCH),
        asList(rootCategory, commissionCategory))).thenReturn(CSV_CONTENT);
    when(miraklOperatorApi.synchronizeCategories(categorySynchroRequestArgumentCaptor.capture()))
        .thenReturn(categorySynchroTracking);
  }

  @Test
  public void exportsCommissionCategories() throws IOException {
    MiraklCategorySynchroTracking result =
        testObj.exportCommissionCategories(rootCategory, Locale.ENGLISH, SYNCHRONIZATION_FILE_NAME);

    assertThat(result).isSameAs(categorySynchroTracking);

    verify(commissionCategoryService).getCategories(rootCategory);
    verify(commissionCategoryService).getCategoryExportCsvContent(Locale.ENGLISH, singleton(Locale.ENGLISH),
        asList(rootCategory, commissionCategory));

    verify(miraklOperatorApi).synchronizeCategories(categorySynchroRequestArgumentCaptor.capture());

    MiraklCategorySynchroRequest miraklCategorySynchroRequest = categorySynchroRequestArgumentCaptor.getValue();
    assertThat(miraklCategorySynchroRequest).isNotNull();
    assertThat(IOUtils.toString(miraklCategorySynchroRequest.getInputStream())).isEqualTo(CSV_CONTENT);
    assertThat(miraklCategorySynchroRequest.getFilename()).isEqualTo(SYNCHRONIZATION_FILE_NAME);
  }

  @Test
  public void exportsCommissionCategoriesWithMultipleLocales() throws IOException {
    MiraklCategorySynchroTracking result =
        testObj.exportCommissionCategories(rootCategory, Locale.ENGLISH, SYNCHRONIZATION_FILE_NAME, singleton(Locale.FRENCH));

    assertThat(result).isSameAs(categorySynchroTracking);

    verify(commissionCategoryService).getCategories(rootCategory);
    verify(commissionCategoryService).getCategoryExportCsvContent(Locale.ENGLISH, singleton(Locale.FRENCH),
        asList(rootCategory, commissionCategory));

    verify(miraklOperatorApi).synchronizeCategories(categorySynchroRequestArgumentCaptor.capture());
    MiraklCategorySynchroRequest miraklCategorySynchroRequest = categorySynchroRequestArgumentCaptor.getValue();
    assertThat(miraklCategorySynchroRequest).isNotNull();
    assertThat(IOUtils.toString(miraklCategorySynchroRequest.getInputStream())).isEqualTo(CSV_CONTENT);
    assertThat(miraklCategorySynchroRequest.getFilename()).isEqualTo(SYNCHRONIZATION_FILE_NAME);
  }

  @Test(expected = MiraklApiException.class)
  public void exportCommissionCategoriesThrowsMiraklApiException() throws IOException {
    when(miraklOperatorApi.synchronizeCategories(categorySynchroRequestArgumentCaptor.capture()))
        .thenThrow(new MiraklApiException(new MiraklErrorResponseBean()));

    testObj.exportCommissionCategories(rootCategory, Locale.ENGLISH, SYNCHRONIZATION_FILE_NAME);
  }

  @Test(expected = IOException.class)
  public void exportCommissionCategoriesThrowsIOException() throws IOException {
    when(commissionCategoryService.getCategoryExportCsvContent(Locale.ENGLISH, singleton(Locale.ENGLISH),
        asList(rootCategory, commissionCategory))).thenThrow(new IOException());

    testObj.exportCommissionCategories(rootCategory, Locale.ENGLISH, SYNCHRONIZATION_FILE_NAME);
  }

  @Test(expected = IllegalArgumentException.class)
  public void exportCommissionCategoriesThrowsIllegalArgumentExceptionIfRootCategoryIsNull() throws IOException {
    testObj.exportCommissionCategories(null, Locale.ENGLISH, SYNCHRONIZATION_FILE_NAME);
  }

}
