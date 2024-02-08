package com.mirakl.hybris.core.product.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mci.request.product.MiraklGetProductDataSheetsRequest;
import com.mirakl.hybris.beans.ProductDataSheetDownloadParams;
import com.mirakl.hybris.core.enums.MarketplaceProductAcceptanceStatus;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMcmProductFileDownloadServiceTest {

  @Mock
  private Converter<ProductDataSheetDownloadParams, MiraklGetProductDataSheetsRequest> productDataSheetsRequestConverter;
  @Mock
  private MiraklCatalogIntegrationFrontApi mciApi;
  @Mock
  private MiraklGetProductDataSheetsRequest request;
  private File file, targetDirectory;

  private ProductDataSheetDownloadParams params;

  @InjectMocks
  private DefaultMcmProductFileDownloadService service;

  @Before
  public void setUp() throws Exception {
    params = new ProductDataSheetDownloadParams();
    when(productDataSheetsRequestConverter.convert(params)).thenReturn(request);
    file = File.createTempFile("result", "tmp");
    targetDirectory = Files.createTempDirectory("archive-folder").toFile();
  }

  @After
  public void tearDown() throws Exception {
    FileUtils.deleteDirectory(targetDirectory);
    FileUtils.deleteQuietly(file);
  }

  @Test
  public void shouldDownloadProductDataSheetFile() throws Exception {
    when(mciApi.getProductDataSheetsFile(request)).thenReturn(file);
    params.setTargetDirectory(targetDirectory);

    boolean result = service.downloadProductDataSheetsFile(params);

    assertThat(result).isTrue();
  }

  @Test
  public void shouldDownloadProductDataSheetFileLegacy() throws Exception {
    params.setTargetDirectory(targetDirectory);

    boolean result = service.downloadProductDataSheetsFile(new Date(),
        Sets.newHashSet(MarketplaceProductAcceptanceStatus.ACCEPTED), targetDirectory);

    assertThat(result).isTrue();
  }

}
