package com.mirakl.hybris.core.utils.mock.server;

import org.springframework.web.util.UriComponents;

public interface EndpointMatcher {

    boolean matches(String method, UriComponents uriComponents);

}
