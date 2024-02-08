package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCategoryAttributeHandlerTest<T extends MiraklCategoryCoreAttributeModel>
    extends AbstractCoreAttributeHandlerTest<T> {

  protected static final String PCM_CATEGORY_CODE = "pcm-category-code";
  protected static final String CATEGORY_ATTRIBUTE_QUALIFIER = "category";
  protected static final String CATEGORY_ATTRIBUTE_UID = "category-attribute";
  protected static final String NEW_SUPER_CATEGORY_CODE = "T-SHIRT";
  protected static final String OLD_SUPER_CATEGORY_CODE = "PANTS";

  @Mock
  protected CategoryService categoryService;
  @Mock
  protected MiraklRawProductModel rawProduct;
  @Mock
  protected CatalogVersionModel productCatalogVersion;
  @Mock
  protected CategoryModel newSuperCategory, oldSuperCategory, oldSuperBrandCategory, pcmCategory, brandCategory;
  @Mock
  protected MiraklCategoryCoreAttributeModel categoryCoreAttribute;

  protected PK productCatalogVersionPK = PK.fromLong(0);
  protected PK newSuperCategoryPK = PK.fromLong(1);
  protected PK oldSuperCategoryPK = PK.fromLong(2);
  protected PK pcmCategoryPK = PK.fromLong(3);
  protected PK oldSuperBrandCategoryPK = PK.fromLong(10);
  protected PK categoryCoreAttributePK = PK.fromLong(20);

  protected Map<String, Set<PK>> categoryAttributePks;

  @Captor
  protected ArgumentCaptor<Collection<CategoryModel>> superCategoriesCaptor;
  @InjectMocks
  protected DefaultCategoryAttributeHandler<T> testObj;

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    categoryAttributePks = new HashMap<>();
    categoryAttributePks.put(CATEGORY_ATTRIBUTE_UID, Sets.newHashSet(newSuperCategoryPK, oldSuperCategoryPK));
    when(importContext.getGlobalContext()).thenReturn(globalImportContext);
    when(attributeValue.getCoreAttribute()).thenReturn(coreAttribute);
    when(globalImportContext.getAllCategoryValues()).thenReturn(categoryAttributePks);
    when(globalImportContext.getProductCatalogVersion()).thenReturn(productCatalogVersionPK);
    when(categoryCoreAttribute.getUid()).thenReturn(CATEGORY_ATTRIBUTE_UID);
    when(modelService.get(productCatalogVersionPK)).thenReturn(productCatalogVersion);
    when(categoryService.getCategoryForCode(productCatalogVersion, NEW_SUPER_CATEGORY_CODE)).thenReturn(newSuperCategory);
    when(categoryService.getCategoryForCode(productCatalogVersion, OLD_SUPER_CATEGORY_CODE)).thenReturn(oldSuperCategory);
    when(ownerProduct.getSupercategories()).thenReturn(asList(oldSuperCategory, oldSuperBrandCategory));
    when(oldSuperCategory.getPk()).thenReturn(oldSuperCategoryPK);
    when(attributeValue.getCoreAttribute()).thenReturn(categoryCoreAttribute);
    when(coreAttributeOwnerStrategy.determineOwner(categoryCoreAttribute, data, importContext)).thenReturn(ownerProduct);
  }

  @Test
  public void setValue() throws Exception {
    when(attributeValue.getValue()).thenReturn(NEW_SUPER_CATEGORY_CODE);

    testObj.setValue(attributeValue, data, importContext);

    verify(ownerProduct, atLeastOnce()).setSupercategories(superCategoriesCaptor.capture());
    Collection<CategoryModel> superCategories = superCategoriesCaptor.getValue();
    assertThat(superCategories).containsOnly(newSuperCategory, oldSuperBrandCategory);
  }

  @Test
  public void setValueWhenNoCategoryWasDefined() throws Exception {
    testObj.setValue(attributeValue, data, importContext);

    verify(ownerProduct, atLeastOnce()).setSupercategories(superCategoriesCaptor.capture());
    Collection<CategoryModel> superCategories = superCategoriesCaptor.getValue();
    assertThat(superCategories).containsOnly(oldSuperBrandCategory);
  }

  @Test
  public void setValueShouldDoNothingWhenCategoryIsAlreadySet() throws Exception {
    when(attributeValue.getValue()).thenReturn(OLD_SUPER_CATEGORY_CODE);

    testObj.setValue(attributeValue, data, importContext);

    verify(ownerProduct, never()).setSupercategories(anyCollectionOf(CategoryModel.class));
  }


}
