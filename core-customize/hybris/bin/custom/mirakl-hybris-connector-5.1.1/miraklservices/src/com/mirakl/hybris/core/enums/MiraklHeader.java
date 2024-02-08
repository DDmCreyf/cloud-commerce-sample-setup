package com.mirakl.hybris.core.enums;

import java.util.Locale;

public interface MiraklHeader {

  /**
   * Returns a code for a header column
   * 
   * @return header code
   */
  String getCode();

  /**
   * Returns a localizable header column name
   * 
   * @param locale the locale to use
   * @return the localized code
   */
  String getCode(Locale locale);

  /**
   * Checks if a header column is localizable
   * 
   * @return true if a header is localizable
   */
  boolean isLocalizable();

  /**
   * Returns header values
   * 
   * @return headerValues
   */
  MiraklHeader[] getValues();


}
