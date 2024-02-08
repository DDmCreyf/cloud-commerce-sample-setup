package com.mirakl.hybris.core.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
public class HybrisVersionUtilsTest {

  private static final String BUILD_VERSION = "6.2.0.0-SNAPSHOT";

  @Test
  public void testMinimumIfFalse() {
    assertFalse(HybrisVersionUtils.versionChecker(BUILD_VERSION).minimumVersion(6.3).isValid());
  }

  @Test
  public void testMinimumIfTrue() {
    assertTrue(HybrisVersionUtils.versionChecker(BUILD_VERSION).minimumVersion(6.1).isValid());
  }

  @Test
  public void testMaximumIfFalse() {
    assertFalse(HybrisVersionUtils.versionChecker(BUILD_VERSION).maximumVersion(6.1).isValid());
  }

  @Test
  public void testMaximumIfTrue() {
    assertTrue(HybrisVersionUtils.versionChecker(BUILD_VERSION).maximumVersion(6.5).isValid());
  }

  @Test
  public void testBoundariesIfTrue() {
    assertTrue(HybrisVersionUtils.versionChecker(BUILD_VERSION).minimumVersion(6.1).maximumVersion(6.5).isValid());
  }

  @Test
  public void testBoundariesIfFalse() {
    assertFalse(HybrisVersionUtils.versionChecker(BUILD_VERSION).minimumVersion(6.3).maximumVersion(6.5).isValid());
  }

  @Test
  public void testDoubleValue() {
    assertThat(HybrisVersionUtils.versionChecker(BUILD_VERSION).getVersion()).isEqualTo(6.2);
  }

}
