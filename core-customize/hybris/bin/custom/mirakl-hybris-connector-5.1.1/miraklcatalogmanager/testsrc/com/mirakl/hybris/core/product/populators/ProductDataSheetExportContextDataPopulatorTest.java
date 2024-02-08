package com.mirakl.hybris.core.product.populators;

import static com.google.common.collect.Sets.newHashSet;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.catalog.services.MiraklCoreAttributeService;
import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.enums.ProductOrigin;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklExportSellableProductsCronJobModel;
import com.mirakl.hybris.core.product.services.MiraklProductService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductDataSheetExportContextDataPopulatorTest {

  private static final PK BASE_SITE_PK = PK.fromLong(1L);
  private static final PK PRODUCT_CATALOG_VERSION_PK = PK.fromLong(2L);
  private static final PK CORE_ATTRIBUTE1_PK = PK.fromLong(3L);
  private static final PK CORE_ATTRIBUTE2_PK = PK.fromLong(4L);
  private static final PK CATEGORY1_PK = PK.fromLong(5L);
  private static final PK CATEGORY2_PK = PK.fromLong(6L);
  private static final PK CATEGORY3_PK = PK.fromLong(7L);
  private static final PK CATEGORY4_PK = PK.fromLong(8L);
  private static final PK CATEGORY5_PK = PK.fromLong(9L);
  private static final PK CATEGORY6_PK = PK.fromLong(10L);
  private static final PK ROOT_CATEGORY_PK = PK.fromLong(11L);
  private static final String CORE_ATTRIBUTE1_CODE = "core-attribute1-code";
  private static final String CORE_ATTRIBUTE1_UID = "core-attribute1-uid";
  private static final String CORE_ATTRIBUTE2_CODE = "core-attribute2-code";
  private static final String EXPORT_FILENAME = "export-filename";
  private static final Date LAST_EXPORT_DATE = new Date();
  private static final String ROOT_CATEGORY_CODE = "ROOT_CATEGORY_CODE";
  private static final Set<ProductOrigin> PRODUCT_ORIGINS = newHashSet(ProductOrigin.OPERATOR);

  @InjectMocks
  private ProductDataSheetExportContextDataPopulator populator;
  @Mock
  private MiraklProductService miraklProductService;
  @Mock
  private MiraklCoreAttributeService coreAttributeService;
  @Mock
  private MiraklExportSellableProductsCronJobModel cronJob;
  @Mock
  private BaseSiteModel baseSite;
  @Mock
  private CommonI18NService commonI18NService;
  @Mock
  private CategoryService categoryService;
  @Mock
  private MiraklCoreAttributeModel coreAttribute1, coreAttribute2;
  @Mock
  private CatalogVersionModel catalogVersion;
  @Mock
  private CatalogModel catalog;
  @Mock
  private CategoryModel rootCategory, category1, category2, category3, category4, category5, category6;
  @Mock
  private LanguageModel languageFr, languageEn;
  @Mock
  private MiraklCoreAttributeConfigurationModel miraklCoreAttributeConfigurationModel;
  @Mock
  private MiraklCategoryCoreAttributeModel miraklCategoryCoreAttributeModel;

  private Set<MiraklCoreAttributeModel> coreAttributes;

  @Before
  public void setUp() throws Exception {
    coreAttributes = newHashSet(coreAttribute1, coreAttribute2);
    Map<String, MiraklCoreAttributeModel> attributeCodes = new HashMap<>();
    attributeCodes.put(CORE_ATTRIBUTE1_CODE, coreAttribute1);
    attributeCodes.put(CORE_ATTRIBUTE2_CODE, coreAttribute2);
    Map<String, Set<CategoryModel>> allCategoryValues = new HashMap<>();
    allCategoryValues.put(CORE_ATTRIBUTE1_UID, newHashSet(category1, category2));
    when(cronJob.getCoreAttributeConfiguration()).thenReturn(miraklCoreAttributeConfigurationModel);
    when(coreAttributeService.getCoreAttributeCodes(coreAttributes)).thenReturn(attributeCodes);
    when(coreAttributeService.getAllCategoryValuesForCategoryCoreAttributes(coreAttributes, catalogVersion))
    .thenReturn(allCategoryValues);
    when(coreAttributeService.getCategoryCoreAttributeForRole(MiraklAttributeRole.CATEGORY_ATTRIBUTE,
        miraklCoreAttributeConfigurationModel)).thenReturn(miraklCategoryCoreAttributeModel);
    when(miraklCategoryCoreAttributeModel.getRootCategoryCode()).thenReturn(ROOT_CATEGORY_CODE);
    when(categoryService.getCategoryForCode(catalogVersion, ROOT_CATEGORY_CODE)).thenReturn(rootCategory);
    when(rootCategory.getCategories()).thenReturn(Lists.newArrayList(category1, category2));
    when(cronJob.getBaseSite()).thenReturn(baseSite);
    when(baseSite.getPk()).thenReturn(BASE_SITE_PK);
    when(cronJob.getCatalogVersion()).thenReturn(catalogVersion);
    when(catalogVersion.getPk()).thenReturn(PRODUCT_CATALOG_VERSION_PK);
    when(catalogVersion.getCatalog()).thenReturn(catalog);
    when(catalog.getTranslatableLanguages()).thenReturn(Sets.newHashSet(languageFr, languageEn));
    when(cronJob.getSynchronizationFileName()).thenReturn(EXPORT_FILENAME);
    when(cronJob.getCoreAttributes()).thenReturn(coreAttributes);
    when(cronJob.getLastExportDate()).thenReturn(LAST_EXPORT_DATE);
    when(cronJob.getProductOrigins()).thenReturn(PRODUCT_ORIGINS);
    when(coreAttribute1.getPk()).thenReturn(CORE_ATTRIBUTE1_PK);
    when(coreAttribute2.getPk()).thenReturn(CORE_ATTRIBUTE2_PK);
    when(category1.getPk()).thenReturn(CATEGORY1_PK);
    when(category2.getPk()).thenReturn(CATEGORY2_PK);
    when(category5.getPk()).thenReturn(CATEGORY5_PK);
    when(rootCategory.getPk()).thenReturn(ROOT_CATEGORY_PK);
    when(commonI18NService.getLocaleForLanguage(languageFr)).thenReturn(Locale.FRENCH);
    when(commonI18NService.getLocaleForLanguage(languageEn)).thenReturn(Locale.ENGLISH);
    when(category3.isOperatorExclusive()).thenReturn(true);
    when(category6.isOperatorExclusive()).thenReturn(true);
    when(category1.getCategories()).thenReturn(Lists.newArrayList(category2));
    when(category2.getCategories()).thenReturn(Lists.newArrayList(category3, category5));
    when(category5.getCategories()).thenReturn(Lists.newArrayList(category6));
  }

  @Test
  public void shouldPopulateContextAndGetAllExportableCategories() throws Exception {
    ProductDataSheetExportContextData target = new ProductDataSheetExportContextData();

    populator.populate(cronJob, target);

    assertThat(target.getBaseSite()).isEqualTo(BASE_SITE_PK);
    assertThat(target.getFilename()).isEqualTo(EXPORT_FILENAME);
    assertThat(target.getProductOrigins()).containsOnly(PRODUCT_ORIGINS.toArray());
    assertThat(target.getModifiedAfter()).isEqualTo(LAST_EXPORT_DATE);
    assertThat(target.getProductCatalogVersion()).isEqualTo(PRODUCT_CATALOG_VERSION_PK);
    assertThat(target.getCoreAttributes()).includes(entry(CORE_ATTRIBUTE1_CODE, CORE_ATTRIBUTE1_PK));
    assertThat(target.getCoreAttributes()).includes(entry(CORE_ATTRIBUTE2_CODE, CORE_ATTRIBUTE2_PK));
    assertThat(target.getAllExportableCategories()).contains(ROOT_CATEGORY_PK, CATEGORY1_PK, CATEGORY2_PK, CATEGORY5_PK);
    assertThat(target.getAllCategoryValues()).includes(entry(CORE_ATTRIBUTE1_UID, newHashSet(CATEGORY1_PK, CATEGORY2_PK)));
  }

  @Test
  public void shouldNotPopulateLastExportDateForFullExport() throws Exception {
    when(cronJob.isFullExport()).thenReturn(true);
    ProductDataSheetExportContextData target = new ProductDataSheetExportContextData();

    populator.populate(cronJob, target);

    assertThat(target.getModifiedAfter()).isNull();
  }

}
