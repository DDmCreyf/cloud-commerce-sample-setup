package com.mirakl.hybris.core.product.strategies.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import com.mirakl.hybris.beans.ProductImportErrorData;
import com.mirakl.hybris.beans.ProductImportFileContextData;

public class DefaultProductImportErrorLineResultHandler extends AbstractProductImportLineResultHandler<ProductImportErrorData> {

  protected String errorLineHeader;
  protected String errorMessageHeader;

  public DefaultProductImportErrorLineResultHandler(ProductImportFileContextData context) {
    super(context);
  }

  @Override
  public void initialize() throws IOException {
    errorLineHeader = configurationService.getConfiguration().getString(PRODUCTS_IMPORT_ERROR_LINE_HEADER);
    errorMessageHeader = configurationService.getConfiguration().getString(PRODUCTS_IMPORT_ERROR_MESSAGE_HEADER);
    super.initialize();
  }

  @Override
  public String getFilename() {
    if (filename == null) {
      Configuration configuration = configurationService.getConfiguration();
      filename = context.getShopFilename() + configuration.getString(PRODUCTS_IMPORT_ERROR_FILENAME_SUFFIX);
    }
    return filename;
  }

  @Override
  protected Map<String, String> buildLine(ProductImportErrorData resultData) {
    Map<String, String> lineValues = newHashMap(resultData.getLineValues());
    lineValues.put(errorLineHeader, String.valueOf(resultData.getRowNumber()));
    lineValues.put(errorMessageHeader, resultData.getErrorMessage());

    return lineValues;
  }

  @Override
  protected String[] getHeader() {
    if (header == null) {
      List<String> headerList = newArrayList(context.getHeaderInfos().keySet());
      headerList.add(errorLineHeader);
      headerList.add(errorMessageHeader);
      header = headerList.toArray(new String[headerList.size()]);
    }
    return header;
  }

  @Override
  protected void storeFileInContext(File file) {
    context.setErrorFile(file);
  }

}
