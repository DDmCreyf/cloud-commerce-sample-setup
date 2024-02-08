package com.mirakl.hybris.facades.inbox.converters.populator;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.List;

import com.mirakl.client.mmp.request.order.message.MiraklThreadReplyMessageInput;
import com.mirakl.client.mmp.request.order.message.MiraklThreadReplyMessageInput.Recipient;
import com.mirakl.client.mmp.request.order.message.MiraklThreadTopic;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.ThreadRecipientData;
import com.mirakl.hybris.core.enums.MiraklThreadTopicType;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklThreadReplyMessageInputPopulator implements Populator<CreateThreadMessageData, MiraklThreadReplyMessageInput> {

  @Override
  public void populate(CreateThreadMessageData source, MiraklThreadReplyMessageInput target) throws ConversionException {
    target.setBody(source.getBody());
    if (isNotEmpty(source.getTopic())) {
      target.setTopic(new MiraklThreadTopic(MiraklThreadTopicType.FREE_TEXT.getCode(), source.getTopic()));
    }
    List<Recipient> recipients = new ArrayList<>();
    for (ThreadRecipientData recipient : source.getTo()) {
      recipients.add(new Recipient(recipient.getId(), recipient.getType()));
    }

    target.setTo(recipients);
  }

}
