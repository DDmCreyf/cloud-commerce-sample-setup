package com.mirakl.hybris.core.customfields.services.impl;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.hybris.core.enums.MiraklCustomFieldType;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue.*;
import static com.mirakl.hybris.core.enums.MiraklCustomFieldType.*;
import static org.fest.assertions.Assertions.assertThat;

@UnitTest
@RunWith(Parameterized.class)
public class DefaultCustomFieldServiceParameterizedTest {

  private static final String CODE = "code";
  private static final String VALUE = "value";
  private static final DefaultCustomFieldService testObj = new DefaultCustomFieldService();

  private final MiraklCustomFieldType inputFieldType;
  private final Class<? extends MiraklAdditionalFieldValue> expectedOutputClass;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { //
        {STRING, MiraklStringAdditionalFieldValue.class}, {DATE, MiraklDateAdditionalFieldValue.class}, //
        {NUMERIC, MiraklNumericAdditionalFieldValue.class}, {BOOLEAN, MiraklBooleanAdditionalFieldValue.class}, //
        {LINK, MiraklLinkAdditionalFieldValue.class}, {LIST, MiraklValueListAdditionalFieldValue.class}, //
        {REGEX, MiraklRegexAdditionalFieldValue.class}, {TEXTAREA, MiraklTextAreaAdditionalFieldValue.class}, //
        {MULTIPLE_VALUES_LIST, MiraklMultipleValuesListAdditionalFieldValue.class}});
  }

  public DefaultCustomFieldServiceParameterizedTest(final MiraklCustomFieldType inputFieldType,
      final Class<? extends MiraklAdditionalFieldValue> expectedOutputClass) {
    this.inputFieldType = inputFieldType;
    this.expectedOutputClass = expectedOutputClass;
  }

  @Test
  public void buildMiraklAdditionalFieldValue() {
    assertThat(expectedOutputClass).isEqualTo(testObj.buildMiraklAdditionalFieldValue(CODE, VALUE, inputFieldType).getClass());
  }

}
