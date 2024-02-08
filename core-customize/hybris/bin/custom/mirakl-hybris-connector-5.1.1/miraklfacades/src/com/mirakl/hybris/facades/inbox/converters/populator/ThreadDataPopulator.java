package com.mirakl.hybris.facades.inbox.converters.populator;

import static com.google.common.collect.FluentIterable.from;
import static com.mirakl.client.mmp.domain.reason.MiraklReasonType.ORDER_MESSAGING;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.mirakl.client.mmp.domain.message.thread.MiraklThread;
import com.mirakl.client.mmp.domain.message.thread.MiraklThread.Entity;
import com.mirakl.client.mmp.domain.message.thread.MiraklThread.Participant;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreadTopic;
import com.mirakl.hybris.beans.ThreadData;
import com.mirakl.hybris.beans.ThreadRecipientData;
import com.mirakl.hybris.beans.ThreadTopicData;
import com.mirakl.hybris.core.enums.MiraklThreadParticipantType;
import com.mirakl.hybris.facades.setting.ReasonFacade;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class ThreadDataPopulator implements Populator<MiraklThread, ThreadData> {

  protected static final String JOINER_STRING = ", ";

  protected Converter<Pair<MiraklThreadTopic, Map<String, String>>, ThreadTopicData> threadTopicDataConverter;
  protected Converter<Participant, ThreadRecipientData> threadRecipientDataConverter;
  protected ReasonFacade reasonFacade;

  @Override
  public void populate(MiraklThread source, ThreadData target) throws ConversionException {
    target.setId(source.getId().toString());
    target.setTopic(threadTopicDataConverter.convert(Pair.of(source.getTopic(), reasonFacade.getReasonsAsMap(ORDER_MESSAGING))));
    target.setCurrentParticipants(threadRecipientDataConverter.convertAll(source.getCurrentParticipants()));
    List<ThreadRecipientData> authorizedParticipants = threadRecipientDataConverter.convertAll(source.getAuthorizedParticipants());
    target.setAuthorizedParticipants(authorizedParticipants);
    target.setSelectableParticipants(from(authorizedParticipants).filter(isNotCustomerPredicate()).toList());
    target.setDateUpdated(source.getDateUpdated());
    target.setDateCreated(source.getDateCreated());
    target.setCurrentParticipantsDisplayValue(Joiner.on(JOINER_STRING)
        .join(from(target.getCurrentParticipants()).filter(isNotCustomerPredicate())
        .transform(toDisplayName()).toList()));
    Entity entity = source.getEntities().get(0);
    if (entity != null) {
      target.setEntityId(entity.getId());
      target.setEntityType(entity.getType());
      target.setEntityLabel(entity.getLabel());
    }
  }

  protected Predicate<ThreadRecipientData> isNotCustomerPredicate() {
    return new Predicate<ThreadRecipientData>() {
      @Override
      public boolean apply(ThreadRecipientData recipient) {
        return !MiraklThreadParticipantType.CUSTOMER.getCode().equals(recipient.getType());
      }
    };
  }

  protected Function<ThreadRecipientData, String> toDisplayName() {
    return new Function<ThreadRecipientData, String>() {
      @Override
      public String apply(ThreadRecipientData recipient) {
        return recipient.getDisplayName();
      }
    };
  }

  @Required
  public void setThreadTopicDataConverter(
      Converter<Pair<MiraklThreadTopic, Map<String, String>>, ThreadTopicData> threadTopicDataConverter) {
    this.threadTopicDataConverter = threadTopicDataConverter;
  }

  @Required
  public void setThreadRecipientDataConverter(Converter<Participant, ThreadRecipientData> threadRecipientDataConverter) {
    this.threadRecipientDataConverter = threadRecipientDataConverter;
  }

  @Required
  public void setReasonFacade(ReasonFacade reasonFacade) {
    this.reasonFacade = reasonFacade;
  }
}
