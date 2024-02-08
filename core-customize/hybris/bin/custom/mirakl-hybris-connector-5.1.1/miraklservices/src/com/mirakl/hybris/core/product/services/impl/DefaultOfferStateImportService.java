package com.mirakl.hybris.core.product.services.impl;

import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.domain.offer.state.MiraklOfferState;
import com.mirakl.client.mmp.front.request.offer.state.MiraklGetOfferStatesRequest;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.product.services.OfferStateImportService;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class DefaultOfferStateImportService implements OfferStateImportService {

  private static final Logger LOG = Logger.getLogger(DefaultOfferStateImportService.class);

  protected EnumerationService enumerationService;
  protected ModelService modelService;
  protected MiraklMarketplacePlatformFrontApi mmpFrontApi;

  @Override
  public Collection<OfferState> importAllOfferStates() {
    return importOfferStates(mmpFrontApi.getOfferStateList(new MiraklGetOfferStatesRequest()));
  }


  protected List<OfferState> importOfferStates(List<MiraklOfferState> miraklOfferStateList) {
    final List<OfferState> importedOfferStates = new ArrayList<>();
    for (MiraklOfferState miraklOfferState : miraklOfferStateList) {
      importedOfferStates.add(createOfferState(miraklOfferState));
    }
    return importedOfferStates;
  }

  protected OfferState createOfferState(MiraklOfferState miraklOfferState) {
    OfferState offerState;
    try {
      offerState = enumerationService.getEnumerationValue(OfferState._TYPECODE, miraklOfferState.getCode());
      enumerationService.setEnumerationName(offerState, miraklOfferState.getLabel());
      return offerState;
    } catch (UnknownIdentifierException exception) {
        LOG.info(String.format("Offer state with code [%s] not found.", miraklOfferState.getCode()));
    }
    final EnumerationValueModel enumerationValueModel = this.modelService.create(OfferState._TYPECODE);
    enumerationValueModel.setCode(miraklOfferState.getCode());
    enumerationValueModel.setName(miraklOfferState.getLabel());
    modelService.save(enumerationValueModel);
    offerState = enumerationService.getEnumerationValue(OfferState._TYPECODE, miraklOfferState.getCode());
    return offerState;
  }


  @Required
  public void setEnumerationService(EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setMmpFrontApi(MiraklMarketplacePlatformFrontApi mmpFrontApi) {
    this.mmpFrontApi = mmpFrontApi;
  }

}
