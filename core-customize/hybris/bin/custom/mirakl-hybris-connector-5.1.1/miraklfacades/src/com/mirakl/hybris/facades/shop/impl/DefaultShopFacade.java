package com.mirakl.hybris.facades.shop.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.evaluation.MiraklEvaluation;
import com.mirakl.client.mmp.domain.evaluation.MiraklEvaluations;
import com.mirakl.hybris.beans.EvaluationData;
import com.mirakl.hybris.beans.EvaluationPageData;
import com.mirakl.hybris.beans.ShopData;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.services.ShopService;
import com.mirakl.hybris.core.util.PaginationUtils;
import com.mirakl.hybris.facades.shop.ShopFacade;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class DefaultShopFacade implements ShopFacade {

  protected Converter<ShopModel, ShopData> shopDataConverter;
  protected Converter<MiraklEvaluation, EvaluationData> evaluationDataConverter;
  protected ShopService shopService;

  @Override
  public ShopData getShopForId(String id) {
    validateParameterNotNullStandardMessage("id", id);
    ShopModel shop = shopService.getShopForId(id);
    if (shop == null) {
      throw new UnknownIdentifierException(format("Impossible to find the shop [%s]", id));
    }
    return shopDataConverter.convert(shop);
  }

  @Override
  public EvaluationPageData getShopEvaluationPage(String id, PageableData pageableData) {
    validateParameterNotNullStandardMessage("id", id);
    MiraklEvaluations miraklEvaluations = shopService.getEvaluations(id, pageableData);

    EvaluationPageData evaluationPageData = new EvaluationPageData();
    evaluationPageData.setEvaluations(evaluationDataConverter.convertAll(miraklEvaluations.getEvaluations()));
    evaluationPageData
    .setEvaluationPageCount(PaginationUtils.getNumberOfPages(miraklEvaluations.getTotalCount(), pageableData.getPageSize()));
    return evaluationPageData;
  }

  @Required
  public void setShopDataConverter(Converter<ShopModel, ShopData> shopDataConverter) {
    this.shopDataConverter = shopDataConverter;
  }

  @Required
  public void setShopService(ShopService shopService) {
    this.shopService = shopService;
  }

  @Required
  public void setEvaluationDataConverter(Converter<MiraklEvaluation, EvaluationData> evaluationDataConverter) {
    this.evaluationDataConverter = evaluationDataConverter;
  }
}
