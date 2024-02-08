package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.events.ExportableCategoryEvent;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogService;
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;
import com.mirakl.hybris.core.enums.MiraklCatalogCategoryExportHeader;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWriteCategoryStrategyTest {

  @InjectMocks
  private DefaultWriteCategoryStrategy testObj;

  @Mock
  private MiraklExportCatalogService exportCatalogService;
  @Mock
  private MiraklExportCatalogContext context;
  @Mock
  private ExportableCategoryEvent event;
  @Mock
  private MiraklExportCatalogConfig config;
  @Mock
  private CategoryModel category, currentParentCategory;
  @Captor
  private ArgumentCaptor<Map<String, String>> lineArgumentCaptor;
  @Mock
  private ExportCatalogWriter writer;

  @Before
  public void setUp() {
    when(event.getContext()).thenReturn(context);
    when(context.getWriter()).thenReturn(writer);
    when(context.getExportConfig()).thenReturn(config);
    when(context.getCurrentParentCategory()).thenReturn(currentParentCategory);
    when(event.getCategory()).thenReturn(category);
  }

  @Test
  public void shouldExportCategories() throws IOException {
    when(config.isExportCategories()).thenReturn(true);

    testObj.handleEvent(event);

    verify(writer).writeCategory(lineArgumentCaptor.capture());
    Map<String, String> writtenMap = lineArgumentCaptor.getValue();
    assertThat(writtenMap.containsKey(MiraklCatalogCategoryExportHeader.HIERARCHY_CODE.getCode())).isTrue();
    assertThat(writtenMap.containsKey(MiraklCatalogCategoryExportHeader.HIERARCHY_LABEL.getCode())).isTrue();
    assertThat(writtenMap.containsKey(MiraklCatalogCategoryExportHeader.HIERARCHY_PARENT_CODE.getCode())).isTrue();
  }

  @Test
  public void shouldHandleMultipleLocales() throws IOException {
    when(config.isExportCategories()).thenReturn(true);
    when(config.getAdditionalLocales()).thenReturn(asList(Locale.ENGLISH, Locale.GERMAN));

    testObj.handleEvent(event);

    verify(writer).writeCategory(lineArgumentCaptor.capture());
    Map<String, String> writtenMap = lineArgumentCaptor.getValue();
    assertThat(writtenMap.containsKey(MiraklCatalogCategoryExportHeader.HIERARCHY_LABEL.getCode() + "[en]")).isTrue();
    assertThat(writtenMap.containsKey(MiraklCatalogCategoryExportHeader.HIERARCHY_LABEL.getCode() + "[de]")).isTrue();
  }

  @Test
  public void shouldNotExportCategories() throws IOException {
    when(config.isExportCategories()).thenReturn(false);

    testObj.handleEvent(event);

    verifyZeroInteractions(writer);
  }
}
