package com.mirakl.hybris.core.order.services.impl;

import static com.mirakl.hybris.core.enums.MiraklOrderLineStatus.INCIDENT_CLOSED;
import static com.mirakl.hybris.core.enums.MiraklOrderLineStatus.INCIDENT_OPEN;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.message.MiraklMessageCreated;
import com.mirakl.client.mmp.domain.reason.MiraklGenericReason;
import com.mirakl.client.mmp.domain.reason.MiraklReason;
import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.request.order.message.MiraklCreateOrderMessageRequest;
import com.mirakl.client.mmp.request.order.incident.MiraklCloseIncidentRequest;
import com.mirakl.client.mmp.request.order.incident.MiraklOpenIncidentRequest;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderMessage;
import com.mirakl.hybris.core.order.services.IncidentService;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.core.setting.services.ReasonService;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultIncidentService implements IncidentService {

  private static final Logger LOG = Logger.getLogger(DefaultIncidentService.class);

  protected static final String ORDER_INCIDENT_SUBJECT_PREFIX = "Order incident: ";

  protected MiraklMarketplacePlatformFrontApi miraklApi;
  protected ReasonService reasonService;
  protected ModelService modelService;
  protected MarketplaceConsignmentService marketplaceConsignmentService;

  @Override
  @Deprecated
  public List<MiraklReason> getReasons() {
    return reasonService.getReasons();
  }

  @Override
  public ConsignmentEntryModel openIncident(String consignmentEntryCode, String reasonCode) {
    validateParameterNotNullStandardMessage("consignmentEntryCode", consignmentEntryCode);
    validateParameterNotNullStandardMessage("reasonCode", reasonCode);

    ConsignmentEntryModel consignmentEntry =
        marketplaceConsignmentService.getConsignmentEntryForMiraklLineId(consignmentEntryCode);
    if (!isTrue(consignmentEntry.getCanOpenIncident())) {
      throw new IllegalStateException(format("Impossible to open an incident for consignment entry [%s]", consignmentEntryCode));
    }
    String consignmentCode = consignmentEntry.getConsignment().getCode();
    MiraklOpenIncidentRequest openIncidentRequest =
        new MiraklOpenIncidentRequest(consignmentCode, consignmentEntryCode, reasonCode);
    miraklApi.openIncident(openIncidentRequest);

    consignmentEntry.setCanOpenIncident(false);
    consignmentEntry.setMiraklOrderLineStatus(INCIDENT_OPEN);

    modelService.save(consignmentEntry);
    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("Opened incident for consignment entry [%s]", consignmentEntryCode));
    }
    return consignmentEntry;
  }

  @Override
  @Deprecated
  public void openIncident(String consignmentEntryCode, String reasonCode, MiraklCreateOrderMessage message) {
    ConsignmentEntryModel consignmentEntry = openIncident(consignmentEntryCode, reasonCode);
    publishIncidentMessage(MiraklReasonType.INCIDENT_OPEN, consignmentEntryCode, reasonCode, message,
        consignmentEntry.getConsignment().getCode());
  }

  @Override
  public ConsignmentEntryModel closeIncident(String consignmentEntryCode, String reasonCode) {
    validateParameterNotNullStandardMessage("consignmentEntryCode", consignmentEntryCode);

    ConsignmentEntryModel consignmentEntry =
        marketplaceConsignmentService.getConsignmentEntryForMiraklLineId(consignmentEntryCode);
    if (consignmentEntry.getCanOpenIncident() || !INCIDENT_OPEN.equals(consignmentEntry.getMiraklOrderLineStatus())) {
      throw new IllegalStateException(format("Impossible to close an incident for consignment entry [%s]", consignmentEntryCode));
    }

    String consignmentCode = consignmentEntry.getConsignment().getCode();
    MiraklCloseIncidentRequest request = new MiraklCloseIncidentRequest(consignmentCode, consignmentEntryCode, reasonCode);
    miraklApi.closeIncident(request);

    consignmentEntry.setCanOpenIncident(true);
    consignmentEntry.setMiraklOrderLineStatus(INCIDENT_CLOSED);

    modelService.save(consignmentEntry);
    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("Closed incident for consignment entry [%s]", consignmentEntryCode));
    }
    return consignmentEntry;
  }

  @Override
  @Deprecated
  public void closeIncident(String consignmentEntryCode, String reasonCode, MiraklCreateOrderMessage message) {
    ConsignmentEntryModel consignmentEntry = closeIncident(consignmentEntryCode, reasonCode);
    publishIncidentMessage(MiraklReasonType.INCIDENT_CLOSE, consignmentEntryCode, reasonCode, message,
        consignmentEntry.getConsignment().getCode());
  }

  @Deprecated
  protected void publishIncidentMessage(MiraklReasonType miraklReasonType, String consignmentEntryCode, String reasonCode,
      MiraklCreateOrderMessage message, String consignmentCode) {
    if (message == null) {
      return;
    }
    String body = message.getBody();
    if (StringUtils.isEmpty(body)) {
      return;
    }
    String reasonLabel = getReason(miraklReasonType, reasonCode);
    if (reasonLabel == null) {
      LOG.error(String.format("Reason code [%s] label not found.", reasonCode));
    }

    message.setSubject(ORDER_INCIDENT_SUBJECT_PREFIX + reasonLabel);

    MiraklCreateOrderMessageRequest messageRequest = new MiraklCreateOrderMessageRequest(consignmentCode, message);
    MiraklMessageCreated miraklMessageCreated = miraklApi.createOrderMessage(messageRequest);

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("Sent incident message [id=%s] for consignment entry [%s]", consignmentEntryCode,
          miraklMessageCreated.getId()));
    }
  }

  protected String getReason(MiraklReasonType miraklReasonType, String reasonCode) {
    for (MiraklGenericReason reason : reasonService.getReasonsByType(miraklReasonType)) {
      if (reason.getCode().equals(reasonCode)) {
        return reason.getLabel();
      }
    }
    return null;
  }

  @Required
  public void setReasonService(ReasonService reasonService) {
    this.reasonService = reasonService;
  }

  @Required
  public void setMiraklApi(MiraklMarketplacePlatformFrontApi miraklApi) {
    this.miraklApi = miraklApi;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }

}
