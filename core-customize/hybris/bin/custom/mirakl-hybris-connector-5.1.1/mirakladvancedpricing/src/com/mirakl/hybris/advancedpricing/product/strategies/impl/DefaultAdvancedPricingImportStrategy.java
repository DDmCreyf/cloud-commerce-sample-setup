package com.mirakl.hybris.advancedpricing.product.strategies.impl;

import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOffer;
import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOfferPrice;
import com.mirakl.hybris.advancedpricing.product.services.OfferPricingService;
import com.mirakl.hybris.advancedpricing.product.utils.OfferPricingKey;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.model.OfferPricingModel;
import com.mirakl.hybris.core.product.decorators.MiraklAsyncOfferPriceDecorator;
import com.mirakl.hybris.core.product.strategies.AsyncOfferImportStrategy;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.core.util.strategies.MiraklPriceImportCompatibilityStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

import static com.mirakl.hybris.advancedpricing.product.utils.OfferPricingKey.newOfferPricingKey;
import static java.lang.String.format;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class DefaultAdvancedPricingImportStrategy implements AsyncOfferImportStrategy {

  // Log
  private static final Logger LOG = Logger.getLogger(DefaultAdvancedPricingImportStrategy.class);

  // Beans
  protected ModelService modelService;
  protected OfferPricingService offerPricingService;
  protected JsonMarshallingService jsonMarshallingService;
  protected Converter<Pair<MiraklAsyncOfferPriceDecorator, String>, OfferPricingData> asyncOfferPriceDetailsConverter;
  protected Populator<Pair<OfferPricingKey, String>, OfferPricingModel> asyncOfferPricingPopulator;
  protected MiraklPriceImportCompatibilityStrategy priceImportCompatibilityStrategy;

  @Override
  public void importMiraklOffer(MiraklAsyncExportOffer miraklOffer) {

    if (isInactive(miraklOffer)) {
      offerPricingService.deleteByOfferId(miraklOffer.getOfferId());
      return;
    }

    List<OfferPricingModel> existingOfferPricing = offerPricingService.findByOfferId(miraklOffer.getOfferId());
    HashMap<OfferPricingKey, OfferPricingModel> existingOfferPricingByKey = new HashMap<>();
    List<OfferPricingModel> duplicatedOfferPricing = new ArrayList<>();
    for (OfferPricingModel offerPricing : existingOfferPricing) {
      OfferPricingKey key = new OfferPricingKey(offerPricing);
      OfferPricingModel duplicate = existingOfferPricingByKey.put(key, offerPricing);
      if (duplicate != null) {
        duplicatedOfferPricing.add(duplicate);
      }
    }

    for (MiraklAsyncExportOfferPrice miraklPrice : miraklOffer.getPrices()) {
      MiraklAsyncOfferPriceDecorator priceEntry = new MiraklAsyncOfferPriceDecorator(miraklPrice);

      if (!priceImportCompatibilityStrategy.supports(priceEntry)) {
        continue;
      }

      for (String channel : priceEntry.getChannelCodes()) {
        OfferPricingData priceDetails = asyncOfferPriceDetailsConverter.convert(Pair.of(priceEntry, channel));
        String priceDetailsJSON = jsonMarshallingService.toJson(priceDetails);

        for (OfferPricingKey key : getOfferPricingKeys(miraklOffer.getOfferId(), channel, priceEntry)) {
          OfferPricingModel offerPricing = getOrCreateOfferPricing(key, existingOfferPricingByKey);
          importOfferPricing(key, priceDetailsJSON, offerPricing);
          existingOfferPricingByKey.remove(key);
        }
      }
    }

    eraseIrrelevantPricingEntries(miraklOffer.getOfferId(), duplicatedOfferPricing, existingOfferPricingByKey.values());
  }

  @Override
  public void invalidateHybrisOffer(OfferModel miraklOffer) {
    offerPricingService.deleteByOfferId(miraklOffer.getId());
  }

  protected Set<OfferPricingKey> getOfferPricingKeys(String offerId, String channel, MiraklAsyncOfferPriceDecorator priceEntry) {
    Set<OfferPricingKey> keys = new HashSet<>();
    if (priceEntry.isDefault()) {
      keys.add(newOfferPricingKey(offerId));
    } else if (!priceEntry.isCustomerSpecific()) {
      keys.add(new OfferPricingKey(offerId, channel, null, null, priceEntry));
    } else {
      for (String cGroupId : priceEntry.getCustomerGroupIds()) {
        keys.add(new OfferPricingKey(offerId, channel, cGroupId, null, priceEntry));
      }
      for (String cOrgId : priceEntry.getCustomerOrganizationIds()) {
        keys.add(new OfferPricingKey(offerId, channel, null, cOrgId, priceEntry));
      }
    }
    return keys;
  }

  protected void importOfferPricing(OfferPricingKey key, String priceDetailsJSON, OfferPricingModel offerPricing) {
    if (!Objects.equals(offerPricing.getPriceDetailsJSON(), priceDetailsJSON)) {
      asyncOfferPricingPopulator.populate(Pair.of(key, priceDetailsJSON), offerPricing);
      modelService.save(offerPricing);
      if (LOG.isDebugEnabled()) {
        LOG.debug(format("Upserted offer pricing [key=%s]", key));
      }
    } else if (LOG.isDebugEnabled()) {
      LOG.debug(format("Ignored unchanged offer pricing [key=%s]", key));
    }
  }

  protected void eraseIrrelevantPricingEntries( //
      String offerId, //
      Collection<OfferPricingModel> duplicatedOfferPricing, //
      Collection<OfferPricingModel> outdatedOfferPricing //
      ) {
    if (isNotEmpty(outdatedOfferPricing)) {
      modelService.removeAll(outdatedOfferPricing);
      if (LOG.isDebugEnabled()) {
        LOG.debug(format("Removed %s outdated offer pricing(s) [offerId=%s]", outdatedOfferPricing.size(), offerId));
      }
    }
    if (isNotEmpty(duplicatedOfferPricing)) {
      modelService.removeAll(duplicatedOfferPricing);
      LOG.warn(format("Cleaned %s duplicated offer pricing(s) [offerId=%s]", duplicatedOfferPricing.size(), offerId));
    }
  }

  protected OfferPricingModel getOrCreateOfferPricing(OfferPricingKey key,
      Map<OfferPricingKey, OfferPricingModel> keyToOfferPricing) {
    OfferPricingModel hybrisPriceEntry = keyToOfferPricing.get(key);
    return hybrisPriceEntry != null ? hybrisPriceEntry : modelService.create(OfferPricingModel.class);
  }

  protected boolean isInactive(MiraklAsyncExportOffer miraklOffer) {
    return miraklOffer.getDeleted() || !miraklOffer.getActive();
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setOfferPricingService(OfferPricingService offerPricingService) {
    this.offerPricingService = offerPricingService;
  }

  @Required
  public void setAsyncOfferPricingPopulator(
      Populator<Pair<OfferPricingKey, String>, OfferPricingModel> asyncOfferPricingPopulator) {
    this.asyncOfferPricingPopulator = asyncOfferPricingPopulator;
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
