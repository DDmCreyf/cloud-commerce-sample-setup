package com.mirakl.hybris.core.product.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.variants.model.VariantTypeModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AcceleratorLikeProductCodeGenerationStrategyTest {

  private static final String STYLE_ATTRIBUTE = "style";
  private static final String APPAREL_SIZE_VARIANT_PRODUCT = "ApparelSizeVariantProduct";
  private static final String APPAREL_STYLE_VARIANT_PRODUCT = "ApparelStyleVariantProduct";
  private static final Object GENERATED_KEY = "generatedKey";
  private static final String BASE_PRODUCT_CODE = "Billobang sun T-Shirt";
  private static final String SIZE_ATTRIBUTE = "size";
  private static final String SIZE_XXL = "XXL";
  private static final String STYLE_BLUE = "blue";

  @Mock
  private KeyGenerator keyGenerator;
  @Mock
  private VariantsService variantsService;
  @Mock
  private VariantTypeModel variantComposedType;
  @Mock
  private ComposedTypeModel unvariantComposedType;
  @Mock
  private MiraklRawProductModel rawProduct;
  @Mock
  private ProductModel baseProduct;
  @Mock
  private ProductImportFileContextData context;
  @Mock
  private ProductImportGlobalContextData globalContext;

  @InjectMocks
  private AcceleratorLikeProductCodeGenerationStrategy testObj;

  @Before
  public void setUp() {
    when(context.getGlobalContext()).thenReturn(globalContext);
    when(globalContext.getDeclaredVariantAttributesPerType()).thenReturn(getDeclaredVariantAttributesPerTypeCode());
    when(keyGenerator.generate()).thenReturn(GENERATED_KEY);
    when(baseProduct.getCode()).thenReturn(BASE_PRODUCT_CODE);
    when(variantComposedType.getCode()).thenReturn(APPAREL_SIZE_VARIANT_PRODUCT);
    when(rawProduct.getValues()).thenReturn(getRawProductValues());
  }

  @Test
  public void generateCode() throws Exception {
    String result = testObj.generateCode(variantComposedType, rawProduct, baseProduct, context);

    assertThat(result).isEqualTo(BASE_PRODUCT_CODE + "_" + SIZE_XXL + "_" + STYLE_BLUE);
  }

  @Test
  public void generateCodeForNullBaseProduct() throws Exception {
    String result = testObj.generateCode(variantComposedType, rawProduct, null, context);

    assertThat(result).isEqualTo(GENERATED_KEY + "_" + SIZE_XXL + "_" + STYLE_BLUE);
  }

  @Test
  public void generateCodeForNonVariantProduct() throws Exception {
    String result = testObj.generateCode(unvariantComposedType, rawProduct, null, context);

    assertThat(result).isEqualTo(String.valueOf(GENERATED_KEY));
  }

  private Map<String, String> getRawProductValues() {
    Map<String, String> rawProductValues = new HashMap<>();
    rawProductValues.put(SIZE_ATTRIBUTE, SIZE_XXL);
    rawProductValues.put(STYLE_ATTRIBUTE, STYLE_BLUE);
    return rawProductValues;
  }

  private Map<String, Set<String>> getDeclaredVariantAttributesPerTypeCode() {
    Map<String, Set<String>> declaredVariantAttributesPerTypeCode = new HashMap<>();
    declaredVariantAttributesPerTypeCode.put(APPAREL_SIZE_VARIANT_PRODUCT, Sets.newSet(SIZE_ATTRIBUTE, STYLE_ATTRIBUTE));
    declaredVariantAttributesPerTypeCode.put(APPAREL_STYLE_VARIANT_PRODUCT, Sets.newSet(STYLE_ATTRIBUTE));
    return declaredVariantAttributesPerTypeCode;
  }

}
