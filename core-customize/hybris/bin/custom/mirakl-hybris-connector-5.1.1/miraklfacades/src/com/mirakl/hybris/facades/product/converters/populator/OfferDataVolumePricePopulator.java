package com.mirakl.hybris.facades.product.converters.populator;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Populates OfferData volume prices from a source of volume prices
 */
public class OfferDataVolumePricePopulator implements Populator<Pair<OfferPricingData, String>, OfferData> {
  protected Comparator<PriceData> volumePriceComparator;
  protected PriceDataFactory priceDataFactory;

  @Override
  public void populate(final Pair<OfferPricingData, String> offerPricingDataWithCurrencyIso, final OfferData offerData) throws ConversionException {
    final OfferPricingData offerPricing = offerPricingDataWithCurrencyIso.getLeft();
    final String offerCurrencyIsoCode = offerPricingDataWithCurrencyIso.getRight();
    List<OfferVolumePricingData> volumePrices = offerPricing.getVolumePrices();
    if (volumePrices.size() < 2) {
      return;
    }

    List<PriceData> volumePricesDatas = new ArrayList<>();
    List<PriceData> originVolumePricesDatas = new ArrayList<>();
    for (OfferVolumePricingData volumePrice : volumePrices) {
      PriceData price = priceDataFactory.create(PriceDataType.BUY, volumePrice.getPrice(), offerCurrencyIsoCode);
      price.setMinQuantity(volumePrice.getQuantityThreshold().longValue());
      volumePricesDatas.add(price);

      BigDecimal originPriceValue = volumePrice.getUnitOriginPrice() == null ? offerPricing.getUnitOriginPrice() : volumePrice.getUnitOriginPrice();
      PriceData originPrice = priceDataFactory.create(PriceDataType.BUY, originPriceValue, offerCurrencyIsoCode);
      originPrice.setMinQuantity(volumePrice.getQuantityThreshold().longValue());
      originVolumePricesDatas.add(originPrice);
    }

    sortAndPopulateMaxQty(volumePricesDatas);
    sortAndPopulateMaxQty(originVolumePricesDatas);
    offerData.setVolumePrices(volumePricesDatas);
    offerData.setVolumeOriginPrices(originVolumePricesDatas);
  }

  protected void sortAndPopulateMaxQty(List<PriceData> volumePricesDatas) {
    volumePricesDatas.sort(volumePriceComparator);
    for (int i = 0; i < volumePricesDatas.size() - 1; i++) {
      volumePricesDatas.get(i).setMaxQuantity(volumePricesDatas.get(i + 1).getMinQuantity() - 1);

    }
  }

  @Required
  public void setVolumePriceComparator(final Comparator<PriceData> volumePriceComparator) {
    this.volumePriceComparator = volumePriceComparator;
  }

  @Required
  public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
    this.priceDataFactory = priceDataFactory;
  }

}


