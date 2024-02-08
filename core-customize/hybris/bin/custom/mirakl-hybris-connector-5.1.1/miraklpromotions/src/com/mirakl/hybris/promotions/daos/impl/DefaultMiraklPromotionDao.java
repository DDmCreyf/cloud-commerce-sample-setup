package com.mirakl.hybris.promotions.daos.impl;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.HashMap;
import java.util.List;

import com.mirakl.hybris.promotions.daos.MiraklPromotionDao;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

public class DefaultMiraklPromotionDao extends DefaultGenericDao<MiraklPromotionModel> implements MiraklPromotionDao {

  public DefaultMiraklPromotionDao() {
    super(MiraklPromotionModel._TYPECODE);
  }

  @Override
  public MiraklPromotionModel findMiraklPromotion(String shopId, String internalId) {
    HashMap<String, Object> params = new HashMap<>();
    params.put(MiraklPromotionModel.INTERNALID, internalId);
    params.put(MiraklPromotionModel.SHOPID, shopId);
    List<MiraklPromotionModel> results = find(params);

    if (isNotEmpty(results) && results.size() > 1) {
      throw new AmbiguousIdentifierException(
          format("Multiple Mirakl Promotions found for internalId [%s] and shopId [%s]", internalId, shopId));
    }

    return isEmpty(results) ? null : results.get(0);
  }

}
