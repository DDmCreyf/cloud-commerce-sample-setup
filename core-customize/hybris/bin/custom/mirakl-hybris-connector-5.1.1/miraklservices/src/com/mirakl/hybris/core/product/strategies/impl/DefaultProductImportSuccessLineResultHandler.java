package com.mirakl.hybris.core.product.strategies.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportSuccessData;

public class DefaultProductImportSuccessLineResultHandler
    extends AbstractProductImportLineResultHandler<ProductImportSuccessData> {

  protected String additionalMessageHeader;
  protected String originalLineNumberHeader;

  public DefaultProductImportSuccessLineResultHandler(ProductImportFileContextData context) {
    super(context);
  }

  @Override
  public void initialize() throws IOException {
    additionalMessageHeader = configurationService.getConfiguration().getString(PRODUCTS_IMPORT_ADDITIONAL_MESSAGE_HEADER);
    originalLineNumberHeader = configurationService.getConfiguration().getString(PRODUCTS_IMPORT_ORIGINAL_LINE_NUMBER_HEADER);
    super.initialize();
  }

  @Override
  public String getFilename() {
    if (filename == null) {
      Configuration configuration = configurationService.getConfiguration();
      filename = context.getShopFilename() + configuration.getString(PRODUCTS_IMPORT_SUCCESS_FILENAME_SUFFIX);
    }
    return filename;
  }

  @Override
  protected Map<String, String> buildLine(ProductImportSuccessData resultData) {
    Map<String, String> lineValues = newHashMap(resultData.getLineValues());
    lineValues.put(originalLineNumberHeader, String.valueOf(resultData.getRowNumber()));
    lineValues.put(additionalMessageHeader, resultData.getAdditionalMessage());

    return lineValues;
  }

  @Override
  protected String[] getHeader() {
    if (header == null) {
      List<String> headerList = newArrayList(context.getHeaderInfos().keySet());
      headerList.add(originalLineNumberHeader);
      headerList.add(additionalMessageHeader);
      header = headerList.toArray(new String[headerList.size()]);
    }
    return header;
  }

  @Override
  protected void storeFileInContext(File file) {
    context.setSuccessFile(file);
  }

}
