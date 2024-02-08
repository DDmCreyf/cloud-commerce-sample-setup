package com.mirakl.hybris.core.catalog.strategies.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;
import com.mirakl.hybris.core.model.MiraklExportCatalogCronJobModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPostProcessExportCatalogStrategyTest {
  @InjectMocks
  private DefaultPostProcessExportCatalogStrategy strategy;

  @Mock
  private MediaService mediaService;
  @Mock
  private ModelService modelService;
  @Mock
  private MiraklExportCatalogContext context;
  @Mock
  private MiraklExportCatalogConfig exportConfig;
  @Mock
  private MiraklExportCatalogCronJobModel cronJob;
  @Mock
  private ExportCatalogWriter writer;
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private File categoriesFile;
  private File attributesFile;
  private File valueListsFile;

  @Before
  public void setUp() throws Exception {
    categoriesFile = folder.newFile();
    attributesFile = folder.newFile();
    valueListsFile = folder.newFile();
    when(context.getWriter()).thenReturn(writer);
    when(context.getExportConfig()).thenReturn(exportConfig);
    when(modelService.create(CatalogUnawareMediaModel.class)).thenReturn(mock(CatalogUnawareMediaModel.class));
  }

  @Test
  public void shouldSaveCategoriesMedia() throws Exception {
    when(exportConfig.isExportCategories()).thenReturn(true);
    when(writer.getCategoriesFile()).thenReturn(categoriesFile);

    strategy.postProcess(cronJob, context);

    verify(cronJob, atLeast(1)).setCategoriesMedia(any(MediaModel.class));
  }

  @Test
  public void shouldSaveAttributesMedia() throws Exception {
    when(exportConfig.isExportAttributes()).thenReturn(true);
    when(writer.getAttributesFile()).thenReturn(attributesFile);

    strategy.postProcess(cronJob, context);

    verify(cronJob, atLeast(1)).setAttributesMedia(any(MediaModel.class));
  }

  @Test
  public void shouldSaveValueListsMedia() throws Exception {
    when(exportConfig.isExportValueLists()).thenReturn(true);
    when(writer.getValueListsFile()).thenReturn(valueListsFile);

    strategy.postProcess(cronJob, context);

    verify(cronJob, atLeast(1)).setValueListsMedia(any(MediaModel.class));
  }



}
