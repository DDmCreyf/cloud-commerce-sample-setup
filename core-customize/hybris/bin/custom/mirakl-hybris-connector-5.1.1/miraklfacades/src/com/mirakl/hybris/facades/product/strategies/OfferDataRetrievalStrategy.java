package com.mirakl.hybris.facades.product.strategies;

import com.mirakl.hybris.beans.OfferData;

import java.util.List;
import java.util.Map;

/**
 * Defines a strategy to retrieve offer data.
 */
public interface OfferDataRetrievalStrategy {

  /**
   * Retrieves a list of OfferData objects associated with the given product code.
   *
   * @param productCode the unique identifier for a product.
   * @return a list of OfferData objects linked to the specified product code.
   */
  List<OfferData> getOffersForProductCode(String productCode);

  /**
   * Retrieves a map containing OfferData objects associated with the provided list of product codes. Each product code is a key mapped to its
   * associated list of OfferData.
   *
   * @param productCodes a list of unique identifiers for products.
   * @return a map with product codes as keys and their corresponding lists of OfferData as values.
   */
  Map<String, List<OfferData>> getOffersForProductCodes(List<String> productCodes);

  /**
   * Checks if the current retrieval strategy is suitable for the current context.
   *
   * @return true if the current strategy is applicable for retrieving offer data; false otherwise.
   */
  boolean isApplicable();
}
