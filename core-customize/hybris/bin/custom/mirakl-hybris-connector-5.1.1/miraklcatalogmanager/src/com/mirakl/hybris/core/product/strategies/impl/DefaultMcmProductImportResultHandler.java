package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.client.mci.request.product.MiraklProductDataSheetSyncRequest.builder;
import static com.mirakl.hybris.core.constants.MiraklcatalogmanagerConstants.MCM_MIRAKL_ACCEPTANCE_STATUS_HEADER;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mci.request.product.MiraklProductDataSheetSyncRequest;
import com.mirakl.client.mci.request.product.MiraklProductDataSheetSyncRequest.MiraklProductDataSheetSyncRequestBuilder;
import com.mirakl.hybris.beans.ProductImportErrorData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportResultData;
import com.mirakl.hybris.beans.ProductImportSuccessData;
import com.mirakl.hybris.core.product.strategies.McmProductAcceptanceStrategy;
import com.mirakl.hybris.core.product.strategies.McmProductExportStrategy;
import com.mirakl.hybris.core.product.strategies.ProductImportLineResultHandler;

import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMcmProductImportResultHandler extends AbstractProductImportResultHandler implements BeanFactoryAware {

  private static final Logger LOG = Logger.getLogger(DefaultMcmProductImportResultHandler.class);

  protected static final String PRODUCT_IMPORT_SUCCESS_LINE_RESULT_HANDLER = "mcmProductImportSuccessLineResultHandler";
  protected static final String PRODUCT_IMPORT_ERROR_LINE_RESULT_HANDLER = "mcmProductImportErrorLineResultHandler";


  protected ModelService modelService;
  protected McmProductExportStrategy mcmProductExportStrategy;
  protected McmProductAcceptanceStrategy productAcceptanceStrategy;
  protected BeanFactory beanFactory;

  @Override
  public void handleImportResults(ProductImportFileContextData context) {
    validateParameterNotNullStandardMessage("context", context);
    validateParameterNotNullStandardMessage("importResultQueue", context.getImportResultQueue());

    try {
      MiraklProductDataSheetSyncRequestBuilder builder = builder();
      handleImportResults(context, builder);
    } catch (IOException e) {
      LOG.error(format("An error occurred in the error handler thread for file [%s]", context.getReceivedFile().getName()), e);
    }
  }

  @Override
  protected <T extends ProductImportResultData> void handleResult(T resultData, ProductImportLineResultHandler<T> handler) {
    if (!acceptanceIsPending(resultData)) {
      super.handleResult(resultData, handler);
    }
  }

  protected boolean acceptanceIsPending(ProductImportResultData resultData) {
    Collection<String> exportableAcceptanceStatusCodes = productAcceptanceStrategy.getExportableAcceptanceStatusCodes();
    Map<String, String> lineValues = resultData.getLineValues();
    return !exportableAcceptanceStatusCodes.contains(lineValues.get(MCM_MIRAKL_ACCEPTANCE_STATUS_HEADER));
  }

  protected void handleImportResults(ProductImportFileContextData context, MiraklProductDataSheetSyncRequestBuilder builder) {
    try (ProductImportLineResultHandler<ProductImportSuccessData> successHandler = getSuccessHandler(context, builder);
        ProductImportLineResultHandler<ProductImportErrorData> errorHandler = getErrorHandler(context, builder)) {

      processImportResultQueue(context, successHandler, errorHandler);
      confirmReceivedProducts(builder.build(), context);

    } catch (InterruptedException e) {
      LOG.warn(format("Error handler thread for file [%s] was interrupted", context.getReceivedFile().getName()), e);
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      LOG.error(format("An error occurred in the error handler thread for file [%s]", context.getReceivedFile().getName()), e);
    }
  }

  protected void confirmReceivedProducts(MiraklProductDataSheetSyncRequest request, ProductImportFileContextData context)
      throws IOException {
    mcmProductExportStrategy.sendRequest(request, modelService.get(context.getGlobalContext().getProductCatalogVersion()));
  }


  @SuppressWarnings("unchecked")
  protected ProductImportLineResultHandler<ProductImportSuccessData> getSuccessHandler(ProductImportFileContextData context,
      MiraklProductDataSheetSyncRequestBuilder builder) {
    return (ProductImportLineResultHandler<ProductImportSuccessData>) beanFactory
        .getBean(PRODUCT_IMPORT_SUCCESS_LINE_RESULT_HANDLER, builder, context);
  }

  @SuppressWarnings("unchecked")
  protected ProductImportLineResultHandler<ProductImportErrorData> getErrorHandler(ProductImportFileContextData context,
      MiraklProductDataSheetSyncRequestBuilder builder) {
    return (ProductImportLineResultHandler<ProductImportErrorData>) beanFactory.getBean(PRODUCT_IMPORT_ERROR_LINE_RESULT_HANDLER,
        builder, context);
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Required
  public void setProductAcceptanceStrategy(McmProductAcceptanceStrategy productAcceptanceStrategy) {
    this.productAcceptanceStrategy = productAcceptanceStrategy;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setMcmProductExportStrategy(McmProductExportStrategy mcmProductExportStrategy) {
    this.mcmProductExportStrategy = mcmProductExportStrategy;
  }
}
