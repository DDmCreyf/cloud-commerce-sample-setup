package com.mirakl.hybris.core.product.services;

import java.util.Map;
import java.util.Set;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

public interface MiraklProductService {

  /**
   * Checks if a product is sellable by the operator
   * 
   * @param productModel the product to be checked
   * @return true if sellable, false otherwise
   */
  boolean isSellableByOperator(ProductModel productModel);

  /**
   * Gets all attribute descriptors for each product type.
   * 
   * @return a Map having as a key the product composed type and as a value all its attribute descriptors
   */
  Map<ComposedTypeModel, Set<AttributeDescriptorModel>> getAttributeDescriptorsPerProductType();

  /**
   * Gets all attribute descriptor qualifiers for each product type.
   * 
   * @return a Map having as a key the product composed type and as a value all its attribute descriptors
   */
  Map<ComposedTypeModel, Set<String>> getAttributeDescriptorQualifiersPerProductType();

}
