package com.mirakl.hybris.core.product.strategies;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;

public interface ProductPrimaryImageSelectionStrategy {

  /**
   * Returns the primary image of a product. By default, the primary image of a product is the first of its media gallery. This is
   * the image displayed at first on the product details page.
   * 
   * @param product
   * @return the primary image, if any. Null otherwise
   */
  MediaModel getPrimaryProductImage(ProductModel product);
}
