package com.mirakl.hybris.advancedpricing.order.populators;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.request.shipping.MiraklGetShippingRatesRequest;
import com.mirakl.hybris.core.user.strategies.MiraklCustomerOrganizationResolvingStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklAdvancedPricingGetShippingRatesRequestPopulatorTest {

  private static final String CUSTOMER_ORG_ID = "customer-org-id";

  @InjectMocks
  private DefaultMiraklAdvancedPricingGetShippingRatesRequestPopulator populator;

  @Mock
  private MiraklCustomerOrganizationResolvingStrategy customerOrganizationResolvingStrategy;
  @Mock
  private AbstractOrderModel cart;
  @Mock
  private MiraklGetShippingRatesRequest request;

  @Test
  public void shouldPopulateCustomerOrganizationWhenResolvedCustomerOrg() {
    when(customerOrganizationResolvingStrategy.resolveCurrentUserOrganizationId()).thenReturn(CUSTOMER_ORG_ID);

    populator.populate(cart, request);

    verify(request).setPricingCustomerOrganizationId(CUSTOMER_ORG_ID);
  }

  @Test
  public void shouldNotPopulateCustomerOrganizationWhenEmptyCustomerOrg() {
    when(customerOrganizationResolvingStrategy.resolveCurrentUserOrganizationId()).thenReturn(null);

    populator.populate(cart, request);

    verifyNoMoreInteractions(request);
  }

}
