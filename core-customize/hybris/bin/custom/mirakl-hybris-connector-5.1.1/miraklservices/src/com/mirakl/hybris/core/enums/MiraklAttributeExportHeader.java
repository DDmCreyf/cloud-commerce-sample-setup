package com.mirakl.hybris.core.enums;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.mirakl.hybris.core.catalog.strategies.MiraklExportHeaderResolverStrategy;

public enum MiraklAttributeExportHeader implements MiraklHeader {
  CODE("code"), //
  LABEL("label", true), //
  HIERARCHY_CODE("hierarchy-code"), //
  DESCRIPTION("description", true), //
  EXAMPLE("example"), //
  REQUIRED("required"), //
  REQUIREMENT_LEVEL("requirement-level"), //
  TYPE("type"), //
  @Deprecated
  TYPE_PARAMETER("type-parameter"), //
  TYPE_PARAMETERS("type-parameters"), //
  VARIANT("variant"), //
  DEFAULT_VALUE("default-value"), //
  TRANSFORMATIONS("transformations"), //
  VALIDATIONS("validations"), //
  UPDATE_DELETE("update-delete");

  private String code;
  private boolean localizable;

  MiraklAttributeExportHeader(String code) {
    this.code = code;
  }

  MiraklAttributeExportHeader(String code, boolean localizable) {
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
