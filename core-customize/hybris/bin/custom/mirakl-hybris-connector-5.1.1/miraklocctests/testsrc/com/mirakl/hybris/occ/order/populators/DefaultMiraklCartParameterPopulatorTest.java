package com.mirakl.hybris.occ.order.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import com.mirakl.hybris.facades.product.OfferFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.storelocator.pos.PointOfServiceService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklCartParameterPopulatorTest {

  private static final String OFFER_CODE = "testOffer";
  private static final String PRODUCT_CODE = "testProduct";

  @Mock
  private ProductService productService;
  @Mock
  private OfferCodeGenerationStrategy offerCodeGenerationStrategy;
  @Mock
  private OfferFacade offerFacade;
  @Mock
  private AddToCartParams addToCartParams;
  @Mock
  private OfferModel offerModel;
  @Mock
  private CartService cartService;
  @Mock
  private PointOfServiceService pointOfServiceService;
  @Mock
  private CartModel cartModel;
  @Mock
  private ProductModel product;

  @InjectMocks
  private DefaultMiraklCartParameterPopulator populator;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(offerCodeGenerationStrategy.isOfferCode(OFFER_CODE)).thenReturn(true);
    when(offerFacade.getOfferForCode(OFFER_CODE)).thenReturn(offerModel);
    when(offerModel.getProductCode()).thenReturn(PRODUCT_CODE);
    when(cartService.getSessionCart()).thenReturn(cartModel);
  }

  @Test
  public void shouldPopulateProductAndOffer() {
    AddToCartParams source = new AddToCartParams();
    CommerceCartParameter target = new CommerceCartParameter();
    source.setProductCode(OFFER_CODE);
    ProductModel product = Mockito.mock(ProductModel.class);
    when(productService.getProductForCode(PRODUCT_CODE)).thenReturn(product);
    when(product.getUnit()).thenReturn(Mockito.mock(UnitModel.class));
    populator.populate(source, target);
    assertSame(product, target.getProduct());
    assertSame(product.getUnit(), target.getUnit());
    assertEquals(PRODUCT_CODE, source.getProductCode());
  }
}
