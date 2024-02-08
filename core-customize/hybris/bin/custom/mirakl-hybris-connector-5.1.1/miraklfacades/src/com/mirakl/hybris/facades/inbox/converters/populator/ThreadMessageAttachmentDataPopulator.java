package com.mirakl.hybris.facades.inbox.converters.populator;

import com.mirakl.client.mmp.domain.message.thread.MiraklThread.Attachment;
import com.mirakl.hybris.beans.ThreadMessageAttachmentData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ThreadMessageAttachmentDataPopulator implements Populator<Attachment, ThreadMessageAttachmentData> {

  @Override
  public void populate(Attachment source, ThreadMessageAttachmentData target) throws ConversionException {
    target.setId(source.getId().toString());
    target.setName(source.getName());
    target.setSize(source.getSize());
  }

}
