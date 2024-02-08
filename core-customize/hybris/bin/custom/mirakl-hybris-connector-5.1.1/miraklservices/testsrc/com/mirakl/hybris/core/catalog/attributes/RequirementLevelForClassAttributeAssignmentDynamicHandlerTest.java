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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RequirementLevelForClassAttributeAssignmentDynamicHandlerTest {

  @Mock
  private ClassAttributeAssignmentModel classAttributeAssignmentModel;

  @InjectMocks
  private RequirementLevelForClassAttributeAssignmentDynamicHandler testObj;

  @Test
  public void requirementLevelDefinedToRecommended() {
    when(classAttributeAssignmentModel.getMarketplaceRequirementLevelInternal())
        .thenReturn(MiraklAttributeRequirementLevel.RECOMMENDED);

    MiraklAttributeRequirementLevel requirementLevel = testObj.get(classAttributeAssignmentModel);

    assertThat(requirementLevel).isEqualTo(MiraklAttributeRequirementLevel.RECOMMENDED);
  }

  @Test
  public void noRequirementLevelDefinedButIsRequiredForMarketplace() {
    when(classAttributeAssignmentModel.getMarketplaceRequirementLevelInternal()).thenReturn(null);
    when(classAttributeAssignmentModel.isRequiredForMarketplace()).thenReturn(true);

    MiraklAttributeRequirementLevel requirementLevel = testObj.get(classAttributeAssignmentModel);

    assertThat(requirementLevel).isEqualTo(MiraklAttributeRequirementLevel.REQUIRED);
  }

  @Test
  public void noRequirementLevelDefinedAndOptionalForMarketplace() {
    when(classAttributeAssignmentModel.getMarketplaceRequirementLevelInternal()).thenReturn(null);
    when(classAttributeAssignmentModel.isRequiredForMarketplace()).thenReturn(false);

    MiraklAttributeRequirementLevel requirementLevel = testObj.get(classAttributeAssignmentModel);

    assertThat(requirementLevel).isEqualTo(MiraklAttributeRequirementLevel.OPTIONAL);
  }

  @Test
  public void shouldSetValueOnInternalField() {
    testObj.set(classAttributeAssignmentModel, MiraklAttributeRequirementLevel.OPTIONAL);

    verify(classAttributeAssignmentModel).setMarketplaceRequirementLevelInternal(MiraklAttributeRequirementLevel.OPTIONAL);
  }
}
