package com.mirakl.hybris.core.customfields.populators;

import com.mirakl.client.mmp.domain.additionalfield.FieldPermission;
import com.mirakl.client.mmp.domain.additionalfield.MiraklAdditionalFieldLinkedEntity;
import com.mirakl.client.mmp.domain.additionalfield.MiraklAdditionalFieldType;
import com.mirakl.client.mmp.domain.additionalfield.MiraklFrontOperatorAdditionalField;
import com.mirakl.hybris.core.enums.MiraklCustomFieldLinkedEntity;
import com.mirakl.hybris.core.enums.MiraklCustomFieldPermission;
import com.mirakl.hybris.core.enums.MiraklCustomFieldType;
import com.mirakl.hybris.core.model.MiraklCustomFieldModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCustomFieldPopulatorTest {
  private static final String CODE = "code", OLD_CODE = "old-code";
  private static final String LABEL = "label";
  private static final String REGEX = "regex";

  @InjectMocks
  private MiraklCustomFieldPopulator testObj;

  @Mock
  private MiraklFrontOperatorAdditionalField additionalField;

  private static final MiraklAdditionalFieldType ADDITIONAL_FIELD_TYPE = MiraklAdditionalFieldType.STRING;
  private static final FieldPermission FIELD_PERMISSION = FieldPermission.READ_WRITE;
  private static final MiraklAdditionalFieldLinkedEntity ADDITIONAL_FIELD_ENTITY = MiraklAdditionalFieldLinkedEntity.SHOP;
  private static final MiraklCustomFieldType CUSTOM_FIELD_TYPE = MiraklCustomFieldType.STRING;
  private static final MiraklCustomFieldPermission PERMISSION = MiraklCustomFieldPermission.READ_WRITE;
  private static final MiraklCustomFieldLinkedEntity ENTITY = MiraklCustomFieldLinkedEntity.SHOP,
      OLD_ENTITY = MiraklCustomFieldLinkedEntity.ORDER;

  private List<String> acceptedValues = Collections.singletonList("acceptedValue");

  @Before
  public void setUp() throws Exception {
    when(additionalField.getCode()).thenReturn(CODE);
    when(additionalField.getEntity()).thenReturn(ADDITIONAL_FIELD_ENTITY);
    when(additionalField.getLabel()).thenReturn(LABEL);
    when(additionalField.getRegex()).thenReturn(REGEX);
    when(additionalField.getShopPermission()).thenReturn(FIELD_PERMISSION);
    when(additionalField.getType()).thenReturn(ADDITIONAL_FIELD_TYPE);
    when(additionalField.getAcceptedValues()).thenReturn(acceptedValues);
  }

  @Test
  public void populateShouldPopulateAllFieldOnCreate() {
    MiraklCustomFieldModel result = new MiraklCustomFieldModel();

    testObj.populate(additionalField, result);

    assertThat(result.getCode()).isEqualTo(CODE);
    assertThat(result.getEntity()).isEqualTo(ENTITY);
    assertThat(result.getLabel()).isEqualTo(LABEL);
    assertThat(result.getRegex()).isEqualTo(REGEX);
    assertThat(result.getShopPermission()).isEqualTo(PERMISSION);
    assertThat(result.getType()).isEqualTo(CUSTOM_FIELD_TYPE);
    assertThat(result.getAcceptedValues()).isEqualTo(acceptedValues);
  }

  @Test
  public void populateShouldPopulateNotAllFieldOnUpdate() {
    MiraklCustomFieldModel result = new MiraklCustomFieldModel();
    result.setCode(OLD_CODE);
    result.setEntity(OLD_ENTITY);

    testObj.populate(additionalField, result);

    assertThat(result.getCode()).isEqualTo(OLD_CODE);
    assertThat(result.getEntity()).isEqualTo(OLD_ENTITY);
    assertThat(result.getLabel()).isEqualTo(LABEL);
    assertThat(result.getRegex()).isEqualTo(REGEX);
    assertThat(result.getShopPermission()).isEqualTo(PERMISSION);
    assertThat(result.getType()).isEqualTo(CUSTOM_FIELD_TYPE);
    assertThat(result.getAcceptedValues()).isEqualTo(acceptedValues);
  }

}
