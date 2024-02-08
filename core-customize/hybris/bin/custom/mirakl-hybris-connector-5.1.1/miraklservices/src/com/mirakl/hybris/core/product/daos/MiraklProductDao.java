package com.mirakl.hybris.core.product.daos;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.mirakl.hybris.core.enums.ProductOrigin;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

public interface MiraklProductDao extends GenericDao<ProductModel> {

  /**
   * Finds products with no variant type modified after a given date (if provided), or all products otherwise.
   * 
   * @param modifiedAfter the earliest date of modification allowed (can be null)
   * @param catalogVersion the product catalog version
   * @return a list a products
   */
  List<ProductModel> findModifiedProductsWithNoVariantType(Date modifiedAfter, CatalogVersionModel catalogVersion);

  /**
   * Finds products with no variant type, coming form a specified origin (Operator and/or Marketpalce) and modified after a given
   * date.
   * 
   * @param modifiedAfter the earliest date of modification allowed (can be null)
   * @param catalogVersion the product catalog version
   * @param origins the product origins to be considered
   * @return a list of products
   */
  List<ProductModel> findModifiedProductsWithNoVariantType(Date modifiedAfter, CatalogVersionModel catalogVersion,
      Set<ProductOrigin> origins);

  /**
   * Finds products with no variant type, coming form a specified origin (Operator and/or Marketpalce), having a given approval
   * statuses and modified after a given date.
   * 
   * @param modifiedAfter the earliest date of modification allowed (can be null)
   * @param catalogVersion the product catalog version
   * @param origins the product origins to be considered
   * @param approvalStatuses the article approval statuses to be considered. If left empty, all statuses are selected
   * @return a list of products
   */
  List<ProductModel> findModifiedProductsWithNoVariantType(Date modifiedAfter, CatalogVersionModel catalogVersion,
      Set<ProductOrigin> origins, Set<ArticleApprovalStatus> approvalStatuses);

}