package com.mirakl.hybris.promotions.services;

import java.util.Collection;
import java.util.Date;

import com.mirakl.hybris.promotions.model.MiraklPromotionModel;

public interface MiraklPromotionImportService {

  /**
   * Imports all the promotions from Mirakl
   *
   * @return a <tt>Collection</tt> containing the imported promotions
   */
  Collection<MiraklPromotionModel> importAllPromotions();

  /**
   * Imports the mapping between promotions and offers. Two kinds of informations are updated: the offers which trigger a
   * promotion, and the offers given as a reward by a promotion.
   * 
   * @param lastImportTime the last import time, used for incremental imports. If null, then a full mapping import will be
   *        performed.
   */
  void importPromotionOffersMapping(Date lastImportTime);
}
