package com.mirakl.hybris.core.product.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.io.Files;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mci.request.product.MiraklGetProductDataSheetsRequest;
import com.mirakl.hybris.beans.ProductDataSheetDownloadParams;
import com.mirakl.hybris.core.enums.MarketplaceProductAcceptanceStatus;
import com.mirakl.hybris.core.product.services.McmProductFileDownloadService;

import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultMcmProductFileDownloadService implements McmProductFileDownloadService {

  private static final Logger LOG = Logger.getLogger(DefaultMcmProductFileDownloadService.class);

  protected Converter<ProductDataSheetDownloadParams, MiraklGetProductDataSheetsRequest> productDataSheetsRequestConverter;
  protected MiraklCatalogIntegrationFrontApi mciApi;

  @Override
  @Deprecated
  public boolean downloadProductDataSheetsFile(Date since, Set<MarketplaceProductAcceptanceStatus> acceptanceStatuses,
      File targetDirectory) {
    ProductDataSheetDownloadParams params = new ProductDataSheetDownloadParams();
    params.setAcceptanceStatuses(acceptanceStatuses);
    params.setTargetDirectory(targetDirectory);
    params.setUpdatedSince(since);

    return downloadProductDataSheetsFile(params);
  }

  @Override
  public boolean downloadProductDataSheetsFile(ProductDataSheetDownloadParams params) {
    try {
      MiraklGetProductDataSheetsRequest request = productDataSheetsRequestConverter.convert(params);
      File productFile = mciApi.getProductDataSheetsFile(request);
      if (productFile != null) {
        Files.move(productFile, new File(params.getTargetDirectory(), productFile.getName()));
      }
      return true;
    } catch (IOException e) {
      LOG.error("Impossible to move product datasheets file", e);
    } catch (MiraklApiException e) {
      LOG.error("Impossible to download product datasheets file", e);
    }
    return false;
  }

  @Required
  public void setProductDataSheetsRequestConverter(
      Converter<ProductDataSheetDownloadParams, MiraklGetProductDataSheetsRequest> productDataSheetsRequestConverter) {
    this.productDataSheetsRequestConverter = productDataSheetsRequestConverter;
  }

  @Required
  public void setMciApi(MiraklCatalogIntegrationFrontApi mciApi) {
    this.mciApi = mciApi;
  }


}
