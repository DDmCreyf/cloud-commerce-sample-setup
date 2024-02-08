package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.google.common.collect.Sets.newHashSet;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeOwnerStrategy;
import com.mirakl.hybris.core.catalog.strategies.ValueListNamingStrategy;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantCategoryModel;
import de.hybris.platform.variants.model.VariantValueCategoryModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultVariantCategoryAttributeHandlerTest {

  private static final String CORE_ATTRIBUTE_UID = "core-attribute-uid";
  private static final String VARIANT_VALUE_CODE = "variant-value";
  private static final PK VARIANT_VALUE_PK = PK.fromLong(1L);

  @Mock
  private CategoryService categoryService;
  @Mock
  private CoreAttributeOwnerStrategy coreAttributeOwnerStrategy;
  @Mock
  private ModelService modelService;
  @Mock
  private ValueListNamingStrategy valueListNamingStrategy;
  @Mock
  private AttributeValueData valueData;
  @Mock
  private MiraklCategoryCoreAttributeModel coreAttribute;
  @Mock
  private ProductImportData data;
  @Mock
  private ProductImportFileContextData context;
  @Mock
  private ProductImportGlobalContextData globalContext;
  @Mock
  private ProductModel productToUpdate, baseProductToUpdate;
  @Mock
  private VariantCategoryModel variantCategory;
  @Mock
  private VariantValueCategoryModel variantValue;
  @Mock
  private CategoryModel category1, category2;
  @Captor
  private ArgumentCaptor<Collection<CategoryModel>> baseSuperCategoriesCaptor;
  private HashMap<String, Set<PK>> categoryValues = new HashMap<>();

  @InjectMocks
  private DefaultVariantCategoryAttributeHandler handler;

  @Before
  public void setUp() throws Exception {
    when(valueData.getCoreAttribute()).thenReturn(coreAttribute);
    when(coreAttributeOwnerStrategy.determineOwner(coreAttribute, data, context)).thenReturn(productToUpdate);
    when(context.getGlobalContext()).thenReturn(globalContext);
    categoryValues.put(CORE_ATTRIBUTE_UID, newHashSet(VARIANT_VALUE_PK, PK.fromLong(2L), PK.fromLong(3L)));
    when(globalContext.getAllCategoryValues()).thenReturn(categoryValues);
    when(coreAttribute.getUid()).thenReturn(CORE_ATTRIBUTE_UID);
    when(data.getProductToUpdate()).thenReturn(productToUpdate);
    when(data.getRootBaseProductToUpdate()).thenReturn(baseProductToUpdate);
    when(productToUpdate.getSupercategories()).thenReturn(Collections.<CategoryModel>singleton(variantValue));
    when(baseProductToUpdate.getSupercategories()).thenReturn(Sets.newHashSet(category1, category2));
    when(variantValue.getPk()).thenReturn(VARIANT_VALUE_PK);
    when(variantValue.getSupercategories()).thenReturn(Collections.<CategoryModel>singletonList(variantCategory));
  }

  @Test
  public void shouldSetVariantCategoryOnBaseProductWhenVariantValueNotEmpty() throws Exception {
    when(valueData.getValue()).thenReturn(VARIANT_VALUE_CODE);

    handler.setValue(valueData, data, context);

    verify(baseProductToUpdate).setSupercategories(baseSuperCategoriesCaptor.capture());
    Collection<CategoryModel> baseSuperCategories = baseSuperCategoriesCaptor.getValue();
    assertThat(baseSuperCategories).isNotNull();
    assertThat(baseSuperCategories).contains(variantCategory);
  }

  @Test
  public void shouldNotChangeBaseProductWhenVariantValueIsEmpty() throws Exception {
    when(valueData.getValue()).thenReturn(null);

    handler.setValue(valueData, data, context);

    verify(baseProductToUpdate, never()).setSupercategories(anyCollectionOf(CategoryModel.class));
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfNoVariantCategoryForVariantValue() throws Exception {
    when(valueData.getValue()).thenReturn(VARIANT_VALUE_CODE);
    when(variantValue.getSupercategories()).thenReturn(null);

    handler.setValue(valueData, data, context);
  }

}
