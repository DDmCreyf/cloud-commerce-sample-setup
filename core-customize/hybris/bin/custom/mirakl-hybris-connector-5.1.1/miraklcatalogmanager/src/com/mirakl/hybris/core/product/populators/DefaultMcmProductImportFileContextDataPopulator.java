package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.PRODUCTS_IMPORT_RESULT_QUEUE_LENGTH;

import java.io.File;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.beans.ProductImportResultData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.util.ServicesUtil;

public class DefaultMcmProductImportFileContextDataPopulator
    implements Populator<Pair<ProductImportGlobalContextData, File>, ProductImportFileContextData> {

  protected ConfigurationService configurationService;

  @Override
  public void populate(Pair<ProductImportGlobalContextData, File> source, ProductImportFileContextData target)
      throws ConversionException {
    ProductImportGlobalContextData globalContext = source.getLeft();
    File file = source.getRight();
    ServicesUtil.validateParameterNotNullStandardMessage("globalContext", globalContext);
    ServicesUtil.validateParameterNotNullStandardMessage("file", file);

    target.setGlobalContext(globalContext);
    target.setFullFilename(file.getName());
    target.setReceivedFile(file);
    target.setImportResultQueue(new LinkedBlockingDeque<ProductImportResultData>(
        configurationService.getConfiguration().getInt(PRODUCTS_IMPORT_RESULT_QUEUE_LENGTH, 200)));
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }
}
