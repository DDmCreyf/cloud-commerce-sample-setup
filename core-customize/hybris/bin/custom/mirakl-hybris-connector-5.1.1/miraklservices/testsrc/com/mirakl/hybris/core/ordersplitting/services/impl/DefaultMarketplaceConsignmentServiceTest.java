package com.mirakl.hybris.core.ordersplitting.services.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.UPDATE_RECEIVED_EVENT_NAME;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue.MiraklRegexAdditionalFieldValue;
import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.domain.order.MiraklOrderLine;
import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreatedOrders;
import com.mirakl.client.mmp.front.request.order.evaluation.MiraklCreateOrderEvaluation;
import com.mirakl.client.mmp.front.request.order.evaluation.MiraklCreateOrderEvaluationRequest;
import com.mirakl.client.mmp.request.order.worflow.MiraklCancelOrderRequest;
import com.mirakl.client.mmp.request.order.worflow.MiraklReceiveOrderRequest;
import com.mirakl.hybris.core.enums.MiraklOrderStatus;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.order.services.MiraklOrderService;
import com.mirakl.hybris.core.ordersplitting.daos.ConsignmentEntryDao;
import com.mirakl.hybris.core.ordersplitting.daos.MarketplaceConsignmentDao;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import shaded.com.fasterxml.jackson.core.type.TypeReference;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultMarketplaceConsignmentServiceTest {

  private static final String CONSIGNMENT_JSON = "json-consignment";
  private static final String DEBIT_REQUEST_JSON = "json-debit-request";
  private static final String LOGISTIC_ORDER_CODE = "logistic-order-code";
  private static final String CONSIGNMENT_PROCESS_CODE = "consignment-process-code";
  private static final String CONSIGNMENT_ENTRY_CODE = "1234567-976543-79840496-A-1";
  private static final String ORDER_CUSTOM_FIELDS_JSON = "order-custom-fields-json";
  private static final String ORDER_LINE_CUSTOM_FIELDS_JSON = "order-line-custom-fields-json";

  @InjectMocks
  @Spy
  private DefaultMarketplaceConsignmentService testObj;

  @Mock
  private ModelService modelService;
  @Mock
  private JsonMarshallingService jsonMarshallingService;
  @Mock
  private BusinessProcessService businessProcessService;
  @Mock
  private MarketplaceConsignmentDao marketplaceConsignmentDao;
  @Mock
  private Converter<Pair<OrderModel, MiraklOrder>, MarketplaceConsignmentModel> miraklCreateConsignmentConverter;
  @Mock
  private OrderModel order;
  @Mock
  private MiraklCreatedOrders miraklCreatedOrders;
  @Mock
  private MiraklOrder miraklOrder;
  @Mock
  private MiraklOrderLine miraklOrderLine;
  @Mock
  private MiraklOrderPayment miraklOrderPayment;
  @Mock
  private MarketplaceConsignmentModel consignment;
  @Mock
  private ConsignmentProcessModel consignmentProcess;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklApi;
  @Mock
  private UserModel user, wrongUser;
  @Mock
  private MiraklCreateOrderEvaluation evaluation;
  @Mock
  private ConsignmentEntryModel consignmentEntry;
  @Mock
  private ConsignmentEntryDao consignmentEntryDao;
  @Mock
  private ProductModel product;
  @Mock
  private OrderEntryModel orderEntry;
  @Mock
  private Date customerDebitDate;
  @Mock
  private MiraklOrderService miraklOrderService;

  @Captor
  private ArgumentCaptor<MiraklCancelOrderRequest> cancelRequestArgumentCaptor;

  private List<MiraklOrder> miraklOrders = asList(mock(MiraklOrder.class), mock(MiraklOrder.class));
  private Set<ConsignmentModel> consignmentModels =
      new HashSet<>(asList(mock(ConsignmentModel.class), mock(ConsignmentModel.class)));
  private List<MiraklAdditionalFieldValue> orderCustomFields = new ArrayList<>(asList(new MiraklBooleanAdditionalFieldValue())),
      orderLineCustomFields = new ArrayList<>(asList(new MiraklRegexAdditionalFieldValue()));

  @Before
  public void setUp() throws Exception {
    when(miraklCreatedOrders.getOrders()).thenReturn(miraklOrders);
    when(miraklOrder.getId()).thenReturn(LOGISTIC_ORDER_CODE);
    when(miraklOrder.getOrderAdditionalFields()).thenReturn(orderCustomFields);
    when(miraklOrder.getOrderLines()).thenReturn(singletonList(miraklOrderLine));
    when(miraklOrderLine.getId()).thenReturn(CONSIGNMENT_ENTRY_CODE);
    when(miraklOrderLine.getAdditionalFields()).thenReturn(orderLineCustomFields);
    when(miraklOrderPayment.getOrderId()).thenReturn(LOGISTIC_ORDER_CODE);
    when(marketplaceConsignmentDao.findMarketplaceConsignmentByCode(miraklOrder.getId())).thenReturn(consignment);
    when(consignment.getMiraklOrderStatus()).thenReturn(MiraklOrderStatus.SHIPPING);
    when(consignment.getOrder()).thenReturn(order);
    when(consignment.getCanEvaluate()).thenReturn(true);
    when(consignment.getCode()).thenReturn(LOGISTIC_ORDER_CODE);
    when(consignment.getCustomerDebitDate()).thenReturn(customerDebitDate);
    when(consignment.getConsignmentEntries()).thenReturn(singleton(consignmentEntry));
    when(consignment.getCustomFieldsJSON()).thenReturn(ORDER_CUSTOM_FIELDS_JSON);
    when(order.getUser()).thenReturn(user);
    when(consignment.getConsignmentProcesses()).thenReturn(singleton(consignmentProcess));
    when(consignmentProcess.getCode()).thenReturn(CONSIGNMENT_PROCESS_CODE);
    when(jsonMarshallingService.toJson(miraklOrder)).thenReturn(CONSIGNMENT_JSON);
    when(jsonMarshallingService.fromJson(CONSIGNMENT_JSON, MiraklOrder.class)).thenReturn(miraklOrder);
    when(jsonMarshallingService.toJson(miraklOrderPayment)).thenReturn(DEBIT_REQUEST_JSON);
    when(jsonMarshallingService.fromJson(DEBIT_REQUEST_JSON, MiraklOrderPayment.class)).thenReturn(miraklOrderPayment);
    when(jsonMarshallingService.fromJson(eq(ORDER_CUSTOM_FIELDS_JSON), any(TypeReference.class))).thenReturn(orderCustomFields);
    when(jsonMarshallingService.fromJson(eq(ORDER_LINE_CUSTOM_FIELDS_JSON), any(TypeReference.class)))
        .thenReturn(orderLineCustomFields);
    when(consignmentEntryDao.findConsignmentEntryByMiraklLineId(CONSIGNMENT_ENTRY_CODE)).thenReturn(consignmentEntry);
    when(consignmentEntry.getOrderEntry()).thenReturn(orderEntry);
    when(consignmentEntry.getMiraklOrderLineId()).thenReturn(CONSIGNMENT_ENTRY_CODE);
    when(consignmentEntry.getCustomFieldsJSON()).thenReturn(ORDER_LINE_CUSTOM_FIELDS_JSON);
    when(orderEntry.getProduct()).thenReturn(product);
    when(order.getUser()).thenReturn(user);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void shouldCreateConsignments() {
    testObj.createMarketplaceConsignments(order, miraklCreatedOrders);

    verify(order).setConsignments(anySetOf(ConsignmentModel.class));
    verify(miraklCreateConsignmentConverter, times(miraklOrders.size())).convert(any(Pair.class));
  }

  @Test
  public void shouldLoadConsignmentUpdate() {
    when(consignment.getConsignmentUpdatePayload()).thenReturn(CONSIGNMENT_JSON);

    MiraklOrder update = testObj.loadConsignmentUpdate(consignment);

    assertThat(update).isNotNull();
  }

  @Test
  public void shouldLoadConsignmentUpdateReturnNullIfNotPresent() {
    when(consignment.getConsignmentUpdatePayload()).thenReturn(null);

    MiraklOrder update = testObj.loadConsignmentUpdate(consignment);

    assertThat(update).isNull();
  }

  @Test
  public void shouldReceiveConsignmentUpdate() {
    MarketplaceConsignmentModel result = testObj.receiveConsignmentUpdate(miraklOrder);

    verify(marketplaceConsignmentDao).findMarketplaceConsignmentByCode(LOGISTIC_ORDER_CODE);
    verify(consignment).setConsignmentUpdatePayload(CONSIGNMENT_JSON);
    verify(consignment).setLastUpdateProcessed(false);
    verify(modelService, times(2)).save(consignment);
    verify(modelService).save(consignmentEntry);
    verify(businessProcessService).triggerEvent(CONSIGNMENT_PROCESS_CODE + UPDATE_RECEIVED_EVENT_NAME);
    assertThat(result).isEqualTo(consignment);
  }

  @Test
  public void shouldStoreDebitRequest() {
    MarketplaceConsignmentModel result = testObj.storeDebitRequest(miraklOrderPayment);

    verify(marketplaceConsignmentDao).findMarketplaceConsignmentByCode(LOGISTIC_ORDER_CODE);
    verify(consignment).setDebitRequestPayload(DEBIT_REQUEST_JSON);
    verify(modelService).save(consignment);
    assertThat(result).isEqualTo(consignment);
  }

  @Test
  public void shouldLoadDebitRequest() {
    when(consignment.getDebitRequestPayload()).thenReturn(DEBIT_REQUEST_JSON);

    MiraklOrderPayment debitRequest = testObj.loadDebitRequest(consignment);

    assertThat(debitRequest).isNotNull();
  }

  @Test
  public void shouldLoadDebitRequestReturnNullIfNotPresent() {
    when(consignment.getDebitRequestPayload()).thenReturn(null);

    MiraklOrderPayment debitRequest = testObj.loadDebitRequest(consignment);

    assertThat(debitRequest).isNull();
  }

  @Test
  public void shouldConfirmConsignmentReceptionForCode() {
    testObj.confirmConsignmentReceptionForCode(LOGISTIC_ORDER_CODE, user);

    verify(miraklApi).receiveOrder(any(MiraklReceiveOrderRequest.class));
    verify(consignment).setMiraklOrderStatus(MiraklOrderStatus.RECEIVED);
    verify(consignment).setCanEvaluate(true);
    verify(modelService).save(any(MarketplaceConsignmentModel.class));
  }

  @Test(expected = UnknownIdentifierException.class)
  public void consignmentConfirmationShouldThrowUnknownIdentifierExceptionForWrongUser() {
    testObj.confirmConsignmentReceptionForCode(LOGISTIC_ORDER_CODE, wrongUser);
  }

  @Test(expected = IllegalStateException.class)
  public void consignmentConfirmationShouldThrowIllegalStateExceptionForWrongState() {
    when(consignment.getMiraklOrderStatus()).thenReturn(MiraklOrderStatus.RECEIVED);

    testObj.confirmConsignmentReceptionForCode(LOGISTIC_ORDER_CODE, user);
  }

  @Test(expected = IllegalStateException.class)
  public void consignmentConfirmationShouldThrowIllegalStateExceptionForUndebitedUser() {
    when(consignment.getCustomerDebitDate()).thenReturn(null);

    testObj.confirmConsignmentReceptionForCode(LOGISTIC_ORDER_CODE, user);
  }

  @Test
  public void shouldPostEvaluation() {
    testObj.postEvaluation(LOGISTIC_ORDER_CODE, evaluation, user);

    verify(miraklApi).createOrderEvaluation(any(MiraklCreateOrderEvaluationRequest.class));
    verify(consignment).setCanEvaluate(false);
    verify(modelService).save(consignment);
  }

  @Test(expected = UnknownIdentifierException.class)
  public void evaluationShouldThrowUnknownIdentifierExceptionForWrongUser() {
    testObj.postEvaluation(LOGISTIC_ORDER_CODE, evaluation, wrongUser);
  }

  @Test(expected = IllegalStateException.class)
  public void evaluationShouldThrowIllegalStateExceptionWhenEvaluationImpossible() {
    when(consignment.getCanEvaluate()).thenReturn(false);

    testObj.postEvaluation(LOGISTIC_ORDER_CODE, evaluation, user);
  }

  @Test
  public void shouldCancelMarketplaceConsignment() {
    testObj.cancelMarketplaceConsignment(consignment);

    verify(miraklApi).cancelOrder(cancelRequestArgumentCaptor.capture());
    assertThat(cancelRequestArgumentCaptor.getValue().getOrderId()).isEqualTo(LOGISTIC_ORDER_CODE);
  }

  @Test
  public void shouldCancelMarketplaceConsignmentForCode() {
    testObj.cancelMarketplaceConsignmentForCode(LOGISTIC_ORDER_CODE);

    verify(miraklApi).cancelOrder(cancelRequestArgumentCaptor.capture());
    assertThat(cancelRequestArgumentCaptor.getValue().getOrderId()).isEqualTo(LOGISTIC_ORDER_CODE);
  }

  @Test
  public void getConsignmentEntry() {
    ConsignmentEntryModel output = testObj.getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE);

    assertThat(output).isEqualTo(consignmentEntry);
  }

  @Test(expected = UnknownIdentifierException.class)
  public void getConsignmentEntryWhenConsignmentEntryDoesNotExist() {
    when(consignmentEntryDao.findConsignmentEntryByMiraklLineId(CONSIGNMENT_ENTRY_CODE)).thenReturn(null);

    testObj.getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE);
  }

  @Test
  public void getProductFromConsignmentEntry() {
    ProductModel output = testObj.getProductForConsignmentEntry(CONSIGNMENT_ENTRY_CODE);

    assertThat(output).isEqualTo(product);
  }


  @Test
  public void shouldNotOverrideOperatorConsignments() {
    when(order.getConsignments()).thenReturn(consignmentModels);

    testObj.createMarketplaceConsignments(order, miraklCreatedOrders);

    ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);
    verify(order).setConsignments(captor.capture());
    assertThat(captor.getValue().size()).isEqualTo(3);
  }

  @Test
  public void shouldLoadMarketplaceConsignmentCustomFields() {
    when(consignment.getCustomFieldsJSON()).thenReturn(ORDER_CUSTOM_FIELDS_JSON);

    List<MiraklAdditionalFieldValue> orderCustomField = testObj.loadMarketplaceConsignmentCustomFields(consignment);

    assertThat(orderCustomField).isNotNull();
  }

  @Test
  public void loadMarketplaceConsignmentCustomFieldsReturnNullIfNotPresent() {
    when(consignment.getCustomFieldsJSON()).thenReturn(null);

    List<MiraklAdditionalFieldValue> orderCustomField = testObj.loadMarketplaceConsignmentCustomFields(consignment);

    assertThat(orderCustomField).isNull();
  }

  @Test
  public void storeConsignmentUpdateShouldSaveCustomFields() {

    testObj.storeConsignmentUpdate(miraklOrder);

    verify(testObj).storeMarketplaceConsignmentCustomFields(orderCustomFields, consignment);
    verify(testObj).storeMarketplaceConsignmentEntryCustomFields(orderLineCustomFields, consignmentEntry);
    verify(modelService, times(2)).save(consignment);
  }

  @Test
  public void shouldLoadMarketplaceConsignmentEntryCustomFields() {
    when(consignmentEntry.getCustomFieldsJSON()).thenReturn(ORDER_LINE_CUSTOM_FIELDS_JSON);

    List<MiraklAdditionalFieldValue> orderLineCustomField = testObj.loadMarketplaceConsignmentEntryCustomFields(consignmentEntry);

    assertThat(orderLineCustomField).isNotNull();
  }

  @Test
  public void loadMarketplaceConsignmentEntryCustomFieldsReturnNullIfNotPresent() {
    when(consignmentEntry.getCustomFieldsJSON()).thenReturn(null);

    List<MiraklAdditionalFieldValue> orderLineCustomField = testObj.loadMarketplaceConsignmentEntryCustomFields(consignmentEntry);

    assertThat(orderLineCustomField).isNull();
  }

}
