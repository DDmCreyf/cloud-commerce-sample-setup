package com.mirakl.hybris.core.shop.services.impl;

import static com.google.common.primitives.Ints.checkedCast;
import static com.mirakl.hybris.core.util.PaginationUtils.getNumberOfPages;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.services.ShopImportService;
import com.mirakl.hybris.core.shop.services.ShopService;
import com.mirakl.hybris.core.shop.strategies.ShopValidationStrategy;
import com.mirakl.hybris.core.util.PaginationUtils;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

public class DefaultShopImportService implements ShopImportService {

  private static final Logger LOG = Logger.getLogger(DefaultShopImportService.class);

  protected MiraklMarketplacePlatformFrontApi miraklOperatorApi;

  protected Converter<MiraklShop, ShopModel> shopConverter;

  protected ShopService shopService;

  protected ConfigurationService configurationService;

  protected ModelService modelService;

  protected ShopValidationStrategy shopValidationStrategy;

  @Override

  public Collection<ShopModel> importAllShops() {
    return importShopsUpdatedSince(null);
  }

  @Override
  public Collection<ShopModel> importShopsUpdatedSince(Date startingDate) {

    int page = 0;
    Integer pagesNeeded = null;
    List<ShopModel> shops = new ArrayList<>();
    List<ItemModel> modelsToSave = new ArrayList<>();
    do {
      MiraklShops miraklShops = miraklOperatorApi.getShops(buildGetShopsRequest(startingDate, page++));
      if (pagesNeeded == null) {
        pagesNeeded = getNumberOfPages(checkedCast(miraklShops.getTotalCount()), getMaxResultsByPage());
      }
      for (MiraklShop miraklShop : miraklShops.getShops()) {
        importShop(miraklShop, shops, modelsToSave);
      }
    } while (page < pagesNeeded);

    modelsToSave.addAll(shops);
    modelService.saveAll(modelsToSave);
    return shops;
  }

  protected void importShop(MiraklShop miraklShop, List<ShopModel> shops, List<ItemModel> modelsToSave) {
    try {
      ShopModel shop = getShopModel(miraklShop);
      if (shopValidationStrategy.isValid(shop)) {
        shops.add(shop);
        modelsToSave.add(shop.getContactInformation());
      }
    } catch (ConversionException e) {
      LOG.error(format("An error occurred when importing shop [%s] with id [%s]", miraklShop.getName(), miraklShop.getId()), e);
    }
  }

  protected ShopModel getShopModel(MiraklShop miraklShop) {
    ShopModel shop = shopService.getShopForId(miraklShop.getId());
    if (shop == null) {
      shop = shopConverter.convert(miraklShop);
    } else {
      shop = shopConverter.convert(miraklShop, shop);
    }
    return shop;
  }

  private MiraklGetShopsRequest buildGetShopsRequest(Date startingDate, int pageNumber) {
    MiraklGetShopsRequest miraklGetShopsRequest = new MiraklGetShopsRequest();
    miraklGetShopsRequest.setUpdatedSince(startingDate);
    return PaginationUtils.applyMiraklFullPagination(miraklGetShopsRequest, true, getMaxResultsByPage(),
        pageNumber * getMaxResultsByPage());
  }

  protected int getMaxResultsByPage() {
    return configurationService.getConfiguration().getInt("mirakl.shops.import.pagesize", 100);
  }

  @Required
  public void setMiraklOperatorApi(MiraklMarketplacePlatformFrontApi miraklOperatorApi) {
    this.miraklOperatorApi = miraklOperatorApi;
  }

  @Required
  public void setShopConverter(Converter<MiraklShop, ShopModel> shopConverter) {
    this.shopConverter = shopConverter;
  }

  @Required
  public void setShopService(ShopService shopService) {
    this.shopService = shopService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setShopValidationStrategy(ShopValidationStrategy shopValidationStrategy) {
    this.shopValidationStrategy = shopValidationStrategy;
  }
}
