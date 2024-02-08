package com.mirakl.hybris.core.catalog.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.mirakl.hybris.core.enums.*;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklExportHeaderResolverStrategyTest {

  private DefaultMiraklExportHeaderResolverStrategy testObj = new DefaultMiraklExportHeaderResolverStrategy();
  private Map<Class<? extends MiraklHeader>, Set<? extends MiraklHeader>> miraklExportHeaders;
  private Set<MiraklCatalogCategoryExportHeader> miraklCatalogCategoryExportHeaders;
  private Set<MiraklAttributeExportHeader> miraklAttributeExportHeaders;
  private Set<MiraklCategoryExportHeader> miraklCategoryExportHeaders;
  private Set<MiraklProductExportHeader> miraklProductExportHeaders;
  private Set<MiraklValueListExportHeader> miraklValueListExportHeaders;

  private Locale locale = Locale.FRANCE;
  private Set<Locale> additionalLocales;

  @Before
  public void setUp() {
    additionalLocales = Sets.newHashSet(locale);
    miraklCatalogCategoryExportHeaders = Sets.newHashSet(MiraklCatalogCategoryExportHeader.values());
    miraklAttributeExportHeaders = Sets.newHashSet(MiraklAttributeExportHeader.values());
    miraklCategoryExportHeaders = Sets.newHashSet(MiraklCategoryExportHeader.values());
    miraklProductExportHeaders = Sets.newHashSet(MiraklProductExportHeader.values());
    miraklValueListExportHeaders = Sets.newHashSet(MiraklValueListExportHeader.values());
    miraklExportHeaders = new HashMap<>();
    miraklExportHeaders.put(MiraklCatalogCategoryExportHeader.class, miraklCatalogCategoryExportHeaders);
    miraklExportHeaders.put(MiraklAttributeExportHeader.class, miraklAttributeExportHeaders);
    miraklExportHeaders.put(MiraklCategoryExportHeader.class, miraklCategoryExportHeaders);
    miraklExportHeaders.put(MiraklProductExportHeader.class, miraklProductExportHeaders);
    miraklExportHeaders.put(MiraklValueListExportHeader.class, miraklValueListExportHeaders);
    testObj.setMiraklExportHeaders(miraklExportHeaders);
  }

  @Test
  public void getSupportedHeader() {
    String[] result1 = testObj.getSupportedHeaders(MiraklCatalogCategoryExportHeader.class);
    assertThat(result1).hasSize(miraklCatalogCategoryExportHeaders.size());

    String[] result2 = testObj.getSupportedHeaders(MiraklAttributeExportHeader.class);
    assertThat(result2).hasSize(miraklAttributeExportHeaders.size());

    String[] result3 = testObj.getSupportedHeaders(MiraklCategoryExportHeader.class);
    assertThat(result3).hasSize(miraklCategoryExportHeaders.size());

    String[] result4 = testObj.getSupportedHeaders(MiraklProductExportHeader.class);
    assertThat(result4).hasSize(miraklProductExportHeaders.size());

    String[] result5 = testObj.getSupportedHeaders(MiraklValueListExportHeader.class);
    assertThat(result5).hasSize(miraklValueListExportHeaders.size());
  }

  @Test
  public void getSupportedHeaderWithAdditionalLocales() {
    String[] result1 = testObj.getSupportedHeaders(MiraklCatalogCategoryExportHeader.class, additionalLocales);
    assertThat(result1).hasSize(miraklCatalogCategoryExportHeaders.size() + 1);

    String[] result2 = testObj.getSupportedHeaders(MiraklAttributeExportHeader.class, additionalLocales);
    assertThat(result2).hasSize(miraklAttributeExportHeaders.size() + 2);

    String[] result3 = testObj.getSupportedHeaders(MiraklCategoryExportHeader.class, additionalLocales);
    assertThat(result3).hasSize(miraklCategoryExportHeaders.size() + 2);

    String[] result4 = testObj.getSupportedHeaders(MiraklProductExportHeader.class, additionalLocales);
    assertThat(result4).hasSize(miraklProductExportHeaders.size());

    String[] result5 = testObj.getSupportedHeaders(MiraklValueListExportHeader.class, additionalLocales);
    assertThat(result5).hasSize(miraklValueListExportHeaders.size() + 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getSupportedHeaderShouldThrowExceptionIfNotSupportedClass() {
    testObj.getSupportedHeaders(MiraklIllegalHeaderTestEnum.class, additionalLocales);
  }

  protected enum MiraklIllegalHeaderTestEnum implements MiraklHeader {
    TEST_ILLEGAL("test-illegal");

    private String code;
    private boolean localizable;

    MiraklIllegalHeaderTestEnum(String code) {
      this.code = code;
    }

    MiraklIllegalHeaderTestEnum(String code, boolean localizable) {
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

    @Override
    public MiraklHeader[] getValues() {
      return values();
    }

    @Override
    public boolean isLocalizable() {
      return localizable;
    }
  }

}
