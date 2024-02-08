package com.mirakl.hybris.facades.setting.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.reason.MiraklGenericReason;
import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.hybris.beans.ReasonData;
import com.mirakl.hybris.core.setting.services.ReasonService;
import com.mirakl.hybris.facades.setting.ReasonFacade;

import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultReasonFacade implements ReasonFacade {

  protected ReasonService reasonService;
  protected Converter<MiraklGenericReason, ReasonData> reasonDataConverter;

  @Override
  public List<ReasonData> getReasons(MiraklReasonType type) {
    return reasonDataConverter.convertAll(reasonService.getReasonsByType(type));
  }

  @Override
  public List<ReasonData> getReasons(MiraklReasonType type, Locale locale) {
    return reasonDataConverter.convertAll(reasonService.getReasonsByType(type, locale));
  }

  @Override
  public Map<String, String> getReasonsAsMap(MiraklReasonType type) {
    List<ReasonData> reasons = getReasons(type);

    Map<String, String> reasonsMap = new HashMap<>();
    for (ReasonData reasonData : reasons) {
      reasonsMap.put(reasonData.getCode(), reasonData.getLabel());
    }

    return reasonsMap;
  }

  @Required
  public void setReasonService(ReasonService reasonService) {
    this.reasonService = reasonService;
  }

  @Required
  public void setReasonDataConverter(Converter<MiraklGenericReason, ReasonData> reasonDataConverter) {
    this.reasonDataConverter = reasonDataConverter;
  }

}
