package com.mirakl.hybris.core.order.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.request.order.message.MiraklCreateOrderMessageRequest;
import com.mirakl.client.mmp.front.request.order.message.MiraklGetOrderMessagesRequest;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderMessage;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.impl.DefaultMarketplaceConsignmentService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMarketplaceConsignmentMessagesServiceTest {

  private static final String CONSIGNMENT_CODE = "b3bcad74-1942-43d1-b428-8f7a0351c5cf-A";

  @Mock
  private MiraklMarketplacePlatformFrontApi miraklApi;

  @Mock
  private DefaultMarketplaceConsignmentService consignmentService;

  @Mock
  private UserService userService;

  @Mock
  private MarketplaceConsignmentModel marketplaceConsignment;

  @Mock
  private UserModel user, wrongUser;

  @Mock
  private OrderModel order;

  @Mock
  private MiraklCreateOrderMessage message;

  @InjectMocks
  private DefaultMarketplaceConsignmentMessagesService testObj;

  @Test
  public void getMessagesForConsignment() throws Exception {
    ArgumentCaptor<MiraklGetOrderMessagesRequest> request = ArgumentCaptor.forClass(MiraklGetOrderMessagesRequest.class);

    testObj.getMessagesForConsignment(CONSIGNMENT_CODE);

    verify(consignmentService).checkUserAccessRightsForConsignment(CONSIGNMENT_CODE);
    verify(miraklApi).getOrderMessages(request.capture());
    assertThat(request.getValue().getOrderId()).isEqualTo(CONSIGNMENT_CODE);
  }

  @Test
  public void postMessageForConsignment() throws Exception {
    ArgumentCaptor<MiraklCreateOrderMessageRequest> request = ArgumentCaptor.forClass(MiraklCreateOrderMessageRequest.class);

    testObj.postMessageForConsignment(CONSIGNMENT_CODE, message);

    verify(consignmentService).checkUserAccessRightsForConsignment(CONSIGNMENT_CODE);
    verify(miraklApi).createOrderMessage(request.capture());
    assertThat(request.getValue().getOrderId()).isEqualTo(CONSIGNMENT_CODE);
    assertThat(request.getValue().getMessage()).isEqualTo(message);
  }

}
