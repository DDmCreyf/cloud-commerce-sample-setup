package com.mirakl.hybris.core.product.exceptions;

import com.mirakl.hybris.core.model.MiraklRawProductModel;

import de.hybris.platform.servicelayer.exceptions.BusinessException;

public class ProductImportException extends BusinessException {

  private static final long serialVersionUID = 674701261456832264L;

  protected MiraklRawProductModel rawProduct;

  public ProductImportException(MiraklRawProductModel rawProduct, Throwable cause) {
    super(cause);
    this.rawProduct = rawProduct;
  }

  public ProductImportException(MiraklRawProductModel rawProduct, String message, Throwable cause) {
    super(message, cause);
    this.rawProduct = rawProduct;
  }

  public ProductImportException(MiraklRawProductModel rawProduct, String message) {
    super(message);
    this.rawProduct = rawProduct;
  }

  public MiraklRawProductModel getRawProduct() {
    return rawProduct;
  }

}
