package com.mirakl.hybris.core.ordersplitting.services.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.UPDATE_RECEIVED_EVENT_NAME;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.domain.order.MiraklOrderLine;
import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreatedOrders;
import com.mirakl.client.mmp.front.request.order.evaluation.MiraklCreateOrderEvaluation;
import com.mirakl.client.mmp.front.request.order.evaluation.MiraklCreateOrderEvaluationRequest;
import com.mirakl.client.mmp.request.order.worflow.MiraklCancelOrderRequest;
import com.mirakl.client.mmp.request.order.worflow.MiraklReceiveOrderRequest;
import com.mirakl.hybris.core.enums.MarketplaceConsignmentPaymentStatus;
import com.mirakl.hybris.core.enums.MiraklOrderStatus;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.daos.ConsignmentEntryDao;
import com.mirakl.hybris.core.ordersplitting.daos.MarketplaceConsignmentDao;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

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

public class DefaultMarketplaceConsignmentService implements MarketplaceConsignmentService {

  private static final Logger LOG = Logger.getLogger(DefaultMarketplaceConsignmentService.class);
  protected final List<MiraklOrderStatus> VALID_CONSIGNMENT_STATES_BEFORE_RECEPTION =
      Arrays.asList(MiraklOrderStatus.SHIPPING, MiraklOrderStatus.SHIPPED, MiraklOrderStatus.TO_COLLECT);

  protected MarketplaceConsignmentDao marketplaceConsignmentDao;
  protected ConsignmentEntryDao consignmentEntryDao;
  protected ModelService modelService;
  protected BusinessProcessService businessProcessService;
  protected JsonMarshallingService jsonMarshallingService;
  protected Converter<Pair<OrderModel, MiraklOrder>, MarketplaceConsignmentModel> miraklCreateConsignmentConverter;
  protected MiraklMarketplacePlatformFrontApi miraklApi;

  @Override
  public Set<MarketplaceConsignmentModel> createMarketplaceConsignments(OrderModel order, MiraklCreatedOrders miraklOrders) {
    validateParameterNotNullStandardMessage("order", order);
    validateParameterNotNullStandardMessage("miraklOrders", miraklOrders);

    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Creating %s marketplace consignments for order [%s]", miraklOrders.getOrders().size(), order.getCode()));
    }

