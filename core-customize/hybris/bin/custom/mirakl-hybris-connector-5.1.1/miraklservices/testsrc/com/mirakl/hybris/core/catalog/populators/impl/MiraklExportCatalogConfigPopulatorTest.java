package com.mirakl.hybris.core.catalog.populators.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;
import com.mirakl.hybris.core.model.MiraklExportCatalogCronJobModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklExportCatalogConfigPopulatorTest {

  private static final String ROOT_CATEGORY_CODE = "root-category-code";
  private static final String CATEGORY_FILE_NAME = "category-file-name";
  private static final String ATTRIBUTE_FILE_NAME = "attribute-file-name";
  private static final String VALUE_LIST_FILE_NAME = "valueList-file-name";
  private static final boolean DRY_RUN_MODE = true;
  private static final boolean EXCLUDE_ROOT_CATEGORY = true;
  private static final boolean EXPORT_CATEGORIES = true;
  private static final boolean EXPORT_ATTRIBUTES = true;
  private static final boolean EXPORT_VALUE_LISTS = true;

  @InjectMocks
  private MiraklExportCatalogConfigPopulator populator;

  @Mock
  private CommonI18NService commonI18NService;
  @Mock
  private CategoryService categoryService;
  @Mock
  private MiraklExportCatalogCronJobModel cronJob;
  @Mock
  private CategoryModel rootCategory;
  @Mock
  private CatalogVersionModel catalogVersion;
  @Mock
  private CatalogModel catalog;
  @Mock
  private LanguageModel enLanguage, deLanguage;
  @Mock
  private LanguageModel languageEn, languageDe, languageFr;
  @Mock
  private MiraklCategoryCoreAttributeModel categoryCoreAttribute;
  @Mock
  private MiraklCoreAttributeConfigurationModel coreAttributeConfiguration;
  @Mock
  private ComposedTypeModel rootProductType;

  @Before
  public void setUp() {
    when(commonI18NService.getLocaleForLanguage(languageEn)).thenReturn(Locale.ENGLISH);
    when(commonI18NService.getLocaleForLanguage(languageFr)).thenReturn(Locale.FRENCH);
    when(commonI18NService.getLocaleForLanguage(languageDe)).thenReturn(Locale.GERMAN);
    when(cronJob.getDefaultLanguage()).thenReturn(languageEn);
    when(cronJob.getAdditionalLanguages()).thenReturn(asList(languageDe, languageFr));
    when(cronJob.getCategoriesFileName()).thenReturn(CATEGORY_FILE_NAME);
    when(cronJob.getAttributesFileName()).thenReturn(ATTRIBUTE_FILE_NAME);
    when(cronJob.getValueListsFileName()).thenReturn(VALUE_LIST_FILE_NAME);
    when(cronJob.isExportCategories()).thenReturn(EXPORT_CATEGORIES);
    when(cronJob.isExportAttributes()).thenReturn(EXPORT_ATTRIBUTES);
    when(cronJob.isExportValueLists()).thenReturn(EXPORT_VALUE_LISTS);
    when(cronJob.getCatalogVersion()).thenReturn(catalogVersion);
    when(cronJob.isDryRunMode()).thenReturn(DRY_RUN_MODE);
    when(cronJob.isExcludeRootCategory()).thenReturn(EXCLUDE_ROOT_CATEGORY);
    when(catalogVersion.getCatalog()).thenReturn(catalog);
    when(catalog.getTranslatableLanguages()).thenReturn(asList(enLanguage, deLanguage));
    when(commonI18NService.getLocaleForLanguage(enLanguage)).thenReturn(Locale.ENGLISH);
    when(commonI18NService.getLocaleForLanguage(deLanguage)).thenReturn(Locale.GERMAN);
    when(cronJob.getCoreAttributeConfiguration()).thenReturn(coreAttributeConfiguration);
    when(coreAttributeConfiguration.getProductRootCategoryCode()).thenReturn(ROOT_CATEGORY_CODE);
    when(categoryService.getCategoryForCode(catalogVersion, ROOT_CATEGORY_CODE)).thenReturn(rootCategory);
    when(catalog.getRootProductType()).thenReturn(rootProductType);
  }

  @Test
  public void shouldPopulateConfig() throws Exception {
    MiraklExportCatalogConfig exportConfig = new MiraklExportCatalogConfig();

    populator.populate(cronJob, exportConfig);

    assertThat(exportConfig.getRootCategory()).isEqualTo(rootCategory);
    assertThat(exportConfig.getDefaultLocale()).isEqualTo(Locale.ENGLISH);
    assertThat(exportConfig.getAdditionalLocales()).containsExactly(Locale.GERMAN, Locale.FRENCH);
    assertThat(exportConfig.getCategoriesFilename()).isEqualTo(CATEGORY_FILE_NAME);
    assertThat(exportConfig.getAttributesFilename()).isEqualTo(ATTRIBUTE_FILE_NAME);
    assertThat(exportConfig.getValueListsFilename()).isEqualTo(VALUE_LIST_FILE_NAME);
    assertThat(exportConfig.isDryRunMode()).isEqualTo(DRY_RUN_MODE);
    assertThat(exportConfig.isExcludeRootCategory()).isEqualTo(EXCLUDE_ROOT_CATEGORY);
    assertThat(exportConfig.isExportCategories()).isEqualTo(EXPORT_CATEGORIES);
    assertThat(exportConfig.isExportAttributes()).isEqualTo(EXPORT_ATTRIBUTES);
    assertThat(exportConfig.isExportValueLists()).isEqualTo(EXPORT_VALUE_LISTS);
  }

}
