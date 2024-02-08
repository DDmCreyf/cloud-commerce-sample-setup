package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

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

import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMcmCategoryAttributeHandlerTest<T extends MiraklCategoryCoreAttributeModel> {

  protected static final String PCM_CATEGORY_CODE = "pcm-category-code";
  protected static final String CATEGORY_ATTRIBUTE_QUALIFIER = "category";
  protected static final String CATEGORY_ATTRIBUTE_UID = "category-attribute";

  @Mock
  protected CategoryModel pcmCategory, brandCategory;
  @Mock
  protected MiraklCategoryCoreAttributeModel categoryCoreAttribute;
  @Mock
  protected VariantProductModel productLevel1, productLevel2;
  @Mock
  protected ProductModel baseProduct;
  @Mock
  protected ProductDataSheetExportContextData exportContext;

  protected PK newSuperCategoryPK = PK.fromLong(1);
  protected PK oldSuperCategoryPK = PK.fromLong(2);
  protected PK pcmCategoryPK = PK.fromLong(3);

  @InjectMocks
  protected DefaultMcmCategoryAttributeHandler<T> handler;

  @Before
  public void setUp() throws Exception {
    when(categoryCoreAttribute.getUid()).thenReturn(CATEGORY_ATTRIBUTE_UID);
    when(productLevel1.getBaseProduct()).thenReturn(productLevel2);
    when(productLevel2.getBaseProduct()).thenReturn(baseProduct);
    when(pcmCategory.getPk()).thenReturn(pcmCategoryPK);
    when(pcmCategory.getCode()).thenReturn(PCM_CATEGORY_CODE);
    Map<String, Set<PK>> allCategoryValues = new HashMap<>();
    allCategoryValues.put(CATEGORY_ATTRIBUTE_UID, newHashSet(newSuperCategoryPK, oldSuperCategoryPK, pcmCategoryPK));
    when(exportContext.getAllCategoryValues()).thenReturn(allCategoryValues);
  }

  @Test
  public void getValueOfVariantProduct() throws Exception {
    when(productLevel1.getSupercategories()).thenReturn(asList(pcmCategory, brandCategory));

    @SuppressWarnings("unchecked")
    Object value = handler.getValue(productLevel1, (T) categoryCoreAttribute, exportContext);

    assertThat(value).isEqualTo(PCM_CATEGORY_CODE);
  }

  @Test
  public void getValueOfVariantProductWhenNoMatches() throws Exception {
    when(productLevel1.getSupercategories()).thenReturn(asList(brandCategory));

    @SuppressWarnings("unchecked")
    Object value = handler.getValue(productLevel1, (T) categoryCoreAttribute, exportContext);

    assertThat(value).isNull();
  }

  @Test
  public void getValueFallbacksOnBaseProduct() throws Exception {
    when(baseProduct.getSupercategories()).thenReturn(asList(pcmCategory, brandCategory));

    @SuppressWarnings("unchecked")
    Object value = handler.getValue(productLevel1, (T) categoryCoreAttribute, exportContext);

    assertThat(value).isEqualTo(PCM_CATEGORY_CODE);
  }

  @Test
  public void getValueWhenNoCategoriesInContext() throws Exception {
    Map<String, Set<PK>> allCategoryValues = new HashMap<>();
    allCategoryValues.put(CATEGORY_ATTRIBUTE_UID, newHashSet());
    when(exportContext.getAllCategoryValues()).thenReturn(allCategoryValues);

    @SuppressWarnings("unchecked")
    Object value = handler.getValue(productLevel1, (T) categoryCoreAttribute, exportContext);

    assertThat(value).isNull();
  }

  @Test
  public void getValueOfProduct() throws Exception {
    when(baseProduct.getSupercategories()).thenReturn(asList(pcmCategory, brandCategory));

    @SuppressWarnings("unchecked")
    Object value = handler.getValue(baseProduct, (T) categoryCoreAttribute, exportContext);

    assertThat(value).isEqualTo(PCM_CATEGORY_CODE);
  }

  @Test
  public void getValueWithLocale() throws Exception {
    when(productLevel1.getSupercategories()).thenReturn(asList(pcmCategory, brandCategory));

    Locale ANY_LOCALE = Locale.ENGLISH;
    @SuppressWarnings("unchecked")
    Object value = handler.getValue(productLevel1, (T) categoryCoreAttribute, ANY_LOCALE, exportContext);

    assertThat(value).isEqualTo(PCM_CATEGORY_CODE);
  }


}
