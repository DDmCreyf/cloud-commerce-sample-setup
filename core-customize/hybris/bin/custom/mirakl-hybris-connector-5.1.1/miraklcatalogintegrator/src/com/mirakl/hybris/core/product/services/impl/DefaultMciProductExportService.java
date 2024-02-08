package com.mirakl.hybris.core.product.services.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.product.services.MciProductExportService;
import com.mirakl.hybris.core.product.strategies.MciProductExportEligibilityStrategy;
import com.mirakl.hybris.core.product.strategies.MciProductExportStrategy;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

public class DefaultMciProductExportService implements MciProductExportService {

  protected MciProductExportStrategy mciProductExportStrategy;
  protected MciProductExportEligibilityStrategy eligibilityStrategy;

  @Override
  public int exportAllProducts(CategoryModel rootCategory, CategoryModel rootBrandCategory, BaseSiteModel baseSite,
      String fileName) throws IOException {
    Collection<ProductModel> products = eligibilityStrategy.getAllProductsEligibleForExport(rootCategory.getCatalogVersion());
    return mciProductExportStrategy.exportProducts(products, rootCategory, rootBrandCategory, baseSite, fileName);
  }

  @Override
  public int exportModifiedProducts(CategoryModel rootCategory, CategoryModel rootBrandCategory, BaseSiteModel baseSite,
      Date modifiedAfter, String fileName) throws IOException {
    Collection<ProductModel> products =
        eligibilityStrategy.getModifiedProductsEligibleForExport(rootCategory.getCatalogVersion(), modifiedAfter);
    return mciProductExportStrategy.exportProducts(products, rootCategory, rootBrandCategory, baseSite, fileName);
  }

  @Required
  public void setMciProductExportStrategy(MciProductExportStrategy mciProductExportStrategy) {
    this.mciProductExportStrategy = mciProductExportStrategy;
  }

  @Required
  public void setEligibilityStrategy(MciProductExportEligibilityStrategy eligibilityStrategy) {
    this.eligibilityStrategy = eligibilityStrategy;
  }

}
