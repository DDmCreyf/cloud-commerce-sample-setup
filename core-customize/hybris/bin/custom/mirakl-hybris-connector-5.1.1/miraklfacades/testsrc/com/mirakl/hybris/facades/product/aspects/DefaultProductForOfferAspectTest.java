package com.mirakl.hybris.facades.product.aspects;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import com.mirakl.hybris.facades.product.OfferFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.ProductOption;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultProductForOfferAspectTest {

  private static final String PRODUCT_CODE = "product-code";
  private static final String OFFER_CODE = "offer-code";

  @InjectMocks
  private DefaultProductForOfferAspect aspect;
  @Mock
  private ProceedingJoinPoint pjp;
  @Mock
  private OfferFacade offerFacade;
  @Mock
  private OfferCodeGenerationStrategy offerCodeGenerationStrategy;
  @Mock
  private OfferModel offerModel;
  @Captor
  private ArgumentCaptor<Object[]> argsCaptor;

  List<ProductOption> options = Arrays.asList(ProductOption.BASIC);

  @Before
  public void setUp() throws Exception {
    when(offerCodeGenerationStrategy.isOfferCode(OFFER_CODE)).thenReturn(true);
    when(offerCodeGenerationStrategy.isOfferCode(PRODUCT_CODE)).thenReturn(false);
    when(offerFacade.getOfferForCodeIgnoreSearchRestrictions(OFFER_CODE)).thenReturn(offerModel);
    when(offerModel.getProductCode()).thenReturn(PRODUCT_CODE);
  }


  @Test
  public void shouldNotReplaceWhenNotAnOfferCode() throws Throwable {
    aspect.preGetProductForCodeAndOptions(pjp, PRODUCT_CODE, options);

    verify(offerFacade, never()).getOfferForCodeIgnoreSearchRestrictions(anyString());
    verify(pjp).proceed(argsCaptor.capture());
    assertThat(argsCaptor.getValue()[0]).isEqualTo(PRODUCT_CODE);
    assertThat(argsCaptor.getValue()[1]).isEqualTo(options);
  }

  @Test
  public void shouldReplaceWhenOfferCode() throws Throwable {
    aspect.preGetProductForCodeAndOptions(pjp, OFFER_CODE, options);

    verify(offerFacade).getOfferForCodeIgnoreSearchRestrictions(OFFER_CODE);
    verify(offerModel).getProductCode();
    verify(pjp).proceed(argsCaptor.capture());
    assertThat(argsCaptor.getValue()[0]).isEqualTo(PRODUCT_CODE);
    assertThat(argsCaptor.getValue()[1]).isEqualTo(options);
  }

}
