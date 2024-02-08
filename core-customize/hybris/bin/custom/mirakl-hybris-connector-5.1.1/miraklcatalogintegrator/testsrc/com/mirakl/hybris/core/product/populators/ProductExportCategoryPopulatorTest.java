package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.ALL_CATEGORIES_CONTEXT_VARIABLE;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.enums.MiraklProductExportHeader;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.variants.model.VariantProductModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ProductExportCategoryPopulatorTest {

  private static final String CATEGORY_CODE = "category_code";
  private static final String CATEGORY_2_CODE = "category_2_code";

  @InjectMocks
  private ProductExportCategoryPopulator populator;

  @Mock
  private SessionService sessionService;

  @Mock
  private ProductModel product;

  @Mock
  private VariantProductModel variantProduct;

  @Mock
  private CategoryModel category, category2;

  @Before
  public void setUp() throws Exception {
    when(variantProduct.getBaseProduct()).thenReturn(product);
    when(product.getSupercategories()).thenReturn(asList(category, mock(CategoryModel.class), mock(CategoryModel.class)));
    when(category.getCode()).thenReturn(CATEGORY_CODE);
    when(category2.getCode()).thenReturn(CATEGORY_2_CODE);
    when(sessionService.getAttribute(ALL_CATEGORIES_CONTEXT_VARIABLE))
        .thenReturn(asList(category, category2, mock(CategoryModel.class)));
  }

  @Test
  public void shouldExportCategoryForProductsWithNoVariants() {
    HashMap<String, String> result = new HashMap<>();
    populator.populate(product, result);

    assertThat(result.get(MiraklProductExportHeader.CATEGORY_CODE.getCode())).isEqualTo(CATEGORY_CODE);
  }

  @Test
  public void shouldNotFallbackToBaseProductIfCategoryIsPresent() {
    when(variantProduct.getSupercategories()).thenReturn(asList(category2, mock(CategoryModel.class)));

    HashMap<String, String> result = new HashMap<>();
    populator.populate(variantProduct, result);

    assertThat(result.get(MiraklProductExportHeader.CATEGORY_CODE.getCode())).isEqualTo(CATEGORY_2_CODE);
  }

  @Test
  public void shouldFallbackToBaseProductIfCategoryIsNotPresent() {
    HashMap<String, String> result = new HashMap<>();
    populator.populate(variantProduct, result);

    assertThat(result.get(MiraklProductExportHeader.CATEGORY_CODE.getCode())).isEqualTo(CATEGORY_CODE);
  }

  @Test
  public void shoudlFallbackOnMultilevelProduct() {
    when(variantProduct.getSupercategories()).thenReturn(asList(mock(CategoryModel.class), mock(CategoryModel.class)));
    VariantProductModel intermediateProductLevel1 = mock(VariantProductModel.class);
    when(variantProduct.getBaseProduct()).thenReturn(intermediateProductLevel1);
    VariantProductModel intermediateProductLevel2 = mock(VariantProductModel.class);
    when(intermediateProductLevel1.getBaseProduct()).thenReturn(intermediateProductLevel2);
    when(intermediateProductLevel1.getBaseProduct()).thenReturn(product);

    HashMap<String, String> result = new HashMap<>();
    populator.populate(variantProduct, result);

    assertThat(result.get(MiraklProductExportHeader.CATEGORY_CODE.getCode())).isEqualTo(CATEGORY_CODE);
  }

}
