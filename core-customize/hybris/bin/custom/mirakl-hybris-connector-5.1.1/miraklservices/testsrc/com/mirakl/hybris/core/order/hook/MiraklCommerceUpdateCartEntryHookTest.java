package com.mirakl.hybris.core.order.hook;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.services.ShippingOptionsService;
import com.mirakl.hybris.core.order.strategies.SynchronousCartUpdateActivationStrategy;
import com.mirakl.hybris.core.product.services.OfferService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCommerceUpdateCartEntryHookTest {
  private static final String OFFER_ID = "12000";

  @InjectMocks
  private MiraklCommerceUpdateCartEntryHook hook;

  @Mock
  private OfferService offerService;
  @Mock
  private ShippingOptionsService shippingOptionsService;
  @Mock
  private SynchronousCartUpdateActivationStrategy synchronousCartUpdateActivationStrategy;
  @Mock
  private CommerceCartParameter cartParameter;
  @Mock
  private OfferModel offer;
  @Mock
  private AbstractOrderEntryModel abstractOrderEntry;
  @Mock
  private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
  @Mock
  private CartModel cart;

  @Before
  public void setUp() throws Exception {
    when(cartParameter.getOffer()).thenReturn(offer);
    when(cartParameter.getCart()).thenReturn(cart);
    when(offer.getId()).thenReturn(OFFER_ID);
  }

  @Test
  public void shouldCallServiceUpdateWhenSynchronousCartUpdateIsEnabledAndInvalidateShippingFees() {
    when(synchronousCartUpdateActivationStrategy.isSynchronousCartUpdateEnabled()).thenReturn(true);

    hook.beforeUpdateCartEntry(cartParameter);

    verify(offerService).updateExistingOfferForId(OFFER_ID);
    verify(shippingOptionsService).resetSavedShippingOptions(cart);
  }

  @Test
  public void shouldNotCallServiceUpdateWhenSynchronousCartUpdateIsEnabled() {
    when(synchronousCartUpdateActivationStrategy.isSynchronousCartUpdateEnabled()).thenReturn(false);

    hook.beforeUpdateCartEntry(cartParameter);

    verifyZeroInteractions(offerService);
    verify(shippingOptionsService).resetSavedShippingOptions(cart);
  }

}
