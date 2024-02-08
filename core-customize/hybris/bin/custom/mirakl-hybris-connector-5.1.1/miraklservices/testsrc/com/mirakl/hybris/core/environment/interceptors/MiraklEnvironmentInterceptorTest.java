package com.mirakl.hybris.core.environment.interceptors;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.environment.services.MiraklEnvironmentService;
import com.mirakl.hybris.core.model.MiraklEnvironmentModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.model.ModelService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklEnvironmentInterceptorTest {

  private static final String FRONT_API_KEY = "fbe46af7-4bef-470b-949a-c10d5ce96135";
  private static final String OPERATOR_API_KEY = "fbe46af7-4bef-470b-949a-c10d5ce96135d";
  private static final String API_URL = "https://test1.mirakl.net";

  @Spy
  @InjectMocks
  private MiraklEnvironmentInterceptor testObj;

  @Mock
  private InterceptorContext context;

  @Mock
  private MiraklEnvironmentService miraklEnvironmentService;

  @Mock
  private ModelService modelService;

  private MiraklEnvironmentModel miraklEnvironmentDefault, miraklEnvironmentPreviousDefault;

  @Before
  public void setUp() {
    miraklEnvironmentDefault = getMiraklEnvironmentInstance(true);
    miraklEnvironmentPreviousDefault = getMiraklEnvironmentInstance(false);
  }

  @Test
  public void shouldNotModifyExistingDefaultIfNonDefault() throws Exception {
    when(context.isNew(miraklEnvironmentDefault)).thenReturn(false);
    when(context.isModified(miraklEnvironmentDefault)).thenReturn(false);

    testObj.onPrepare(miraklEnvironmentDefault, context);

    verify(testObj, never()).resetDefaultEnvironment(context);
  }

  @Test
  public void shouldCreateNewDefaultAndUpdateOldDefault() throws Exception {
    when(context.isNew(miraklEnvironmentDefault)).thenReturn(true);
    when(miraklEnvironmentService.getDefault()).thenReturn(miraklEnvironmentPreviousDefault);
    when(context.getModelService()).thenReturn(modelService);

    testObj.onPrepare(miraklEnvironmentDefault, context);

    verify(testObj).resetDefaultEnvironment(context);
    verify(modelService).save(miraklEnvironmentPreviousDefault);
  }

  @Test
  public void shouldNotModifyIfIsDefaultValueNotUpdated() throws Exception {
    when(context.isModified(miraklEnvironmentDefault)).thenReturn(true);
    Map<String, Set<Locale>> map = new HashMap<>();
    map.put(MiraklEnvironmentModel.FRONTAPIKEY, null);
    when(context.getDirtyAttributes(miraklEnvironmentDefault)).thenReturn(map);

    testObj.onPrepare(miraklEnvironmentDefault, context);

    verify(testObj, never()).resetDefaultEnvironment(context);
    verify(modelService, never()).save(miraklEnvironmentPreviousDefault);
  }

  @Test
  public void shouldModifyIfIsDefaultValueUpdated() throws Exception {
    when(context.isModified(miraklEnvironmentDefault)).thenReturn(true);
    when(miraklEnvironmentService.getDefault()).thenReturn(miraklEnvironmentPreviousDefault);
    when(context.getModelService()).thenReturn(modelService);
    Map<String, Set<Locale>> map = new HashMap<>();
    map.put(MiraklEnvironmentModel.DEFAULT, null);
    when(context.getDirtyAttributes(miraklEnvironmentDefault)).thenReturn(map);

    testObj.onPrepare(miraklEnvironmentDefault, context);

    verify(testObj).resetDefaultEnvironment(context);
    verify(modelService).save(miraklEnvironmentPreviousDefault);
  }


  private MiraklEnvironmentModel getMiraklEnvironmentInstance(boolean isDefault) {
    MiraklEnvironmentModel miraklEnvironment = new MiraklEnvironmentModel();
    miraklEnvironment.setDefault(isDefault);
    miraklEnvironment.setFrontApiKey(FRONT_API_KEY);
    miraklEnvironment.setOperatorApiKey(OPERATOR_API_KEY);
    miraklEnvironment.setApiUrl(API_URL);
    return miraklEnvironment;
  }

}
