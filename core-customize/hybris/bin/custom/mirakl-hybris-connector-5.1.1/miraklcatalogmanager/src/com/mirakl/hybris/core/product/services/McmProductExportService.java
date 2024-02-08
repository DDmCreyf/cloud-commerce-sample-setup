package com.mirakl.hybris.core.product.services;

import java.io.IOException;

import com.mirakl.hybris.beans.ProductDataSheetExportContextData;

public interface McmProductExportService {

  /**
   * Exports product data sheets to Mirakl, using the CM21 API.
   *
   * @param exportContext export context used for the export
   * @return the number of exported products
   * @throws IOException
   */
  int exportProductDataSheets(ProductDataSheetExportContextData exportContext) throws IOException;


}
