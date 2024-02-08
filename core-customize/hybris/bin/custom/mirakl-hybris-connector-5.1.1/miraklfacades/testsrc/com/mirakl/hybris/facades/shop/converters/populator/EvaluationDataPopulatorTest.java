package com.mirakl.hybris.facades.shop.converters.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.evaluation.MiraklEvaluation;
import com.mirakl.hybris.beans.EvaluationData;

@RunWith(value = MockitoJUnitRunner.class)
public class EvaluationDataPopulatorTest {

  public static final String CUSTOMER_LAST_NAME = "customer_last_name";
  public static final String CUSTOMER_FIRST_NAME = "customer_first_name";
  public static final String CUSTOMER_ID = "customer_id";
  public static final String EVALUATION_COMMENT = "evaluation_comment";
  public static final int EVALUATION_GRADE = 5;
  public static final Date EVALUATION_DATE = new Date();

  @Mock
  private MiraklEvaluation miraklEvaluation;

  @InjectMocks
  private EvaluationDataPopulator testObj;

  @Before
  public void setUp() {
    when(miraklEvaluation.getComment()).thenReturn(EVALUATION_COMMENT);
    when(miraklEvaluation.getDate()).thenReturn(EVALUATION_DATE);
    when(miraklEvaluation.getFirstname()).thenReturn(CUSTOMER_FIRST_NAME);
    when(miraklEvaluation.getGrade()).thenReturn(EVALUATION_GRADE);
    when(miraklEvaluation.getLastname()).thenReturn(CUSTOMER_LAST_NAME);
  }

  @Test
  public void populateTest() {
    EvaluationData output = new EvaluationData();
    testObj.populate(miraklEvaluation, output);
    assertThat(output.getComment()).isEqualTo(EVALUATION_COMMENT);
    assertThat(output.getDate()).isEqualTo(EVALUATION_DATE);
    assertThat(output.getFirstName()).isEqualTo(CUSTOMER_FIRST_NAME);
    assertThat(output.getGrade()).isEqualTo(EVALUATION_GRADE);
    assertThat(output.getLastName()).isEqualTo(CUSTOMER_LAST_NAME);
  }

}
