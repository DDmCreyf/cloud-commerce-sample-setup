package com.mirakl.hybris.facades.inbox.converters.populator;

import java.util.ArrayList;
import java.util.List;

import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderThread;
import com.mirakl.client.mmp.request.order.message.MiraklThreadTopic;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.ThreadRecipientData;
import com.mirakl.hybris.core.enums.MiraklThreadTopicType;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklCreateOrderThreadPopulator implements Populator<CreateThreadMessageData, MiraklCreateOrderThread> {

  @Override
  public void populate(CreateThreadMessageData source, MiraklCreateOrderThread target) throws ConversionException {
    target.setBody(source.getBody());
    List<String> recipients = new ArrayList<>();
    for (ThreadRecipientData recipient : source.getTo()) {
      recipients.add(recipient.getType());
    }
    target.setTo(recipients);
    MiraklThreadTopic miraklThreadTopic = new MiraklThreadTopic();
    miraklThreadTopic.setValue(source.getTopic());
    miraklThreadTopic.setType(MiraklThreadTopicType.FREE_TEXT.getCode());
    target.setTopic(miraklThreadTopic);
  }

}
