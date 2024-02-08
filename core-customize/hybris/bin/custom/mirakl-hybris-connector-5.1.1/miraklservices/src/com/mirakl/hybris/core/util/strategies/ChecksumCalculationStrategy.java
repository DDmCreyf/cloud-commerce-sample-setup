package com.mirakl.hybris.core.util.strategies;

public interface ChecksumCalculationStrategy {

  /**
   * Calculates a checksum on a given value
   * 
   * @param value on which a checksum is calculated
   * @return a checksum of the given value
   */
  String calculateChecksum(String value);

}
