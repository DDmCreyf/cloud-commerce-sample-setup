package com.mirakl.hybris.core.request.decorators;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.MIRAKL_CUSTOM_USER_AGENT_ENABLED;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.MIRAKL_CUSTOM_USER_AGENT_VALUE;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.USER_AGENT_DEFAULT_USER_AGENT_PLACEHOLDER;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.USER_AGENT_HEADER;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.USER_AGENT_HYBRIS_VERSION_PLACEHOLDER;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.USER_AGENT_MIRAKL_CONNECTOR_VERSION_PLACEHOLDER;
import static com.mirakl.hybris.core.util.HybrisVersionUtils.getHybrisVersion;
import static com.mirakl.hybris.core.util.HybrisVersionUtils.getMiraklConnectorVersion;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.core.filter.internal.MiraklRequestDecorator;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import shaded.org.apache.http.Header;
import shaded.org.apache.http.HttpRequest;
import shaded.org.apache.http.protocol.HttpContext;

public class MiraklUserAgentRequestDecorator implements MiraklRequestDecorator {

  protected ConfigurationService configurationService;

  @Override
  public void decorate(HttpRequest httpRequest, HttpContext httpContext) {
    boolean customUserAgentEnabled = configurationService.getConfiguration().getBoolean(MIRAKL_CUSTOM_USER_AGENT_ENABLED, true);
    String customUserAgentTemplate = configurationService.getConfiguration().getString(MIRAKL_CUSTOM_USER_AGENT_VALUE);

    Header[] userAgent = httpRequest.getHeaders(USER_AGENT_HEADER);
    String defaultUserAgent = userAgent.length > 0 ? userAgent[0].getValue() : "";

    if (customUserAgentEnabled && isNotBlank(customUserAgentTemplate)) {
      String miraklConnectorVersion = getMiraklConnectorVersion();
      Map<String, String> substitutionMap = new HashMap<>();
      substitutionMap.put(USER_AGENT_MIRAKL_CONNECTOR_VERSION_PLACEHOLDER,
          miraklConnectorVersion == null ? "n/a" : miraklConnectorVersion);
      substitutionMap.put(USER_AGENT_HYBRIS_VERSION_PLACEHOLDER, getHybrisVersion());
      substitutionMap.put(USER_AGENT_DEFAULT_USER_AGENT_PLACEHOLDER, defaultUserAgent);

      httpRequest.setHeader(USER_AGENT_HEADER, StrSubstitutor.replace(customUserAgentTemplate, substitutionMap));
    }
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

}
