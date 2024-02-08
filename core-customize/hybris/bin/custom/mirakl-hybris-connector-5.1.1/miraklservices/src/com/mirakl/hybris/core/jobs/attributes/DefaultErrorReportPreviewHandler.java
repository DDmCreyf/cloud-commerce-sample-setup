package com.mirakl.hybris.core.jobs.attributes;

import com.mirakl.hybris.core.model.MiraklJobReportModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import org.springframework.beans.factory.annotation.Required;

public class DefaultErrorReportPreviewHandler extends AbstractDynamicAttributeHandler<String, MiraklJobReportModel> {

  protected static final String NO_ERROR_REPORT_FOUND = "No error report found";

  private MediaService mediaService;

  @Override
  public String get(MiraklJobReportModel exportReport) {
    MediaModel errorReport = exportReport.getErrorReport();

    if (errorReport == null) {
      return NO_ERROR_REPORT_FOUND;
    }

    byte[] dataFromMedia = mediaService.getDataFromMedia(errorReport);
    return new String(dataFromMedia);
  }

  @Required
  public void setMediaService(MediaService mediaService) {
    this.mediaService = mediaService;
  }
}
