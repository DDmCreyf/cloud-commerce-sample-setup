package com.mirakl.hybris.facades.inbox.converters.populator;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.mirakl.client.mmp.domain.message.thread.MiraklThreadTopic;
import com.mirakl.hybris.beans.ThreadTopicData;
import com.mirakl.hybris.core.enums.MiraklThreadTopicType;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ThreadTopicDataPopulator implements Populator<Pair<MiraklThreadTopic, Map<String, String>>, ThreadTopicData> {

  @Override
  public void populate(Pair<MiraklThreadTopic, Map<String, String>> source, ThreadTopicData target) throws ConversionException {
    MiraklThreadTopic miraklThreadTopic = source.getLeft();
    Map<String, String> reasonsMap = source.getRight();

    if (MiraklThreadTopicType.REASON_CODE.getCode().equals(miraklThreadTopic.getType())) {
      target.setCode(miraklThreadTopic.getValue());
      target.setDisplayValue(reasonsMap.get(miraklThreadTopic.getValue()));
    } else {
      target.setDisplayValue(miraklThreadTopic.getValue());
    }
  }

}
