package com.mirakl.hybris.addon.controllers.cms;

import com.mirakl.hybris.addon.controllers.MirakladdonControllerConstants;
import com.mirakl.hybris.addon.model.CMSTabOfferListComponentModel;
import com.mirakl.hybris.beans.OfferData;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@RequestMapping(value = MirakladdonControllerConstants.Cms.CMSTabOfferListComponent)
public class CMSTabOfferListComponentController extends AbstractCMSOfferComponentController<CMSTabOfferListComponentModel> {

  @Override
  protected void fillModel(Model model, List<OfferData> offers) {
    model.addAttribute("offers", offers);
  }
}
