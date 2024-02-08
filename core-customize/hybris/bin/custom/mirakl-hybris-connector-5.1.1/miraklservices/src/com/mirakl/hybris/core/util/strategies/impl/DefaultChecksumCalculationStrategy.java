package com.mirakl.hybris.core.util.strategies.impl;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.hash.Hashing;
import com.mirakl.hybris.core.util.strategies.ChecksumCalculationStrategy;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultChecksumCalculationStrategy implements ChecksumCalculationStrategy {

  @Override
  public String calculateChecksum(String value) {
    return Hashing.sha256().hashString(value, UTF_8).toString();
  }

}
