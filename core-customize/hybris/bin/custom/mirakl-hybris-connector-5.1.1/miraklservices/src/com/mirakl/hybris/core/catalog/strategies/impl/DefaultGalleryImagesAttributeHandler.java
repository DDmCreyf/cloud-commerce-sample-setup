package com.mirakl.hybris.core.catalog.strategies.impl;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.constants.MiraklservicesConstants;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.collect.Sets.newHashSet;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.MEDIA_URL_SECURE;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.PRODUCTS_IMPORT_IMAGES_FOLDER;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.apache.commons.collections4.ListUtils.union;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class DefaultGalleryImagesAttributeHandler extends AbstractCoreAttributeHandler<MiraklCoreAttributeModel> {

  private static final Logger LOG = Logger.getLogger(DefaultGalleryImagesAttributeHandler.class);
  private static final String QUALIFIER_PATTERN = "%s_%s";

  protected MediaService mediaService;
  protected L10NService l10nService;
  protected SiteBaseUrlResolutionService siteBaseUrlResolutionService;
  protected ConfigurationService configurationService;
  protected FlexibleSearchService flexibleSearchService;

  @Override
  public void setValue(AttributeValueData attributeValue, ProductImportData data, ProductImportFileContextData context) throws ProductImportException {
    MiraklCoreAttributeModel coreAttribute = attributeValue.getCoreAttribute();
    String mediaUrl = attributeValue.getValue();

    ProductModel product = determineOwner(coreAttribute, data, context);
    CatalogVersionModel productCatalogVersion = modelService.get(context.getGlobalContext().getProductCatalogVersion());
    final String galleryImageId = getMediaContainerQualifier(mediaUrl, product, coreAttribute, data, context);
    MediaContainerModel existingGalleryImage = findGalleryImage(galleryImageId, productCatalogVersion);

    if (isBlank(mediaUrl)) {
      if (existingGalleryImage != null) {
        removeGalleryImage(existingGalleryImage, product);
        markItemsToSave(data, product);
      }
      return;
    }

    if (existingGalleryImage != null && findGalleryImage(product, galleryImageId) != null && mediaUrl.equals(existingGalleryImage.getMasterUrl()) && !context.getGlobalContext().isForceProductUpdate()) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(format("Already received image with URL [%s] for product [%s] and attribute [%s]. Won't reimport it.", mediaUrl, product.getCode(), coreAttribute.getCode()));
      }
      return;
    }

    if (existingGalleryImage != null) {
      removeGalleryImage(existingGalleryImage, product);
    }

    MediaModel media = createMedia(mediaUrl, product, coreAttribute, data, productCatalogVersion, context);
    MediaContainerModel galleryImageWithMedia = createGalleryImageWithMedia(galleryImageId, media, mediaUrl, productCatalogVersion);
    addGalleryImage(product, galleryImageWithMedia);

    markItemsToSave(data, product);
  }

  protected void addGalleryImage(ProductModel product, MediaContainerModel galleryImageWithMedia) {
    List<MediaContainerModel> newGalleryImages = union(emptyIfNull(product.getGalleryImages()), singletonList(galleryImageWithMedia));
    product.setGalleryImages(newGalleryImages);
  }

  protected MediaModel createMedia(String mediaUrl, ProductModel product, MiraklCoreAttributeModel coreAttribute, ProductImportData data, CatalogVersionModel productCatalogVersion,

                                   ProductImportFileContextData context) throws ProductImportException {
    MediaModel media = modelService.create(MediaModel.class);
    media.setFolder(getMediaFolder());
    media.setCode(getMediaCode(mediaUrl, product, coreAttribute, data, context));
    media.setCatalogVersion(productCatalogVersion);
    media.setRealFileName(getMediaRealFileName(mediaUrl, product, coreAttribute, data, context));
    media.setURL(mediaUrl);
    modelService.save(media);
    downloadMedia(mediaUrl, media, data, context);
    return media;
  }

  protected MediaFolderModel getMediaFolder() {
    return mediaService.getFolder(configurationService.getConfiguration().getString(PRODUCTS_IMPORT_IMAGES_FOLDER, "images"));
  }

  protected MediaContainerModel createGalleryImageWithMedia(final String containerId, MediaModel media, String masterUrl, CatalogVersionModel productCatalogVersion) {
    MediaContainerModel container = modelService.create(MediaContainerModel.class);
    container.setQualifier(containerId);
    container.setCatalogVersion(productCatalogVersion);
    container.setMedias(newHashSet(media));
    container.setMasterUrl(masterUrl);
    container.setFromMarketplace(true);
    modelService.save(container);
    return container;
  }

  protected void downloadMedia(String mediaUrl, MediaModel media, ProductImportData data, ProductImportFileContextData context) throws ProductImportException {
    try {
      URLConnection urlConnection = new URL(mediaUrl).openConnection();
      configureRequestHeaders(urlConnection, context);
      mediaService.setStreamForMedia(media, urlConnection.getInputStream());
    } catch (Exception e) {
      LOG.error(format("Unable to set stream for media [%s] from url [%s]", media.getCode(), mediaUrl), e);
      throw new ProductImportException(data.getRawProduct(),
          l10nService.getLocalizedString(MiraklservicesConstants.PRODUCTS_IMPORT_MEDIA_DOWNLOAD_FAILURE_MESSAGE, new Object[] {mediaUrl}));
    }
  }

  protected void configureRequestHeaders(URLConnection urlConnection, ProductImportFileContextData context) {
    Map<String, String> mediaDownloadHttpHeaders = context.getGlobalContext().getMediaDownloadHttpHeaders();
    if (MapUtils.isNotEmpty(mediaDownloadHttpHeaders)) {
      for (Entry<String, String> entry : mediaDownloadHttpHeaders.entrySet()) {
        urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
      }
    }
  }

  protected void removeGalleryImage(MediaContainerModel galleryImage, ProductModel product) {
    removeGalleryImageFromProduct(product, galleryImage);
    modelService.removeAll(galleryImage.getMedias());
    modelService.remove(galleryImage);
  }

  protected void removeGalleryImageFromProduct(ProductModel product, MediaContainerModel galleryImage) {
    List<MediaContainerModel> existingGalleryImages = new ArrayList<>(CollectionUtils.emptyIfNull(product.getGalleryImages()));
    existingGalleryImages.remove(galleryImage);
    product.setGalleryImages(existingGalleryImages);
  }

  protected MediaContainerModel findGalleryImage(ProductModel product, final String galleryImageId) {
    return CollectionUtils.emptyIfNull(emptyIfNull(product.getGalleryImages())).stream().filter(container -> galleryImageId.equals(container.getQualifier())).findFirst().orElse(null);
  }

  protected MediaContainerModel findGalleryImage(final String galleryImageId, CatalogVersionModel productCatalogVersion) {
    MediaContainerModel container = new MediaContainerModel();
    container.setCatalogVersion(productCatalogVersion);
    container.setQualifier(galleryImageId);
    try {
      container = flexibleSearchService.getModelByExample(container);
    } catch (final ModelNotFoundException ignored) {
      //No container found
      container = null;
    }
    return container;
  }

  protected String getMediaCode(String mediaUrl, ProductModel product, MiraklCoreAttributeModel coreAttribute, ProductImportData data, ProductImportFileContextData context) {
    return format(QUALIFIER_PATTERN, product.getCode(), coreAttribute.getCode());
  }

  protected String getMediaRealFileName(String mediaUrl, ProductModel product, MiraklCoreAttributeModel coreAttribute, ProductImportData data,
                                        ProductImportFileContextData context) {
    return Paths.get(mediaUrl).getFileName().toString();
  }

  protected String getMediaContainerQualifier(String mediaUrl, ProductModel product, MiraklCoreAttributeModel coreAttribute, ProductImportData data,
      ProductImportFileContextData context) {
    return format(QUALIFIER_PATTERN, product.getCode(), coreAttribute.getCode());
  }

  public boolean isSecureMediaUrl() {
    return configurationService.getConfiguration().getBoolean(MEDIA_URL_SECURE, true);
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setMediaService(MediaService mediaService) {
    this.mediaService = mediaService;
  }

  @Required
  public void setL10nService(L10NService l10nService) {
    this.l10nService = l10nService;
  }

  @Required
  public void setSiteBaseUrlResolutionService(SiteBaseUrlResolutionService siteBaseUrlResolutionService) {
    this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
  }

  @Required
  public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
    this.flexibleSearchService = flexibleSearchService;
  }

}