    Set<MarketplaceConsignmentModel> consignments = new HashSet<>();
    for (MiraklOrder miraklOrder : miraklOrders.getOrders()) {
      consignments.add(miraklCreateConsignmentConverter.convert(Pair.of(order, miraklOrder)));
    }
    Set<ConsignmentModel> orderConsignments = new HashSet<>(consignments);
    if (CollectionUtils.isNotEmpty(order.getConsignments())) {
      orderConsignments.addAll(order.getConsignments());
    }
    order.setConsignments(orderConsignments);
    modelService.saveAll(consignments);
    modelService.save(order);
    return consignments;
  }

  @Override
  public MiraklOrder loadConsignmentUpdate(MarketplaceConsignmentModel consignment) {
    validateParameterNotNullStandardMessage("consignment", consignment);

    String consignmentUpdatePayload = consignment.getConsignmentUpdatePayload();
    if (isEmpty(consignmentUpdatePayload)) {
      return null;
    }

    return jsonMarshallingService.fromJson(consignmentUpdatePayload, MiraklOrder.class);
  }

  @Override
  public MarketplaceConsignmentModel storeDebitRequest(MiraklOrderPayment miraklOrderPayment) {
    validateParameterNotNullStandardMessage("miraklOrderPayment", miraklOrderPayment);

    MarketplaceConsignmentModel consignment = getMarketplaceConsignmentForCode(miraklOrderPayment.getOrderId());
    consignment.setDebitRequestPayload(jsonMarshallingService.toJson(miraklOrderPayment));
    modelService.save(consignment);

    return consignment;
  }

  @Override
  public MiraklOrderPayment loadDebitRequest(MarketplaceConsignmentModel consignment) {
    validateParameterNotNullStandardMessage("consignment", consignment);

    String debitRequestPayload = consignment.getDebitRequestPayload();
    if (isEmpty(debitRequestPayload)) {
      return null;
    }

    return jsonMarshallingService.fromJson(debitRequestPayload, MiraklOrderPayment.class);
  }

  @Override
  public MarketplaceConsignmentModel getMarketplaceConsignmentForCode(String code) {
    validateParameterNotNullStandardMessage("code", code);
    MarketplaceConsignmentModel consignment = marketplaceConsignmentDao.findMarketplaceConsignmentByCode(code);
    if (consignment == null) {
      throw new UnknownIdentifierException(format("Unable to find consignment with code [%s]", code));
    }

    return consignment;
  }

  @Override
  public MarketplaceConsignmentModel confirmConsignmentReceptionForCode(String code, UserModel currentCustomer) {
    validateParameterNotNullStandardMessage("code", code);

    MarketplaceConsignmentModel consignment = getMarketplaceConsignmentForCode(code);
    if (consignment == null || !currentCustomer.equals(consignment.getOrder().getUser())) {
      throw new UnknownIdentifierException(format("Unable to find consignment with code [%s]", code));
    }

    if (!VALID_CONSIGNMENT_STATES_BEFORE_RECEPTION.contains(consignment.getMiraklOrderStatus())) {
      throw new IllegalStateException(format("The consignment with code [%s] can not be received", code));
    }

    if (consignment.getCustomerDebitDate() == null) {
      throw new IllegalStateException(format("Impossible to confirm consignment [%s] reception before customer debit", code));
    }

    MiraklReceiveOrderRequest receiveOrderRequest = new MiraklReceiveOrderRequest(code);
    miraklApi.receiveOrder(receiveOrderRequest);

    consignment.setMiraklOrderStatus(MiraklOrderStatus.RECEIVED);
    consignment.setCanEvaluate(true);
    modelService.save(consignment);

    LOG.debug(String.format("Sent reception confirmation for consignment[%s]", code));

    return consignment;
  }

  @Override
  public MarketplaceConsignmentModel receiveConsignmentUpdate(MiraklOrder miraklOrder) {
    MarketplaceConsignmentModel updatedConsignment = storeConsignmentUpdate(miraklOrder);
    triggerUpdateEvent(updatedConsignment);

    return updatedConsignment;
  }

  @Override
  public void postEvaluation(String code, MiraklCreateOrderEvaluation evaluation, UserModel currentCustomer) {
    validateParameterNotNullStandardMessage("code", code);

    MarketplaceConsignmentModel consignment = getMarketplaceConsignmentForCode(code);
    if (consignment == null || !currentCustomer.equals(consignment.getOrder().getUser())) {
      throw new UnknownIdentifierException(format("Unable to find consignment with code [%s]", code));
    }

    if (!consignment.getCanEvaluate()) {
      throw new IllegalStateException(format("The consignment with code [%s] can not be evaluated", code));
    }

    MiraklCreateOrderEvaluationRequest consignmentEvaluationRequest = new MiraklCreateOrderEvaluationRequest(code, evaluation);
    miraklApi.createOrderEvaluation(consignmentEvaluationRequest);

    consignment.setCanEvaluate(false);
    modelService.save(consignment);

    LOG.debug(String.format("Sent evaluation for consignment[%s]", code));
  }

  @Override
  public void cancelMarketplaceConsignment(MarketplaceConsignmentModel marketplaceConsignment) {
    cancelMarketplaceConsignmentForCode(marketplaceConsignment.getCode());
  }

  @Override
  public void cancelMarketplaceConsignmentForCode(String consignmentCode) {
    miraklApi.cancelOrder(new MiraklCancelOrderRequest(consignmentCode));
  }

  protected MarketplaceConsignmentModel storeConsignmentUpdate(MiraklOrder miraklOrder) {
    validateParameterNotNullStandardMessage("miraklOrder", miraklOrder);
    MarketplaceConsignmentModel consignment = getMarketplaceConsignmentForCode(miraklOrder.getId());
    consignment.setConsignmentUpdatePayload(jsonMarshallingService.toJson(miraklOrder));
    consignment.setLastUpdateProcessed(false);
    updateConsignmentCustomFields(miraklOrder, consignment);
    modelService.save(consignment);
    return consignment;
  }

  protected void updateConsignmentCustomFields(MiraklOrder miraklOrder, MarketplaceConsignmentModel consignment) {
    storeMarketplaceConsignmentCustomFields(miraklOrder.getOrderAdditionalFields(), consignment);
    for (ConsignmentEntryModel consignmentEntry : consignment.getConsignmentEntries()) {
      for (MiraklOrderLine orderLine : miraklOrder.getOrderLines()) {
        if (consignmentEntry.getMiraklOrderLineId().equals(orderLine.getId())) {
          storeMarketplaceConsignmentEntryCustomFields(orderLine.getAdditionalFields(), consignmentEntry);
        }
      }
    }
  }

  protected void triggerUpdateEvent(MarketplaceConsignmentModel updatedConsignment) {
    if (isNotEmpty(updatedConsignment.getConsignmentProcesses())) {
      for (ConsignmentProcessModel consignmentProcess : updatedConsignment.getConsignmentProcesses()) {
        businessProcessService.triggerEvent(consignmentProcess.getCode() + UPDATE_RECEIVED_EVENT_NAME);
      }
    }
  }

  @Override
  public ProductModel getProductForConsignmentEntry(String consignmentEntryCode) {
    ConsignmentEntryModel consignmentEntry = getConsignmentEntryForMiraklLineId(consignmentEntryCode);
    return consignmentEntry.getOrderEntry().getProduct();
  }

  @Override
  public ConsignmentEntryModel getConsignmentEntryForMiraklLineId(String miraklLineId) {
    ConsignmentEntryModel consignmentEntry = consignmentEntryDao.findConsignmentEntryByMiraklLineId(miraklLineId);
    if (consignmentEntry == null) {
      throw new UnknownIdentifierException(format("Impossible to find consignment entry for Mirakl line id [%s]", miraklLineId));
    }
    return consignmentEntry;
  }

  @Override
  public List<MarketplaceConsignmentModel> getMarketplaceConsignmentsForPaymentStatuses(
      Set<MarketplaceConsignmentPaymentStatus> paymentStatuses) {
    return marketplaceConsignmentDao.findMarketplaceConsignmentsByPaymentStatuses(paymentStatuses);
  }

  @Override
  public void checkUserAccessRightsForConsignment(String consignmentCode) {
    getMarketplaceConsignmentForCode(consignmentCode);
  }

  @Override
  public void storeMarketplaceConsignmentCustomFields(List<MiraklAdditionalFieldValue> customFields,
      MarketplaceConsignmentModel consignment) {
    validateParameterNotNullStandardMessage("consignment", consignment);
    if (isNotEmpty(customFields)) {
      consignment.setCustomFieldsJSON(
          jsonMarshallingService.toJson(customFields, new TypeReference<List<MiraklAdditionalFieldValue>>() {}));
      modelService.save(consignment);
    }
  }

  @Override
  public List<MiraklAdditionalFieldValue> loadMarketplaceConsignmentCustomFields(MarketplaceConsignmentModel consignment) {
    validateParameterNotNullStandardMessage("consignment", consignment);
    return jsonMarshallingService.fromJson(consignment.getCustomFieldsJSON(),
        new TypeReference<List<MiraklAdditionalFieldValue>>() {});
  }

  @Override
  public void storeMarketplaceConsignmentEntryCustomFields(List<MiraklAdditionalFieldValue> customFields,
      ConsignmentEntryModel entry) {
    validateParameterNotNullStandardMessage("entry", entry);
    if (isNotEmpty(customFields)) {
      entry.setCustomFieldsJSON(
          jsonMarshallingService.toJson(customFields, new TypeReference<List<MiraklAdditionalFieldValue>>() {}));
      modelService.save(entry);
    }
  }

  @Override
  public List<MiraklAdditionalFieldValue> loadMarketplaceConsignmentEntryCustomFields(ConsignmentEntryModel entry) {
    validateParameterNotNullStandardMessage("consignment", entry);
    return jsonMarshallingService.fromJson(entry.getCustomFieldsJSON(), new TypeReference<List<MiraklAdditionalFieldValue>>() {});
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setMiraklCreateConsignmentConverter(
      Converter<Pair<OrderModel, MiraklOrder>, MarketplaceConsignmentModel> miraklCreateConsignmentConverter) {
    this.miraklCreateConsignmentConverter = miraklCreateConsignmentConverter;
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

  @Required
  public void setMarketplaceConsignmentDao(MarketplaceConsignmentDao marketplaceConsignmentDao) {
    this.marketplaceConsignmentDao = marketplaceConsignmentDao;
  }

  @Required
  public void setMiraklApi(MiraklMarketplacePlatformFrontApi miraklApi) {
    this.miraklApi = miraklApi;
  }

  @Required
  public void setBusinessProcessService(BusinessProcessService businessProcessService) {
    this.businessProcessService = businessProcessService;
  }

  @Required
  public void setConsignmentEntryDao(ConsignmentEntryDao consignmentEntryDao) {
    this.consignmentEntryDao = consignmentEntryDao;
  }

}
