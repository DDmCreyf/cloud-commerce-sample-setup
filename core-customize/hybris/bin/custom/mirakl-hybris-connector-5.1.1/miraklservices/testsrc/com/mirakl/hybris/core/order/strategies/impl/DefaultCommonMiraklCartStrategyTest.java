package com.mirakl.hybris.core.order.strategies.impl;

import com.mirakl.client.mmp.domain.offer.MiraklOffer;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.hybris.beans.CartAdjustment;
import com.mirakl.hybris.beans.OfferOrderingConditions;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.MiraklCommerceCartModificationStatus;
import com.mirakl.hybris.core.order.daos.MiraklAbstractOrderEntryDao;
import com.mirakl.hybris.core.order.strategies.SynchronousCartUpdateActivationStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultCommonMiraklCartStrategyTest {

  public static final String OFFER_ID = "OFFER_ID";
  @Spy
  @InjectMocks
  private DefaultCommonMiraklCartStrategy commonCartStrategy;

  @Mock
  private CartService cartService;
  @Mock
  private OfferModel offerModel;
  @Mock
  private ProductModel productModel;
  @Mock
  private CartModel cartModel;
  @Mock
  private Converter<OfferModel, OfferOrderingConditions> offerOrderingConditionsConverter;
  @Mock
  private CartEntryModel cartEntryModel;
  @Mock
  private MiraklAbstractOrderEntryDao<CartEntryModel> miraklCartEntryDao;
  @Mock
  private SynchronousCartUpdateActivationStrategy synchronousCartUpdateActivationStrategy;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklApi;
  @Mock
  private MiraklOffer miraklOffer;

  private CommerceCartParameter cartParameter;
  private OfferOrderingConditions offerOrderingConditions;

  @Before
  public void setUp() {
    offerOrderingConditions = new OfferOrderingConditions();
    when(miraklCartEntryDao.findEntryByOffer(cartModel, offerModel)).thenReturn(cartEntryModel);
    when(offerOrderingConditionsConverter.convert(offerModel)).thenReturn(offerOrderingConditions);
    cartParameter = new CommerceCartParameter();
    cartParameter.setCart(cartModel);
    cartParameter.setOffer(offerModel);
    cartParameter.setProduct(productModel);
  }

  @Test
  public void shouldReturnSuccessOnAddToCartWithNoOrderConditions() {
    long cartLevelForOffer = 0;
    long requestedQuantity = 2;
    int stock = 2;
    Integer minOrderQty = null;
    Integer maxOrderQty = null;
    Integer packageQty = null;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.SUCCESS);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(2);
  }

  @Test
  public void shouldReturnSuccessOnAddToCartWithOrderConditions() {
    long cartLevelForOffer = 0;
    long requestedQuantity = 3;
    int stock = 3;
    Integer minOrderQty = 3;
    Integer maxOrderQty = 3;
    Integer packageQty = 3;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.SUCCESS);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(3);
  }

  @Test
  public void shouldReturnSuccessOnUpdateCartWithNoOrderConditions() {
    long cartLevelForOffer = 1;
    long requestedQuantity = 2;
    int stock = 2;
    Integer minOrderQty = null;
    Integer maxOrderQty = null;
    Integer packageQty = null;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.SUCCESS);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(1);
  }

  @Test
  public void shouldReturnSuccessOnUpdateCartWithOrderConditions() {
    long cartLevelForOffer = 2;
    long requestedQuantity = 3;
    int stock = 5;
    Integer minOrderQty = 2;
    Integer maxOrderQty = 5;
    Integer packageQty = null;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.SUCCESS);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(1);
  }

  @Test
  public void shouldReturnMinOrderLimitOnAddToCart() {
    long cartLevelForOffer = 0;
    long requestedQuantity = 2;
    int stock = 3;
    Integer minOrderQty = 3;
    Integer maxOrderQty = 10;
    Integer packageQty = null;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.MIN_ORDER_QUANTITY_UNREACHED);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(0);
  }

  @Test
  public void shouldReturnMinOrderLimitOnUpdateCart() {
    long cartLevelForOffer = 4;
    long requestedQuantity = 3;
    int stock = 4;
    Integer minOrderQty = 4;
    Integer maxOrderQty = null;
    Integer packageQty = null;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.MIN_ORDER_QUANTITY_UNREACHED);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(0);
  }

  @Test
  public void shouldReturnPackageConstraintOnAddToCart() {
    long cartLevelForOffer = 0;
    long requestedQuantity = 2;
    int stock = 2;
    Integer minOrderQty = 1;
    Integer maxOrderQty = 5;
    Integer packageQty = 3;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.PACKAGE_QUANTITY_CONSTRAINT);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(0);
  }

  @Test
  public void shouldReturnPackageConstraintOnUpdateCart() {
    long cartLevelForOffer = 2;
    long requestedQuantity = 3;
    int stock = 3;
    Integer minOrderQty = 2;
    Integer maxOrderQty = null;
    Integer packageQty = 2;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.PACKAGE_QUANTITY_CONSTRAINT);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(0);
  }

  @Test
  public void shouldReturnPackageConstraintAndReduceQtyOnUpdateCart() {
    long cartLevelForOffer = 3;
    long requestedQuantity = 8;
    int stock = 8;
    Integer minOrderQty = 3;
    Integer maxOrderQty = null;
    Integer packageQty = 3;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.PACKAGE_QUANTITY_CONSTRAINT);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(3);
  }

  @Test
  public void shouldReturnLowStockOnAddToCart() {
    long cartLevelForOffer = 0;
    long requestedQuantity = 3;
    int stock = 2;
    Integer minOrderQty = 3;
    Integer maxOrderQty = null;
    Integer packageQty = null;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.LOW_STOCK);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(0);
  }

  @Test
  public void shouldReturnLowStockAndReduceOnAddToCart() {
    long cartLevelForOffer = 0;
    long requestedQuantity = 4;
    int stock = 2;
    Integer minOrderQty = null;
    Integer maxOrderQty = null;
    Integer packageQty = null;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.LOW_STOCK);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(2);
  }

  @Test
  public void shouldReturnLowStockOnUpdateCart() {
    long cartLevelForOffer = 3;
    long requestedQuantity = 6;
    int stock = 5;
    Integer minOrderQty = null;
    Integer maxOrderQty = null;
    Integer packageQty = 3;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.LOW_STOCK);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(0);
  }

  @Test
  public void shouldReturnLowStockAndReduceOnUpdateCart() {
    long cartLevelForOffer = 2;
    long requestedQuantity = 4;
    int stock = 3;
    Integer minOrderQty = null;
    Integer maxOrderQty = null;
    Integer packageQty = null;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.LOW_STOCK);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(1);
  }

  @Test
  public void shouldReturnMaxOrderConstaintOnAddToCart() {
    long cartLevelForOffer = 0;
    long requestedQuantity = 5;
    int stock = 5;
    Integer minOrderQty = null;
    Integer maxOrderQty = 4;
    Integer packageQty = null;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(4);
  }

  @Test
  public void shouldReturnMaxOrderConstaintOnUpdateCart() {
    long cartLevelForOffer = 2;
    long requestedQuantity = 5;
    int stock = 5;
    Integer minOrderQty = null;
    Integer maxOrderQty = 4;
    Integer packageQty = null;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(2);
  }

  @Test
  public void shouldReturnMaxOrderConstaintOnAndReduceUpdateCart() {
    long cartLevelForOffer = 3;
    long requestedQuantity = 12;
    int stock = 12;
    Integer minOrderQty = null;
    Integer maxOrderQty = 10;
    Integer packageQty = 3;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(6);
  }

  @Test
  public void shouldReturnNoStockOnAddToCart() {
    long cartLevelForOffer = 0;
    long requestedQuantity = 1;
    int stock = 0;
    Integer minOrderQty = 1;
    Integer maxOrderQty = 1;
    Integer packageQty = 1;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.NO_STOCK);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(0);
  }

  @Test
  public void shouldReturnNoStockOnUpdateCart() {
    long cartLevelForOffer = 4;
    long requestedQuantity = 6;
    int stock = 4;
    Integer minOrderQty = 4;
    Integer maxOrderQty = null;
    Integer packageQty = null;

    mockQuantities(cartLevelForOffer, requestedQuantity, stock, minOrderQty, maxOrderQty, packageQty);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(cartParameter);

    assertThat(addToCartResult.getStatus()).isEqualTo(MiraklCommerceCartModificationStatus.NO_STOCK);
    assertThat(addToCartResult.getAllowedQuantityChange()).isEqualTo(0);
  }

  @Test
  public void shouldReturnCartLevel() {
    long cartEntryQuantity = 3;
    when(cartEntryModel.getQuantity()).thenReturn(cartEntryQuantity);

    long cartLevel = commonCartStrategy.checkCartLevel(offerModel, cartEntryModel, cartModel);

    assertThat(cartLevel).isEqualTo(cartEntryQuantity);
  }

  protected void mockQuantities(long cartLevelForOffer, long requestedQuantity, int stock, Integer minOrderQty,
      Integer maxOrderQty, Integer packageQty) {
    if (cartLevelForOffer == 0) {
      when(miraklCartEntryDao.findEntryByOffer(cartModel, offerModel)).thenReturn(null);
    } else {
      int entryNumber = 0;
      cartParameter.setEntryNumber(entryNumber);
      when(cartEntryModel.getEntryNumber()).thenReturn(entryNumber);
      when(cartEntryModel.getQuantity()).thenReturn(cartLevelForOffer);
      when(cartModel.getEntries()).thenReturn(Arrays.asList(cartEntryModel));
    }
    cartParameter.setQuantity(requestedQuantity);
    when(offerModel.getQuantity()).thenReturn(stock);
    populateOfferOrderingConditions(maxOrderQty, minOrderQty, packageQty);
  }

  protected void populateOfferOrderingConditions(final Integer maxOrderQuantity, final Integer minOrderQuantity,
      final Integer packageQuantity) {
    offerOrderingConditions.setMaxOrderQuantity(maxOrderQuantity);
    offerOrderingConditions.setMinOrderQuantity(minOrderQuantity);
    offerOrderingConditions.setPackageQuantity(packageQuantity);
  }
}
