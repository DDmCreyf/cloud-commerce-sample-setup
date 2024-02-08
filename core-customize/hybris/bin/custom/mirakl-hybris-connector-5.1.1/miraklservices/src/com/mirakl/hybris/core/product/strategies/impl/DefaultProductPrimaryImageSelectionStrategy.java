package com.mirakl.hybris.core.product.strategies.impl;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.product.strategies.ProductPrimaryImageSelectionStrategy;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;

public class DefaultProductPrimaryImageSelectionStrategy implements ProductPrimaryImageSelectionStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultProductPrimaryImageSelectionStrategy.class);

  protected List<String> preferredMediaFormatQualifiers; // Media formats, ordered by preference

  @Override
  public MediaModel getPrimaryProductImage(ProductModel product) {
    if (isNotEmpty(product.getGalleryImages())) {
      return getBestMediaAvailable(product.getGalleryImages().get(0));
    }
    return null;
  }

  protected MediaModel getBestMediaAvailable(MediaContainerModel mediaContainer) {
    Map<String, MediaModel> mediasByFormatQualifier = buildMediasByFormatQualifierMap(mediaContainer);
    for (String mediaFormatQualifier : preferredMediaFormatQualifiers) {
      if (mediasByFormatQualifier.get(mediaFormatQualifier) != null) {
        return mediasByFormatQualifier.get(mediaFormatQualifier);
      }
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("Can't find a format matching the preferences in media container [%s]. Falling back...",
          mediaContainer.getName()));
    }
    return getFallbackMediaForContainer(mediaContainer);
  }

  protected Map<String, MediaModel> buildMediasByFormatQualifierMap(MediaContainerModel mediaContainer) {
    Map<String, MediaModel> mediasByFormatQualifier = new HashMap<>();
    if (isEmpty(mediaContainer.getMedias())) {
      return mediasByFormatQualifier;
    }
    for (MediaModel media : mediaContainer.getMedias()) {
      if (media.getMediaFormat() != null) {
        mediasByFormatQualifier.put(media.getMediaFormat().getQualifier(), media);
      }
    }
    return mediasByFormatQualifier;
  }

  protected MediaModel getFallbackMediaForContainer(MediaContainerModel mediaContainer) {
    if (isNotEmpty(mediaContainer.getMedias())) {
      return mediaContainer.getMedias().iterator().next();
    }
    return null;
  }

  @Required
  public void setPreferredMediaFormatQualifiers(List<String> preferredMediaFormatQualifiers) {
    this.preferredMediaFormatQualifiers = preferredMediaFormatQualifiers;
  }
}
