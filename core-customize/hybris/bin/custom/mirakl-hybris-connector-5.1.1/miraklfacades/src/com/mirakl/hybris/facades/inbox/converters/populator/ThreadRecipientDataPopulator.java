package com.mirakl.hybris.facades.inbox.converters.populator;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.message.thread.MiraklThread.Participant;
import com.mirakl.hybris.beans.ThreadRecipientData;
import com.mirakl.hybris.core.enums.MiraklThreadParticipantType;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ThreadRecipientDataPopulator implements Populator<Participant, ThreadRecipientData> {
  public static final String DEFAULT_OPERATOR_NAME = "Operator";

  protected CMSSiteService cmsSiteService;

  @Override
  public void populate(Participant source, ThreadRecipientData target) throws ConversionException {
    if (MiraklThreadParticipantType.OPERATOR.getCode().equals(source.getType())) {
      final String operatorName = cmsSiteService.getCurrentSite().getOperatorName() != null ? cmsSiteService.getCurrentSite().getOperatorName() : DEFAULT_OPERATOR_NAME;
      target.setDisplayName(operatorName);
    } else {
      target.setDisplayName(source.getDisplayName());
    }
    target.setId(source.getId());
    target.setType(source.getType());
  }

  @Required
  public void setCmsSiteService(CMSSiteService cmsSiteService) {
    this.cmsSiteService = cmsSiteService;
  }

}
