package com.mirakl.hybris.core.catalog.strategies.impl;

import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.*;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDeleteCatalogEntriesStrategyTest {

  private static final String CATEGORY_CODE_1 = "shoes";
  private static final String CATEGORY_CODE_2 = "fitness";
  private static final String CATEGORY_CODE_3 = "T-shirt";
  private static final String ATTRIBUTE_CODE_1 = "size";
  private static final String VALUE_CODE_1 = "adidas";
  private static final String VALUE_LIST_CODE_1 = "brand";

  @Mock
  private MiraklExportCatalogContext context;

  @Mock
  private ExportCatalogWriter writer;

  @Mock
  private MiraklExportCatalogConfig exportConfig;

  @InjectMocks
  private DefaultDeleteCatalogEntriesStrategy testObj;

  @Before
  public void setUp() throws Exception {
    when(context.getWriter()).thenReturn(writer);
    when(context.getExportConfig()).thenReturn(exportConfig);
    when(exportConfig.isExportAttributes()).thenReturn(true);
    when(exportConfig.isExportCategories()).thenReturn(true);
    when(exportConfig.isExportValueLists()).thenReturn(true);
  }

  @Test
  public void writeRemovedCategories() throws Exception {
    when(context.getMiraklCategoryCodes()).thenReturn(Sets.newSet(CATEGORY_CODE_1, CATEGORY_CODE_2, CATEGORY_CODE_3));

    testObj.writeRemovedCategories(context);

    verify(writer, times(3)).writeCategory(anyMapOf(String.class, String.class));
  }

  @Test
  public void writeRemovedAttributes() throws Exception {
    when(context.getMiraklAttributeCodes()).thenReturn(Sets.newSet(Pair.of(ATTRIBUTE_CODE_1, CATEGORY_CODE_1)));

    testObj.writeRemovedAttributes(context);

    verify(writer).writeAttribute(anyMapOf(String.class, String.class));
  }

  @Test
  public void writeRemovedValues() throws Exception {
    when(context.getMiraklValueCodes()).thenReturn(Sets.newSet(Pair.of(VALUE_CODE_1, VALUE_LIST_CODE_1)));

    testObj.writeRemovedValues(context);

    verify(writer).writeAttributeValue(anyMapOf(String.class, String.class));
  }

  @Test
  public void doNotWriteRemovedCategoriesWhenNoExport() throws Exception {
    when(exportConfig.isExportCategories()).thenReturn(false);

    testObj.writeRemovedCategories(context);

    verifyZeroInteractions(writer);
  }

  @Test
  public void doNotWriteRemovedAttributesWhenNoExport() throws Exception {
    when(exportConfig.isExportAttributes()).thenReturn(false);

    testObj.writeRemovedAttributes(context);

    verifyZeroInteractions(writer);
  }

  @Test
  public void doNotWriteRemovedValueListsWhenNoExport() throws Exception {
    when(exportConfig.isExportValueLists()).thenReturn(false);

    testObj.writeRemovedValues(context);

    verifyZeroInteractions(writer);
  }

}
