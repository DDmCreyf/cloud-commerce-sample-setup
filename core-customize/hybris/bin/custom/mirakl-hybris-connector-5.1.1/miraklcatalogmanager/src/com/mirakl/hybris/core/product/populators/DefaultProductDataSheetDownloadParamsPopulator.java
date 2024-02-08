package com.mirakl.hybris.core.product.populators;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Date;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductDataSheetDownloadParams;
import com.mirakl.hybris.core.model.MiraklDownloadProductFilesCronjobModel;
import com.mirakl.hybris.core.product.strategies.DownloadProductFilesDirectorySelectionStrategy;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DefaultProductDataSheetDownloadParamsPopulator
implements Populator<MiraklDownloadProductFilesCronjobModel, ProductDataSheetDownloadParams> {

  protected DownloadProductFilesDirectorySelectionStrategy directorySelectionStrategy;

  @Override
  public void populate(MiraklDownloadProductFilesCronjobModel source, ProductDataSheetDownloadParams target)
      throws ConversionException {
    target.setAcceptanceStatuses(source.getAcceptanceStatuses());
    target.setUpdatedSince(getLastExecutionDate(source));
    target.setTargetDirectory(directorySelectionStrategy.getTargetDirectory(source));
    if (isNotEmpty(source.getMiraklCatalogs())) {
      target.setCatalogs(new HashSet<String>(source.getMiraklCatalogs()));
    }
  }

  protected Date getLastExecutionDate(MiraklDownloadProductFilesCronjobModel cronJob) {
    return cronJob.isFullDownload() ? null : cronJob.getLastExecutionDate();
  }

  @Required
  public void setDirectorySelectionStrategy(DownloadProductFilesDirectorySelectionStrategy directorySelectionStrategy) {
    this.directorySelectionStrategy = directorySelectionStrategy;
  }

}
