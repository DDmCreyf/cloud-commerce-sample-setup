package com.mirakl.hybris.core.shop.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.evaluation.MiraklEvaluations;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.shop.MiraklGetShopEvaluationsRequest;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.daos.ShopDao;
import com.mirakl.hybris.core.shop.services.ShopService;
import com.mirakl.hybris.core.util.PaginationUtils;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.model.ModelService;
import shaded.com.fasterxml.jackson.core.type.TypeReference;

public class DefaultShopService implements ShopService {

  protected ShopDao shopDao;
  protected MiraklMarketplacePlatformFrontApi miraklApi;
  protected JsonMarshallingService jsonMarshallingService;
  protected ModelService modelService;

  @Override
  public ShopModel getShopForId(String id) {
    return shopDao.findShopById(id);
  }

  @Override
  public MiraklEvaluations getEvaluations(String id, PageableData pageableData) {
    validateParameterNotNull(pageableData, "PageableData must not be null");
    if (pageableData.getCurrentPage() < 1) {
      throw new IllegalArgumentException(
          format("Illegal evaluation page number: %d for shop [%s]", pageableData.getCurrentPage(), id));
    }

    return miraklApi.getShopEvaluations(PaginationUtils.applyMiraklFullPagination(new MiraklGetShopEvaluationsRequest(id), true,
        pageableData.getPageSize(), pageableData.getPageSize() * (pageableData.getCurrentPage() - 1)));
  }

  @Override
  public void storeShopCustomFields(List<MiraklAdditionalFieldValue> customFields, ShopModel shop) {
    validateParameterNotNullStandardMessage("shop", shop);
    if (isNotEmpty(customFields)) {
      shop.setCustomFieldsJSON(
          jsonMarshallingService.toJson(customFields, new TypeReference<List<MiraklAdditionalFieldValue>>() {}));
      modelService.save(shop);
    }
  }

  @Override
  public List<MiraklAdditionalFieldValue> loadShopCustomFields(ShopModel shop) {
    validateParameterNotNullStandardMessage("shop", shop);
    return jsonMarshallingService.fromJson(shop.getCustomFieldsJSON(), new TypeReference<List<MiraklAdditionalFieldValue>>() {});
  }

  @Required
  public void setShopDao(ShopDao shopDao) {
    this.shopDao = shopDao;
  }

  @Required
  public void setMiraklApi(MiraklMarketplacePlatformFrontApi miraklApi) {
    this.miraklApi = miraklApi;
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

}
