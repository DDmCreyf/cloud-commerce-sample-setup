package com.mirakl.hybris.core.product.strategies.impl;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOffer;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.daos.OfferDao;
import com.mirakl.hybris.core.product.strategies.AsyncOfferImportStrategy;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultAsyncOfferImportStrategy implements AsyncOfferImportStrategy {

  // Log
  private static final Logger LOG = Logger.getLogger(DefaultAsyncOfferImportStrategy.class);

  // Beans
  protected ModelService modelService;
  protected OfferDao offerDao;
  protected Converter<MiraklAsyncExportOffer, OfferModel> asyncOfferCoreConverter;

  @Override
  public void importMiraklOffer(MiraklAsyncExportOffer miraklOffer) {

    OfferModel hybrisOffer = getOrCreateHybrisOffer(miraklOffer);

    if (isOfferUpdate(hybrisOffer) && isInactive(miraklOffer)) {
      invalidateHybrisOffer(hybrisOffer);

      if (LOG.isDebugEnabled()) {
        LOG.debug(format("Invalidated offer [offerId=%s]", miraklOffer.getOfferId()));
      }
    } else {
      importMiraklOffer(miraklOffer, hybrisOffer);

      if (LOG.isDebugEnabled()) {
        LOG.debug(format("Imported offer [offerId=%s]", miraklOffer.getOfferId()));
      }
    }

  }

  @Override
  public void invalidateHybrisOffer(OfferModel offer) {
    offer.setDeleted(true);
    offer.setActive(false);
    modelService.save(offer);
  }

  protected void importMiraklOffer(MiraklAsyncExportOffer miraklOffer, OfferModel hybrisOffer) {
    asyncOfferCoreConverter.convert(miraklOffer, hybrisOffer);
    modelService.save(hybrisOffer);
  }

  protected boolean isInactive(MiraklAsyncExportOffer miraklOffer) {
    return miraklOffer.getDeleted() || !miraklOffer.getActive();
  }

  protected boolean isOfferUpdate(OfferModel hybrisOffer) {
    return !isBlank(hybrisOffer.getId());
  }

  protected OfferModel getOrCreateHybrisOffer(MiraklAsyncExportOffer miraklOffer) {
    OfferModel hybrisOffer = offerDao.findOfferById(miraklOffer.getOfferId());
    return hybrisOffer != null ? hybrisOffer : modelService.create(OfferModel.class);
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setOfferDao(OfferDao offerDao) {
    this.offerDao = offerDao;
  }

  @Required
  public void setAsyncOfferCoreConverter(Converter<MiraklAsyncExportOffer, OfferModel> asyncOfferCoreConverter) {
    this.asyncOfferCoreConverter = asyncOfferCoreConverter;
  }
}
