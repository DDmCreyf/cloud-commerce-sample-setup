package com.mirakl.hybris.core.environment.services.impl;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonMap;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.environment.daos.MiraklEnvironmentDao;
import com.mirakl.hybris.core.model.MiraklEnvironmentModel;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklEnvironmentServiceTest {

  private static final Map<String, Boolean> QUERY_PARAMETERS = singletonMap(MiraklEnvironmentModel.DEFAULT, Boolean.TRUE);
  private static final String FRONT_API_KEY = "fbe46af7-4bef-470b-949a-c10d5ce96135";
  private static final String OPERATOR_API_KEY = "fb94dd9f-352a-4674-8226-94a3d716b65d";
  private static final String API_URL = "https://documentation1-test.mirakl.net";

  @InjectMocks
  private DefaultMiraklEnvironmentService testObj;

  @Mock
  private MiraklEnvironmentDao miraklEnvironmentDao;
  @Mock
  private MiraklEnvironmentModel miraklEnvironmentDefault, miraklEnvironmentDefault2;

  @Test
  public void findByDefaultShouldReturnOnlyOneMiraklEnvironment() {
    when(miraklEnvironmentDao.find(QUERY_PARAMETERS)).thenReturn(newArrayList(miraklEnvironmentDefault));
    MiraklEnvironmentModel resultObj = testObj.getDefault();

    assertThat(resultObj).isEqualTo(miraklEnvironmentDefault);
  }

  @Test(expected = IllegalStateException.class)
  public void findByDefaultShouldThrowIllegalStateExceptionOnMultipleMiraklEnvironmentDefaultValues() {
    when(miraklEnvironmentDao.find(QUERY_PARAMETERS))
        .thenReturn(newArrayList(miraklEnvironmentDefault, miraklEnvironmentDefault2));
    testObj.getDefault();
  }

  @Test
  public void findByDefaultShouldReturnNullOnEmptyList() {
    when(miraklEnvironmentDao.find(QUERY_PARAMETERS)).thenReturn(newArrayList());
    MiraklEnvironmentModel resultObj = testObj.getDefault();

    assertThat(resultObj).isNull();
  }

}
