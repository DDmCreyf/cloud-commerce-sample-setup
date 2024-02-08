package com.mirakl.hybris.facades.product.converters.populator;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;

public class OfferDataPopulator implements Populator<OfferModel, OfferData> {

  protected EnumerationService enumerationService;
  protected PriceDataFactory priceDataFactory;
  protected OfferCodeGenerationStrategy offerCodeGenerationStrategy;
  protected MiraklPriceService miraklPriceService;
  protected OfferDataVolumePricePopulator offerDataVolumePricePopulator;

  @Override
  public void populate(OfferModel offerModel, OfferData offerData) throws ConversionException {
    populatePrices(offerModel, offerData);
    if (offerModel.getMinShippingPrice() != null) {
      offerData.setMinShippingPrice(priceDataFactory.create(PriceDataType.BUY, offerModel.getMinShippingPrice(), offerModel.getCurrency()));
    }
    if (offerModel.getMinShippingPriceAdditional() != null) {
      offerData.setMinShippingPriceAdditional(
          priceDataFactory.create(PriceDataType.BUY, offerModel.getMinShippingPriceAdditional(), offerModel.getCurrency()));
    }
    offerData.setAvailableEndDate(offerModel.getAvailableEndDate());
    offerData.setAvailableStartDate(offerModel.getAvailableStartDate());
    offerData.setCode(offerCodeGenerationStrategy.generateCode(offerModel.getId()));
    offerData.setDescription(offerModel.getDescription());
    offerData.setId(offerModel.getId());
    offerData.setLeadTimeToShip(offerModel.getLeadTimeToShip());
    offerData.setPriceAdditionalInfo(offerModel.getPriceAdditionalInfo());
    offerData.setProductCode(offerModel.getProductCode());
    offerData.setQuantity(offerModel.getQuantity());
    offerData.setShopCode(offerModel.getShop().getId());
    offerData.setShopName(offerModel.getShop().getName());
    offerData.setShopGrade(offerModel.getShop().getGrade());
    offerData.setShopEvaluationCount(offerModel.getShop().getEvaluationCount());
    offerData.setState(enumerationService.getEnumerationName(offerModel.getState()));
    offerData.setStateCode(offerModel.getState().getCode());
    offerData.setMaxOrderQuantity(offerModel.getMaxOrderQuantity());
    offerData.setMinOrderQuantity(offerModel.getMinOrderQuantity());
    offerData.setPackageQuantity(offerModel.getPackageQuantity());
  }

  protected void populatePrices(OfferModel offerModel, OfferData offerData) {
    OfferPricingData offerPricing = miraklPriceService.getOfferPricing(offerModel);
    if (offerPricing == null) {
      return;
    }

    BigDecimal discountPrice = offerPricing.getUnitDiscountPrice();
    if (discountPrice != null) {
      offerData.setDiscountPrice(priceDataFactory.create(PriceDataType.BUY, discountPrice, offerModel.getCurrency()));
    }
    BigDecimal originPrice = offerPricing.getUnitOriginPrice();
    if (originPrice != null) {
      offerData.setOriginPrice(priceDataFactory.create(PriceDataType.BUY, originPrice, offerModel.getCurrency()));
    }
    offerData.setPrice(priceDataFactory.create(PriceDataType.BUY, offerPricing.getPrice(), offerModel.getCurrency()));
    offerData.setTotalPrice(
        priceDataFactory.create(PriceDataType.BUY, miraklPriceService.getOfferTotalPrice(offerModel, offerPricing), offerModel.getCurrency()));
    offerData.setDiscountEndDate(offerPricing.getDiscountEndDate());
    offerData.setDiscountStartDate(offerPricing.getDiscountStartDate());
    populateVolumePrices(offerModel, offerPricing, offerData);
  }

  protected void populateVolumePrices(OfferModel offerModel, OfferPricingData offerPricing, OfferData offerData) {
    offerDataVolumePricePopulator.populate(Pair.of(offerPricing, offerModel.getCurrency().getIsocode()), offerData);
  }

  @Required
  public void setVolumePricePopulator(final OfferDataVolumePricePopulator offerDataVolumePricePopulator) {
    this.offerDataVolumePricePopulator = offerDataVolumePricePopulator;
  }

  @Required
  public void setEnumerationService(EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }

  @Required
  public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
    this.priceDataFactory = priceDataFactory;
  }

  @Required
  public void setOfferCodeGenerationStrategy(OfferCodeGenerationStrategy offerCodeGenerationStrategy) {
    this.offerCodeGenerationStrategy = offerCodeGenerationStrategy;
  }

  @Required
  public void setMiraklPriceService(MiraklPriceService miraklPriceService) {
    this.miraklPriceService = miraklPriceService;
  }
}
