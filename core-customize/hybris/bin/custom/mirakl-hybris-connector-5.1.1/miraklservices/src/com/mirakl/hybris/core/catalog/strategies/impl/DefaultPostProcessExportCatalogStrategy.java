package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.toByteArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.strategies.PostProcessExportCatalogStrategy;
import com.mirakl.hybris.core.model.MiraklExportCatalogCronJobModel;

import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultPostProcessExportCatalogStrategy implements PostProcessExportCatalogStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultPostProcessExportCatalogStrategy.class);

  protected MediaService mediaService;
  protected ModelService modelService;

  @Override
  public void postProcess(MiraklExportCatalogCronJobModel cronJob, MiraklExportCatalogContext context) throws IOException {
    resetCronJobMedias(cronJob);

    if (context.getExportConfig().isExportCategories()) {
      cronJob.setCategoriesMedia(createMediaFromExportFile(context.getWriter().getCategoriesFile()));
    }
    if (context.getExportConfig().isExportAttributes()) {
      cronJob.setAttributesMedia(createMediaFromExportFile(context.getWriter().getAttributesFile()));
    }
    if (context.getExportConfig().isExportValueLists()) {
      cronJob.setValueListsMedia(createMediaFromExportFile(context.getWriter().getValueListsFile()));
    }
    modelService.save(cronJob);
  }

  protected void resetCronJobMedias(MiraklExportCatalogCronJobModel cronJob) {
    removeMedia(cronJob.getCategoriesMedia());
    removeMedia(cronJob.getAttributesMedia());
    removeMedia(cronJob.getValueListsMedia());
    cronJob.setCategoriesMedia(null);
    cronJob.setAttributesMedia(null);
    cronJob.setValueListsMedia(null);
  }

  protected MediaModel createMediaFromExportFile(File file) throws IOException {
    CatalogUnawareMediaModel media = modelService.create(CatalogUnawareMediaModel.class);
    media.setCode(file.getName());
    modelService.save(media);
    mediaService.setDataForMedia(media, toByteArray(new FileInputStream(file)));

    return media;
  }

  protected void removeMedia(MediaModel media) {
    if (media != null) {
      try {
        modelService.remove(media);
      } catch (Exception e) {
        LOG.error(format("unable to remove media [%s]", media.getCode()), e);
      }
    }
  }

  @Required
  public void setMediaService(MediaService mediaService) {
    this.mediaService = mediaService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }
}
