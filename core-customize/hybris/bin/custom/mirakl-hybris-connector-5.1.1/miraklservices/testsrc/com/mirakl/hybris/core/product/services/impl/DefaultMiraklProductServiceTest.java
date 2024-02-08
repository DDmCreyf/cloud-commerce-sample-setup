package com.mirakl.hybris.core.product.services.impl;

import static de.hybris.platform.basecommerce.enums.StockLevelStatus.INSTOCK;
import static de.hybris.platform.basecommerce.enums.StockLevelStatus.OUTOFSTOCK;
import static de.hybris.platform.catalog.enums.ArticleApprovalStatus.APPROVED;
import static de.hybris.platform.catalog.enums.ArticleApprovalStatus.UNAPPROVED;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.variants.model.VariantTypeModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultMiraklProductServiceTest {

  @InjectMocks
  private DefaultMiraklProductService miraklProductService;

  @Mock
  private CommerceStockService commerceStockService;
  @Mock
  private CommercePriceService commercePriceService;
  @Mock
  private BaseStoreService baseStoreService;
  @Mock
  private ProductModel product;
  @Mock
  private BaseStoreModel store;
  @Mock
  private VariantTypeModel variantType;
  @Mock
  private PriceInformation priceInfo;

  @Before
  public void setUp() {
    when(product.getApprovalStatus()).thenReturn(APPROVED);
    when(product.getVariantType()).thenReturn(null);
    when(baseStoreService.getCurrentBaseStore()).thenReturn(store);
    when(commerceStockService.getStockLevelStatusForProductAndBaseStore(product, store)).thenReturn(INSTOCK);
    when(commercePriceService.getWebPriceForProduct(product)).thenReturn(priceInfo);
  }

  @Test
  public void sellableByOperatorReturnsTrueIfProductIsPurchasableAndHasStockAndPrice() {
    boolean result = miraklProductService.isSellableByOperator(product);

    assertThat(result).isTrue();

    verify(baseStoreService).getCurrentBaseStore();
    verify(commerceStockService).getStockLevelStatusForProductAndBaseStore(product, store);
    verify(commercePriceService).getWebPriceForProduct(product);
  }

  @Test
  public void sellableByOperatorReturnsFalseIfProductIsNotPurchasableAndHasNOStock() {
    when(commerceStockService.getStockLevelStatusForProductAndBaseStore(product, store)).thenReturn(OUTOFSTOCK);

    boolean result = miraklProductService.isSellableByOperator(product);

    assertThat(result).isFalse();

    verify(baseStoreService).getCurrentBaseStore();
    verify(commerceStockService).getStockLevelStatusForProductAndBaseStore(product, store);
    verify(commercePriceService, never()).getWebPriceForProduct(any(ProductModel.class));
  }

  @Test
  public void sellableByOperatorReturnsFalseIfProductWithOffersIsNotApproved() {
    when(product.getApprovalStatus()).thenReturn(UNAPPROVED);

    boolean result = miraklProductService.isSellableByOperator(product);

    assertThat(result).isFalse();
  }

  @Test
  public void sellableByOperatorReturnsFalseHasDefinedVariantType() {
    when(product.getVariantType()).thenReturn(variantType);

    boolean result = miraklProductService.isSellableByOperator(product);

    assertThat(result).isFalse();
  }

  @Test
  public void sellableByOperatorReturnsFalseIfProductHasNoStock() {
    when(commerceStockService.getStockLevelStatusForProductAndBaseStore(product, store)).thenReturn(OUTOFSTOCK);

    boolean result = miraklProductService.isSellableByOperator(product);

    assertThat(result).isFalse();
  }

  @Test
  public void sellableByOperatorReturnsFalseIfProductHasNoPrice() {
    when(commercePriceService.getWebPriceForProduct(product)).thenReturn(null);

    boolean result = miraklProductService.isSellableByOperator(product);

    assertThat(result).isFalse();
  }

  @Test
  public void sellableByOperatorReturnsFalseIfNoCurrentBaseStoreFoundForProduct() {
    when(baseStoreService.getCurrentBaseStore()).thenReturn(null);

    boolean result = miraklProductService.isSellableByOperator(product);

    assertThat(result).isFalse();
  }

}
