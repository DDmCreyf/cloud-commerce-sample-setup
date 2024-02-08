package com.mirakl.hybris.core.product.strategies;

import java.io.Flushable;
import java.io.IOException;

import com.mirakl.hybris.beans.ProductImportResultData;

public interface ProductImportLineResultHandler<T extends ProductImportResultData> extends AutoCloseable, Flushable {

  /**
   * Creates the writers and the temporary files. Must be called after spring injection.
   *
   * @throws IOException If an error is encountered during writers creation
   */
  void initialize() throws IOException;

  /**
   * Handles the result of the import of a product line. By default, the handler writes the result into a file.
   *
   * @param result the result to handle
   */
  void handleLineResult(T result);

  /**
   * Returns the name of the result file.
   *
   * @return the name of the result file
   */
  String getFilename();

}
