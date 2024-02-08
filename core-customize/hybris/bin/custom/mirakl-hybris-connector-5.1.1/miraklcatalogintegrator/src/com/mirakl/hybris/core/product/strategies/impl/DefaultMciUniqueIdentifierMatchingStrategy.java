package com.mirakl.hybris.core.product.strategies.impl;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.product.daos.impl.DefaultMiraklProductDao;
import com.mirakl.hybris.core.product.strategies.UniqueIdentifierMatchingStrategy;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMciUniqueIdentifierMatchingStrategy implements UniqueIdentifierMatchingStrategy {

  protected ModelService modelService;
  protected DefaultMiraklProductDao miraklProductDao;

  @Override
  public Set<ProductModel> getMatches(ProductImportData data, ProductImportFileContextData context) {
    Set<String> uidCoreAttributes = context.getGlobalContext().getUniqueIdentifierCoreAttributes();
    if (isEmpty(uidCoreAttributes)) {
      return Collections.emptySet();
    }

    Map<String, Object> params = new HashMap<>();
    for (String uidCoreAttribute : uidCoreAttributes) {
      params.put(uidCoreAttribute, data.getRawProduct().getValues().get(uidCoreAttribute));
    }

    CatalogVersionModel catalogVersion = modelService.get(context.getGlobalContext().getProductCatalogVersion());
    params.put(ProductModel.CATALOGVERSION, catalogVersion);

    return new HashSet<>(miraklProductDao.find(params));
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setMiraklProductDao(DefaultMiraklProductDao miraklProductDao) {
    this.miraklProductDao = miraklProductDao;
  }

}
