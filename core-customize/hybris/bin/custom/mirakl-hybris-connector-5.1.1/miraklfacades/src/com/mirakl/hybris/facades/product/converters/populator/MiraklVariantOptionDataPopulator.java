package com.mirakl.hybris.facades.product.converters.populator;

import static de.hybris.platform.basecommerce.enums.StockLevelStatus.INSTOCK;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.product.services.MiraklProductService;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.variants.model.VariantProductModel;

public class MiraklVariantOptionDataPopulator implements Populator<VariantProductModel, VariantOptionData> {

  protected OfferService offerService;
  protected MiraklProductService miraklProductService;
  protected MiraklPriceService miraklPriceService;
  protected PriceDataFactory priceDataFactory;

  @Override
  public void populate(final VariantProductModel source, final VariantOptionData target) {
    if (!miraklProductService.isSellableByOperator(source)) {

      List<OfferModel> productOffers = offerService.getSortedOffersForProductCode(target.getCode());
      if (!isEmpty(productOffers)) {
        OfferModel topOffer = productOffers.get(0);
        getStock(target).setStockLevelStatus(INSTOCK);
        getStock(target).setStockLevel(topOffer.getQuantity().longValue());
        OfferPricingData offerPricing = miraklPriceService.getOfferPricing(topOffer);
        if (offerPricing != null) {
          target.setPriceData(priceDataFactory.create(PriceDataType.BUY, offerPricing.getPrice(), topOffer.getCurrency()));
        }
      }
    }
  }

  protected StockData getStock(final VariantOptionData target) {
    if (target.getStock() == null) {
      target.setStock(new StockData());
    }
    return target.getStock();
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setMiraklProductService(MiraklProductService miraklProductService) {
    this.miraklProductService = miraklProductService;
  }

  @Required
  public void setMiraklPriceService(MiraklPriceService miraklPriceService) {
    this.miraklPriceService = miraklPriceService;
  }

  @Required
  public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
    this.priceDataFactory = priceDataFactory;
  }
}
