package com.mirakl.hybris.facades.product.helpers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PriceDataFactoryHelperTest {

  private static final BigDecimal PRICE_VALUE = BigDecimal.valueOf(150);

  @InjectMocks
  private PriceDataFactoryHelper priceDataFactoryHelper;

  @Mock
  private PriceDataFactory priceDataFactory;
  @Mock
  private CommonI18NService commonI18NService;
  @Mock
  private CurrencyModel currentCurrency;

  @Before
  public void setUp() throws Exception {
    when(commonI18NService.getCurrentCurrency()).thenReturn(currentCurrency);
  }

  @Test
  public void shouldCreatePriceWithSessionCurrency() {
    priceDataFactoryHelper.createPrice(PRICE_VALUE);

    verify(priceDataFactory).create(PriceDataType.BUY, PRICE_VALUE, currentCurrency);
  }

}
