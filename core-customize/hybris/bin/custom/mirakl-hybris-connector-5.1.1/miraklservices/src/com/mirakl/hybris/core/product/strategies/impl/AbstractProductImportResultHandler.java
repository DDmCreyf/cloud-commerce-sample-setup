package com.mirakl.hybris.core.product.strategies.impl;

import static java.lang.String.format;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.hybris.beans.ProductImportErrorData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportResultData;
import com.mirakl.hybris.beans.ProductImportSuccessData;
import com.mirakl.hybris.core.product.strategies.ProductImportLineResultHandler;
import com.mirakl.hybris.core.product.strategies.ProductImportResultHandler;

public abstract class AbstractProductImportResultHandler implements ProductImportResultHandler {

  private static final Logger LOG = Logger.getLogger(AbstractProductImportResultHandler.class);

  protected MiraklCatalogIntegrationFrontApi mciApi;

  protected void processImportResultQueue(ProductImportFileContextData context,
      ProductImportLineResultHandler<ProductImportSuccessData> successHandler,
      ProductImportLineResultHandler<ProductImportErrorData> errorHandler) throws InterruptedException {
    while (true) {
      ProductImportResultData resultData = context.getImportResultQueue().take();
      if (resultData.isTerminationSignal()) {
        displayTerminationMessage(context);
        break;
      }

      if (resultData instanceof ProductImportErrorData) {
        handleResult((ProductImportErrorData) resultData, errorHandler, false);
      } else if (resultData instanceof ProductImportSuccessData) {
        handleResult((ProductImportSuccessData) resultData, successHandler, true);
      } else {
        LOG.error(format("No handler defined for result of type [%s]", resultData.getClass()));
      }
    }
  }

  protected <T extends ProductImportResultData> void handleResult(T resultData, ProductImportLineResultHandler<T> handler,
      boolean success) {
    if (LOG.isDebugEnabled()) {
      LOG.debug(format("%s line [%s]", success ? "Imported" : "Rejected", resultData.getRowNumber()));
    }
    handleResult(resultData, handler);
  }

  protected <T extends ProductImportResultData> void handleResult(T resultData, ProductImportLineResultHandler<T> handler) {
    handler.handleLineResult(resultData);
  }

  protected void displayTerminationMessage(ProductImportFileContextData context) {
    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Received termination signal for file [%s]", context.getFullFilename()));
    }
  }

  @Required
  public void setMciApi(MiraklCatalogIntegrationFrontApi mciApi) {
    this.mciApi = mciApi;
  }
}
