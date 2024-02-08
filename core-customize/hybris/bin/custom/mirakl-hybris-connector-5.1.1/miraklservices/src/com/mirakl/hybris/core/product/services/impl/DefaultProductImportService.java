package com.mirakl.hybris.core.product.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportErrorData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.beans.ProductImportSuccessData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;
import com.mirakl.hybris.core.product.services.ProductImportService;
import com.mirakl.hybris.core.product.strategies.PostProcessProductLineImportStrategy;
import com.mirakl.hybris.core.product.strategies.ProductCreationStrategy;
import com.mirakl.hybris.core.product.strategies.ProductIdentificationStrategy;
import com.mirakl.hybris.core.product.strategies.ProductImportCredentialCheckStrategy;
import com.mirakl.hybris.core.product.strategies.ProductImportValidationStrategy;
import com.mirakl.hybris.core.product.strategies.ProductReceptionCheckStrategy;
import com.mirakl.hybris.core.product.strategies.ProductUpdateStrategy;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionBody;
import de.hybris.platform.variants.model.VariantProductModel;

public class DefaultProductImportService implements ProductImportService {

  private static final Logger LOG = Logger.getLogger(DefaultProductImportService.class);

  protected ModelService modelService;
  protected ProductIdentificationStrategy productIdentificationStrategy;
  protected ProductImportValidationStrategy productImportValidationStrategy;
  protected ProductCreationStrategy productCreationStrategy;
  protected ProductUpdateStrategy productUpdateStrategy;
  protected ProductImportCredentialCheckStrategy credentialCheckStrategy;
  protected List<PostProcessProductLineImportStrategy> postProcessProductLineImportStrategies;
  protected Converter<Pair<MiraklRawProductModel, ProductImportFileContextData>, ProductImportData> productImportDataConverter;
  protected Converter<ProductImportException, ProductImportErrorData> errorDataConverter;
  protected ProductReceptionCheckStrategy productReceptionCheckStrategy;

  @Override
  public void importProducts(Collection<MiraklRawProductModel> variants, ProductImportFileContextData context) {
    validate(variants, context);

    for (MiraklRawProductModel variant : variants) {
      try {
        Transaction.current().execute(new TransactionBody() {

          @SuppressWarnings("unchecked")
          @Override
          public Object execute() throws Exception {
            importProduct(variant, context);
            return null;
          }
        });
      } catch (ProductImportException e) {
        LOG.error(format("An error occurred during product import. Raw line: [%s]", variant.getValues()), e);
        writeErrorToResultQueue(e, context);
      } catch (Exception e) {
        LOG.error(format("Unable to import product. Raw line: [%s]", variant.getValues()), e);
        writeErrorToResultQueue(new ProductImportException(variant, e), context);
      }
    }
  }

  protected void importProduct(MiraklRawProductModel variant, ProductImportFileContextData context)
      throws ProductImportException {
    if (productReceptionCheckStrategy.isAlreadyReceived(variant, context)) {
      productReceptionCheckStrategy.handleAlreadyReceived(variant, context);
      return;
    }
    validateProduct(variant, context);
    ProductImportData data = productImportDataConverter.convert(Pair.of(variant, context));
    identifyProduct(data);

    ProductModel productToUpdate = data.getIdentifiedProduct();
    if (productToUpdate == null) {
      productToUpdate = createProduct(data, context);
    }

    data.setProductToUpdate(productToUpdate);
    data.setRootBaseProductToUpdate(getRootBaseProduct(productToUpdate));

    applyReceivedValues(data, context);
    postProcessProductLineImport(data, variant, context);
    modelService.saveAll(data.getModelsToSave());
    writeSuccessToResultQueue(data, null, context);
  }

  protected void validate(Collection<MiraklRawProductModel> variants, ProductImportFileContextData context) {
    validateParameterNotNullStandardMessage("context", context);
    ProductImportGlobalContextData globalContext = context.getGlobalContext();
    validateParameterNotNullStandardMessage("globalContext", globalContext);
    validateParameterNotNullStandardMessage("productCatalogVersion", globalContext.getProductCatalogVersion());
    validateParameterNotNullStandardMessage("variantAttributesPerType", globalContext.getVariantAttributesPerType());
    validateParameterNotNullStandardMessage("variantTypeHierarchyPerType", globalContext.getVariantTypeHierarchyPerType());
    validateParameterNotNullStandardMessage("miraklCatalogSystem", globalContext.getMiraklCatalogSystem());
  }

