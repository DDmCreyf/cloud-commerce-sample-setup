package com.mirakl.hybris.core.product.populators;

import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOffer;
import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOfferPrice;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.decorators.MiraklAsyncOfferPriceDecorator;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.core.util.strategies.MiraklPriceImportCompatibilityStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class AsyncOfferPricePopulator implements Populator<MiraklAsyncExportOffer, OfferModel> {

  protected JsonMarshallingService jsonMarshallingService;
  protected Converter<Pair<MiraklAsyncOfferPriceDecorator, String>, OfferPricingData> asyncOfferPriceDetailsConverter;
  protected MiraklPriceImportCompatibilityStrategy priceImportCompatibilityStrategy;

  @Override
  public void populate(MiraklAsyncExportOffer miraklOffer, OfferModel hybrisOffer) throws ConversionException {
    validateParameterNotNullStandardMessage("miraklOffer", miraklOffer);
    validateParameterNotNullStandardMessage("hybrisOffer", hybrisOffer);

    List<OfferPricingData> offerPricingList = new ArrayList<>();
    for (MiraklAsyncExportOfferPrice miraklPrice : miraklOffer.getPrices()) {
      MiraklAsyncOfferPriceDecorator priceEntry = new MiraklAsyncOfferPriceDecorator(miraklPrice);

      if (priceEntry.isDefault()) {
        BigDecimal price = computePrice(priceEntry);
        hybrisOffer.setPrice(price);
        hybrisOffer.setTotalPrice(price.add(miraklOffer.getMinShippingPrice()));
        hybrisOffer.setDiscountEndDate(priceEntry.getDiscountEndDate());
        hybrisOffer.setDiscountStartDate(priceEntry.getDiscountStartDate());
        hybrisOffer.setDiscountPrice(priceEntry.getDiscountPrice());
        hybrisOffer.setOriginPrice(priceEntry.getOriginPrice());
      }

      if (priceImportCompatibilityStrategy.supports(priceEntry)) {
        populateOfferPricingList(priceEntry, offerPricingList);
      }
    }

    hybrisOffer.setAllOfferPricingsJSON(jsonMarshallingService.toJson(offerPricingList));
  }

  protected BigDecimal computePrice(MiraklAsyncOfferPriceDecorator priceEntry) {
    return priceEntry.hasActiveDiscount() ? priceEntry.getDiscountPrice() : priceEntry.getOriginPrice();
  }

  protected void populateOfferPricingList(MiraklAsyncOfferPriceDecorator priceEntry, List<OfferPricingData> pricingList) {
    for (String channelCode : priceEntry.getChannelCodes()) {
      pricingList.add(asyncOfferPriceDetailsConverter.convert(Pair.of(priceEntry, channelCode)));
    }
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

  @Required
  public void setAsyncOfferPriceDetailsConverter(
      Converter<Pair<MiraklAsyncOfferPriceDecorator, String>, OfferPricingData> asyncOfferPriceDetailsConverter) {
    this.asyncOfferPriceDetailsConverter = asyncOfferPriceDetailsConverter;
  }

  @Required
  public void setPriceImportCompatibilityStrategy(MiraklPriceImportCompatibilityStrategy priceImportCompatibilityStrategy) {
    this.priceImportCompatibilityStrategy = priceImportCompatibilityStrategy;
  }
}
