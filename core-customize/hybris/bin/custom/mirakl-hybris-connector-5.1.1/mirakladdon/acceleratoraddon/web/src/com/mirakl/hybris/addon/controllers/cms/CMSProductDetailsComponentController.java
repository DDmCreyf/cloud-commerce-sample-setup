package com.mirakl.hybris.addon.controllers.cms;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mirakl.hybris.addon.controllers.MirakladdonControllerConstants;
import com.mirakl.hybris.addon.model.CMSProductDetailsComponentModel;
import com.mirakl.hybris.beans.OfferData;

@RequestMapping(value = MirakladdonControllerConstants.Cms.CMSProductDetailsComponent)
public class CMSProductDetailsComponentController extends AbstractCMSOfferComponentController<CMSProductDetailsComponentModel> {

  @Override
  protected void fillModel(Model model, List<OfferData> offers) {
    // Do nothing
  }
}
