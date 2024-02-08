package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.mirakl.hybris.core.enums.MiraklHeaderUtils.getCodes;
import static java.lang.String.format;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.strategies.MiraklExportHeaderResolverStrategy;
import com.mirakl.hybris.core.enums.MiraklHeader;

public class DefaultMiraklExportHeaderResolverStrategy implements MiraklExportHeaderResolverStrategy {

  private Map<Class<? extends MiraklHeader>, Set<? extends MiraklHeader>> miraklExportHeaders;

  @Override
  public <T extends MiraklHeader> String[] getSupportedHeaders(Class<T> miraklHeaderType, Set<Locale> locales) {
    Set<? extends MiraklHeader> result = miraklExportHeaders.get(miraklHeaderType);
    if (result == null) {
      throw new IllegalArgumentException(format("The MiraklHeader type [%s] is not supported.", miraklHeaderType));
    }
    return getCodes(result, locales);
  }

  @Override
  public <T extends MiraklHeader> String[] getSupportedHeaders(Class<T> miraklHeaderType) {
    return getSupportedHeaders(miraklHeaderType, Collections.emptySet());
  }

  @Required
  public void setMiraklExportHeaders(Map<Class<? extends MiraklHeader>, Set<? extends MiraklHeader>> miraklExportHeaders) {
    this.miraklExportHeaders = miraklExportHeaders;
  }
}
