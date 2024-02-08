package com.mirakl.hybris.core.catalog.populators.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.services.MiraklCatalogService;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ExportCatalogContextPopulatorTest {

  @Mock
  private MiraklExportCatalogConfig exportConfig;
  @Mock
  private MiraklCatalogService miraklCatalogService;
  @Mock
  private Pair<MiraklExportCatalogConfig, ExportCatalogWriter> source;
  @Mock
  private ExportCatalogWriter writer;

  @InjectMocks
  private ExportCatalogContextPopulator testObj;

  @Before
  public void setUp() {
    when(source.getLeft()).thenReturn(exportConfig);
    when(source.getRight()).thenReturn(writer);
  }

  @Test
  public void populate() {
      MiraklExportCatalogContext target = new MiraklExportCatalogContext();

      testObj.populate(source, target);

      assertThat(target.getVisitedClassIds()).isEmpty();
      assertThat(target.getWriter()).isEqualTo(writer);
      assertThat(target.getExportConfig()).isEqualTo(exportConfig);
  }

  @Test
  public void shouldRetrieveCategoriesCodesFromMirakl() throws IOException {
    when(exportConfig.isExportCategories()).thenReturn(true);
    MiraklExportCatalogContext target = new MiraklExportCatalogContext();

    testObj.populate(source, target);

    verify(miraklCatalogService).getMiraklCategoryCodes();
  }

  @Test
  public void shouldRetrieveAttributesCodesFromMirakl() throws IOException {
    when(exportConfig.isExportAttributes()).thenReturn(true);
    MiraklExportCatalogContext target = new MiraklExportCatalogContext();

    testObj.populate(source, target);

    verify(miraklCatalogService).getMiraklAttributeCodes();
  }

  @Test
  public void shouldRetrieveValueListCodesFromMirakl() throws IOException {
    when(exportConfig.isExportValueLists()).thenReturn(true);
    MiraklExportCatalogContext target = new MiraklExportCatalogContext();

    testObj.populate(source, target);

    verify(miraklCatalogService).getMiraklValueCodes();
  }

  @Test
  public void shouldNotRetrieveCategoriesCodesFromMirakl() throws IOException {
    when(exportConfig.isExportCategories()).thenReturn(false);
    MiraklExportCatalogContext target = new MiraklExportCatalogContext();

    testObj.populate(source, target);

    verifyZeroInteractions(miraklCatalogService);
  }

  @Test
  public void shouldNotRetrieveAttributesCodesFromMirakl() throws IOException {
    when(exportConfig.isExportAttributes()).thenReturn(false);
    MiraklExportCatalogContext target = new MiraklExportCatalogContext();

    testObj.populate(source, target);

    verifyZeroInteractions(miraklCatalogService);
  }

  @Test
  public void shouldNotRetrieveValueListCodesFromMirakl() throws IOException {
    when(exportConfig.isExportValueLists()).thenReturn(false);
    MiraklExportCatalogContext target = new MiraklExportCatalogContext();

    testObj.populate(source, target);

    verifyZeroInteractions(miraklCatalogService);
  }

}
