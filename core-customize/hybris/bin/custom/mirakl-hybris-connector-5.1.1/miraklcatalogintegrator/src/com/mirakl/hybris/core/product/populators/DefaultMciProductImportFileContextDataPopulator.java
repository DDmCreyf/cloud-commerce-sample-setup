package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.PRODUCTS_IMPORT_RESULT_QUEUE_LENGTH;
import static java.lang.String.format;

import java.io.File;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.beans.ProductImportResultData;
import com.mirakl.hybris.core.shop.services.ShopService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.util.ServicesUtil;

public class DefaultMciProductImportFileContextDataPopulator
    implements Populator<Pair<ProductImportGlobalContextData, File>, ProductImportFileContextData> {

  protected ConfigurationService configurationService;
  protected ShopService shopService;

  @Override
  public void populate(Pair<ProductImportGlobalContextData, File> source, ProductImportFileContextData target)
      throws ConversionException {
    ProductImportGlobalContextData globalContext = source.getLeft();
    File file = source.getRight();
    ServicesUtil.validateParameterNotNullStandardMessage("globalContext", globalContext);
    ServicesUtil.validateParameterNotNullStandardMessage("file", file);

    target.setGlobalContext(globalContext);
    target.setImportResultQueue(new LinkedBlockingDeque<ProductImportResultData>(
        configurationService.getConfiguration().getInt(PRODUCTS_IMPORT_RESULT_QUEUE_LENGTH, 200)));
    Matcher matcher = getPatternMatcher(file, globalContext);
    verifyShopExistence(matcher.group(1));
    target.setShopId(matcher.group(1));
    target.setMiraklImportId(matcher.group(2));
    target.setShopFilename(matcher.group(3));
    target.setFullFilename(file.getName());
    target.setReceivedFile(file);
  }

  protected Matcher getPatternMatcher(File file, ProductImportGlobalContextData globalContext) {
    Pattern pattern = Pattern.compile(globalContext.getInputFilePattern());
    Matcher matcher = pattern.matcher(file.getName());
    if (!matcher.matches()) {
      throw new IllegalArgumentException(
          format("Input file names should follow this pattern: [%s]", globalContext.getInputFilePattern()));
    }
    return matcher;
  }

  protected void verifyShopExistence(String shopId) {
    if (shopService.getShopForId(shopId) == null) {
      throw new UnknownIdentifierException(format("Unable to find shop with id [%s]", shopId));
    }
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setShopService(ShopService shopService) {
    this.shopService = shopService;
  }
}
