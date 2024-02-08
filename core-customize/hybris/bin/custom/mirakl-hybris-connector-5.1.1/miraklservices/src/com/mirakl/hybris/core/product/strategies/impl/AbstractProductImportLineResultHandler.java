package com.mirakl.hybris.core.product.strategies.impl;

import static java.io.File.createTempFile;
import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportResultData;
import com.mirakl.hybris.core.product.strategies.ProductImportLineResultHandler;
import com.mirakl.hybris.core.util.services.CsvService;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import shaded.org.supercsv.io.CsvMapWriter;
import shaded.org.supercsv.io.ICsvMapWriter;

public abstract class AbstractProductImportLineResultHandler<T extends ProductImportResultData>
    implements ProductImportLineResultHandler<T> {

  private static final Logger LOG = Logger.getLogger(AbstractProductImportLineResultHandler.class);

  protected static final String DEFAULT_ENCODING = "UTF-8";
  protected static final String FILE_EXTENSION = ".csv";

  protected CsvService csvService;
  protected ConfigurationService configurationService;

  protected File file;
  protected ICsvMapWriter writer;
  protected String[] header;
  protected boolean initialized;
  protected String filename;

  protected ProductImportFileContextData context;

  public AbstractProductImportLineResultHandler(ProductImportFileContextData context) {
    this.context = context;
  }

  @Override
  public void initialize() throws IOException {
    file = initializeFile();
    header = getHeader();
    writer = initializeWriter();
    initialized = true;
  }

  @Override
  public void handleLineResult(T resultData) {
    try {
      // Needs to be lazily initialized (waiting for context.getHeaderInfos() to be available)
      if (!initialized) {
        initialize();
      }
      csvService.writeLine(writer, header, buildLine(resultData));
    } catch (IOException e) {
      LOG.error(format("An I/O error occurred in the result handler thread for file [%s]", context.getReceivedFile().getName()),
          e);
    }
  }

  @Override
  public void flush() throws IOException {
    if (writer != null) {
      writer.flush();
    }
  }

  protected ICsvMapWriter initializeWriter() throws IOException {
    ICsvMapWriter csvMapWriter =
        new CsvMapWriter(new FileWriterWithEncoding(file, DEFAULT_ENCODING), csvService.getDefaultCsvPreference());
    csvMapWriter.writeHeader(header);

    return csvMapWriter;
  }

  protected File initializeFile() throws IOException {
    File tempFile = createTempFile(getFilename(), FILE_EXTENSION);
    storeFileInContext(tempFile);
    return tempFile;
  }


  @Override
  public void close() throws Exception {
    if (writer != null) {
      closeQuietly(writer);
    }
  }

  protected abstract String[] getHeader();

  protected abstract Map<String, String> buildLine(T resultData);

  protected abstract void storeFileInContext(File file);


  @Required
  public void setCsvService(CsvService csvService) {
    this.csvService = csvService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

}
