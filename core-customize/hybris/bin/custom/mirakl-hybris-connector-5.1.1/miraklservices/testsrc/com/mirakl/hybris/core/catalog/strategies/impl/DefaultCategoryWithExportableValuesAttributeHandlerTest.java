package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static com.mirakl.hybris.core.enums.MiraklAttributeType.LIST;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.enums.MiraklValueListExportHeader;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCategoryWithExportableValuesAttributeHandlerTest {

  private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
  private static final String ROOT_CATEGORY_CODE = "root-category-code";
  private static final String CATEGORY1_CODE = "category1-code";
  private static final String CATEGORY2_CODE = "category2-code";
  private static final String CATEGORY1_NAME = "category1-name";
  private static final String CATEGORY2_NAME = "category2-name";
  private static final String BRAND_VALUE_LIST_CODE = "brand-values";
  private static final String BRAND_VALUE_LIST_LABEL = "Brand Values";

  @Mock
  private CategoryService categoryService;
  @Mock
  private DefaultValueListNamingStrategy valueListNamingStrategy;
  @Mock
  private CategoryModel rootCategory, category1, category2;
  @Mock
  protected CatalogVersionModel productCatalogVersion;
  @Mock
  private MiraklCategoryCoreAttributeModel categoryCoreAttribute;
  @Mock
  private MiraklExportCatalogContext exportContext;
  @Mock
  private MiraklExportCatalogConfig exportConfig;

  @InjectMocks
  private DefaultCategoryWithExportableValuesAttributeHandler handler;

  @Before
  public void setUp() throws Exception {
    when(valueListNamingStrategy.getLabel(categoryCoreAttribute, DEFAULT_LOCALE)).thenReturn(BRAND_VALUE_LIST_LABEL);
    when(categoryCoreAttribute.getTypeParameters()).thenReturn(format(CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN, BRAND_VALUE_LIST_CODE));
    when(categoryCoreAttribute.getType()).thenReturn(LIST);
    when(categoryCoreAttribute.getRootCategoryCode()).thenReturn(ROOT_CATEGORY_CODE);
    when(categoryService.getCategoryForCode(productCatalogVersion, ROOT_CATEGORY_CODE)).thenReturn(rootCategory);
    when(rootCategory.getAllSubcategories()).thenReturn(asList(category1, category2));
    when(category1.getCode()).thenReturn(CATEGORY1_CODE);
    when(category2.getCode()).thenReturn(CATEGORY2_CODE);
    when(category1.getName(DEFAULT_LOCALE)).thenReturn(CATEGORY1_NAME);
    when(category2.getName(DEFAULT_LOCALE)).thenReturn(CATEGORY2_NAME);
    when(exportContext.getExportConfig()).thenReturn(exportConfig);
    when(exportConfig.getCatalogVersion()).thenReturn(productCatalogVersion);
    when(categoryService.getCategoryForCode(productCatalogVersion, ROOT_CATEGORY_CODE)).thenReturn(rootCategory);
    when(exportConfig.getDefaultLocale()).thenReturn(DEFAULT_LOCALE);

  }

  @Test
  public void testGetValues() throws Exception {
    List<Map<String, String>> result = handler.getValues(categoryCoreAttribute, exportContext);

    assertThat(result.get(0).get(MiraklValueListExportHeader.LIST_LABEL.getCode())).isEqualTo(BRAND_VALUE_LIST_LABEL);
    assertThat(result.get(0).get(MiraklValueListExportHeader.LIST_CODE.getCode())).isEqualTo(BRAND_VALUE_LIST_CODE);
    assertThat(result.get(0).get(MiraklValueListExportHeader.VALUE_CODE.getCode())).isEqualTo(CATEGORY1_CODE);
    assertThat(result.get(0).get(MiraklValueListExportHeader.VALUE_LABEL.getCode())).isEqualTo(CATEGORY1_NAME);
    assertThat(result.get(1).get(MiraklValueListExportHeader.LIST_LABEL.getCode())).isEqualTo(BRAND_VALUE_LIST_LABEL);
    assertThat(result.get(1).get(MiraklValueListExportHeader.LIST_CODE.getCode())).isEqualTo(BRAND_VALUE_LIST_CODE);
    assertThat(result.get(1).get(MiraklValueListExportHeader.VALUE_CODE.getCode())).isEqualTo(CATEGORY2_CODE);
    assertThat(result.get(1).get(MiraklValueListExportHeader.VALUE_LABEL.getCode())).isEqualTo(CATEGORY2_NAME);

  }

}
