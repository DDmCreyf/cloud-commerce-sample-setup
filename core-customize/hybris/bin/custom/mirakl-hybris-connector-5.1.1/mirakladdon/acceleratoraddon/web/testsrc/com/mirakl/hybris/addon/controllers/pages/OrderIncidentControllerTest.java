package com.mirakl.hybris.addon.controllers.pages;

import static com.mirakl.client.mmp.domain.reason.MiraklReasonType.INCIDENT_CLOSE;
import static com.mirakl.client.mmp.domain.reason.MiraklReasonType.INCIDENT_OPEN;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.hybris.addon.controllers.MirakladdonControllerConstants;
import com.mirakl.hybris.addon.forms.IncidentForm;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.ReasonData;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.facades.order.IncidentFacade;
import com.mirakl.hybris.facades.order.MarketplaceConsignmentFacade;
import com.mirakl.hybris.facades.setting.ReasonFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderIncidentControllerTest {

  private static final String ORDER_CODE = "1234a7984-45687e6541-12354987";
  private static final String CONSIGNMENT_CODE = "1234a7984-45687e6541-12354987-A";
  private static final String CONSIGNMENT_ENTRY_CODE = "1234a7984-45687e6541-12354987-A-1";
  private static final String OPEN_REASON_CODE = "5";
  private static final String CLOSE_REASON_CODE = "12";
  private static final String OPEN_REASON_DISPLAY_VALUE = "Counterfeit item";
  private static final String CLOSE_REASON_DISPLAY_VALUE = "Solution found by the operator";
  private static final String MESSAGE_OPEN = "I didn't receive my order.";
  private static final String MESSAGE_CLOSE = "I found my order next door.";

  @Mock
  private UserService userService;
  @Mock
  private IncidentFacade incidentFacade;
  @Mock
  private ReasonFacade reasonFacade;
  @Mock
  private MarketplaceConsignmentService marketplaceConsignmentService;
  @Mock
  private ReasonData incidentCloseReason1, incidentCloseReason2, incidentOpenReason1, incidentOpenReason2;
  @Mock
  private ProductData product;
  @Mock
  private Model model;
  @Mock
  private RedirectAttributes redirect;
  @Mock
  private IncidentForm incidentForm;
  @Mock
  private UserModel user;
  @Mock
  private MarketplaceConsignmentModel marketplaceConsignment;
  @Mock
  private OrderModel order;
  @Mock
  private CreateThreadMessageData messageDataOpen;
  @Mock
  private CreateThreadMessageData messageDataClose;
  @Mock
  private MarketplaceConsignmentFacade marketplaceConsignmentFacade;

  @InjectMocks
  private OrderIncidentController testObj;

  @Before
  public void setUp() {
    when(reasonFacade.getReasons(MiraklReasonType.INCIDENT_CLOSE))
        .thenReturn(asList(incidentCloseReason1, incidentCloseReason2));
    when(reasonFacade.getReasons(MiraklReasonType.INCIDENT_OPEN))
        .thenReturn(asList(incidentOpenReason1, incidentOpenReason2));
    when( reasonFacade.getReasonsAsMap(INCIDENT_OPEN)).thenReturn(singletonMap(OPEN_REASON_CODE, OPEN_REASON_DISPLAY_VALUE));
    when(reasonFacade.getReasonsAsMap(INCIDENT_CLOSE)).thenReturn(singletonMap(CLOSE_REASON_CODE, CLOSE_REASON_DISPLAY_VALUE));
    when(marketplaceConsignmentFacade.getProductForConsignmentEntry(CONSIGNMENT_ENTRY_CODE)).thenReturn(product);
    when(marketplaceConsignmentService.getMarketplaceConsignmentForCode(CONSIGNMENT_CODE)).thenReturn(marketplaceConsignment);
    when(marketplaceConsignment.getOrder()).thenReturn(order);
    when(order.getCode()).thenReturn(ORDER_CODE);
    when(messageDataOpen.getBody()).thenReturn(MESSAGE_OPEN);
    when(messageDataClose.getBody()).thenReturn(MESSAGE_CLOSE);
  }

  @Test
  public void openIncidentPage() throws UnsupportedEncodingException, CMSItemNotFoundException {
    when(incidentForm.getMessage()).thenReturn(MESSAGE_OPEN);

    String output = testObj.openIncidentPage(CONSIGNMENT_ENTRY_CODE, model, null, null);

    verify(model).addAttribute(eq("reasons"), eq(asList(incidentOpenReason1, incidentOpenReason2)));
    verify(model).addAttribute(eq("product"), eq(product));

    assertThat(output).isEqualTo(MirakladdonControllerConstants.Fragments.Order.orderIncidentPopup);
  }


  @Test
  public void closeIncidentPage() throws UnsupportedEncodingException, CMSItemNotFoundException {
    when(incidentForm.getMessage()).thenReturn(MESSAGE_CLOSE);

    String output = testObj.closeIncidentPage(CONSIGNMENT_ENTRY_CODE, model, null, null);

    verify(model).addAttribute(eq("reasons"), eq(asList(incidentCloseReason1, incidentCloseReason2)));
    verify(model).addAttribute(eq("product"), eq(product));

    assertThat(output).isEqualTo(MirakladdonControllerConstants.Fragments.Order.orderIncidentPopup);
  }

  @Test
  public void postOpenIncident() {
    when(incidentForm.getMessage()).thenReturn(MESSAGE_OPEN);
    when(incidentForm.getReasonCode()).thenReturn(OPEN_REASON_CODE);

    String output = testObj.postOpenIncident(CONSIGNMENT_CODE, CONSIGNMENT_ENTRY_CODE, model, null, incidentForm, null, redirect);

    verify(marketplaceConsignment).getOrder();
    verify(incidentFacade).openIncident(eq(CONSIGNMENT_ENTRY_CODE), eq(OPEN_REASON_CODE), any(CreateThreadMessageData.class));
    assertThat(output).isEqualTo(OrderIncidentController.REDIRECT_TO_ORDER_DETAIL_PAGE + ORDER_CODE);
  }

  @Test
  public void postOpenIncidentWhenException() {
    when(incidentForm.getMessage()).thenReturn(MESSAGE_OPEN);
    when(incidentForm.getReasonCode()).thenReturn(OPEN_REASON_CODE);

    doThrow(new IllegalStateException()).when(incidentFacade).openIncident(CONSIGNMENT_ENTRY_CODE, OPEN_REASON_CODE,
        messageDataOpen);

    String output = testObj.postOpenIncident(CONSIGNMENT_CODE, CONSIGNMENT_ENTRY_CODE, model, null, incidentForm, null, redirect);
    verify(marketplaceConsignment).getOrder();
    assertThat(output).isEqualTo(OrderIncidentController.REDIRECT_TO_ORDER_DETAIL_PAGE + ORDER_CODE);
  }

  @Test
  public void postCloseIncident() {
    when(incidentForm.getMessage()).thenReturn(MESSAGE_CLOSE);
    when(incidentForm.getReasonCode()).thenReturn(CLOSE_REASON_CODE);

    String output =
        testObj.postCloseIncident(CONSIGNMENT_CODE, CONSIGNMENT_ENTRY_CODE, model, null, incidentForm, null, redirect);

    verify(marketplaceConsignment).getOrder();
    verify(incidentFacade).closeIncident(eq(CONSIGNMENT_ENTRY_CODE), eq(CLOSE_REASON_CODE), any(CreateThreadMessageData.class));
    assertThat(output).isEqualTo(OrderIncidentController.REDIRECT_TO_ORDER_DETAIL_PAGE + ORDER_CODE);
  }

  @Test
  public void postCloseIncidentWhenException() {
    when(incidentForm.getMessage()).thenReturn(MESSAGE_CLOSE);
    when(incidentForm.getReasonCode()).thenReturn(CLOSE_REASON_CODE);

    doThrow(new IllegalStateException()).when(incidentFacade).closeIncident(CONSIGNMENT_ENTRY_CODE, CLOSE_REASON_CODE,
        messageDataClose);

    String output =
        testObj.postCloseIncident(CONSIGNMENT_CODE, CONSIGNMENT_ENTRY_CODE, model, null, incidentForm, null, redirect);

    verify(marketplaceConsignment).getOrder();
    assertThat(output).isEqualTo(OrderIncidentController.REDIRECT_TO_ORDER_DETAIL_PAGE + ORDER_CODE);
  }
}
