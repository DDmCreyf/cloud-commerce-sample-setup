package com.mirakl.hybris.core.product.strategies.impl;

import static com.google.common.collect.Sets.newHashSet;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductIdentificationStrategyTest {

  @InjectMocks
  private DefaultProductIdentificationStrategy identificationStrategy;

  @Mock
  private ProductModel product1, product2;
  @Mock
  private MiraklRawProductModel rawProduct;
  @Spy
  private ProductImportData data;

  @Before
  public void setUp() {
    when(data.getRawProduct()).thenReturn(rawProduct);
  }

  @Test
  public void shouldIdentifyByUid() throws Exception {
    when(data.getProductsResolvedByUID()).thenReturn(newHashSet(product1));

    identificationStrategy.identifyProduct(data);

    assertThat(data.getIdentifiedProduct()).isEqualTo(product1);
  }

  @Test(expected = ProductImportException.class)
  public void shouldThrowExceptionWhenMultipleMatchesOnUid() throws Exception {
    when(data.getProductsResolvedByUID()).thenReturn(newHashSet(product1, product2));

    identificationStrategy.identifyProduct(data);
  }

  @Test
  public void shouldIdentifyByShopSku() throws Exception {
    when(data.getProductsResolvedByUID()).thenReturn(Collections.<ProductModel>emptySet());
    when(data.getProductResolvedBySku()).thenReturn(product1);

    identificationStrategy.identifyProduct(data);

    assertThat(data.getIdentifiedProduct()).isEqualTo(product1);
  }

}
