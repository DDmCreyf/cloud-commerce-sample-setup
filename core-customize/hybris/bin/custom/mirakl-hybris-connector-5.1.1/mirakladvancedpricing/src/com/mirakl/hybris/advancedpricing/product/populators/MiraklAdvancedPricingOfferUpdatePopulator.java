package com.mirakl.hybris.advancedpricing.product.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.mirakl.client.mmp.domain.offer.MiraklOffer;
import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * This bean alias overrides the populator of miraklservices
 * {@link com.mirakl.hybris.core.product.populators.MiraklOfferUpdatePopulator}. <br/>
 * As we can't update advanced prices synchronously, we only update the stock.
 */
public class MiraklAdvancedPricingOfferUpdatePopulator implements Populator<MiraklOffer, OfferModel> {

  @Override
  public void populate(MiraklOffer miraklOffer, OfferModel offerModel) throws ConversionException {
    validateParameterNotNullStandardMessage("miraklOffer", miraklOffer);
    validateParameterNotNullStandardMessage("offerModel", offerModel);

    offerModel.setQuantity(miraklOffer.getQuantity());
  }

}
