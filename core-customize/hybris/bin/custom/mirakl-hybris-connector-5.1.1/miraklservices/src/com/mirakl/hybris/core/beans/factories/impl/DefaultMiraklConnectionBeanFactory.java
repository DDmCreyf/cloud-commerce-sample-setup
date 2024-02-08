package com.mirakl.hybris.core.beans.factories.impl;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.core.AbstractMiraklApiClient;
import com.mirakl.client.core.filter.internal.MiraklRequestDecorator;
import com.mirakl.client.core.internal.MiraklClientConfigWrapper;
import com.mirakl.client.core.security.MiraklCredential;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApiClient;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApiClient;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApi;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.hybris.core.beans.factories.MiraklConnectionFactoryBean;
import com.mirakl.hybris.core.beans.strategies.MiraklApiClientConfigurationStrategy;
import com.mirakl.hybris.core.constants.MiraklservicesConstants;
import com.mirakl.hybris.core.environment.strategies.MiraklEnvironmentSelectionStrategy;
import com.mirakl.hybris.core.model.MiraklEnvironmentModel;

public class DefaultMiraklConnectionBeanFactory implements MiraklConnectionFactoryBean {

  protected List<MiraklRequestDecorator> requestDecorators;
  protected MiraklEnvironmentSelectionStrategy miraklEnvironmentSelectionStrategy;
  protected MiraklApiClientConfigurationStrategy mmpFrontApiClientConfigurationStrategy;
  protected MiraklApiClientConfigurationStrategy mmpOperatorApiClientConfigurationStrategy;
  protected MiraklApiClientConfigurationStrategy mciFrontApiClientConfigurationStrategy;

  protected Map<String, MiraklMarketplacePlatformFrontApiClient> cachedMmpFrontApiClients = new HashMap<>();
  protected Map<String, MiraklMarketplacePlatformOperatorApiClient> cachedMmpOperatorApiClients = new HashMap<>();
  protected Map<String, MiraklCatalogIntegrationFrontApiClient> cachedMciFrontApiClients = new HashMap<>();

  @Override
  public MiraklMarketplacePlatformFrontApi getMmpFrontApiClient() {
    final MiraklEnvironmentModel defaultMiraklEnvironment = miraklEnvironmentSelectionStrategy.resolveCurrentMiraklEnvironment();
    assertNotEmpty(defaultMiraklEnvironment);
    String urlApi = defaultMiraklEnvironment.getApiUrl();
    String frontApiKey = defaultMiraklEnvironment.getFrontApiKey();

    return getApiClient(urlApi, frontApiKey, cachedMmpFrontApiClients, MiraklMarketplacePlatformFrontApiClient.class);
  }

  @Override
  public MiraklMarketplacePlatformOperatorApi getMmpOperatorApiClient() {
    final MiraklEnvironmentModel defaultMiraklEnvironment = miraklEnvironmentSelectionStrategy.resolveCurrentMiraklEnvironment();
    assertNotEmpty(defaultMiraklEnvironment);
    String urlApi = defaultMiraklEnvironment.getApiUrl();
    String operatorApiKey = defaultMiraklEnvironment.getOperatorApiKey();

    return getApiClient(urlApi, operatorApiKey, cachedMmpOperatorApiClients, MiraklMarketplacePlatformOperatorApiClient.class);
  }

  @Override
  public MiraklCatalogIntegrationFrontApi getMciFrontApiClient() {
    final MiraklEnvironmentModel defaultMiraklEnvironment = miraklEnvironmentSelectionStrategy.resolveCurrentMiraklEnvironment();
    assertNotEmpty(defaultMiraklEnvironment);
    String urlApi = defaultMiraklEnvironment.getApiUrl();
    String frontApiKey = defaultMiraklEnvironment.getFrontApiKey();

    return getApiClient(urlApi, frontApiKey, cachedMciFrontApiClients, MiraklCatalogIntegrationFrontApiClient.class);
  }

  protected void assertNotEmpty(final MiraklEnvironmentModel defaultMiraklEnvironment) {
    if (defaultMiraklEnvironment == null) {
      throw new IllegalStateException(format(
          "Unable to resolve the Mirakl environment to use. If you have no default MiraklEnvironment in your system, "
              + "you might need to perform a System Update and ensure that the [%s] extension project data is checked.",
          MiraklservicesConstants.EXTENSIONNAME));
    }
  }

