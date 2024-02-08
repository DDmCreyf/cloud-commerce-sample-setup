package com.mirakl.hybris.core.enums;

import static java.lang.String.format;

import java.util.*;

public class MiraklHeaderUtils {

  private MiraklHeaderUtils() {}

  public static <T extends MiraklHeader> String getCode(T header, Locale locale) {
    if (header.isLocalizable()) {
      return format("%s[%s]", header.getCode(), locale.getLanguage());
    }
    return header.getCode();
  }

  public static <T extends MiraklHeader> String[] codes(T[] headerValues) {
    return codes(headerValues, Collections.emptyList());
  }

  public static <T extends MiraklHeader> String[] codes(T[] headerValues, List<Locale> locales) {
    return getCodes(Arrays.asList(headerValues), locales);
  }

  public static <T extends MiraklHeader> String[] getCodes(Collection<T> headerValues, Iterable<Locale> locales) {
    List<String> codes = new ArrayList<>(headerValues.size());
    for (MiraklHeader headerColumn : headerValues) {
      codes.add(headerColumn.getCode());
      if (headerColumn.isLocalizable()) {
        for (Locale locale : locales) {
          codes.add(headerColumn.getCode(locale));
        }
      }
    }
    return codes.toArray(new String[0]);
  }
}
