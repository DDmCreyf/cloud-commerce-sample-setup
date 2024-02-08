package com.mirakl.hybris.core.enums;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.mirakl.hybris.core.catalog.strategies.MiraklExportHeaderResolverStrategy;

public enum MiraklValueListExportHeader implements MiraklHeader {
  LIST_CODE("list-code"), //
  LIST_LABEL("list-label", true), //
  VALUE_CODE("value-code"), //
  VALUE_LABEL("value-label", true), //
  UPDATE_DELETE("update-delete");

  private String code;
  private boolean localizable;

  MiraklValueListExportHeader(String code) {
    this.code = code;
  }

  MiraklValueListExportHeader(String code, boolean localizable) {
    this.localizable = localizable;
    this.code = code;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getCode(Locale locale) {
    return MiraklHeaderUtils.getCode(this, locale);
  }

  /**
   * This return all the enum codes.
   *
   * @deprecated use {@link MiraklExportHeaderResolverStrategy#getSupportedHeaders(Class)} instead.
   */
  @Deprecated
  public static String[] codes() {
    return MiraklHeaderUtils.codes(values());
  }

  /**
   * This return all the enum codes with localization.
   *
   * @deprecated use {@link MiraklExportHeaderResolverStrategy#getSupportedHeaders(Class, Set)} instead.
   */
  @Deprecated
  public static String[] codes(List<Locale> locales) {
    return MiraklHeaderUtils.codes(values(), locales);
  }

  @Override
  public MiraklHeader[] getValues() {
    return values();
  }

  @Override
  public boolean isLocalizable() {
    return localizable;
  }

}
