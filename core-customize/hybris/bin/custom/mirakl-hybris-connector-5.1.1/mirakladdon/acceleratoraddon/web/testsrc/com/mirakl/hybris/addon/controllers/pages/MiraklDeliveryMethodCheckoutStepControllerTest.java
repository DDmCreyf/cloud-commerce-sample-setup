package com.mirakl.hybris.addon.controllers.pages;

import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.hybris.addon.controllers.MirakladdonControllerConstants;
import com.mirakl.hybris.addon.forms.UpdateShippingOptionForm;
import com.mirakl.hybris.facades.order.ShippingFacade;
import com.mirakl.hybris.facades.shipping.data.ShippingOfferDiscrepancyData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CartFacade;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklDeliveryMethodCheckoutStepControllerTest {

  private static final String SHIPPING_OPTION_CODE = "shippingOptionCode";
  private static final String SHOP_ID = "shopId";
  private static final String NEXT_CHECKOUT_STEP = "nextCheckoutStep";
  private static final String REDIRECT_TO_DELIVERY_ADDRESS = "redirectToDeliveryAddress";
  private static final String REDIRECT_TO_DELIVERY_METHOD = "redirectToDeliveryMethod";
  private static final int LEAD_TIME_TO_SHIP = 1;

  @Spy
  @InjectMocks
  private MiraklDeliveryMethodCheckoutStepController testObj;
  @Mock
  private ShippingFacade shippingFacade;
  @Mock
  private CartFacade cartFacade;
  @Mock
  private AcceleratorCheckoutFacade checkoutFacade;
  @Mock
  private CheckoutStep checkoutStep;
  @Mock
  private Model model;
  @Mock
  private RedirectAttributes redirectAttributes;
  @Mock
  private ShippingOfferDiscrepancyData shippingOfferDiscrepancyData;
  @Mock
  private UpdateShippingOptionForm updateShippingOptionForm;
  @Mock
  private BindingResult bindingResult;

  @Before
  public void setUp() throws CMSItemNotFoundException {
    testObj.setRedirectToDeliveryAddress(REDIRECT_TO_DELIVERY_ADDRESS);
    testObj.setRedirectToDeliveryMethod(REDIRECT_TO_DELIVERY_METHOD);
    doNothing().when(testObj).enterStepInternal(model, redirectAttributes);
    doNothing().when(testObj).preparePage(model);
    doReturn(checkoutStep).when(testObj).getCheckoutStep();

    when(shippingFacade.getOfferDiscrepancies()).thenReturn(singletonList(shippingOfferDiscrepancyData));
    when(updateShippingOptionForm.getShopId()).thenReturn(SHOP_ID);
    when(updateShippingOptionForm.getLeadTimeToShip()).thenReturn(LEAD_TIME_TO_SHIP);
    when(checkoutStep.nextStep()).thenReturn(NEXT_CHECKOUT_STEP);
  }

  @Test
  public void entersStep() throws CMSItemNotFoundException {
    String result = testObj.enterStep(model, redirectAttributes);

    assertThat(result).isSameAs(MirakladdonControllerConstants.Views.Pages.MultiStepCheckout.ChooseDeliveryMethodPage);

    verify(shippingFacade).updateAvailableShippingOptions();
    verify(model).addAttribute("offerErrors", singletonList(shippingOfferDiscrepancyData));
    verify(testObj).enterStepInternal(model, redirectAttributes);
    verify(shippingFacade).updateOffersPrice();
  }

  @Test
  public void redirectsToDeliveryAddressPageIfMiraklApiExceptionIsThrown() throws CMSItemNotFoundException {
    doThrow(MiraklApiException.class).when(shippingFacade).updateAvailableShippingOptions();

    String result = testObj.enterStep(model, redirectAttributes);

    assertThat(result).isSameAs(REDIRECT_TO_DELIVERY_ADDRESS);

    verify(shippingFacade).updateAvailableShippingOptions();
    verify(model, never()).addAttribute(anyString(), anyListOf(ShippingOfferDiscrepancyData.class));
    verify(redirectAttributes).addFlashAttribute(eq(GlobalMessages.ERROR_MESSAGES_HOLDER), anyListOf(String.class));
    verify(testObj, never()).enterStepInternal(any(Model.class), any(RedirectAttributes.class));
  }

  @Test
  public void updatesCartShopShippingOptions() throws CMSItemNotFoundException {
    String result =
        testObj.updateCartShippingOptions(SHIPPING_OPTION_CODE, model, updateShippingOptionForm, bindingResult);

    assertThat(result).isSameAs(REDIRECT_TO_DELIVERY_METHOD);

    verify(shippingFacade).updateShippingOptions(SHIPPING_OPTION_CODE, LEAD_TIME_TO_SHIP, SHOP_ID);
    verify(checkoutFacade, never()).setDeliveryMode(anyString());
    verify(redirectAttributes, never()).addFlashAttribute(anyString(), anyListOf(String.class));
  }

  @Test
  public void updatesCartOperatorShippingOptions() throws CMSItemNotFoundException {
    when(updateShippingOptionForm.getShopId()).thenReturn(null);

    String result =
        testObj.updateCartShippingOptions(SHIPPING_OPTION_CODE, model, updateShippingOptionForm, bindingResult);

    assertThat(result).isSameAs(REDIRECT_TO_DELIVERY_METHOD);

    verify(shippingFacade, never()).updateShippingOptions(anyString(), anyInt(), anyString());
    verify(checkoutFacade).setDeliveryMode(SHIPPING_OPTION_CODE);
    verify(redirectAttributes, never()).addFlashAttribute(anyString(), anyListOf(String.class));
  }

  @Test
  public void updateCartShippingOptionsReturnsToPageWithoutSettingAnyShippingOptionsIfFormContainsErrors()
      throws CMSItemNotFoundException {
    when(bindingResult.hasErrors()).thenReturn(true);

    String result =
        testObj.updateCartShippingOptions(SHIPPING_OPTION_CODE, model, updateShippingOptionForm, bindingResult);

    assertThat(result).isSameAs(MirakladdonControllerConstants.Views.Pages.MultiStepCheckout.ChooseDeliveryMethodPage);

    verify(shippingFacade, never()).updateShippingOptions(anyString(), anyInt(), anyString());
    verify(checkoutFacade, never()).setDeliveryMode(anyString());
  }

  @Test
  public void entersNextStep() {
    String result = testObj.next(redirectAttributes);

    assertThat(result).isSameAs(NEXT_CHECKOUT_STEP);

    verify(shippingFacade).removeInvalidOffers();
    verify(shippingFacade).updateAvailableShippingOptions();
    verify(shippingFacade).updateOffersPrice();
  }

  @Test
  public void nextRedirectsToSelectDeliveryMethodPageIfMiraklApiExceptionIsThrown() {
    doThrow(MiraklApiException.class).when(shippingFacade).updateAvailableShippingOptions();

    String result = testObj.next(redirectAttributes);

    assertThat(result).isSameAs(REDIRECT_TO_DELIVERY_METHOD);

    verify(shippingFacade).removeInvalidOffers();
    verify(redirectAttributes).addFlashAttribute(eq(GlobalMessages.ERROR_MESSAGES_HOLDER), anyListOf(String.class));
  }
}
