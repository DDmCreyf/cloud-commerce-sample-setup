package com.mirakl.hybris.facades.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderMessage;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.MessageData;
import com.mirakl.hybris.beans.ReasonData;
import com.mirakl.hybris.core.order.services.IncidentService;
import com.mirakl.hybris.facades.message.MessagingThreadFacade;
import com.mirakl.hybris.facades.order.IncidentFacade;
import com.mirakl.hybris.facades.setting.ReasonFacade;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultIncidentFacade implements IncidentFacade {

  protected MessagingThreadFacade messagingThreadFacade;
  protected IncidentService incidentService;
  protected ReasonFacade reasonFacade;
  protected Converter<MessageData, MiraklCreateOrderMessage> miraklCreateOrderMessageConverter;

  @Override
  @Deprecated
  public List<ReasonData> getReasons(MiraklReasonType wantedType) {
    return reasonFacade.getReasons(wantedType);
  }

  @Override
  public void openIncident(String consignmentEntryCode, String reasonCode) {
    validateParameterNotNullStandardMessage("consignmentEntryCode", consignmentEntryCode);
    validateParameterNotNullStandardMessage("reasonCode", reasonCode);

    incidentService.openIncident(consignmentEntryCode, reasonCode);
  }

  @Override
  public void openIncident(String consignmentEntryCode, String reasonCode, CreateThreadMessageData createThreadMessageData) {
    validateParameterNotNullStandardMessage("consignmentEntryCode", consignmentEntryCode);
    validateParameterNotNullStandardMessage("reasonCode", reasonCode);
    validateParameterNotNullStandardMessage("createThreadMessageData", createThreadMessageData);

    ConsignmentEntryModel updatedConsignmentEntry = incidentService.openIncident(consignmentEntryCode, reasonCode);
    messagingThreadFacade.createConsignmentThread(updatedConsignmentEntry.getConsignment().getCode(), createThreadMessageData);
  }

  @Override
  @Deprecated
  public void openIncident(String consignmentEntryCode, String reasonCode, MessageData message) {
    incidentService.openIncident(consignmentEntryCode, reasonCode, miraklCreateOrderMessageConverter.convert(message));
  }

  @Override
  public void closeIncident(String consignmentEntryCode, String reasonCode) {
    validateParameterNotNullStandardMessage("consignmentEntryCode", consignmentEntryCode);
    validateParameterNotNullStandardMessage("reasonCode", reasonCode);

    incidentService.closeIncident(consignmentEntryCode, reasonCode);
  }

  @Override
  public void closeIncident(String consignmentEntryCode, String reasonCode, CreateThreadMessageData createThreadMessageData) {
    validateParameterNotNullStandardMessage("consignmentEntryCode", consignmentEntryCode);
    validateParameterNotNullStandardMessage("reasonCode", reasonCode);
    validateParameterNotNullStandardMessage("createThreadMessageData", createThreadMessageData);

    ConsignmentEntryModel updatedConsignmentEntry = incidentService.closeIncident(consignmentEntryCode, reasonCode);
    messagingThreadFacade.createConsignmentThread(updatedConsignmentEntry.getConsignment().getCode(), createThreadMessageData);
  }

  @Override
  @Deprecated
  public void closeIncident(String consignmentEntryCode, String reasonCode, MessageData message) {
    incidentService.closeIncident(consignmentEntryCode, reasonCode, miraklCreateOrderMessageConverter.convert(message));
  }

  @Required
  public void setMessagingThreadFacade(MessagingThreadFacade messagingThreadFacade) {
    this.messagingThreadFacade = messagingThreadFacade;
  }

  @Required
  public void setReasonFacade(ReasonFacade reasonFacade) {
    this.reasonFacade = reasonFacade;
  }

  @Required
  public void setIncidentService(IncidentService incidentService) {
    this.incidentService = incidentService;
  }

  @Required
  public void setMiraklCreateOrderMessageConverter(
      Converter<MessageData, MiraklCreateOrderMessage> miraklCreateOrderMessageConverter) {
    this.miraklCreateOrderMessageConverter = miraklCreateOrderMessageConverter;
  }

}
