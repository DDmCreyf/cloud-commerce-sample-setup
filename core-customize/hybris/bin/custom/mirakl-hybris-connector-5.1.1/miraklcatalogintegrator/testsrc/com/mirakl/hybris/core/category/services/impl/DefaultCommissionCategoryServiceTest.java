package com.mirakl.hybris.core.category.services.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.catalog.strategies.MiraklExportHeaderResolverStrategy;
import com.mirakl.hybris.core.category.populators.CommissionCategoryPopulator;
import com.mirakl.hybris.core.enums.MiraklCategoryExportHeader;
import com.mirakl.hybris.core.util.services.CsvService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCommissionCategoryServiceTest {

  private static final String CSV_CONTENT = "csvContent";

  @InjectMocks
  @Spy
  private DefaultCommissionCategoryService testObj;

  @Mock
  private CategoryService categoryService;
  @Mock
  private CsvService csvService;
  @Mock
  private CategoryModel rootCategory;
  @Mock
  private CategoryModel category1, category2;
  @Mock
  private Map<String, String> rootCategoryMap, subCategoryMap;
  @Mock
  private CatalogVersionModel rootCatalogVersion, otherCatalogVersion;
  @Mock
  private CommissionCategoryPopulator commissionCategoryPopulator;
  @Mock
  private SessionService sessionService;
  @Mock
  private MiraklExportHeaderResolverStrategy miraklExportHeaderResolverStrategy;
  @Captor
  private ArgumentCaptor<Collection<Pair<CategoryModel, Collection<CategoryModel>>>> categoryPairsArgumentCaptor;

  @Before
  public void setUp() throws IOException {
    when(rootCategory.getCatalogVersion()).thenReturn(rootCatalogVersion);
    when(category1.getCatalogVersion()).thenReturn(rootCatalogVersion);
    when(category2.getCatalogVersion()).thenReturn(otherCatalogVersion);

    when(categoryService.getAllSubcategoriesForCategory(rootCategory)).thenReturn(asList(category1, category2));
    when(sessionService.executeInLocalView(any(SessionExecutionBody.class))).thenReturn(asList(rootCategory, subCategoryMap));
    when(csvService.createCsvWithHeaders(any(), any())).thenReturn(CSV_CONTENT);
  }

  @Test
  public void getsCommissionCategories() {
    Collection<CategoryModel> result = testObj.getCategories(rootCategory);

    assertThat(result).containsOnly(rootCategory, category1);
  }

  @Test
  public void getsCategoryExportCsvContent() throws IOException {
    String result = testObj.getCategoryExportCsvContent(Locale.ENGLISH, asList(rootCategory, category1));

    assertThat(result).isEqualTo(CSV_CONTENT);

    verify(csvService).createCsvWithHeaders(any(), any());

    verify(testObj).mapExportCategories(eq(Locale.ENGLISH), eq(singleton(Locale.ENGLISH)), categoryPairsArgumentCaptor.capture());
    Collection<Pair<CategoryModel, Collection<CategoryModel>>> categoryPairs = categoryPairsArgumentCaptor.getValue();
    assertThat(categoryPairs).isNotEmpty();
    assertThat(categoryPairs).hasSize(2);

    Iterator<Pair<CategoryModel, Collection<CategoryModel>>> categoryPairsIterator = categoryPairs.iterator();
    Pair<CategoryModel, Collection<CategoryModel>> rootCategoryPair = categoryPairsIterator.next();
    assertThat(rootCategoryPair.getKey()).isEqualTo(rootCategory);
    assertThat(rootCategoryPair.getValue()).isEqualTo(asList(rootCategory, category1));

    Pair<CategoryModel, Collection<CategoryModel>> subCategoryPair = categoryPairsIterator.next();
    assertThat(subCategoryPair.getKey()).isEqualTo(category1);
    assertThat(subCategoryPair.getValue()).isEqualTo(asList(rootCategory, category1));
  }

  @Test
  public void getsCategoryMultipleLocalesExportCsvContent() throws IOException {
    final List<Locale> localeList = asList(Locale.ENGLISH, Locale.FRENCH);
    final Set<Locale> additionalLocales = new HashSet<>(localeList);
    when(miraklExportHeaderResolverStrategy.getSupportedHeaders(MiraklCategoryExportHeader.class, additionalLocales))
    .thenReturn(new String[0]);

    String result = testObj.getCategoryExportCsvContent(Locale.ENGLISH, additionalLocales, asList(rootCategory, category1));

    assertThat(result).isEqualTo(CSV_CONTENT);

    verify(csvService).createCsvWithHeaders(any(), any());

    verify(testObj).mapExportCategories(eq(Locale.ENGLISH), eq(additionalLocales), categoryPairsArgumentCaptor.capture());
    Collection<Pair<CategoryModel, Collection<CategoryModel>>> categoryPairs = categoryPairsArgumentCaptor.getValue();
    assertThat(categoryPairs).isNotEmpty();
    assertThat(categoryPairs).hasSize(2);

    Iterator<Pair<CategoryModel, Collection<CategoryModel>>> categoryPairsIterator = categoryPairs.iterator();
    Pair<CategoryModel, Collection<CategoryModel>> rootCategoryPair = categoryPairsIterator.next();
    assertThat(rootCategoryPair.getKey()).isEqualTo(rootCategory);
    assertThat(rootCategoryPair.getValue()).isEqualTo(asList(rootCategory, category1));

    Pair<CategoryModel, Collection<CategoryModel>> subCategoryPair = categoryPairsIterator.next();
    assertThat(subCategoryPair.getKey()).isEqualTo(category1);
    assertThat(subCategoryPair.getValue()).isEqualTo(asList(rootCategory, category1));
  }

  @Test(expected = IOException.class)
  public void getCategoryExportCsvContentThrowsIOExceptionIfCSVContentCannotBeCreated() throws IOException {
    when(csvService.createCsvWithHeaders(any(), any())).thenThrow(new IOException());

    testObj.getCategoryExportCsvContent(Locale.ENGLISH, asList(rootCategory, category1));
  }

  @Test(expected = IOException.class)
  public void getCategoryExportCsvContentThrowsIOExceptionIfCSVContentCannotBeCreatedWithListOfLocales() throws IOException {
    when(csvService.createCsvWithHeaders(any(), any())).thenThrow(new IOException());

    testObj.getCategoryExportCsvContent(Locale.ENGLISH, singleton(Locale.ENGLISH), asList(rootCategory, category1));
  }

}
