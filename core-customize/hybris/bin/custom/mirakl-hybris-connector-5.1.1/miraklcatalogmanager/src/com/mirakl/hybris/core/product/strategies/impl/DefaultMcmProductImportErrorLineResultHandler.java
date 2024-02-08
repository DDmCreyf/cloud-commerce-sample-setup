package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklcatalogmanagerConstants.MCM_MIRAKL_PRODUCT_ID_HEADER;
import static java.lang.String.format;
import static java.util.Collections.singletonList;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.mirakl.client.mci.domain.product.MiraklProductDataSheetIntegrationError;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncItem;
import com.mirakl.client.mci.request.product.MiraklProductDataSheetSyncRequest;
import com.mirakl.hybris.beans.ProductImportErrorData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.product.strategies.ProductImportLineResultHandler;

public class DefaultMcmProductImportErrorLineResultHandler implements ProductImportLineResultHandler<ProductImportErrorData> {

  private static final Logger LOG = Logger.getLogger(DefaultMcmProductImportErrorLineResultHandler.class);

  protected MiraklProductDataSheetSyncRequest.MiraklProductDataSheetSyncRequestBuilder requestBuilder;
  protected ProductImportFileContextData context;

  public DefaultMcmProductImportErrorLineResultHandler(
      MiraklProductDataSheetSyncRequest.MiraklProductDataSheetSyncRequestBuilder requestBuilder,
      ProductImportFileContextData context) {
    this.requestBuilder = requestBuilder;
    this.context = context;
  }

  @Override
  public void initialize() throws IOException {
    throw new UnsupportedOperationException("Not supported for MCM synchronization");
  }

  @Override
  public void handleLineResult(ProductImportErrorData result) {
    try {
      MiraklProductDataSheetIntegrationError integrationError = new MiraklProductDataSheetIntegrationError();
      integrationError.setMessage(result.getErrorMessage());
      MiraklProductDataSheetSyncItem productSynchronizationItem = new MiraklProductDataSheetSyncItem();
      productSynchronizationItem.setMiraklProductId(result.getLineValues().get(MCM_MIRAKL_PRODUCT_ID_HEADER));
      productSynchronizationItem.setIntegrationErrors(singletonList(integrationError));
      requestBuilder.addProduct(productSynchronizationItem);
    } catch (IOException e) {
      LOG.error(format("An I/O error occurred in the result handler thread for file [%s]", context.getReceivedFile().getName()),
          e);
    }
  }

  @Override
  public String getFilename() {
    throw new UnsupportedOperationException("Not supported for MCM synchronization");
  }

  @Override
  public void flush() throws IOException {
    throw new UnsupportedOperationException("Not supported for MCM synchronization");
  }

  @Override
  public void close() throws Exception {
    // Nothing to close
  }
}
