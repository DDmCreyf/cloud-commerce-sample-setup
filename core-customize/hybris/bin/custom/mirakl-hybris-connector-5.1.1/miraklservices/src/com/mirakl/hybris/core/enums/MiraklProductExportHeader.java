package com.mirakl.hybris.core.enums;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.mirakl.hybris.core.catalog.strategies.MiraklExportHeaderResolverStrategy;

public enum MiraklProductExportHeader implements MiraklHeader {
  PRODUCT_SKU("product-sku"), //
  PRODUCT_DESCRIPTION("product-description"), //
  PRODUCT_TITLE("product-title"), //
  CATEGORY_CODE("category-code"), //
  ACTIVE("active"), //
  PRODUCT_REFERENCES("product-references"), //
  SHOP_SKUS("shop-skus"), //
  BRAND("brand"), //
  UPDATE_DELETE("update-delete"), //
  PRODUCT_URL("product-url"), //
  MEDIA_URL("media-url"), //
  AUTHORIZED_SHOP_IDS("authorized-shop-ids"), //
  VARIANT_GROUP_CODE("variant-group-code"), //
  LOGISTIC_CLASS("logistic-class");

  private String code;
  private boolean localizable;

  MiraklProductExportHeader(String code) {
    this.code = code;
  }

  MiraklProductExportHeader(String code, boolean localizable) {
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
