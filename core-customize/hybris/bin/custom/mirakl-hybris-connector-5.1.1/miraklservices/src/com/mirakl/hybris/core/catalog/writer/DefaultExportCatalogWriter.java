package com.mirakl.hybris.core.catalog.writer;

import static com.google.common.base.Preconditions.checkState;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DEFAULT_ENCODING;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_FILE_EXTENSION;
import static java.io.File.createTempFile;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.strategies.MiraklExportHeaderResolverStrategy;
import com.mirakl.hybris.core.enums.MiraklAttributeExportHeader;
import com.mirakl.hybris.core.enums.MiraklCatalogCategoryExportHeader;
import com.mirakl.hybris.core.enums.MiraklValueListExportHeader;
import com.mirakl.hybris.core.util.services.CsvService;

import shaded.org.supercsv.io.CsvMapWriter;
import shaded.org.supercsv.io.ICsvMapWriter;

public class DefaultExportCatalogWriter implements ExportCatalogWriter {

  private static final Logger LOG = Logger.getLogger(DefaultExportCatalogWriter.class);
  protected static final String DEFAULT_CATEGORIES_FILE_NAME = "categories";
  protected static final String DEFAULT_ATTRIBUTES_FILE_NAME = "attributes";
  protected static final String DEFAULT_VALUE_LISTS_FILE_NAME = "valuelists";

  protected CsvService csvService;

  protected ICsvMapWriter categoriesWriter;
  protected ICsvMapWriter attributesWriter;
  protected ICsvMapWriter valueListsWriter;
  protected File categoriesFile;
  protected File attributesFile;
  protected File valueListsFile;
  protected String[] categoriesHeader;
  protected String[] attributesHeader;
  protected String[] valueListsHeader;

  protected MiraklExportCatalogConfig exportConfig;
  protected MiraklExportHeaderResolverStrategy miraklExportHeaderResolverStrategy;

  public DefaultExportCatalogWriter(MiraklExportCatalogConfig exportConfig) {
    this.exportConfig = exportConfig;
  }

  @Override
  @PostConstruct
  public void initialize() throws IOException {
    createFilesAndWriters();
    writeHeaders();
  }

  protected void createFilesAndWriters() throws IOException {
    if (exportConfig.isExportCategories() && categoriesWriter == null) {
      categoriesFile = createTempFile(defaultIfNull(exportConfig.getCategoriesFilename(), DEFAULT_CATEGORIES_FILE_NAME),
          CATALOG_EXPORT_FILE_EXTENSION);
      categoriesWriter = createWriter(categoriesFile);
    }
    if (exportConfig.isExportAttributes() && attributesWriter == null) {
      attributesFile = createTempFile(defaultIfNull(exportConfig.getAttributesFilename(), DEFAULT_ATTRIBUTES_FILE_NAME),
          CATALOG_EXPORT_FILE_EXTENSION);
      attributesWriter = createWriter(attributesFile);
    }
    if (exportConfig.isExportValueLists() && valueListsWriter == null) {
      valueListsFile = createTempFile(defaultIfNull(exportConfig.getValueListsFilename(), DEFAULT_VALUE_LISTS_FILE_NAME),
          CATALOG_EXPORT_FILE_EXTENSION);
      valueListsWriter = createWriter(valueListsFile);
    }
  }

  protected void writeHeaders() throws IOException {

    checkState(!exportConfig.isExportCategories() || categoriesWriter != null, "categories writer is not initialized");
    checkState(!exportConfig.isExportAttributes() || attributesWriter != null, "attributes writer is not initialized");
    checkState(!exportConfig.isExportValueLists() || valueListsWriter != null, "value lists writer is not initialized");

    Set<Locale> additionalLocales = new HashSet<>(exportConfig.getAdditionalLocales());
    if (exportConfig.isExportCategories()) {
      categoriesHeader =
          miraklExportHeaderResolverStrategy.getSupportedHeaders(MiraklCatalogCategoryExportHeader.class, additionalLocales);
      categoriesWriter.writeHeader(categoriesHeader);
    }
    if (exportConfig.isExportAttributes()) {
      attributesHeader =
          miraklExportHeaderResolverStrategy.getSupportedHeaders(MiraklAttributeExportHeader.class, additionalLocales);
      attributesWriter.writeHeader(attributesHeader);
    }
    if (exportConfig.isExportValueLists()) {
      valueListsHeader =
          miraklExportHeaderResolverStrategy.getSupportedHeaders(MiraklValueListExportHeader.class, additionalLocales);
      valueListsWriter.writeHeader(valueListsHeader);
    }
  }

  @Override
  public boolean writeCategory(Map<String, String> line) throws IOException {
    return csvService.writeLine(categoriesWriter, categoriesHeader, line);
  }

  @Override
  public boolean writeAttribute(Map<String, String> line) throws IOException {
    return csvService.writeLine(attributesWriter, attributesHeader, line);
  }

  @Override
  public boolean writeAttributeValue(Map<String, String> line) throws IOException {
    return csvService.writeLine(valueListsWriter, valueListsHeader, line);
  }

  @Override
  public File getCategoriesFile() throws IOException {
    categoriesWriter.flush();
    return categoriesFile;
  }

  @Override
  public File getAttributesFile() throws IOException {
    attributesWriter.flush();
    return attributesFile;
  }

  @Override
  public File getValueListsFile() throws IOException {
    valueListsWriter.flush();
    return valueListsFile;
  }

  @Override
  public void close() throws Exception {
    closeQuietly(categoriesWriter);
    closeQuietly(attributesWriter);
    closeQuietly(valueListsWriter);
    deleteQuietly(categoriesFile);
    deleteQuietly(attributesFile);
    deleteQuietly(valueListsFile);
  }

  protected CsvMapWriter createWriter(File file) throws IOException {
    return new CsvMapWriter(new FileWriterWithEncoding(file, CATALOG_EXPORT_DEFAULT_ENCODING),
        csvService.getDefaultCsvPreference());
  }

  protected void deleteQuietly(File file) {
    if (file != null && !file.delete()) {
      LOG.error(String.format("Impossible to delete temporary file [%s] after catalog export !", file.getName()));
    }
  }

  @Required
  public void setCsvService(CsvService csvService) {
    this.csvService = csvService;
  }


  @Required
  public void setMiraklExportHeaderResolverStrategy(MiraklExportHeaderResolverStrategy miraklExportHeaderResolverStrategy) {
    this.miraklExportHeaderResolverStrategy = miraklExportHeaderResolverStrategy;
  }
}
