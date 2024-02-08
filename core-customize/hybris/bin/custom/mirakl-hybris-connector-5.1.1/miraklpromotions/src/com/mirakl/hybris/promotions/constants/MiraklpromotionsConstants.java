/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with SAP.
 */
package com.mirakl.hybris.promotions.constants;

import static java.lang.String.format;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;

/**
 * Global class for all Miraklpromotions constants. You can add global constants for your extension into this class.
 */
public final class MiraklpromotionsConstants extends GeneratedMiraklpromotionsConstants {
  public static final String EXTENSIONNAME = "miraklpromotions";

  private MiraklpromotionsConstants() {
    // empty to avoid instantiating this constant class
  }

  public static final String DEFAULT_FILE_ENCODING = "UTF-8";
  public static final String UPDATE_OFFER_IMPEX_COLUMN = format("UPDATE %s", OfferModel._TYPECODE);
  public static final String OFFER_ID_IMPEX_COLUMN = OfferModel.ID + "[unique=true]";
  public static final String TRIGGER_PROMOTIONS_IMPEX_COLUMN =
      format("%s(%s,%s)", OfferModel.TRIGGERPROMOTIONS, MiraklPromotionModel.INTERNALID, MiraklPromotionModel.SHOPID);
  public static final String REWARD_PROMOTIONS_IMPEX_COLUMN =
      format("%s(%s,%s)", OfferModel.REWARDPROMOTIONS, MiraklPromotionModel.INTERNALID, MiraklPromotionModel.SHOPID);
  public static final String[] OFFER_PROMOTION_MAPPING_HEADER = new String[] {UPDATE_OFFER_IMPEX_COLUMN, OFFER_ID_IMPEX_COLUMN,
      TRIGGER_PROMOTIONS_IMPEX_COLUMN, REWARD_PROMOTIONS_IMPEX_COLUMN};
  public static final String PROMOTION_MESSAGES_SEPARATOR = "mirakl.promotions.messages.separator";
  public static final String SOLR_INDEX_PROMOTION_ID_SEPARATOR = "###";
  public static final String SOLR_MIRAKL_PROMOTIONS_KEY = "marketplacePromotions";
}
