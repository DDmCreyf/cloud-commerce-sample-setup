package com.mirakl.hybris.facades.inbox.converters.populator;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.message.thread.MiraklThreadDetails;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreadDetails.Message;
import com.mirakl.hybris.beans.ThreadDetailsData;
import com.mirakl.hybris.beans.ThreadMessageData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class ThreadDetailsDataPopulator implements Populator<MiraklThreadDetails, ThreadDetailsData> {

  protected Converter<Message, ThreadMessageData> threadMessageDataConverter;

  @Override
  public void populate(MiraklThreadDetails source, ThreadDetailsData target) throws ConversionException {
    target.setMessages(threadMessageDataConverter.convertAll(source.getMessages()));
  }

  @Required
  public void setThreadMessageDataConverter(Converter<Message, ThreadMessageData> threadMessageDataConverter) {
    this.threadMessageDataConverter = threadMessageDataConverter;
  }

}
