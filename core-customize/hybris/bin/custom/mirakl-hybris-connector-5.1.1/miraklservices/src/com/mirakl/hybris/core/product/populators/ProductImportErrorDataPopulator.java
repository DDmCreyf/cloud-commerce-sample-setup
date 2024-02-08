package com.mirakl.hybris.core.product.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.mirakl.hybris.beans.ProductImportErrorData;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ProductImportErrorDataPopulator implements Populator<ProductImportException, ProductImportErrorData> {

  @Override
  public void populate(ProductImportException source, ProductImportErrorData target) throws ConversionException {
    validateParameterNotNullStandardMessage("rawProduct", source.getRawProduct());

    target.setLineValues(source.getRawProduct().getValues());
    target.setRowNumber(source.getRawProduct().getRowNumber());
    target.setErrorMessage(getErrorMessage(source, target));
  }

  protected String getErrorMessage(ProductImportException source, ProductImportErrorData target) {
    if (isNotBlank(source.getMessage())) {
      return source.getMessage();
    }

    if (source.getCause() != null && isNotBlank(source.getCause().getMessage())) {
      return source.getCause().getMessage();
    }
    return null;
  }

}
