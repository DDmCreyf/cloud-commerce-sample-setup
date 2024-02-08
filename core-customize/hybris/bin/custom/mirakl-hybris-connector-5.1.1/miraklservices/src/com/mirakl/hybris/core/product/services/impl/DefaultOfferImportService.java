package com.mirakl.hybris.core.product.services.impl;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.core.internal.MiraklStream;
import com.mirakl.client.mmp.domain.offer.MiraklExportOffer;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.offer.MiraklOffersExportRequest;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.daos.OfferDao;
import com.mirakl.hybris.core.product.services.OfferImportService;
import com.mirakl.hybris.core.product.strategies.OfferImportErrorHandler;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultOfferImportService implements OfferImportService {

  private static final Logger LOG = Logger.getLogger(DefaultOfferImportService.class);

  protected ModelService modelService;
  protected OfferDao offerDao;
  protected Converter<MiraklExportOffer, OfferModel> offerConverter;
  protected MiraklMarketplacePlatformFrontApi miraklOperatorApi;
  protected OfferImportErrorHandler errorHandler;


  @Override
  public Collection<OfferModel> importAllOffers(Date missingOffersDeletionDate, boolean includeInactiveOffers) {
    MiraklOffersExportRequest miraklOffersExportRequest = new MiraklOffersExportRequest();
    miraklOffersExportRequest.setIncludeInactiveOffers(includeInactiveOffers);
    List<OfferModel> updatedOffers = importOffers(miraklOperatorApi.exportOffersAsStream(miraklOffersExportRequest));
    LOG.info("Full Offer Import. Set missing offers to DELETED...");
    setMissingOffersDeleted(missingOffersDeletionDate, updatedOffers);
    return updatedOffers;
  }

  @Override
  public Collection<OfferModel> importOffersUpdatedSince(Date lastImportDate) {
    MiraklOffersExportRequest miraklOffersExportRequest = new MiraklOffersExportRequest();
    miraklOffersExportRequest.setLastRequestDate(lastImportDate);
    return importOffers(miraklOperatorApi.exportOffersAsStream(miraklOffersExportRequest));
  }

  protected List<OfferModel> importOffers(MiraklStream<MiraklExportOffer> miraklOfferStream) {
    List<OfferModel> importedOffers = new ArrayList<>();
    for (MiraklExportOffer miraklExportOffer : miraklOfferStream) {
      try {
        OfferModel currentOffer = offerDao.findOfferById(miraklExportOffer.getId());
        if (currentOffer == null) {
          currentOffer = offerConverter.convert(miraklExportOffer);
        } else {
          currentOffer = updateExistingOffer(miraklExportOffer, currentOffer);
        }
        modelService.save(currentOffer);
        importedOffers.add(currentOffer);
      } catch (Exception e) {
        errorHandler.handle(e, miraklExportOffer.getId());
      }
    }
    return importedOffers;
  }

  protected OfferModel updateExistingOffer(MiraklExportOffer miraklExportOffer, OfferModel existingOffer) {
    if (isDeactivated(miraklExportOffer)) {
      existingOffer.setDeleted(miraklExportOffer.isDeleted());
      existingOffer.setActive(miraklExportOffer.isActive());
    } else {
      offerConverter.convert(miraklExportOffer, existingOffer);
    }
    return existingOffer;
  }

  protected Boolean isDeactivated(MiraklExportOffer miraklExportOffer) {
    return miraklExportOffer.isDeleted() || !miraklExportOffer.isActive();
  }

  protected void setMissingOffersDeleted(Date missingOffersDeletionDate, List<OfferModel> updatedOffers) {
    List<OfferModel> missingOffers = offerDao.findUndeletedOffersModifiedBeforeDate(missingOffersDeletionDate);
    for (OfferModel offer : missingOffers) {
      if (updatedOffers.contains(offer)) {
        continue;
      }
      try {
        LOG.info(format("Offer [%s] set DELETED", offer.getId()));
        offer.setDeleted(true);
        offer.setActive(false);
        modelService.save(offer);
      } catch (Exception e) {
        errorHandler.handle(e, offer.getId());
      }
    }
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setOfferConverter(Converter<MiraklExportOffer, OfferModel> offerConverter) {
    this.offerConverter = offerConverter;
  }

  @Required
  public void setOfferDao(OfferDao offerDao) {
    this.offerDao = offerDao;
  }

  @Required
  public void setMiraklOperatorApi(MiraklMarketplacePlatformFrontApi miraklOperatorApi) {
    this.miraklOperatorApi = miraklOperatorApi;
  }

  @Required
  public void setErrorHandler(OfferImportErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }
}
