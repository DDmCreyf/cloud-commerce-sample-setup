package com.mirakl.hybris.core.product.strategies;

import java.io.IOException;
import java.util.Collection;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

public interface MciProductExportStrategy {

  /**
   * Exports products to Mirakl, using the P21 API
   * 
   * @param products products to be exported
   * @param rootCategory root PCM category
   * @param rootBrandCategory root brand category
   * @param baseSite base site, used for defining product page URLs
   * @param fileName export filename
   * @return
   * @throws IOException
   */
  int exportProducts(Collection<ProductModel> products, CategoryModel rootCategory, CategoryModel rootBrandCategory,
      BaseSiteModel baseSite, String fileName) throws IOException;
}
