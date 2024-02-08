package com.mirakl.hybris.core.beans.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CLIENT_DROP_IDLE_CONNECTIONS;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CLIENT_KEEP_ALIVE_DURATION;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.REQUEST_CONFIG_CONNECT_TIMEOUT;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.REQUEST_CONFIG_SOCKET_TIMEOUT;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.SOCKET_CONFIG_SO_KEEPALIVE;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.SOCKET_CONFIG_SO_TIMEOUT;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.core.internal.MiraklClientConfigWrapper;
import com.mirakl.hybris.core.beans.strategies.MiraklApiClientConfigurationStrategy;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import shaded.org.apache.http.HeaderElement;
import shaded.org.apache.http.HeaderElementIterator;
import shaded.org.apache.http.HttpResponse;
import shaded.org.apache.http.client.config.RequestConfig;
import shaded.org.apache.http.config.SocketConfig;
import shaded.org.apache.http.conn.ConnectionKeepAliveStrategy;
import shaded.org.apache.http.message.BasicHeaderElementIterator;
import shaded.org.apache.http.protocol.HTTP;
import shaded.org.apache.http.protocol.HttpContext;

public class DefaultMiraklApiClientConfigurationStrategy implements MiraklApiClientConfigurationStrategy {

  protected ConfigurationService configurationService;

  @Override
  public void configure(MiraklClientConfigWrapper clientConfigWrapper) {
    configureSocketConfig(clientConfigWrapper);
    configureRequestConfig(clientConfigWrapper);
    if (configurationService.getConfiguration().getBoolean(CLIENT_DROP_IDLE_CONNECTIONS, false)) {
      configureKeepAlive(clientConfigWrapper);
    }
  }


  protected void configureSocketConfig(MiraklClientConfigWrapper clientConfigWrapper) {
    Boolean soKeepAlive = configurationService.getConfiguration().getBoolean(SOCKET_CONFIG_SO_KEEPALIVE, null);
    Integer soTimeout = configurationService.getConfiguration().getInteger(SOCKET_CONFIG_SO_TIMEOUT, null);

    SocketConfig.Builder socketConfigBuilder = SocketConfig.custom();

    if (soKeepAlive != null) {
      socketConfigBuilder.setSoKeepAlive(soKeepAlive);
    }
    if (soTimeout != null) {
      socketConfigBuilder.setSoTimeout(soTimeout);
    }

    clientConfigWrapper.getHttpClientBuilder().setDefaultSocketConfig(socketConfigBuilder.build());
  }

  protected void configureRequestConfig(MiraklClientConfigWrapper clientConfigWrapper) {
    Integer connectTimeout = configurationService.getConfiguration().getInteger(REQUEST_CONFIG_CONNECT_TIMEOUT, null);
    Integer socketTimeout = configurationService.getConfiguration().getInteger(REQUEST_CONFIG_SOCKET_TIMEOUT, null);

    RequestConfig.Builder requestConfigBuilder = clientConfigWrapper.getRequestConfigBuilder();

    if (connectTimeout != null) {
      requestConfigBuilder.setConnectTimeout(connectTimeout);
    }
    if (socketTimeout != null) {
      requestConfigBuilder.setSocketTimeout(socketTimeout);
    }
  }

  protected void configureKeepAlive(MiraklClientConfigWrapper clientConfigWrapper) {
    clientConfigWrapper.getHttpClientBuilder().setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

      @Override
      public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
        HeaderElementIterator it = new BasicHeaderElementIterator(httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
          HeaderElement headerElement = it.nextElement();
          if (headerElement.getValue() != null && "timeout".equalsIgnoreCase(headerElement.getName())) {
            try {
              return Long.parseLong(headerElement.getValue()) * 1000;
            } catch (NumberFormatException ignore) {
              // Nothing to do, ignore
            }
          }
        }

        return configurationService.getConfiguration().getLong(CLIENT_KEEP_ALIVE_DURATION, 210000);
      }
    });
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

}
