package com.mirakl.hybris.addon.cms.evaluators.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.addon.model.restrictions.CMSPurchasableProductRestrictionModel;
import com.mirakl.hybris.core.product.services.MiraklProductService;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSPurchasableProductRestrictionEvaluatorTest {

  @InjectMocks
  private CMSPurchasableProductRestrictionEvaluator productRestrictionEvaluator;

  @Mock
  private OfferService offerService;
  @Mock
  private MiraklProductService miraklProductService;
  @Mock
  private CommonI18NService commonI18NService;
  @Mock
  private ProductModel product;
  @Mock
  private CurrencyModel currency;
  @Mock
  private RestrictionData restrictionData;
  @Mock
  private CMSPurchasableProductRestrictionModel restriction;

  @Before
  public void setUp() {
    when(restrictionData.getProduct()).thenReturn(product);
    when(commonI18NService.getCurrentCurrency()).thenReturn(currency);
  }

  @Test
  public void evaluateReturnsFalseIfProductWithOffersIsNotSellableByOperator() {
    when(miraklProductService.isSellableByOperator(product)).thenReturn(false);

    boolean result = productRestrictionEvaluator.evaluate(restriction, restrictionData);

    assertThat(result).isTrue();
  }

  @Test
  public void evaluateReturnsTrueIfProductIsSellableByOperator() {
    when(miraklProductService.isSellableByOperator(product)).thenReturn(true);

    boolean result = productRestrictionEvaluator.evaluate(restriction, restrictionData);

    assertThat(result).isTrue();
  }

  @Test
  public void evaluateReturnsTrueIfProductHasNoOffer() {
    boolean result = productRestrictionEvaluator.evaluate(restriction, restrictionData);

    assertThat(result).isTrue();
  }


}
