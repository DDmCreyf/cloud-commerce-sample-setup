package com.mirakl.hybris.core.product.strategies.impl;

import static java.lang.String.format;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mci.domain.product.MiraklSynchronizedProductDataSheetAcceptance;
import com.mirakl.client.mci.domain.product.MiraklSynchronizedProductDataSheetAcceptanceStatus;
import com.mirakl.hybris.core.enums.MiraklProductRejectionReason;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.strategies.McmProductAcceptanceStrategy;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;

public class DefaultMcmProductAcceptanceStrategy implements McmProductAcceptanceStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultMcmProductAcceptanceStrategy.class);

  protected static final MiraklProductRejectionReason DEFAULT_PRODUCT_REJECTION_REASON = MiraklProductRejectionReason.QUALITY;

  protected Map<String, String> hybrisToMiraklAcceptanceStatusMapping;
  protected Map<String, String> miraklToHybrisAcceptanceStatusMapping;

  @Override
  public MiraklSynchronizedProductDataSheetAcceptance getAcceptance(ProductModel product) {
    MiraklSynchronizedProductDataSheetAcceptance acceptanceStatus = new MiraklSynchronizedProductDataSheetAcceptance();
    String acceptanceStatusCode = hybrisToMiraklAcceptanceStatusMapping.get(product.getApprovalStatus().name());
    acceptanceStatus.setStatus(MiraklSynchronizedProductDataSheetAcceptanceStatus.valueOf(acceptanceStatusCode));
    if (acceptanceStatus.getStatus() == MiraklSynchronizedProductDataSheetAcceptanceStatus.REJECTED) {
      acceptanceStatus.setReasonCode(getRejectionReasonCode(product));
      acceptanceStatus.setMessage(product.getRejectionMessage());
    }
    return acceptanceStatus;
  }

  @Override
  public String getArticleApprovalStatusCode(MiraklRawProductModel rawProduct) {
    String approvalStatusCode = null;
    if (rawProduct.getAcceptanceStatus() != null) {
      approvalStatusCode = miraklToHybrisAcceptanceStatusMapping.get(rawProduct.getAcceptanceStatus().name());
    }
    return approvalStatusCode == null ? getDefaultArticleApprovalStatusCode() : approvalStatusCode;
  }

  @Override
  public String getRejectionReasonCode(ProductModel product) {
    if (product.getRejectionReason() == null) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(format("No default rejection reason specified for product [%s]. Using default reason [%s]", product.getCode(),
            DEFAULT_PRODUCT_REJECTION_REASON.getCode()));
      }
      return DEFAULT_PRODUCT_REJECTION_REASON.getCode();
    }

    return product.getRejectionReason().getCode();
  }

  @Override
  public String getDefaultArticleApprovalStatusCode() {
    return ArticleApprovalStatus.CHECK.name();
  }

  @Override
  public Collection<String> getMappableApprovalStatusCodes() {
    return hybrisToMiraklAcceptanceStatusMapping.keySet();
  }

  @Override
  public Collection<String> getExportableAcceptanceStatusCodes() {
    return new HashSet<>(hybrisToMiraklAcceptanceStatusMapping.values());
  }

  @Required
  public void setHybrisToMiraklAcceptanceStatusMapping(Map<String, String> hybrisToMiraklAcceptanceStatusMapping) {
    this.hybrisToMiraklAcceptanceStatusMapping = hybrisToMiraklAcceptanceStatusMapping;
  }

  @Required
  public void setMiraklToHybrisAcceptanceStatusMapping(Map<String, String> miraklToHybrisAcceptanceStatusMapping) {
    this.miraklToHybrisAcceptanceStatusMapping = miraklToHybrisAcceptanceStatusMapping;
  }


}
