package com.mirakl.hybris.core.util.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import shaded.org.supercsv.io.ICsvMapWriter;
import shaded.org.supercsv.prefs.CsvPreference;

public interface CsvService {

  /**
   * Creates a csv as a string
   * 
   * @param headers the csv header
   * @param lines the lines to export
   * @return a string containing the csv data
   * @throws IOException
   */
  String createCsvWithHeaders(String[] headers, List<Map<String, String>> lines) throws IOException;

  /**
   * Creates a csv file from an iterable. The file should be deleted after being used.
   *
   * @param header the csv header
   * @param data the lines to export
   * @param dataToLineConverter a converter transforming a data line to a csv line
   * @return the name of the generated csv file
   * @throws IOException
   */
  <T> File createCsvFileWithHeaders(String[] header, Iterable<T> data, Converter<T, Map<String, String>> dataToLineConverter)
      throws IOException;

  /**
   * Returns the default CSV export preferences
   * 
   * @return a csvPreference configuration
   */
  CsvPreference getDefaultCsvPreference();

  /**
   * Writes a line using the given mapWriter
   * 
   * @param mapWriter the map writer
   * @param header the csv header
   * @param line the line to write
   * @return true if the line has been properly written
   * @throws IOException
   */
  boolean writeLine(ICsvMapWriter mapWriter, String[] header, Map<String, String> line) throws IOException;

}
