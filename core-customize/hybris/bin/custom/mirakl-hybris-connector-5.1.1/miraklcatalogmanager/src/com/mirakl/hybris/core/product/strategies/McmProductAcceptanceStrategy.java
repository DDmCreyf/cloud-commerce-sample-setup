package com.mirakl.hybris.core.product.strategies;

import java.util.Collection;

import com.mirakl.client.mci.domain.product.MiraklSynchronizedProductDataSheetAcceptance;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

import de.hybris.platform.core.model.product.ProductModel;

public interface McmProductAcceptanceStrategy {
  /**
   * Returns the MiraklSynchronizedProductDataSheetAcceptance for the given product. The
   * MiraklSynchronizedProductDataSheetAcceptance is sent to Mirakl during MCM product exports (CM21)
   *
   * @param product
   * @return the MiraklSynchronizedProductDataSheetAcceptance for the given product
   */
  MiraklSynchronizedProductDataSheetAcceptance getAcceptance(ProductModel product);

  /**
   * Returns the article Hybris approval status code for the given raw product.
   *
   * @param rawProduct
   * @return the article approval status code
   */
  String getArticleApprovalStatusCode(MiraklRawProductModel rawProduct);

  /**
   * Returns the rejection reason code (defined in Mirakl) for the given product. Falls back to a rejection code by default if
   * none is provided. The reason code is sent to Mirakl during MCM product exports (CM21)
   *
   * @param product
   * @return the rejection reason code for the given product.
   */
  String getRejectionReasonCode(ProductModel product);

  /**
   * Returns the product approval status to set on new products (CM51) that are not approved yet in Mirakl
   * 
   * @return the code of the default article approval status
   */
  String getDefaultArticleApprovalStatusCode();

  /**
   * Returns the Hybris approval status codes having an equivalent in Mirakl ( = acceptance status)
   *
   * @return a collection of approval status codes
   */
  Collection<String> getMappableApprovalStatusCodes();

  /**
   * Returns the Mirakl Acceptance status codes which can be exported using CM21
   *
   * @return a collection of acceptance status codes
   */
  Collection<String> getExportableAcceptanceStatusCodes();
}
