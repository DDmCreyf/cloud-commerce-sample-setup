package com.mirakl.hybris.facades.order.impl;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.request.order.evaluation.MiraklCreateOrderEvaluation;
import com.mirakl.hybris.beans.EvaluationData;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.facades.order.MarketplaceConsignmentFacade;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;


public class DefaultMarketplaceConsignmentFacade implements MarketplaceConsignmentFacade {

  protected MarketplaceConsignmentService consignmentService;
  protected Converter<EvaluationData, MiraklCreateOrderEvaluation> miraklCreateOrderEvaluationConverter;
  protected Converter<MarketplaceConsignmentModel, ConsignmentData> consignmentConverter;
  protected Converter<ProductModel, ProductData> productDataConverter;

  @Override
  public EvaluationData postEvaluation(String consignmentCode, EvaluationData evaluationData, UserModel user) {
    consignmentService.postEvaluation(consignmentCode, miraklCreateOrderEvaluationConverter.convert(evaluationData), user);
    return evaluationData;
  }

  @Override
  public ProductData getProductForConsignmentEntry(String consignmentEntryCode) {
    ProductModel product = consignmentService.getProductForConsignmentEntry(consignmentEntryCode);
    return productDataConverter.convert(product);
  }

  @Override
  public ConsignmentData confirmConsignmentReceptionForCode(String code, UserModel currentCustomer){
    return consignmentConverter.convert(consignmentService.confirmConsignmentReceptionForCode(code, currentCustomer));
  }

  @Required
  public void setConsignmentService(MarketplaceConsignmentService consignmentService) {
    this.consignmentService = consignmentService;
  }

  @Required
  public void setMiraklCreateOrderEvaluationConverter(
      Converter<EvaluationData, MiraklCreateOrderEvaluation> miraklCreateOrderEvaluationConverter) {
    this.miraklCreateOrderEvaluationConverter = miraklCreateOrderEvaluationConverter;
  }

  @Required
  public void setConsignmentConverter(
      Converter<MarketplaceConsignmentModel, ConsignmentData> consignmentConverter) {
    this.consignmentConverter = consignmentConverter;
  }

  @Required
  public void setProductDataConverter(Converter<ProductModel, ProductData> productDataConverter) {
    this.productDataConverter = productDataConverter;
  }
}
