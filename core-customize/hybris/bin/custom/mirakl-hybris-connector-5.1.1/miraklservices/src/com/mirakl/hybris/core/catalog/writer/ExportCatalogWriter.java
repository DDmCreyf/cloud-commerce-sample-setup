package com.mirakl.hybris.core.catalog.writer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface ExportCatalogWriter extends AutoCloseable {

  /**
   * Creates the writers and the temporary files.
   * 
   * @throws IOException If an error is encountered during writers creation
   */
  void initialize() throws IOException;

  /**
   * Writes a new line in the temporary category csv file
   * 
   * @param line The line to write. The key of the map is the header name.
   * @return A boolean depending on the success of the writing
   * @throws IOException If the writing was unsuccessful
   */
  boolean writeCategory(Map<String, String> line) throws IOException;

  /**
   * Writes a new line in the temporary attribute csv file
   * 
   * @param line The line to write. The key of the map is the header name.
   * @return A boolean depending on the success of the writing
   * @throws IOException If the writing was unsuccessful
   */
  boolean writeAttribute(Map<String, String> line) throws IOException;

  /**
   * Writes a new line in the temporary value list csv file
   * 
   * @param line The line to write. The key of the map is the header name.
   * @return A boolean depending on the success of the writing
   * @throws IOException If the writing was unsuccessful
   */
  boolean writeAttributeValue(Map<String, String> line) throws IOException;

  /**
   * Flushes the writer and returns the temporary Category csv file
   * 
   * @return The temporary Category file
   * @throws IOException If the flushing was unsuccessful
   */
  File getCategoriesFile() throws IOException;

  /**
   * Flushes the writer and returns the temporary Attribute csv file
   * 
   * @return The temporary Attribute file
   * @throws IOException If the flushing was unsuccessful
   */
  File getAttributesFile() throws IOException;

  /**
   * Flushes the writer and returns the temporary Value List csv file
   * 
   * @return The temporary Value List file
   * @throws IOException If the flushing was unsuccessful
   */
  File getValueListsFile() throws IOException;
}