  protected <T extends AbstractMiraklApiClient> T getApiClient(String envUrl, String apiKey, Map<String, T> cachedApiClients,
      Class<T> type) {
    String cacheKey = getCacheKey(envUrl, apiKey);
    T cachedClient = cachedApiClients.get(cacheKey);
    if (cachedClient != null) {
      return cachedClient;
    } else {
      T apiClient = createApiClient(envUrl, apiKey, type);
      cachedApiClients.put(cacheKey, apiClient);
      return apiClient;
    }
  }

  @SuppressWarnings("unchecked")
  protected <T extends AbstractMiraklApiClient> T createApiClient(String envUrl, String apiKey, Class<T> type) {
    MiraklCredential credential = new MiraklCredential(apiKey);
    T client = null;

    if (MiraklMarketplacePlatformFrontApiClient.class.equals(type)) {
      client = (T) new CustomMiraklMarketplacePlatformFrontApiClient(envUrl, credential);
    } else if (MiraklMarketplacePlatformOperatorApiClient.class.equals(type)) {
      client = (T) new CustomMiraklMarketplacePlatformOperatorApiClient(envUrl, credential);
    } else if (MiraklCatalogIntegrationFrontApiClient.class.equals(type)) {
      client = (T) new CustomMiraklCatalogIntegrationFrontApiClient(envUrl, credential);
    } else {
      throw new IllegalArgumentException(format("Unable to instanciate an API client for type [%s]", type));
    }

    client.addDecorators(requestDecorators);
    return client;
  }

  protected class CustomMiraklMarketplacePlatformFrontApiClient extends MiraklMarketplacePlatformFrontApiClient {
    public CustomMiraklMarketplacePlatformFrontApiClient(String endpoint, MiraklCredential credential) {
      super(endpoint, credential);
    }

    @Override
    protected void configure(MiraklClientConfigWrapper clientConfigWrapper) {
      mmpFrontApiClientConfigurationStrategy.configure(clientConfigWrapper);
    }
  }
  protected class CustomMiraklMarketplacePlatformOperatorApiClient extends MiraklMarketplacePlatformOperatorApiClient {
    public CustomMiraklMarketplacePlatformOperatorApiClient(String endpoint, MiraklCredential credential) {
      super(endpoint, credential);
    }

    @Override
    protected void configure(MiraklClientConfigWrapper clientConfigWrapper) {
      mmpOperatorApiClientConfigurationStrategy.configure(clientConfigWrapper);
    }
  }
  protected class CustomMiraklCatalogIntegrationFrontApiClient extends MiraklCatalogIntegrationFrontApiClient {
    public CustomMiraklCatalogIntegrationFrontApiClient(String endpoint, MiraklCredential credential) {
      super(endpoint, credential);
    }

    @Override
    protected void configure(MiraklClientConfigWrapper clientConfigWrapper) {
      mciFrontApiClientConfigurationStrategy.configure(clientConfigWrapper);
    }
  }

  protected String getCacheKey(String envUrl, String apiKey) {
    return format("%s:%s", envUrl, apiKey);
  }

  @Required
  public void setMmpFrontApiClientConfigurationStrategy(
      MiraklApiClientConfigurationStrategy mmpFrontApiClientConfigurationStrategy) {
    this.mmpFrontApiClientConfigurationStrategy = mmpFrontApiClientConfigurationStrategy;
  }

  @Required
  public void setMmpOperatorApiClientConfigurationStrategy(
      MiraklApiClientConfigurationStrategy mmpOperatorApiClientConfigurationStrategy) {
    this.mmpOperatorApiClientConfigurationStrategy = mmpOperatorApiClientConfigurationStrategy;
  }

  @Required
  public void setMciFrontApiClientConfigurationStrategy(
      MiraklApiClientConfigurationStrategy mciFrontApiClientConfigurationStrategy) {
    this.mciFrontApiClientConfigurationStrategy = mciFrontApiClientConfigurationStrategy;
  }

  @Required
  public void setRequestDecorators(List<MiraklRequestDecorator> requestDecorators) {
    this.requestDecorators = requestDecorators;
  }

  @Required
  public void setMiraklEnvironmentSelectionStrategy(MiraklEnvironmentSelectionStrategy miraklEnvironmentSelectionStrategy) {
    this.miraklEnvironmentSelectionStrategy = miraklEnvironmentSelectionStrategy;
  }

}
