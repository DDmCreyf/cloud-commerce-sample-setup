package com.mirakl.hybris.core.media.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.toByteArray;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.media.services.MiraklMediaService;

import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMiraklMediaService implements MiraklMediaService {

  private static final Logger LOG = Logger.getLogger(DefaultMiraklMediaService.class);

  protected ModelService modelService;
  protected MediaService mediaService;

  @Override
  public MediaModel downloadMedia(String mediaId, String downloadUrl) {
    return downloadMedia(mediaId, downloadUrl, false);
  }

  @Override
  public MediaModel downloadMedia(String mediaId, String downloadUrl, boolean forceDownload) {
    validateParameterNotNull(mediaId, "Media ID can not be null");
    validateParameterNotNull(downloadUrl, "Download URL of the media can not be null");
    try {
      URL url = new URL(downloadUrl);
      return getOrCreateMedia(mediaId, url, forceDownload);
    } catch (Exception e) {
      LOG.error(format("Unable to download media [id=%s, url=%s]", mediaId, downloadUrl), e);
      return null;
    }
  }

  protected MediaModel getOrCreateMedia(String mediaId, URL url, boolean forceDownload) throws IOException {
    try {
      return getMedia(mediaId, url, forceDownload);
    } catch (UnknownIdentifierException e) {
      return createMedia(mediaId, url);
    }
  }

  protected MediaModel getMedia(String mediaId, URL url, boolean forceDownload) throws IOException, UnknownIdentifierException {
    if (forceDownload) {
      downloadMediaFromURL(mediaService.getMedia(mediaId), url);
    }
    return mediaService.getMedia(mediaId);
  }

  protected MediaModel createMedia(String mediaId, URL url) throws IOException {
    MediaModel media;
    media = modelService.create(CatalogUnawareMediaModel.class);
    media.setCode(mediaId);
    modelService.save(media);
    downloadMediaFromURL(media, url);
    return media;
  }

  protected void downloadMediaFromURL(MediaModel media, URL url) throws IOException {
    mediaService.setDataForMedia(media, toByteArray(url.openStream()));
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setMediaService(MediaService mediaService) {
    this.mediaService = mediaService;
  }
}
