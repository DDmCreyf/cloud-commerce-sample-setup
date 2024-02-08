package com.mirakl.hybris.core.catalog.populators.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.ExportCatalogAdditionalData;
import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ExportCatalogContextCopyPopulatorTest {

  private static final String VISITED_CLASS_ID = "CLASS_ID";

  @Mock
  private MiraklExportCatalogContext context;
  @Mock
  private ExportCatalogAdditionalData additionalData;
  @Mock
  private ExportCatalogWriter writer;
  @Mock
  private MiraklExportCatalogConfig exportConfig;
  @Mock
  private Set<Pair<java.lang.String, java.lang.String>> attributeCodes;
  @Mock
  private Set<java.lang.String> categoryCodes;
  @Mock
  private Set<Pair<java.lang.String, java.lang.String>> valueCodes;

  @InjectMocks
  private ExportCatalogContextCopyPopulator testObj;

  @Test
  public void populate() throws Exception {
    Set<java.lang.String> visitedClassIds = new HashSet<>();
    visitedClassIds.add(VISITED_CLASS_ID);
    MiraklExportCatalogContext copy = new MiraklExportCatalogContext();
    when(context.getAdditionalData()).thenReturn(additionalData);
    when(context.getWriter()).thenReturn(writer);
    when(context.getExportConfig()).thenReturn(exportConfig);
    when(context.getMiraklAttributeCodes()).thenReturn(attributeCodes);
    when(context.getMiraklCategoryCodes()).thenReturn(categoryCodes);
    when(context.getMiraklValueCodes()).thenReturn(valueCodes);
    when(context.getVisitedClassIds()).thenReturn(visitedClassIds);

    testObj.populate(context, copy);

    assertThat(copy.getAdditionalData() == additionalData).isTrue();
    assertThat(copy.getWriter() == writer).isTrue();
    assertThat(copy.getExportConfig() == exportConfig).isTrue();
    assertThat(copy.getMiraklAttributeCodes() == attributeCodes).isTrue();
    assertThat(copy.getMiraklCategoryCodes() == categoryCodes).isTrue();
    assertThat(copy.getMiraklValueCodes() == valueCodes).isTrue();
    assertThat(copy.getVisitedClassIds()).isEqualTo(visitedClassIds);
    assertThat(copy.getVisitedClassIds() != visitedClassIds).isTrue();
  }

}
