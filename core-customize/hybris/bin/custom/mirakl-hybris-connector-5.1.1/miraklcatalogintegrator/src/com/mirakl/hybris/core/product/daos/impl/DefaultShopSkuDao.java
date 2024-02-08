package com.mirakl.hybris.core.product.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.HashMap;
import java.util.List;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.model.ShopSkuModel;
import com.mirakl.hybris.core.product.daos.ShopSkuDao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultShopSkuDao extends DefaultGenericDao<ShopSkuModel> implements ShopSkuDao {

  private static final String SHOP_SKU_BY_CHECKSUM_QUERY =
      "SELECT {sk:" + ShopSkuModel.PK + "} " + " FROM {" + ShopSkuModel._TYPECODE + " AS sk"//
          + " JOIN " + ProductModel._TYPECODE + " AS p ON {sk:" + ShopSkuModel.PRODUCT + "}={p:" + ProductModel.PK + "}}" //
          + " WHERE {sk:" + ShopSkuModel.CHECKSUM + "}=?" + ShopSkuModel.CHECKSUM //
          + " AND {sk:" + ShopSkuModel.SHOP + "} = ?" + ShopSkuModel.SHOP //
          + " AND {p:" + ProductModel.CATALOGVERSION + "} = ?" + ProductModel.CATALOGVERSION;


  private static final String SHOP_SKU_BY_SKU_QUERY =
      "SELECT {sk:" + ShopSkuModel.PK + "} " + " FROM {" + ShopSkuModel._TYPECODE + " AS sk"//
          + " JOIN " + ProductModel._TYPECODE + " AS p ON {sk:" + ShopSkuModel.PRODUCT + "}={p:" + ProductModel.PK + "}}" //
          + " WHERE {sk:" + ShopSkuModel.SKU + "}=?" + ShopSkuModel.SKU //
          + " AND {sk:" + ShopSkuModel.SHOP + "} = ?" + ShopSkuModel.SHOP //
          + " AND {p:" + ProductModel.CATALOGVERSION + "} = ?" + ProductModel.CATALOGVERSION;

  public DefaultShopSkuDao() {
    super(ShopSkuModel._TYPECODE);
  }

  @Override
  public ShopSkuModel findShopSkuByChecksum(String checksum, ShopModel shop, CatalogVersionModel catalogVersion) {
    validateParameterNotNullStandardMessage("checksum", checksum);

    HashMap<String, Object> params = new HashMap<>();
    params.put(ShopSkuModel.CHECKSUM, checksum);
    params.put(ShopSkuModel.SHOP, shop);
    params.put(ProductModel.CATALOGVERSION, catalogVersion);
    FlexibleSearchQuery query = new FlexibleSearchQuery(SHOP_SKU_BY_CHECKSUM_QUERY, params);
    List<ShopSkuModel> shopSkus = getFlexibleSearchService().<ShopSkuModel>search(query).getResult();

    if (isNotEmpty(shopSkus) && shopSkus.size() > 1) {
      throw new AmbiguousIdentifierException(format("Multiple shop skus for checksum [%s]", checksum));
    }
    return isEmpty(shopSkus) ? null : shopSkus.get(0);
  }

  @Override
  public ShopSkuModel findShopSkuBySku(String sku, ShopModel shop, CatalogVersionModel catalogVersion) {
    validateParameterNotNullStandardMessage("sku", sku);

    HashMap<String, Object> params = new HashMap<>();
    params.put(ShopSkuModel.SKU, sku);
    params.put(ShopSkuModel.SHOP, shop);
    params.put(ProductModel.CATALOGVERSION, catalogVersion);
    FlexibleSearchQuery query = new FlexibleSearchQuery(SHOP_SKU_BY_SKU_QUERY, params);
    List<ShopSkuModel> shopSkus = getFlexibleSearchService().<ShopSkuModel>search(query).getResult();


    if (isNotEmpty(shopSkus) && shopSkus.size() > 1) {
      throw new AmbiguousIdentifierException(format("Multiple shop skus for sku [%s]", sku));
    }
    return isEmpty(shopSkus) ? null : shopSkus.get(0);
  }
}
