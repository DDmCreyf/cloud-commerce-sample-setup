package com.mirakl.hybris.core.product.populators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOfferVolumePrice;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.product.decorators.MiraklAsyncOfferPriceDecorator;

import de.hybris.platform.converters.Populator;

public class AsyncOfferPriceDetailsPopulator implements Populator<Pair<MiraklAsyncOfferPriceDecorator, String>, OfferPricingData> {

  @Override
  public void populate(Pair<MiraklAsyncOfferPriceDecorator, String> priceAndChannel, OfferPricingData pricingData) {
    MiraklAsyncOfferPriceDecorator priceEntry = priceAndChannel.getLeft();
    pricingData.setDiscountEndDate(priceEntry.getDiscountEndDate());
    pricingData.setDiscountStartDate(priceEntry.getDiscountStartDate());
    if (priceEntry.hasActiveDiscount()) {
      pricingData.setPrice(priceEntry.getDiscountPrice());
    } else {
      pricingData.setPrice(priceEntry.getOriginPrice());
    }
    pricingData.setUnitDiscountPrice(priceEntry.getDiscountPrice());
    pricingData.setUnitOriginPrice(priceEntry.getOriginPrice());
    pricingData.setChannelCode(priceAndChannel.getRight());
    pricingData.setVolumePrices(toVolumePricingList(priceEntry));
  }

  protected List<OfferVolumePricingData> toVolumePricingList(MiraklAsyncOfferPriceDecorator priceEntry) {
    List<OfferVolumePricingData> volumePricingList = new ArrayList<>();
    for (MiraklAsyncExportOfferVolumePrice volumePrice : priceEntry.getVolumePrices()) {
      OfferVolumePricingData volumePricing = new OfferVolumePricingData();
      if (priceEntry.hasActiveDiscount()) {
        volumePricing.setPrice(volumePrice.getUnitDiscountPrice());
      } else {
        volumePricing.setPrice(volumePrice.getUnitOriginPrice());
      }
      volumePricing.setUnitDiscountPrice(volumePrice.getUnitDiscountPrice());
      volumePricing.setUnitOriginPrice(volumePrice.getUnitOriginPrice());
      volumePricing.setQuantityThreshold(volumePrice.getQuantityThreshold());
      volumePricingList.add(volumePricing);
    }
    return volumePricingList;
  }

}
