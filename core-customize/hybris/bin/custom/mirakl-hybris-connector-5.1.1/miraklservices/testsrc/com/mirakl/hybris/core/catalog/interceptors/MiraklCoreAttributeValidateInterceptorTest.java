package com.mirakl.hybris.core.catalog.interceptors;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCoreAttributeValidateInterceptorTest {

  private static final String SPRING_ID = "spring-id";

  @Spy
  @InjectMocks
  private MiraklCoreAttributeValidateInterceptor interceptor;

  @Mock
  private MiraklCoreAttributeModel coreAttribute;
  @Mock
  private InterceptorContext context;


  @Test(expected = InterceptorException.class)
  public void shouldThrowExceptionWhenBeanNotFound() throws InterceptorException {
    doReturn(false).when(interceptor).beanDoesExist(SPRING_ID);
    when(coreAttribute.getImportExportHandlerStringId()).thenReturn(SPRING_ID);

    interceptor.onValidate(coreAttribute, context);
  }

}
