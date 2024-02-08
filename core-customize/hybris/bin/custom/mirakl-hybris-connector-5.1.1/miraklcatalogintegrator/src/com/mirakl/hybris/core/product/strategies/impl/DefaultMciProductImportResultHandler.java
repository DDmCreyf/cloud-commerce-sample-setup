package com.mirakl.hybris.core.product.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.mirakl.client.mci.front.request.product.MiraklUpdateProductImportStatusRequest;
import com.mirakl.client.mci.request.product.AbstractMiraklUpdateProductImportStatusRequest.ProductImportStatus;
import com.mirakl.hybris.beans.ProductImportErrorData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportSuccessData;
import com.mirakl.hybris.core.product.strategies.ProductImportLineResultHandler;

public class DefaultMciProductImportResultHandler extends AbstractProductImportResultHandler implements BeanFactoryAware {

  private static final Logger LOG = Logger.getLogger(DefaultMciProductImportResultHandler.class);

  protected static final String PRODUCT_IMPORT_SUCCESS_LINE_RESULT_HANDLER = "productImportSuccessLineResultHandler";
  protected static final String PRODUCT_IMPORT_ERROR_LINE_RESULT_HANDLER = "productImportErrorLineResultHandler";

  protected BeanFactory beanFactory;

  @Override
  public void handleImportResults(ProductImportFileContextData context) {
    validateParameterNotNullStandardMessage("context", context);
    validateParameterNotNullStandardMessage("importResultQueue", context.getImportResultQueue());

    try (ProductImportLineResultHandler<ProductImportSuccessData> successHandler = getSuccessHandler(context);
         ProductImportLineResultHandler<ProductImportErrorData> errorHandler = getErrorHandler(context)) {

      processImportResultQueue(context, successHandler, errorHandler);
      updateProductImportStatus(context, successHandler, errorHandler);

    } catch (InterruptedException e) {
      LOG.warn(format("Error handler thread for file [%s] was interrupted", context.getReceivedFile().getName()), e);
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      LOG.error(format("An error occurred in the error handler thread for file [%s]", context.getReceivedFile().getName()), e);
    }

  }

  protected void updateProductImportStatus(ProductImportFileContextData context,
      ProductImportLineResultHandler<ProductImportSuccessData> successHandler,
      ProductImportLineResultHandler<ProductImportErrorData> errorHandler) throws IOException {
    try {
      successHandler.flush();
      errorHandler.flush();
      mciApi.updateProductImportStatus(buildRequest(successHandler.getFilename(), errorHandler.getFilename(), context));
    } catch (FileNotFoundException e) {
      LOG.error(format("Unable to update product import status in Mirakl for file [%s]", context.getFullFilename()), e);
    }
  }

  protected MiraklUpdateProductImportStatusRequest buildRequest(String successFilename, String errorFilename,
      ProductImportFileContextData context) throws FileNotFoundException {
    MiraklUpdateProductImportStatusRequest request =
        new MiraklUpdateProductImportStatusRequest(context.getMiraklImportId(), ProductImportStatus.COMPLETE);
    if (context.getErrorFile() != null) {
      request.setErrorFilename(errorFilename);
      request.setErrorsStream(new FileInputStream(context.getErrorFile()));
    }
    if (context.getSuccessFile() != null) {
      request.setImporterProductsFilename(successFilename);
      request.setImportedProductsStream(new FileInputStream(context.getSuccessFile()));
    }

    return request;
  }

  @SuppressWarnings("unchecked")
  protected ProductImportLineResultHandler<ProductImportSuccessData> getSuccessHandler(ProductImportFileContextData context) {
    return (ProductImportLineResultHandler<ProductImportSuccessData>) beanFactory
        .getBean(PRODUCT_IMPORT_SUCCESS_LINE_RESULT_HANDLER, context);
  }

  @SuppressWarnings("unchecked")
  protected ProductImportLineResultHandler<ProductImportErrorData> getErrorHandler(ProductImportFileContextData context) {
    return (ProductImportLineResultHandler<ProductImportErrorData>) beanFactory.getBean(PRODUCT_IMPORT_ERROR_LINE_RESULT_HANDLER,
        context);
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }
}
