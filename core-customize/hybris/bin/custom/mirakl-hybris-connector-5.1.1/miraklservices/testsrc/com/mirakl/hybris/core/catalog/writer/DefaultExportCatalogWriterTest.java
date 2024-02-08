package com.mirakl.hybris.core.catalog.writer;

import static com.mirakl.hybris.core.catalog.writer.DefaultExportCatalogWriter.DEFAULT_ATTRIBUTES_FILE_NAME;
import static com.mirakl.hybris.core.catalog.writer.DefaultExportCatalogWriter.DEFAULT_CATEGORIES_FILE_NAME;
import static com.mirakl.hybris.core.catalog.writer.DefaultExportCatalogWriter.DEFAULT_VALUE_LISTS_FILE_NAME;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.strategies.MiraklExportHeaderResolverStrategy;
import com.mirakl.hybris.core.enums.MiraklAttributeExportHeader;
import com.mirakl.hybris.core.enums.MiraklCatalogCategoryExportHeader;
import com.mirakl.hybris.core.enums.MiraklValueListExportHeader;
import com.mirakl.hybris.core.util.services.CsvService;

import de.hybris.bootstrap.annotations.UnitTest;
import shaded.org.supercsv.io.CsvMapWriter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultExportCatalogWriterTest {

  @Mock
  private CsvMapWriter writer;
  @Mock
  private MiraklExportCatalogConfig exportConfig;
  @Mock
  private CsvService csvService;
  @Mock
  private MiraklExportHeaderResolverStrategy miraklExportHeaderResolverStrategy;

  private Set<Locale> additionalLocales = Sets.newHashSet(Locale.ENGLISH, Locale.FRENCH);
  private List<Locale> additionalLocalesList = new ArrayList<>(additionalLocales);
  private String[] categoriesHeader = MiraklCatalogCategoryExportHeader.codes(additionalLocalesList);
  private String[] attributesHeader = MiraklAttributeExportHeader.codes(additionalLocalesList);
  private String[] valueListsHeader = MiraklValueListExportHeader.codes(additionalLocalesList);

  private DefaultExportCatalogWriterMock testObj;

  @Before
  public void setUp() throws IOException {
    testObj = new DefaultExportCatalogWriterMock(exportConfig);
    testObj.setCsvService(csvService);
    testObj.setWriter(writer);
    testObj.setMiraklExportHeaderResolverStrategy(miraklExportHeaderResolverStrategy);
    testObj.initialize();
    when(exportConfig.getAdditionalLocales()).thenReturn(additionalLocalesList);
    when(miraklExportHeaderResolverStrategy.getSupportedHeaders(MiraklCatalogCategoryExportHeader.class, additionalLocales))
        .thenReturn(categoriesHeader);
    when(miraklExportHeaderResolverStrategy.getSupportedHeaders(MiraklAttributeExportHeader.class, additionalLocales))
        .thenReturn(attributesHeader);
    when(miraklExportHeaderResolverStrategy.getSupportedHeaders(MiraklValueListExportHeader.class, additionalLocales))
        .thenReturn(valueListsHeader);
  } 

  @Test
  public void shouldWriteAttributeHeader() throws IOException {
    when(exportConfig.isExportAttributes()).thenReturn(true);

    testObj.initialize();

    verify(writer).writeHeader(attributesHeader);
  }

  @Test
  public void shouldNotWriteAttributeHeaderIfNoExport() throws IOException {
    when(exportConfig.isExportAttributes()).thenReturn(false);

    testObj.initialize();

    verify(writer, never()).writeHeader(attributesHeader);
  }

  @Test
  public void shouldWriteCategoriesHeader() throws IOException {
    when(exportConfig.isExportCategories()).thenReturn(true);

    testObj.initialize();

    verify(writer).writeHeader(categoriesHeader);
  }

  @Test
  public void shouldNotWriteCategoriesHeaderIfNoExport() throws IOException {
    when(exportConfig.isExportAttributes()).thenReturn(false);

    testObj.initialize();

    verify(writer, never()).writeHeader(categoriesHeader);
  }

  @Test
  public void shouldWriteValueListsHeader() throws IOException {
    when(exportConfig.isExportValueLists()).thenReturn(true);

    testObj.initialize();

    verify(writer).writeHeader(valueListsHeader);
  }

  @Test
  public void shouldNotWriteValueListsHeaderIfNoExport() throws IOException {
    when(exportConfig.isExportAttributes()).thenReturn(false);

    testObj.initialize();

    verify(writer, never()).writeHeader(categoriesHeader);
  }

  @Test
  public void getCategoriesFile() throws IOException {
    when(exportConfig.isExportCategories()).thenReturn(true);
    testObj.initialize();

    File file = testObj.getCategoriesFile();

    verify(writer).flush();
    assertThat(file.getName()).contains(DEFAULT_CATEGORIES_FILE_NAME);
  }

  @Test
  public void getAttributesFile() throws IOException {
    when(exportConfig.isExportAttributes()).thenReturn(true);
    testObj.initialize();

    File file = testObj.getAttributesFile();

    verify(writer).flush();
    assertThat(file.getName()).contains(DEFAULT_ATTRIBUTES_FILE_NAME);
  }

  @Test
  public void getValueListsFile() throws IOException {
    when(exportConfig.isExportValueLists()).thenReturn(true);
    testObj.initialize();

    File file = testObj.getValueListsFile();

    verify(writer).flush();
    assertThat(file.getName()).contains(DEFAULT_VALUE_LISTS_FILE_NAME);
  }

  @Test
  public void writeCategory() throws IOException {
    when(exportConfig.isExportCategories()).thenReturn(true);
    testObj.initialize();

    testObj.writeCategory(Collections.<String, String>emptyMap());

    verify(csvService).writeLine(writer, categoriesHeader, Collections.<String, String>emptyMap());
  }

  @Test
  public void writeAttribute() throws IOException {
    when(exportConfig.isExportAttributes()).thenReturn(true);
    testObj.initialize();

    testObj.writeAttribute(Collections.<String, String>emptyMap());

    verify(csvService).writeLine(writer, attributesHeader, Collections.<String, String>emptyMap());
  }

  @Test
  public void writeValue() throws IOException {
    when(exportConfig.isExportValueLists()).thenReturn(true);
    testObj.initialize();

    testObj.writeAttributeValue(Collections.<String, String>emptyMap());

    verify(csvService).writeLine(writer, valueListsHeader, Collections.<String, String>emptyMap());
  }

  class DefaultExportCatalogWriterMock extends DefaultExportCatalogWriter {

    private CsvMapWriter writer;

    DefaultExportCatalogWriterMock(MiraklExportCatalogConfig exportConfig) {
      super(exportConfig);
    }

    @Override
    protected CsvMapWriter createWriter(File file) throws IOException {
      return writer;
    }

    public void setWriter(CsvMapWriter writer) {
      this.writer = writer;
    }

  }
}
