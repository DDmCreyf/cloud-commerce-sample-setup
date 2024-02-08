package com.mirakl.hybris.core.beans.factories.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.core.filter.internal.MiraklRequestDecorator;
import com.mirakl.client.core.internal.MiraklClientConfigWrapper;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApi;
import com.mirakl.hybris.core.beans.strategies.MiraklApiClientConfigurationStrategy;
import com.mirakl.hybris.core.environment.strategies.MiraklEnvironmentSelectionStrategy;
import com.mirakl.hybris.core.model.MiraklEnvironmentModel;

import de.hybris.bootstrap.annotations.UnitTest;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklConnectionBeanFactoryTest {
  private static final String ENV_URL = "https://mirakl-env-url.com";
  private static final String FRONT_API_KEY = UUID.randomUUID().toString();
  private static final String OPERATOR_API_KEY = UUID.randomUUID().toString();

  @InjectMocks
  private DefaultMiraklConnectionBeanFactory factory;

  @Mock
  protected MiraklApiClientConfigurationStrategy mmpFrontApiClientConfigurationStrategy;
  @Mock
  protected MiraklApiClientConfigurationStrategy mmpOperatorApiClientConfigurationStrategy;
  @Mock
  protected MiraklApiClientConfigurationStrategy mciFrontApiClientConfigurationStrategy;
  @Mock
  private MiraklRequestDecorator requestDecorator;
  @Mock
  private MiraklEnvironmentSelectionStrategy miraklEnvironmentSelectionStrategy;
  @Mock
  private MiraklEnvironmentModel miraklEnvironment;

  @Before
  public void setUp() throws Exception {
    factory.setRequestDecorators(Arrays.asList(requestDecorator));
    when(miraklEnvironmentSelectionStrategy.resolveCurrentMiraklEnvironment()).thenReturn(miraklEnvironment);
    when(miraklEnvironment.getApiUrl()).thenReturn(ENV_URL);
    when(miraklEnvironment.getFrontApiKey()).thenReturn(FRONT_API_KEY);
    when(miraklEnvironment.getOperatorApiKey()).thenReturn(OPERATOR_API_KEY);
  }

  @Test
  public void testGetMmpFrontApiClient() throws Exception {
    MiraklMarketplacePlatformFrontApi mmpFrontApiClient = factory.getMmpFrontApiClient();
    assertThat(mmpFrontApiClient).isNotNull();
    assertThat(mmpFrontApiClient).isEqualTo(factory.getMmpFrontApiClient());
    verify(mmpFrontApiClientConfigurationStrategy, times(1)).configure(any(MiraklClientConfigWrapper.class));
    verify(mmpOperatorApiClientConfigurationStrategy, never()).configure(any(MiraklClientConfigWrapper.class));
    verify(mciFrontApiClientConfigurationStrategy, never()).configure(any(MiraklClientConfigWrapper.class));
  }

  @Test
  public void testGetMmpOperatorApiClient() throws Exception {
    MiraklMarketplacePlatformOperatorApi mmpOperatorApiClient = factory.getMmpOperatorApiClient();
    assertThat(mmpOperatorApiClient).isNotNull();
    assertThat(mmpOperatorApiClient).isEqualTo(factory.getMmpOperatorApiClient());
    verify(mmpFrontApiClientConfigurationStrategy, never()).configure(any(MiraklClientConfigWrapper.class));
    verify(mmpOperatorApiClientConfigurationStrategy, times(1)).configure(any(MiraklClientConfigWrapper.class));
    verify(mciFrontApiClientConfigurationStrategy, never()).configure(any(MiraklClientConfigWrapper.class));
  }

  @Test
  public void testGetMciFrontApiClient() throws Exception {
    MiraklCatalogIntegrationFrontApi mciFrontApiClient = factory.getMciFrontApiClient();
    assertThat(mciFrontApiClient).isNotNull();
    assertThat(mciFrontApiClient).isEqualTo(factory.getMciFrontApiClient());
    verify(mmpFrontApiClientConfigurationStrategy, never()).configure(any(MiraklClientConfigWrapper.class));
    verify(mmpOperatorApiClientConfigurationStrategy, never()).configure(any(MiraklClientConfigWrapper.class));
    verify(mciFrontApiClientConfigurationStrategy, times(1)).configure(any(MiraklClientConfigWrapper.class));
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThroweExceptionWhenNoResolvedMiraklEnvironment() {
    when(miraklEnvironmentSelectionStrategy.resolveCurrentMiraklEnvironment()).thenReturn(null);
    factory.getMmpFrontApiClient();
  }
}
