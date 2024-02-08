package com.mirakl.hybris.webservices.controllers;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.hybris.beans.MiraklRefundRequestData;
import com.mirakl.hybris.core.constants.MiraklservicesConstants;
import com.mirakl.hybris.core.payment.events.DebitRequestReceivedEvent;
import com.mirakl.hybris.core.payment.events.RefundRequestReceivedEvent;
import com.mirakl.hybris.core.payment.jobs.MiraklProcessDebitsJob;
import com.mirakl.hybris.core.payment.jobs.MiraklRetrieveDebitRequestsJob;
import com.mirakl.hybris.core.payment.jobs.MiraklRetrieveRefundRequestsJob;
import com.mirakl.hybris.webservices.dto.*;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

/**
 * @deprecated As of release 2.3.1, this controller's features are handled by cronjobs using PA11 & PA12 APIs.<br/>
 *             To use the new cronjobs:
 *             <ul>
 *             <li>In Mirakl, set the debit and refund connectors to "None"</li>
 *             <li>Set the miraklservices property 'mirakl.payment.enablerequestpulling' to 'true'</li>
 *             </ul>
 * 
 * @see MiraklProcessDebitsJob
 * @see MiraklRetrieveDebitRequestsJob
 * @see MiraklRetrieveRefundRequestsJob
 */
@Controller
@RequestMapping("/payment")
@Deprecated
public class PaymentController {

  private static final Logger LOG = Logger.getLogger(PaymentController.class);

  @Resource(name = "eventService")
  protected EventService eventService;

  @Resource(name = "dataMapper")
  protected DataMapper dataMapper;

  @Resource(name = "configurationService")
  protected ConfigurationService configurationService;

  @ResponseStatus(value = HttpStatus.OK)
  @RequestMapping(value = "/debit", method = RequestMethod.POST)
  public void debitPayment(@RequestBody final DebitRequestWsDTO debitRequestWsDTO) {
    if (usePullPaymentRequest()) {
      LOG.warn(format("The debit webservice endpoint has been triggered whereas the pull mode is acticvated"));
      return;
    }
    List<MiraklOrderPaymentWsDTO> orders = debitRequestWsDTO.getOrders();
    checkArgument(isNotEmpty(orders), "Received an empty debit request");

    for (MiraklOrderPaymentWsDTO miraklOrderPaymentWsDTO : orders) {
      MiraklOrderPayment debitRequest = dataMapper.map(miraklOrderPaymentWsDTO, MiraklOrderPayment.class);
      LOG.info(format("Received a debit request of [%s] for order [%s]", debitRequest.getAmount(), debitRequest.getOrderId()));
      eventService.publishEvent(new DebitRequestReceivedEvent(debitRequest));
    }
  }

  @ResponseStatus(value = HttpStatus.OK)
  @RequestMapping(value = "/refund", method = RequestMethod.POST)
  public void refundPayment(@RequestBody final RefundRequestWsDTO refundRequestWsDTO) {
    if (usePullPaymentRequest()) {
      LOG.warn(format("The debit webservice endpoint has been triggered whereas the pull mode is acticvated"));
      return;
    }
    List<MiraklOrderPaymentWsDTO> orders = refundRequestWsDTO.getOrders();
    checkArgument(isNotEmpty(orders), "Received an empty refund request");

    for (MiraklOrderPaymentWsDTO order : orders) {
      if (isEmpty(order.getOrderLines())) {
        LOG.warn(format("Received a refund request with no lines for order [%s]", order.getOrder_id()));
        continue;
      }

      for (MiraklOrderLinePaymentWsDTO orderLine : order.getOrderLines()) {
        if (isNotEmpty(orderLine.getRefunds())) {
          publishRefundRequestEvent(order, orderLine);
        }
      }
    }
  }

  protected void publishRefundRequestEvent(MiraklOrderPaymentWsDTO order, MiraklOrderLinePaymentWsDTO orderLine) {
    for (MiraklRefundWsDto refund : orderLine.getRefunds()) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(format("Received a refund request of [%s] for order [%s]. Refund Id: [%s]", refund.getAmount(),
            order.getOrder_id(), refund.getId()));
      }
      eventService.publishEvent(new RefundRequestReceivedEvent(populateRefundRequestData(order, orderLine, refund)));
    }
  }

  protected MiraklRefundRequestData populateRefundRequestData(MiraklOrderPaymentWsDTO order,
      MiraklOrderLinePaymentWsDTO orderLine, MiraklRefundWsDto refund) {
    MiraklRefundRequestData refundRequestData = new MiraklRefundRequestData();
    refundRequestData.setRefundId(refund.getId());
    refundRequestData.setAmount(BigDecimal.valueOf(refund.getAmount()));
    refundRequestData.setCommercialOrderId(order.getOrder_commercial_id());
    refundRequestData.setMiraklOrderId(order.getOrder_id());
    refundRequestData.setMiraklOrderLineId(orderLine.getOrder_line_id());

    return refundRequestData;
  }

  protected boolean usePullPaymentRequest() {
    return configurationService.getConfiguration().getBoolean(MiraklservicesConstants.ENABLE_PAYMENT_REQUEST_PULLING, true);
  }
}
