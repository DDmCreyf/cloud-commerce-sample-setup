package com.mirakl.hybris.core.product.strategies;

import java.io.IOException;
import java.util.Collection;

import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncTracking;
import com.mirakl.client.mci.request.product.MiraklProductDataSheetSyncRequest;
import com.mirakl.hybris.beans.ProductDataSheetExportContextData;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;

public interface McmProductExportStrategy {

  /**
   * Exports products to Mirakl, using the CM21 API.
   * 
   * @param products products to be exported
   * @param context product export context
   * @return
   * @throws IOException
   */
  int exportProductDataSheets(Collection<ProductModel> products, ProductDataSheetExportContextData context) throws IOException;

  /**
   * Sends a product data sheets export request to Mirakl (CM21) and creates a report for tracking its progress
   * 
   * @param request export request to be sent to Mirakl
   * @param catalogVersion report's catalog version
   * @return a Mirakl export tracking reference
   */
  MiraklProductDataSheetSyncTracking sendRequest(MiraklProductDataSheetSyncRequest request,
      CatalogVersionModel catalogVersion);

}
