package com.mirakl.hybris.fulfilmentprocess.actions.consignment;

import static com.google.common.base.Functions.toStringFunction;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static com.mirakl.client.mmp.domain.order.state.AbstractMiraklOrderStatus.State.WAITING_DEBIT_PAYMENT;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.ImmutableMap;
import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.domain.order.state.AbstractMiraklOrderStatus.State;
import com.mirakl.client.mmp.domain.order.state.MiraklOrderStatus;
import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.hybris.core.constants.MiraklservicesConstants;
import com.mirakl.hybris.core.fulfilment.strategies.ProcessMarketplacePaymentStrategy;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.model.MarketplaceConsignmentProcessModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.task.RetryLaterException;

public class ProcessLogisticOrderUpdateAction extends AbstractAction<MarketplaceConsignmentProcessModel> {

  private static final Logger LOG = Logger.getLogger(ProcessLogisticOrderUpdateAction.class);

  protected MarketplaceConsignmentService marketplaceConsignmentService;

  protected Populator<MiraklOrder, MarketplaceConsignmentModel> updateConsignmentPopulator;

  protected ProcessMarketplacePaymentStrategy processMarketplacePaymentStrategy;

  protected ConfigurationService configurationService;

  protected Map<State, Transition> stateToTransition = ImmutableMap.of( //
      State.CLOSED, Transition.CLOSE, //
      State.REFUSED, Transition.REFUSE, //
      State.CANCELED, Transition.CANCEL //
  );

  @Override
  public String execute(MarketplaceConsignmentProcessModel consignmentProcess) throws RetryLaterException, Exception {
    return String.valueOf(executeAction(consignmentProcess));
  }

  public Transition executeAction(MarketplaceConsignmentProcessModel consignmentProcess) throws RetryLaterException, Exception {
    MarketplaceConsignmentModel consignment = (MarketplaceConsignmentModel) consignmentProcess.getConsignment();
    MiraklOrder miraklOrder = marketplaceConsignmentService.loadConsignmentUpdate(consignment);

    if (miraklOrder == null) {
      LOG.warn(format("No stored update was found for consignment [%s]", consignment.getCode()));
      return Transition.WAIT;
    }

    if (!consignment.isLastUpdateProcessed()) {
      LOG.info(format("Processing a received update for consignment [%s]", consignment.getCode()));
      processConsignmentUpdate(consignment, miraklOrder);
      saveConsignment(consignment);
    }

    MiraklOrderStatus miraklStatus = miraklOrder.getStatus();
    if (!useMiraklPullPaymentRequests()) {
      MiraklOrderPayment debitRequest = marketplaceConsignmentService.loadDebitRequest(consignment);
      if (WAITING_DEBIT_PAYMENT.equals(miraklStatus.getState()) && debitRequest != null) {
        processMarketplacePaymentStrategy.processPayment(consignment, debitRequest);
      }
    }
    return nextTransition(miraklStatus);
  }

  protected void processConsignmentUpdate(MarketplaceConsignmentModel consignment, MiraklOrder miraklOrder) {
    updateConsignmentPopulator.populate(miraklOrder, consignment);
    consignment.setLastUpdateProcessed(true);
  }

  protected void saveConsignment(MarketplaceConsignmentModel consignment) {
    getModelService().saveAll(consignment.getConsignmentEntries());
    getModelService().save(consignment);
    List<ReturnRequestModel> returnRequests = ((OrderModel) consignment.getOrder()).getReturnRequests();
    if (isNotEmpty(returnRequests)) {
      getModelService().saveAll(returnRequests);
      for (ReturnRequestModel returnRequest : returnRequests) {
        getModelService().saveAll(returnRequest.getReturnEntries());
      }
    }
  }

  protected Transition nextTransition(MiraklOrderStatus miraklStatus) {
    Transition transition = stateToTransition.get(miraklStatus.getState());
    return (transition != null) ? transition : Transition.WAIT;
  }

  protected enum Transition {
    WAIT, CANCEL, CLOSE, REFUSE;

    private static final Set<String> stringValues = newHashSet(transform(asList(values()), toStringFunction()));

    public static Set<String> getStringValues() {
      return stringValues;
    }
  }

  @Override
  public Set<String> getTransitions() {
    return Transition.getStringValues();
  }

  protected boolean useMiraklPullPaymentRequests() {
    return configurationService.getConfiguration().getBoolean(MiraklservicesConstants.ENABLE_PAYMENT_REQUEST_PULLING, false);
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }

  @Required
  public void setUpdateConsignmentPopulator(Populator<MiraklOrder, MarketplaceConsignmentModel> updateConsignmentPopulator) {
    this.updateConsignmentPopulator = updateConsignmentPopulator;
  }

  @Required
  public void setProcessMarketplacePaymentStrategy(ProcessMarketplacePaymentStrategy processMarketplacePaymentStrategy) {
    this.processMarketplacePaymentStrategy = processMarketplacePaymentStrategy;
  }
}
