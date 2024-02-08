package com.mirakl.hybris.core.catalog.strategies;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.core.util.services.impl.TranslationException;

import de.hybris.platform.core.model.product.ProductModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface CoreAttributeValueTranslationStrategy {
  /**
   * Parses the given value to the type of the persistence object
   *
   * @param attributeValue the imported attribute value (must contain a core attribute)
   * @param product the product linked to the core attribute
   * @return the parsed value, ready to be persisted
   * @throws TranslationException if the value can not be translated
   */
  Object translateAttributeValue(AttributeValueData attributeValue, ProductModel product) throws TranslationException;
}
