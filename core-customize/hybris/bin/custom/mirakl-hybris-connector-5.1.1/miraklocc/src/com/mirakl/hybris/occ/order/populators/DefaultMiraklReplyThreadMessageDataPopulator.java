package com.mirakl.hybris.occ.order.populators;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.ThreadDetailsData;
import com.mirakl.hybris.beans.ThreadRecipientData;
import com.mirakl.hybris.core.enums.MiraklThreadParticipantType;
import com.mirakl.hybris.facades.message.MessagingThreadFacade;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DefaultMiraklReplyThreadMessageDataPopulator implements Populator<UUID, CreateThreadMessageData> {

  private MessagingThreadFacade messagingThreadFacade;

  @Override
  public void populate(UUID threadId, CreateThreadMessageData createThreadMessageData) throws ConversionException {
    final ThreadDetailsData threadDetails = messagingThreadFacade.getThreadDetails(threadId);
    final Optional<ThreadRecipientData> first = threadDetails.getSelectableParticipants().stream()
        .filter(participant -> MiraklThreadParticipantType.SHOP.getCode().equals(participant.getType())).findFirst();
    first.ifPresent(threadRecipientData -> createThreadMessageData.getTo().stream()
        .filter(recipient -> MiraklThreadParticipantType.SHOP.getCode().equals(recipient.getType()))
        .forEach(recipient -> recipient.setId(threadRecipientData.getId())));;

    if (isEmpty(createThreadMessageData.getAttachements())) {
      createThreadMessageData.setAttachements(new ArrayList<>());
    }
  }

  @Required
  public void setMessagingThreadFacade(MessagingThreadFacade messagingThreadFacade) {
    this.messagingThreadFacade = messagingThreadFacade;
  }
}
