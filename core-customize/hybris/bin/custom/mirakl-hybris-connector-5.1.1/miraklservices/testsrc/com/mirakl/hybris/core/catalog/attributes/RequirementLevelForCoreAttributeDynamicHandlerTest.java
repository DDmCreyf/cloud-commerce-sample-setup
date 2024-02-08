package com.mirakl.hybris.core.catalog.attributes;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.enums.MiraklAttributeRequirementLevel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RequirementLevelForCoreAttributeDynamicHandlerTest {

  @Mock
  private MiraklCoreAttributeModel miraklCoreAttributeModel;

  @InjectMocks
  private RequirementLevelForCoreAttributeDynamicHandler testObj;

  @Test
  public void requirementLevelDefinedToRecommended() {
    when(miraklCoreAttributeModel.getRequirementLevelInternal()).thenReturn(MiraklAttributeRequirementLevel.RECOMMENDED);

    MiraklAttributeRequirementLevel requirementLevel = testObj.get(miraklCoreAttributeModel);

    assertThat(requirementLevel).isEqualTo(MiraklAttributeRequirementLevel.RECOMMENDED);
  }

  @Test
  public void noRequirementLevelDefinedButIsRequired() {
    when(miraklCoreAttributeModel.getRequirementLevelInternal()).thenReturn(null);
    when(miraklCoreAttributeModel.isRequired()).thenReturn(true);

    MiraklAttributeRequirementLevel requirementLevel = testObj.get(miraklCoreAttributeModel);

    assertThat(requirementLevel).isEqualTo(MiraklAttributeRequirementLevel.REQUIRED);
  }

  @Test
  public void noRequirementLevelDefinedAndOptional() {
    when(miraklCoreAttributeModel.getRequirementLevelInternal()).thenReturn(null);
    when(miraklCoreAttributeModel.isRequired()).thenReturn(false);

    MiraklAttributeRequirementLevel requirementLevel = testObj.get(miraklCoreAttributeModel);

    assertThat(requirementLevel).isEqualTo(MiraklAttributeRequirementLevel.OPTIONAL);
  }

  @Test
  public void shouldSetValueOnInternalField() {
    testObj.set(miraklCoreAttributeModel, MiraklAttributeRequirementLevel.OPTIONAL);

    verify(miraklCoreAttributeModel).setRequirementLevelInternal(MiraklAttributeRequirementLevel.OPTIONAL);
  }

}
