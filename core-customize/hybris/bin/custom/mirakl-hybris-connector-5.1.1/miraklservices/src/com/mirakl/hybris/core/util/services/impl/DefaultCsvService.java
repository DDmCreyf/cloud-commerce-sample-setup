package com.mirakl.hybris.core.util.services.impl;

import static com.mirakl.client.core.internal.util.Preconditions.checkArgument;
import static java.io.File.createTempFile;
import static java.lang.String.format;
import static org.fest.util.Arrays.isEmpty;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.log4j.Logger;

import com.mirakl.hybris.core.util.services.CsvService;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import shaded.org.supercsv.exception.SuperCsvConstraintViolationException;
import shaded.org.supercsv.io.CsvMapWriter;
import shaded.org.supercsv.io.ICsvMapWriter;
import shaded.org.supercsv.prefs.CsvPreference;
import shaded.org.supercsv.quote.AlwaysQuoteMode;

public class DefaultCsvService implements CsvService {

  private static final Logger LOG = Logger.getLogger(DefaultCsvService.class);
  private static final String DEFAULT_CSV_EXTENSION = ".csv";
  private static final String TEMP_CSV_FILE_PREFIX = "mirakl_connector_";
  private static final String DEFAULT_CSV_FILE_ENCODING = "UTF-8";

  @Override
  public String createCsvWithHeaders(String[] header, List<Map<String, String>> lines) throws IOException {
    checkArgument(!isEmpty(header), "Header array cannot be empty for CSV file");
    ServicesUtil.validateParameterNotNull(lines, "Value lines cannot be empty for CSV file");

    int fails = 0;
    int successes = 0;
    try (StringBuilderWriter writer = new StringBuilderWriter();
        ICsvMapWriter csvMapWriter = new CsvMapWriter(writer, getDefaultCsvPreference())) {
      csvMapWriter.writeHeader(header);

      for (Map<String, String> line : lines) {
        try {
          csvMapWriter.write(line, header);
          successes++;
        } catch (SuperCsvConstraintViolationException e) {
          LOG.error(format("CSV Constraint Violation occurred on line :[%s]", line), e);
          fails++;
        }
      }
      LOG.info(format("Finished writing CSV content. [%s successful lines] / [%s lines in error]", successes, fails));
      csvMapWriter.flush();

      return writer.toString();

    }
  }

  @Override
  public <T> File createCsvFileWithHeaders(String[] header, Iterable<T> data,
      Converter<T, Map<String, String>> dataToLineConverter) throws IOException {
    checkArgument(!isEmpty(header), "Header array cannot be empty for CSV file");
    ServicesUtil.validateParameterNotNull(data, "Data cannot be null for CSV file");
    ServicesUtil.validateParameterNotNull(dataToLineConverter, "The converter cannot be null for CSV file");

    int fails = 0;
    int successes = 0;

    final File csvFile = createTempFile(TEMP_CSV_FILE_PREFIX, DEFAULT_CSV_EXTENSION);
    try (FileWriterWithEncoding writer = new FileWriterWithEncoding(csvFile, DEFAULT_CSV_FILE_ENCODING);
        ICsvMapWriter csvMapWriter = new CsvMapWriter(writer, getDefaultCsvPreference())) {
      csvMapWriter.writeHeader(header);

      Map<String, String> line = null;
      for (T rawLine : data) {
        try {
          line = dataToLineConverter.convert(rawLine);
          csvMapWriter.write(line, header);
          successes++;
        } catch (SuperCsvConstraintViolationException e) {
          LOG.error(String.format("CSV Constraint Violation occurred on line: [%s]", line), e);
          fails++;
        } catch (ConversionException e) {
          LOG.error("Impossible to convert data to write in CSV", e);
          fails++;
        }
      }
      LOG.info(format("Finished writing CSV content. [%s successful lines] / [%s lines in error]", successes, fails));
      csvMapWriter.flush();

      return csvFile;
    }
  }

  @Override
  public CsvPreference getDefaultCsvPreference() {
    return new CsvPreference.Builder(CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE)//
        .useQuoteMode(new AlwaysQuoteMode()) //
        .surroundingSpacesNeedQuotes(true) //
        .build();
  }

  @Override
  public boolean writeLine(ICsvMapWriter mapWriter, String[] header, Map<String, String> line) throws IOException {
    try {
      mapWriter.write(line, header);
    } catch (SuperCsvConstraintViolationException e) {
      LOG.error(format("CSV Constraint Violation occurred on line :[%s]", line), e);
      return false;
    }
    return true;
  }

}
