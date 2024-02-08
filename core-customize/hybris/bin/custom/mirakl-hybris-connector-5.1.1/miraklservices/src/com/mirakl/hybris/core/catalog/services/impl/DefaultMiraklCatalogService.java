package com.mirakl.hybris.core.catalog.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mci.domain.attribute.MiraklAttribute;
import com.mirakl.client.mci.domain.hierarchy.MiraklHierarchy;
import com.mirakl.client.mci.domain.value.list.MiraklValueList;
import com.mirakl.client.mci.domain.value.list.MiraklValueListItem;
import com.mirakl.client.mci.domain.value.list.MiraklValueLists;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mci.front.request.attribute.MiraklGetAttributesRequest;
import com.mirakl.client.mci.front.request.hierarchy.MiraklGetHierarchiesRequest;
import com.mirakl.client.mci.front.request.value.list.MiraklGetValueListsItemsRequest;
import com.mirakl.hybris.core.catalog.services.MiraklCatalogService;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultMiraklCatalogService implements MiraklCatalogService {

  protected MiraklCatalogIntegrationFrontApi mciApi;

  @Override
  public Set<String> getMiraklCategoryCodes() {
    List<MiraklHierarchy> miraklHierarchies = mciApi.getHierarchies(new MiraklGetHierarchiesRequest());
    Set<String> miraklCategoryCodes = new HashSet<>();
    for (MiraklHierarchy miraklHierarchy : miraklHierarchies) {
      miraklCategoryCodes.add(miraklHierarchy.getCode());
    }

    return miraklCategoryCodes;
  }

  @Override
  public Set<Pair<String, String>> getMiraklAttributeCodes() {
    List<MiraklAttribute> miraklAttributes = mciApi.getAttributes(new MiraklGetAttributesRequest());
    Set<Pair<String, String>> miraklAttributeCodes = new HashSet<>();
    for (MiraklAttribute miraklAttribute : miraklAttributes) {
      miraklAttributeCodes.add(Pair.of(miraklAttribute.getCode(), miraklAttribute.getHierarchyCode()));
    }

    return miraklAttributeCodes;
  }

  @Override
  public Set<Pair<String, String>> getMiraklValueCodes() {
    MiraklValueLists valueLists = mciApi.getValueLists(new MiraklGetValueListsItemsRequest());
    Set<Pair<String, String>> valueListItemCodes = new HashSet<>();
    for (MiraklValueList miraklValueList : valueLists.getValueLists()) {
      for (MiraklValueListItem value : miraklValueList.getItems()) {
        valueListItemCodes.add(Pair.of(value.getCode(), miraklValueList.getCode()));
      }
    }

    return valueListItemCodes;
  }

  @Required
  public void setMciApi(MiraklCatalogIntegrationFrontApi mciApi) {
    this.mciApi = mciApi;
  }
}
