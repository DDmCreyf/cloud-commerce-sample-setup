package com.mirakl.hybris.core.customfields.services.impl;

import static com.mirakl.hybris.core.enums.MiraklCustomFieldType.STRING;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.hybris.core.customfields.daos.MiraklCustomFieldDao;
import com.mirakl.hybris.core.enums.MiraklCustomFieldLinkedEntity;
import com.mirakl.hybris.core.model.MiraklCustomFieldModel;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCustomFieldServiceTest {
  private static final String CODE = "CODE";
  private static final String VALUE = "VALUE";
  private static final Map<String, String> EXPORTED_VALUES = new HashMap<>();
  private static final MiraklCustomFieldLinkedEntity MIRAKL_CUSTOM_FIELD_LINKED_ENTITY = MiraklCustomFieldLinkedEntity.SHOP;

  @InjectMocks
  private DefaultCustomFieldService testObj;

  @Mock
  private MiraklCustomFieldDao miraklCustomFieldsDao;
  @Mock
  private MiraklCustomFieldModel miraklCustomField;

  @Before
  public void setUp() throws IOException {
    when(miraklCustomFieldsDao.findCustomFieldByCodeAndEntity(CODE, MIRAKL_CUSTOM_FIELD_LINKED_ENTITY))
        .thenReturn(miraklCustomField);
    EXPORTED_VALUES.put(CODE, VALUE);
    when(miraklCustomField.getType()).thenReturn(STRING);
  }

  @Test
  public void findCustomFieldByCodeAndEntity() {
    MiraklCustomFieldModel customFieldByCodeAndEntity =
        testObj.findCustomFieldByCodeAndEntity(CODE, MIRAKL_CUSTOM_FIELD_LINKED_ENTITY);

    assertThat(customFieldByCodeAndEntity).isEqualTo(miraklCustomField);
  }

  @Test
  public void getCustomFieldValues() {
    List<MiraklAdditionalFieldValue> miraklAdditionalFieldValues =
        testObj.getCustomFieldValues(EXPORTED_VALUES, MIRAKL_CUSTOM_FIELD_LINKED_ENTITY);

    MiraklAdditionalFieldValue miraklAdditionalFieldValue = miraklAdditionalFieldValues.get(0);
    assertThat(miraklAdditionalFieldValue.getCode()).isEqualTo(CODE);
    assertThat(miraklAdditionalFieldValue.getClass())
        .isEqualTo(MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue.class);
    assertThat(((MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue) miraklAdditionalFieldValue).getValue())
        .isEqualTo(VALUE);
  }
}
