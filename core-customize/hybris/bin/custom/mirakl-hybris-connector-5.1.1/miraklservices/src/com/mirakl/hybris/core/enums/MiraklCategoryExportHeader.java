package com.mirakl.hybris.core.enums;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.mirakl.hybris.core.catalog.strategies.MiraklExportHeaderResolverStrategy;

public enum MiraklCategoryExportHeader implements MiraklHeader {

  CATEGORY_CODE("category-code"), //
  CATEGORY_LABEL("category-label", true), //
  LOGISTIC_CLASS("logistic-class"), //
  UPDATE_DELETE("update-delete"), //
  PARENT_CODE("parent-code"), //
  CATEGORY_DESCRIPTION("category-description", true), //
  MEDIA_URL("media-url");

  private String code;
  private boolean localizable;

  MiraklCategoryExportHeader(String code) {
    this.code = code;
  }

  MiraklCategoryExportHeader(String code, boolean localizable) {
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
