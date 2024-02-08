package com.mirakl.hybris.core.product.daos.impl;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mirakl.hybris.core.enums.ProductOrigin;
import com.mirakl.hybris.core.product.daos.MiraklProductDao;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultMiraklProductDao extends DefaultGenericDao<ProductModel> implements MiraklProductDao {

  public DefaultMiraklProductDao() {
    super(ProductModel._TYPECODE);
  }

  @Override
  public List<ProductModel> findModifiedProductsWithNoVariantType(Date modifiedAfter, CatalogVersionModel catalogVersion) {
    return findModifiedProductsWithNoVariantType(modifiedAfter, catalogVersion, null);
  }

  @Override
  public List<ProductModel> findModifiedProductsWithNoVariantType(Date modifiedAfter, CatalogVersionModel catalogVersion,
      Set<ProductOrigin> origins) {
    return findModifiedProductsWithNoVariantType(modifiedAfter, catalogVersion, origins, null);
  }

  @Override
  public List<ProductModel> findModifiedProductsWithNoVariantType(Date modifiedAfter, CatalogVersionModel catalogVersion,
      Set<ProductOrigin> origins, Set<ArticleApprovalStatus> approvalStatuses) {
    Map<String, Object> params = new HashMap<>();
    params.put(ProductModel.CATALOGVERSION, catalogVersion);

    StringBuilder queryString =
        new StringBuilder("SELECT {").append(ProductModel.PK).append("} FROM {").append(ProductModel._TYPECODE).append("}") //
            .append(" WHERE {").append(ProductModel.CATALOGVERSION).append("}=?").append(ProductModel.CATALOGVERSION) //
            .append(" AND {").append(ProductModel.VARIANTTYPE).append("} IS NULL");

    if (modifiedAfter != null) {
      queryString.append(" AND {").append(ProductModel.MODIFIEDTIME).append("} > ?").append(ProductModel.MODIFIEDTIME);
      params.put(ProductModel.MODIFIEDTIME, modifiedAfter);
    }
    if (isNotEmpty(origins)) {
      queryString.append(" AND {").append(ProductModel.ORIGIN).append("} IN (?").append(ProductModel.ORIGIN).append(")");
      params.put(ProductModel.ORIGIN, origins);
    }
    if (isNotEmpty(approvalStatuses)) {
      queryString.append(" AND {" + ProductModel.APPROVALSTATUS + "} IN (?" + ProductModel.APPROVALSTATUS).append(")");
      params.put(ProductModel.APPROVALSTATUS, approvalStatuses);
    }

    SearchResult<ProductModel> searchResult =
        getFlexibleSearchService().search(new FlexibleSearchQuery(queryString.toString(), params));
    return searchResult.getResult();
  }


}
