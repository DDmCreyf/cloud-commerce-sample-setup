package com.mirakl.hybris.core.setting.services;

import java.util.List;
import java.util.Locale;

import com.mirakl.client.mmp.domain.reason.MiraklGenericReason;
import com.mirakl.client.mmp.domain.reason.MiraklReason;
import com.mirakl.client.mmp.domain.reason.MiraklReasonType;

public interface ReasonService {

  /**
   * Gets reasons from Mirakl (RE01). <br>
   * The reason labels are retrieved in the default Mirakl locale.
   *
   * @return a list of MiraklReason from Mirakl
   */
  List<MiraklReason> getReasons();

  /**
   * Gets reasons from Mirakl (RE01)
   *
   * @param locale the locale on which the reason labels are retrieved
   * @return a list of MiraklReason from Mirakl
   */
  List<MiraklReason> getReasons(Locale locale);

  /**
   * Gets reasons from Mirakl filtered by type (RE01). The reason labels are retrieved in the default Mirakl locale.
   *
   * @param miraklReasonType the requested type to retrieve
   * @return a list of MiraklReason from Mirakl
   */
  List<MiraklGenericReason> getReasonsByType(MiraklReasonType miraklReasonType);

  /**
   * Gets reasons from Mirakl filtered by type (RE01)
   *
   * @param miraklReasonType the requested type to retrieve
   * @param locale the locale on which the reason labels are retrieved
   * @return a list of MiraklReason from Mirakl
   */
  List<MiraklGenericReason> getReasonsByType(MiraklReasonType miraklReasonType, Locale locale);

}
