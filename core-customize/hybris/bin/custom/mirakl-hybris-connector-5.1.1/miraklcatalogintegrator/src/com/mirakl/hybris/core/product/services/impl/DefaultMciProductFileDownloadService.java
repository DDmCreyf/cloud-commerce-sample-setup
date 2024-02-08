package com.mirakl.hybris.core.product.services.impl;

import static com.mirakl.client.mci.domain.product.MiraklProductImportStatus.valueOf;
import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.PRODUCTS_IMPORT_STATUSES_PAGESIZE;
import static com.mirakl.hybris.core.util.PaginationUtils.getNumberOfPages;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.mirakl.hybris.core.util.PaginationUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.io.Files;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mci.domain.product.MiraklProductImportResult;
import com.mirakl.client.mci.domain.product.MiraklProductImportResults;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mci.front.request.product.MiraklDownloadProductImportTransformedFileRequest;
import com.mirakl.client.mci.front.request.product.MiraklProductImportStatusesRequest;
import com.mirakl.hybris.core.enums.MiraklProductImportStatus;
import com.mirakl.hybris.core.product.services.MciProductFileDownloadService;

import de.hybris.platform.servicelayer.config.ConfigurationService;

public class DefaultMciProductFileDownloadService implements MciProductFileDownloadService {

  private static final Logger LOG = Logger.getLogger(DefaultMciProductFileDownloadService.class);

  protected MiraklCatalogIntegrationFrontApi mciApi;
  protected ConfigurationService configurationService;

  @Override
  public List<String> getImportIds(Date since, String shopId, Collection<MiraklProductImportStatus> statuses) {
    List<String> importIds = new ArrayList<>();
    MiraklProductImportResults importResults = getImportResults(statuses, since, shopId, 0);
    addImportIds(importResults, statuses, importIds);
    for (int page = 1; page < getNumberOfPages(importResults.getTotalCount(), getPageSize()); page++) {
      importResults = getImportResults(statuses, since, shopId, page);
      addImportIds(importResults, statuses, importIds);
    }
    return importIds;
  }

  protected MiraklProductImportResults getImportResults(Collection<MiraklProductImportStatus> statuses, Date since, String shopId,
      int page) {
    MiraklProductImportStatusesRequest request = new MiraklProductImportStatusesRequest();
    request.setLastRequestDate(since);
    request.setShopId(shopId);
    request.setHasTransformedFile(true);
    request = PaginationUtils.applyMiraklPagination(request, getPageSize(), page * getPageSize());
    if (!isEmpty(statuses) && statuses.size() == 1) {
      request.setProductImportStatus(valueOf(statuses.iterator().next().toString()));
    }
    return mciApi.getProductImportStatuses(request);
  }

  protected List<String> addImportIds(MiraklProductImportResults importResults, Collection<MiraklProductImportStatus> statuses,
      List<String> importIds) {
    for (MiraklProductImportResult importResult : importResults.getProductImportResults()) {
      MiraklProductImportStatus importStatus = MiraklProductImportStatus.valueOf(importResult.getImportStatus().toString());
      if (isEmpty(statuses) || statuses.contains(importStatus)) {
        importIds.add(importResult.getImportId());
      }
    }
    return importIds;
  }

  @Override
  public int downloadProductFiles(List<String> importIds, File targetDirectory) {
    int downloadedFiles = 0;
    for (String importId : importIds) {
      try {
        File productFile =
            mciApi.downloadProductImportTransformedFile(new MiraklDownloadProductImportTransformedFileRequest(importId));
        Files.move(productFile, new File(targetDirectory, productFile.getName()));
        downloadedFiles++;
      } catch (IOException e) {
        LOG.error(String.format("Impossible to move product file [%s] to [%s]", importId, targetDirectory), e);
      } catch (MiraklApiException e) {
        LOG.error(String.format("Impossible to download product file [%s]", importId), e);
      }
    }
    return downloadedFiles;
  }

  protected int getPageSize() {
    return configurationService.getConfiguration().getInt(PRODUCTS_IMPORT_STATUSES_PAGESIZE, 100);
  }

  @Required
  public void setMciApi(MiraklCatalogIntegrationFrontApi mciApi) {
    this.mciApi = mciApi;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

}
