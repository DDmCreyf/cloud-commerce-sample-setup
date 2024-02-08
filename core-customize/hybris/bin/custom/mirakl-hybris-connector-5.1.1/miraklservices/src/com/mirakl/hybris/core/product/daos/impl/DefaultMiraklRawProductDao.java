package com.mirakl.hybris.core.product.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mirakl.hybris.beans.ShopVariantGroupCode;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.daos.MiraklRawProductDao;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultMiraklRawProductDao extends DefaultGenericDao<MiraklRawProductModel> implements MiraklRawProductDao {

  private static final String SHOP_VARIANT_GROUP_CODES_BY_IMPORT_ID_QUERY = //
      "SELECT distinct {p:" + MiraklRawProductModel.SHOPID + "}, {" + MiraklRawProductModel.VARIANTGROUPCODE + "}" //
          + " FROM {" + MiraklRawProductModel._TYPECODE + " AS p}" //
          + " WHERE {p:" + MiraklRawProductModel.IMPORTID + "} = ?" + MiraklRawProductModel.IMPORTID //
          + " AND {" + MiraklRawProductModel.VARIANTGROUPCODE + "} IS NOT NULL";

  private static final String MIRAKL_VARIANT_GROUP_CODES_BY_IMPORT_ID_QUERY = //
      "SELECT distinct {" + MiraklRawProductModel.VARIANTGROUPCODE + "}" //
          + " FROM {" + MiraklRawProductModel._TYPECODE + " AS p}" //
          + " WHERE {p:" + MiraklRawProductModel.IMPORTID + "} = ?" + MiraklRawProductModel.IMPORTID //
          + " AND {" + MiraklRawProductModel.VARIANTGROUPCODE + "} IS NOT NULL";


  private static final String PRODUCTS_WITH_NO_VARIANT_GROUP_BY_IMPORT_ID_QUERY = //
      "SELECT {p:" + MiraklRawProductModel.PK + "}" //
          + " FROM {" + MiraklRawProductModel._TYPECODE + " AS p}" //
          + " WHERE {p:" + MiraklRawProductModel.IMPORTID + "} = ?" + MiraklRawProductModel.IMPORTID //
          + " AND {" + MiraklRawProductModel.VARIANTGROUPCODE + "} IS NULL";

  public DefaultMiraklRawProductDao() {
    super(MiraklRawProductModel._TYPECODE);
  }

  @Override
  public List<ShopVariantGroupCode> findShopVariantGroupCodesByImportId(String importId) {
    validateParameterNotNullStandardMessage("importId", importId);

    FlexibleSearchQuery query = new FlexibleSearchQuery(SHOP_VARIANT_GROUP_CODES_BY_IMPORT_ID_QUERY,
        singletonMap(MiraklRawProductModel.IMPORTID, importId));
    query.setResultClassList(asList(String.class, String.class));
    SearchResult<List<String>> queryResult = getFlexibleSearchService().search(query);

    List<ShopVariantGroupCode> result = new ArrayList<>(queryResult.getCount());
    for (final List<String> row : queryResult.getResult()) {
      if (row != null) {
        ShopVariantGroupCode shopVariantGroupCode = new ShopVariantGroupCode();
        shopVariantGroupCode.setShopId(row.get(0));
        shopVariantGroupCode.setVariantGroupCode(row.get(1));
        result.add(shopVariantGroupCode);
      }
    }

    return result;
  }

  @Override
  public List<String> findMiraklVariantGroupCodesByImportId(String importId) {
    validateParameterNotNullStandardMessage("importId", importId);

    FlexibleSearchQuery query = new FlexibleSearchQuery(MIRAKL_VARIANT_GROUP_CODES_BY_IMPORT_ID_QUERY,
        singletonMap(MiraklRawProductModel.IMPORTID, importId));
    query.setResultClassList(asList(String.class));
    SearchResult<String> queryResult = getFlexibleSearchService().search(query);
    return queryResult.getResult();
  }

  @Override
  public List<MiraklRawProductModel> findRawProductsByImportIdAndVariantGroupCode(String importId, String variantGroupCode) {
    validateParameterNotNullStandardMessage("importId", importId);
    validateParameterNotNullStandardMessage("variantGroupCode", variantGroupCode);

    Map<String, Object> params = new HashMap<>();
    params.put(MiraklRawProductModel.IMPORTID, importId);
    params.put(MiraklRawProductModel.VARIANTGROUPCODE, variantGroupCode);

    return find(params);
  }

  @Override
  public List<MiraklRawProductModel> findRawProductsWithNoVariantGroupByImportId(String importId) {
    validateParameterNotNullStandardMessage("importId", importId);

    SearchResult<MiraklRawProductModel> searchResult = getFlexibleSearchService()
        .search(PRODUCTS_WITH_NO_VARIANT_GROUP_BY_IMPORT_ID_QUERY, singletonMap(MiraklRawProductModel.IMPORTID, importId));

    return searchResult.getResult();
  }

}
