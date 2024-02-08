package com.mirakl.hybris.facades.product.converters.populator;

import com.mirakl.client.mmp.domain.offer.price.MiraklOfferPricing;
import com.mirakl.client.mmp.domain.product.MiraklOfferOnProduct;
import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.product.services.MiraklRealtimePriceService;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;

/**
 * Populates an OfferData from a MiraklOfferOnProduct
 */
public class MiraklRealtimeOfferDataPopulator implements Populator<Pair<MiraklOfferOnProduct, String>, OfferData> {

  protected EnumerationService enumerationService;
  protected PriceDataFactory priceDataFactory;
  protected OfferCodeGenerationStrategy offerCodeGenerationStrategy;
  protected Converter<MiraklOfferPricing, OfferPricingData> offerPricingDataConverter;
  protected MiraklRealtimePriceService realtimePriceService;
  protected OfferDataVolumePricePopulator offerDataVolumePricePopulator;

  public void populate(final Pair<MiraklOfferOnProduct, String> offerSourceWithProductCode, OfferData offerData) throws ConversionException {
    offerData.setProductCode(offerSourceWithProductCode.getRight());

    MiraklOfferOnProduct offerSource = offerSourceWithProductCode.getLeft();
    populatePrices(offerSource, offerData);
    if (offerSource.getMinShipping() != null) {
      offerData.setMinShippingPrice(
          priceDataFactory.create(PriceDataType.BUY, offerSource.getMinShipping().getPrice(), offerSource.getCurrencyIsoCode().name()));

      if (offerSource.getMinShipping().getPriceAdditional() != null)
        offerData.setMinShippingPriceAdditional(
            priceDataFactory.create(PriceDataType.BUY, offerSource.getMinShipping().getPriceAdditional(), offerSource.getCurrencyIsoCode().name()));
    }
    offerData.setCode(offerCodeGenerationStrategy.generateCode(offerSource.getId()));
    offerData.setDescription(offerSource.getDescription());
    offerData.setId(offerSource.getId());
    offerData.setLeadTimeToShip(offerSource.getLeadtimeToShip());
    offerData.setPriceAdditionalInfo(offerSource.getPriceAdditionalInfo());
    offerData.setQuantity(offerSource.getQuantity());
    offerData.setShopCode(offerSource.getShopId());
    offerData.setShopName(offerSource.getShopName());
    offerData.setShopGrade(offerSource.getShopGrade() == null ? null : offerSource.getShopGrade().doubleValue());
    offerData.setShopEvaluationCount(offerSource.getNbEvaluation() == null ? 0 : offerSource.getNbEvaluation().intValue());
    offerData.setState(enumerationService.getEnumerationName(OfferState.valueOf(offerSource.getStateCode())));
    offerData.setStateCode(offerSource.getStateCode());
    offerData.setMaxOrderQuantity(offerSource.getMaxOrderQuantity());
    offerData.setMinOrderQuantity(offerSource.getMinOrderQuantity());
    offerData.setPackageQuantity(offerSource.getPackageQuantity());
  }

  protected void populatePrices(final MiraklOfferOnProduct offerSource, OfferData offerData) {

    String offerCurrencyIsoCode = offerSource.getCurrencyIsoCode().name();
    OfferPricingData offerPricing = offerPricingDataConverter.convert(offerSource.getApplicablePricing());

    BigDecimal discountPrice = offerPricing.getUnitDiscountPrice();
    if (discountPrice != null) {
      offerData.setDiscountPrice(priceDataFactory.create(PriceDataType.BUY, discountPrice, offerCurrencyIsoCode));
    }
    BigDecimal originPrice = offerPricing.getUnitOriginPrice();
    if (originPrice != null) {
      offerData.setOriginPrice(priceDataFactory.create(PriceDataType.BUY, originPrice, offerCurrencyIsoCode));
    }
    offerData.setPrice(priceDataFactory.create(PriceDataType.BUY, offerPricing.getPrice(), offerCurrencyIsoCode));
    offerData.setTotalPrice(priceDataFactory.create(PriceDataType.BUY, realtimePriceService.getOfferTotalPrice(offerSource), offerCurrencyIsoCode));
    offerData.setDiscountEndDate(offerPricing.getDiscountEndDate());
    offerData.setDiscountStartDate(offerPricing.getDiscountStartDate());
    populateVolumePrices(offerPricing, offerCurrencyIsoCode, offerData);
  }

  protected void populateVolumePrices(final OfferPricingData offerPricing, final String offerCurrencyIsoCode, OfferData offerData) {
    offerDataVolumePricePopulator.populate(Pair.of(offerPricing, offerCurrencyIsoCode), offerData);
  }

  @Required
  public void setOfferPricingDataConverter(Converter<MiraklOfferPricing, OfferPricingData> offerPricingDataConverter) {
    this.offerPricingDataConverter = offerPricingDataConverter;
  }

  @Required
  public void setOfferDataVolumePricePopulator(final OfferDataVolumePricePopulator offerDataVolumePricePopulator) {
    this.offerDataVolumePricePopulator = offerDataVolumePricePopulator;
  }

  @Required
  public void setRealtimePriceService(MiraklRealtimePriceService realtimePriceService) {
    this.realtimePriceService = realtimePriceService;
  }

  @Required
  public void setEnumerationService(final EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }

  @Required
  public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
    this.priceDataFactory = priceDataFactory;
  }

  @Required
  public void setOfferCodeGenerationStrategy(final OfferCodeGenerationStrategy offerCodeGenerationStrategy) {
    this.offerCodeGenerationStrategy = offerCodeGenerationStrategy;
  }
}
