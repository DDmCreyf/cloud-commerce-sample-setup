package com.mirakl.hybris.facades.setting;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.hybris.beans.ReasonData;

public interface ReasonFacade {

  /**
   * Get the reasons with the given type
   *
   * @param type the type of reasons to retrieve
   * @return a list of reasons
   */
  List<ReasonData> getReasons(MiraklReasonType type);

  /**
   * Get the reasons with the given type and locale
   *
   * @param type the type of reasons to retrieve
   * @param locale the locale on which to retrieve the labels
   * @return a list of reasons
   */
  List<ReasonData> getReasons(MiraklReasonType type, Locale locale);


  /**
   * Get the reasons with the given type and returns a map of code/label
   * 
   * @param type the type of reasons to retrieve
   * @return a map having for key the reason code and for value the reason label
   */
  Map<String, String> getReasonsAsMap(MiraklReasonType type);

}
