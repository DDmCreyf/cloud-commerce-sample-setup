package com.mirakl.hybris.core.catalog.strategies;

import java.util.Locale;
import java.util.Set;

import com.mirakl.hybris.core.enums.MiraklHeader;

public interface MiraklExportHeaderResolverStrategy {

  <T extends MiraklHeader> String[] getSupportedHeaders(Class<T> miraklHeaderType, Set<Locale> locales);

  <T extends MiraklHeader> String[] getSupportedHeaders(Class<T> miraklHeaderType);
}
