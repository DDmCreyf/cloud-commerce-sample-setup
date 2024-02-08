package com.mirakl.hybris.core.setting.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.reason.MiraklGenericReason;
import com.mirakl.client.mmp.domain.reason.MiraklReason;
import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.reason.MiraklGetReasonsRequest;
import com.mirakl.hybris.core.setting.services.ReasonService;

public class DefaultReasonService implements ReasonService {

  protected MiraklMarketplacePlatformFrontApi miraklApi;

  @Override
  public List<MiraklReason> getReasons() {
    return miraklApi.getReasons(new MiraklGetReasonsRequest());
  }

  @Override
  public List<MiraklReason> getReasons(Locale locale) {
    return miraklApi.getReasons(new MiraklGetReasonsRequest(locale));
  }

  @Override
  public List<MiraklGenericReason> getReasonsByType(final MiraklReasonType miraklReasonType) {
    return filterReasonsByType(getReasons(), miraklReasonType);
  }

  @Override
  public List<MiraklGenericReason> getReasonsByType(MiraklReasonType miraklReasonType, Locale locale) {
    return filterReasonsByType(getReasons(locale), miraklReasonType);
  }

  protected List<MiraklGenericReason> filterReasonsByType(List<MiraklReason> reasons, MiraklReasonType miraklReasonType) {
    List<MiraklGenericReason> filteredReasons = new ArrayList<>();
    for (MiraklReason miraklReason : reasons) {
      if (miraklReasonType.equals(miraklReason.getType())) {
        filteredReasons.add(miraklReason);
      }
    }
    return filteredReasons;
  }

  @Required
  public void setMiraklApi(MiraklMarketplacePlatformFrontApi miraklApi) {
    this.miraklApi = miraklApi;
  }

}
