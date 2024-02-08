package com.mirakl.hybris.facades.inbox.converters.populator;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.message.thread.MiraklThread;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreads;
import com.mirakl.hybris.beans.ThreadListData;
import com.mirakl.hybris.beans.ThreadData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class ThreadListDataPopulator implements Populator<MiraklThreads, ThreadListData> {

  protected Converter<MiraklThread, ThreadData> threadDataConverter;

  @Override
  public void populate(MiraklThreads source, ThreadListData target) throws ConversionException {
    target.setThreads(threadDataConverter.convertAll(source.getData()));
    target.setNextPageToken(source.getNextPageToken());
    target.setPreviousPageToken(source.getPreviousPageToken());
  }

  @Required
  public void setThreadDataConverter(Converter<MiraklThread, ThreadData> threadDataConverter) {
    this.threadDataConverter = threadDataConverter;
  }

}
