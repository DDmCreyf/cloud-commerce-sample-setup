package com.mirakl.hybris.facades.inbox.converters.populator;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.message.thread.MiraklThread.Attachment;
import com.mirakl.client.mmp.domain.message.thread.MiraklThread.Participant;
import com.mirakl.client.mmp.domain.message.thread.MiraklThread.Sender;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreadDetails.Message;
import com.mirakl.hybris.beans.ThreadMessageAttachmentData;
import com.mirakl.hybris.beans.ThreadMessageData;
import com.mirakl.hybris.beans.ThreadRecipientData;
import com.mirakl.hybris.core.enums.MiraklMessageSenderType;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class ThreadMessageDataPopulator implements Populator<Message, ThreadMessageData> {

  protected Converter<Attachment, ThreadMessageAttachmentData> threadMessageAttachmentDataConverter;
  protected Converter<Participant, ThreadRecipientData> threadRecipientDataConverter;

  @Override
  public void populate(Message source, ThreadMessageData target) throws ConversionException {
    target.setBody(source.getBody());
    target.setDateCreated(source.getDateCreated());
    Sender from = source.getFrom();
    target.setSenderDisplayName(
        from.getOrganizationDetails() != null ? from.getOrganizationDetails().getDisplayName() : from.getDisplayName());
    String senderType = from.getType();
    target.setSenderType(senderType);
    target.setIsFromCustomer(MiraklMessageSenderType.CUSTOMER_USER.getCode().equals(senderType)
        || MiraklMessageSenderType.CUSTOMER.getCode().equals(senderType));
    target.setAttachments(threadMessageAttachmentDataConverter.convertAll(source.getAttachments()));
    target.setTo(threadRecipientDataConverter.convertAll(source.getTo()));
  }

  @Required
  public void setThreadMessageAttachmentDataConverter(
      Converter<Attachment, ThreadMessageAttachmentData> threadMessageAttachmentDataConverter) {
    this.threadMessageAttachmentDataConverter = threadMessageAttachmentDataConverter;
  }

  @Required
  public void setThreadRecipientDataConverter(Converter<Participant, ThreadRecipientData> threadRecipientDataConverter) {
    this.threadRecipientDataConverter = threadRecipientDataConverter;
  }

}
