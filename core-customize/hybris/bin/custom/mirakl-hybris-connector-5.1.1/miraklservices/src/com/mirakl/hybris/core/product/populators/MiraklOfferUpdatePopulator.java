package com.mirakl.hybris.core.product.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.offer.MiraklOffer;
import com.mirakl.client.mmp.domain.offer.price.MiraklOfferPricing;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklOfferUpdatePopulator implements Populator<MiraklOffer, OfferModel> {
  protected OfferService offerService;
  protected Converter<MiraklOfferPricing, OfferPricingData> offerPricingToDataConverter;

  @Override
  public void populate(MiraklOffer miraklOffer, OfferModel offerModel) throws ConversionException {
    validateParameterNotNullStandardMessage("miraklOffer", miraklOffer);
    validateParameterNotNullStandardMessage("offerModel", offerModel);

    offerModel.setQuantity(miraklOffer.getQuantity());
    offerModel.setPrice(miraklOffer.getPrice());
    offerModel.setPriceAdditionalInfo(miraklOffer.getPriceAdditionalInfo());
    offerModel.setTotalPrice(miraklOffer.getTotalPrice());
    offerService.storeAllOfferPricings(offerPricingToDataConverter.convertAll(miraklOffer.getAllPrices()), offerModel);
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setOfferPricingToDataConverter(Converter<MiraklOfferPricing, OfferPricingData> offerPricingToDataConverter) {
    this.offerPricingToDataConverter = offerPricingToDataConverter;
  }
}
