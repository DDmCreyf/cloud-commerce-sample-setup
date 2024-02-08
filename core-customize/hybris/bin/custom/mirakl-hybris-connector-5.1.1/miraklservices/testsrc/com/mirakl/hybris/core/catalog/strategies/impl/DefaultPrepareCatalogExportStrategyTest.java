package com.mirakl.hybris.core.catalog.strategies.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPrepareCatalogExportStrategyTest {

  @InjectMocks
  private DefaultPrepareCatalogExportStrategy strategy;
  @Mock
  private MiraklExportCatalogContext context;
  @Mock
  private MiraklExportCatalogConfig config;
  @Mock
  private ExportCatalogWriter writer;

  @Before
  public void setUp() {
    when(context.getWriter()).thenReturn(writer);
    when(context.getExportConfig()).thenReturn(config);
  }

  @Test
  public void shouldCreateBooleanValueList() throws IOException {
    when(config.isExportValueLists()).thenReturn(true);

    strategy.prepareExport(context);

    verify(writer, times(2)).writeAttributeValue(anyMapOf(String.class, String.class));
  }

  @Test
  public void shouldNotCreateBooleanValueListIfNoExport() throws IOException {
    when(config.isExportValueLists()).thenReturn(false);

    strategy.prepareExport(context);

    verify(writer, never()).writeAttributeValue(anyMapOf(String.class, String.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldNotCreateBooleanValueListWhenAlreadyInMirakl() throws IOException {
    when(context.removeMiraklValueCode(any(Pair.class))).thenReturn(true);

    strategy.prepareExport(context);

    verify(writer, never()).writeAttributeValue(anyMapOf(String.class, String.class));
  }

}
