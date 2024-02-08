package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.COLLECTION_ITEM_SEPARATOR;
import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.KEY_VALUE_SEPARATOR;
import static com.mirakl.hybris.core.enums.MiraklProductExportHeader.PRODUCT_REFERENCES;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Joiner;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

public class ProductExportReferencesPopulator implements Populator<ProductModel, Map<String, String>> {

  protected ModelService modelService;
  protected Map<String, String> referenceAttributesConfig;

  @Override
  public void populate(ProductModel source, Map<String, String> target) {
    target.put(PRODUCT_REFERENCES.getCode(), getProductReferences(source));
  }

  protected String getProductReferences(ProductModel productModel) {
    List<String> references = new ArrayList<>();
    for (Map.Entry<String, String> entry : referenceAttributesConfig.entrySet()) {
      Object attributeValue = modelService.getAttributeValue(productModel, entry.getKey());
      if (attributeValue != null) {
        references.add(entry.getValue() + KEY_VALUE_SEPARATOR + attributeValue);
      }
    }

    return Joiner.on(COLLECTION_ITEM_SEPARATOR).join(references);
  }


  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setReferenceAttributesConfig(Map<String, String> referenceAttributesConfig) {
    this.referenceAttributesConfig = referenceAttributesConfig;
  }

}