  protected ProductModel createProduct(ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException {
    credentialCheckStrategy.checkProductCreationCredentials(data, context);
    return productCreationStrategy.createProduct(data, context);
  }

  protected void validateProduct(MiraklRawProductModel miraklRawProduct, ProductImportFileContextData context)
      throws ProductImportException {
    productImportValidationStrategy.validate(miraklRawProduct, context);
  }

  protected void identifyProduct(ProductImportData productImportData) throws ProductImportException {
    productIdentificationStrategy.identifyProduct(productImportData);
  }

  protected void applyReceivedValues(ProductImportData data, ProductImportFileContextData context) throws ProductImportException {
    credentialCheckStrategy.checkProductUpdateCredentials(data, context);
    productUpdateStrategy.applyValues(data, context);
  }

  protected void writeErrorToResultQueue(ProductImportException exception, ProductImportFileContextData context) {
    try {
      context.getImportResultQueue().put(errorDataConverter.convert(exception));
    } catch (InterruptedException e) {
      LOG.warn(format("Unable to write to the error queue. Line value: [%s], Line number: [%s]",
          exception.getRawProduct().getValues(), exception.getRawProduct().getRowNumber()), e);
      Thread.currentThread().interrupt();
    }
  }

  protected void writeSuccessToResultQueue(ProductImportData data, String message, ProductImportFileContextData context) {
    MiraklRawProductModel rawProduct = data.getRawProduct();
    try {
      ProductImportSuccessData successData = new ProductImportSuccessData();
      successData.setLineValues(rawProduct.getValues());
      successData.setRowNumber(rawProduct.getRowNumber());
      successData.setAdditionalMessage(message);
      if (data.getProductToUpdate() != null) {
        successData.setProductCode(data.getProductToUpdate().getCode());
      }
      context.getImportResultQueue().put(successData);
    } catch (InterruptedException e) {
      LOG.warn(format("Unable to write to the success queue. Line value: [%s], Line number: [%s]", rawProduct.getValues(),
          rawProduct.getRowNumber()), e);
      Thread.currentThread().interrupt();
    }
  }

  protected void postProcessProductLineImport(ProductImportData data, MiraklRawProductModel variant,
      ProductImportFileContextData context) {
    for (PostProcessProductLineImportStrategy strategy : postProcessProductLineImportStrategies) {
      strategy.postProcess(data, variant, context);
    }
  }

  protected ProductModel getRootBaseProduct(ProductModel identifiedProduct) {
    ProductModel rootBaseProduct = identifiedProduct;
    while (rootBaseProduct instanceof VariantProductModel) {
      rootBaseProduct = ((VariantProductModel) rootBaseProduct).getBaseProduct();
    }
    return rootBaseProduct;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setProductCreationStrategy(ProductCreationStrategy productCreationStrategy) {
    this.productCreationStrategy = productCreationStrategy;
  }

  @Required
  public void setProductIdentificationStrategy(ProductIdentificationStrategy productIdentificationStrategy) {
    this.productIdentificationStrategy = productIdentificationStrategy;
  }

  @Required
  public void setProductImportValidationStrategy(ProductImportValidationStrategy productImportValidationStrategy) {
    this.productImportValidationStrategy = productImportValidationStrategy;
  }

  @Required
  public void setProductUpdateStrategy(ProductUpdateStrategy productUpdateStrategy) {
    this.productUpdateStrategy = productUpdateStrategy;
  }

  @Required
  public void setCredentialCheckStrategy(ProductImportCredentialCheckStrategy setCredentialCheckStrategy) {
    this.credentialCheckStrategy = setCredentialCheckStrategy;
  }

  @Required
  public void setPostProcessProductLineImportStrategies(
      List<PostProcessProductLineImportStrategy> postProcessProductLineImportStrategies) {
    this.postProcessProductLineImportStrategies = postProcessProductLineImportStrategies;
  }

  @Required
  public void setProductImportDataConverter(
      Converter<Pair<MiraklRawProductModel, ProductImportFileContextData>, ProductImportData> productImportDataConverter) {
    this.productImportDataConverter = productImportDataConverter;
  }

  @Required
  public void setErrorDataConverter(Converter<ProductImportException, ProductImportErrorData> errorDataConverter) {
    this.errorDataConverter = errorDataConverter;
  }

  @Required
  public void setProductReceptionCheckStrategy(ProductReceptionCheckStrategy productReceptionCheckStrategy) {
    this.productReceptionCheckStrategy = productReceptionCheckStrategy;
  }

}
