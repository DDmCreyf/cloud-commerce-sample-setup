package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.PRODUCTS_IMPORT_IMAGES_FOLDER;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeOwnerStrategy;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGalleryImagesAttributeHandlerTest {

  private static final String PRODUCT_CODE = "product-code";
  private static final String MEDIA_FOLDER_QUALIFIER = "media-folder";
  private static final String IMAGE_URL = "image-url";
  private static final String OUTDATED_IMAGE_URL = "outdated-image-url";
  private static final String MEDIA_1_ATTRIBUTE = "media1";
  private static final PK PRODUCT_CATALOG_VERSION_PK = PK.fromLong(1L);

  @Mock
  private CoreAttributeOwnerStrategy coreAttributeOwnerStrategy;
  @Mock
  private MediaService mediaService;
  @Mock
  private ModelService modelService;
  @Mock
  private L10NService l10nService;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private FlexibleSearchService flexibleSearchService;
  @Mock
  private MediaContainerService mediaContainerService;
  @Mock
  private CatalogVersionModel catalogVersion;
  @Mock
  private Configuration configuration;
  @Mock
  private AttributeValueData attributeValue;
  @Mock
  private ProductImportData data;
  @Mock
  private ProductImportFileContextData context;
  @Mock
  private ProductImportGlobalContextData globalContext;
  @Mock
  private MiraklCoreAttributeModel coreAttribute;
  @Mock
  private MediaModel media1, media2, media3;
  @Mock
  private MediaContainerModel mediaContainer1, mediaContainer2;
  @Mock
  private MediaFolderModel mediaFolder;
  private ProductModel product;


  @InjectMocks
  @Spy
  private DefaultGalleryImagesAttributeHandler defaultGalleryImagesValueHandler;

  @Before
  public void setUp() throws Exception {
    product = new ProductModel();
    product.setCatalogVersion(catalogVersion);
    product.setCode(PRODUCT_CODE);
    when(coreAttribute.getCode()).thenReturn(MEDIA_1_ATTRIBUTE);
    when(attributeValue.getValue()).thenReturn(IMAGE_URL);
    when(attributeValue.getCoreAttribute()).thenReturn(coreAttribute);
    when(coreAttributeOwnerStrategy.determineOwner(coreAttribute, data, context)).thenReturn(product);
    when(context.getGlobalContext()).thenReturn(globalContext);
    when(globalContext.getProductCatalogVersion()).thenReturn(PRODUCT_CATALOG_VERSION_PK);
    doNothing().when(defaultGalleryImagesValueHandler).downloadMedia(eq(IMAGE_URL), any(MediaModel.class), eq(data), eq(context));
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(modelService.create(MediaModel.class)).thenReturn(media1);

  }

  @Test
  public void shouldCreateGalleryImage() throws Exception {
    when(modelService.create(MediaContainerModel.class)).thenReturn(mediaContainer1);
    when(flexibleSearchService.getModelByExample(any(MediaContainerModel.class))).thenThrow(new ModelNotFoundException("ModelNotFoundException thrown from test. Model not found in a junit test"));
    defaultGalleryImagesValueHandler.setValue(attributeValue, data, context);

    verify(defaultGalleryImagesValueHandler).markItemsToSave(eq(data), eq(product));

  }

  @Test
  public void shouldDoNothingWhenNoMediaUrlAndNoMediaContainer() throws Exception {
    when(attributeValue.getValue()).thenReturn(null);

    defaultGalleryImagesValueHandler.setValue(attributeValue, data, context);

    verifyNoMoreInteractions(mediaService);
    verify(defaultGalleryImagesValueHandler, never()).markItemsToSave(eq(data), eq(product));
    verify(modelService, never()).saveAll(anyCollection());
    verify(modelService, never()).save(any());
  }

  @Test
  public void shouldDeleteMediaWhenNoMediaUrlAndExistingMediaContainer() throws Exception {
    when(attributeValue.getValue()).thenReturn(null);
    product.setGalleryImages(Arrays.asList(mediaContainer1, mediaContainer2));
    when(flexibleSearchService.getModelByExample(any(MediaContainerModel.class))).thenReturn(mediaContainer1);
    when(mediaContainer1.getMedias()).thenReturn(asList(media1, media2, media3));

    defaultGalleryImagesValueHandler.setValue(attributeValue, data, context);

    verify(modelService).remove(mediaContainer1);
    verifyNoMoreInteractions(mediaContainer2);
  }

  @Test
  public void shouldReplaceExistingGalleryImage() throws Exception {
    String mediaContainerQualifier = format("%s_%s", PRODUCT_CODE, MEDIA_1_ATTRIBUTE);
    when(mediaContainer1.getQualifier()).thenReturn(mediaContainerQualifier);
    product.setGalleryImages(Arrays.asList(mediaContainer1));
    when(flexibleSearchService.getModelByExample(any(MediaContainerModel.class))).thenReturn(mediaContainer1);
    when(mediaContainer1.getMedias()).thenReturn(asList(media1, media2, media3));
    when(mediaContainer1.getMasterUrl()).thenReturn(OUTDATED_IMAGE_URL);
    when(modelService.create(MediaContainerModel.class)).thenReturn(mediaContainer2);

    defaultGalleryImagesValueHandler.setValue(attributeValue, data, context);

    verify(modelService).remove(mediaContainer1);
    assertThat(product.getGalleryImages()).containsOnly(mediaContainer2);
    verify(mediaContainer2).setMasterUrl(IMAGE_URL);
    verify(mediaContainer2).setQualifier(mediaContainerQualifier);
    verify(mediaContainer2).setFromMarketplace(true);

  }


  @Test
  public void shouldNotDownloadMediaWhenSameMasterUrlInLinkedGallery() throws Exception {
    product.setGalleryImages(Arrays.asList(mediaContainer1));

    when(flexibleSearchService.getModelByExample(any(MediaContainerModel.class))).thenReturn(mediaContainer1);
    when(mediaContainer1.getMasterUrl()).thenReturn(IMAGE_URL);
    when(mediaContainer1.getQualifier()).thenReturn(format("%s_%s", PRODUCT_CODE, MEDIA_1_ATTRIBUTE));

    defaultGalleryImagesValueHandler.setValue(attributeValue, data, context);

    verifyNoMoreInteractions(mediaService);
    verify(defaultGalleryImagesValueHandler, never()).markItemsToSave(eq(data), any());

  }

  @Test
  public void shouldDownloadMediaWhenSameMasterUrlAndForceUpdate() throws Exception {
    product.setGalleryImages(Arrays.asList(mediaContainer1));
    when(flexibleSearchService.getModelByExample(any(MediaContainerModel.class))).thenReturn(mediaContainer1);
    when(mediaContainer1.getMedias()).thenReturn(asList(media1, media2, media3));
    when(mediaContainer1.getMasterUrl()).thenReturn(IMAGE_URL);
    when(mediaContainer1.getQualifier()).thenReturn(format("%s_%s", PRODUCT_CODE, MEDIA_1_ATTRIBUTE));
    when(globalContext.isForceProductUpdate()).thenReturn(true);
    when(modelService.create(MediaContainerModel.class)).thenReturn(mediaContainer2);

    defaultGalleryImagesValueHandler.setValue(attributeValue, data, context);

    verify(modelService).remove(mediaContainer1);
    verify(mediaContainer2).setMasterUrl(IMAGE_URL);
    assertThat(product.getGalleryImages()).containsOnly(mediaContainer2);
  }

  @Test
  public void shouldSetFolderOnMedia() throws Exception {
    when(modelService.create(MediaContainerModel.class)).thenReturn(mediaContainer1);
    when(configuration.getString(eq(PRODUCTS_IMPORT_IMAGES_FOLDER), Mockito.anyString())).thenReturn(MEDIA_FOLDER_QUALIFIER);
    when(mediaService.getFolder(MEDIA_FOLDER_QUALIFIER)).thenReturn(mediaFolder);
    defaultGalleryImagesValueHandler.setValue(attributeValue, data, context);

    verify(media1).setFolder(mediaFolder);
  }


  @Test
  public void shouldDoNothingIfNoMediaUrlAndNoMediaContainer() throws Exception {
    when(attributeValue.getValue()).thenReturn(null);

    defaultGalleryImagesValueHandler.setValue(attributeValue, data, context);

    verify(defaultGalleryImagesValueHandler, never()).removeGalleryImage(any(), eq(product));
    verify(defaultGalleryImagesValueHandler, never()).createMedia(anyString(), any(), any(), any(), any(), any());
    verify(mediaService, never()).setStreamForMedia(any(), any());
    verify(modelService, never()).save(any());
  }

  @Test
  public void shouldRemoveMediaContainerWhenNoMediaUrlButWithMediaContainer() throws Exception {
    when(attributeValue.getValue()).thenReturn(null);
    product.setGalleryImages(Collections.singletonList(mediaContainer1));
    when(flexibleSearchService.getModelByExample(any(MediaContainerModel.class))).thenReturn(mediaContainer1);

    defaultGalleryImagesValueHandler.setValue(attributeValue, data, context);

    verify(defaultGalleryImagesValueHandler).removeGalleryImage(eq(mediaContainer1), eq(product));
    verify(defaultGalleryImagesValueHandler).markItemsToSave(eq(data), eq(product));
    verify(modelService).remove(eq(mediaContainer1));
  }

  @Test
  public void shouldReturnContainerWhenListContainsContainer_() {
    when(mediaContainer1.getQualifier()).thenReturn("container-id");
    product.setGalleryImages(Collections.singletonList(mediaContainer1));
    MediaContainerModel result = defaultGalleryImagesValueHandler.findGalleryImage(product, "container-id");

    assertThat(result).isEqualTo(mediaContainer1);
  }

  @Test
  public void shouldReturnNullWhenListDoesNotContainContainer() {
    when(mediaContainer1.getQualifier()).thenReturn("container-id");
    product.setGalleryImages(Collections.singletonList(mediaContainer1));

    MediaContainerModel result = defaultGalleryImagesValueHandler.findGalleryImage(product, "other-container-id");

    assertThat(result).isNull();
  }


}
