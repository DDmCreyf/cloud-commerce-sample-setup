package com.mirakl.hybris.core.utils.mock.server;

import static com.mirakl.client.mmp.core.internal.MiraklMarketplacePlatformCommonApiEndpoint.*;

import java.util.*;

import com.google.common.collect.Lists;
import com.mirakl.client.core.internal.MiraklApiEndpoint;

public class MiraklEndpointMatchers {

  public static DefaultEndpointMatcher of52(String... parameters) {
    return new DefaultEndpointMatcher("POST", toPathSegments(OF52), toParametersMap(parameters));
  }

  public static DefaultEndpointMatcher of53(String... parameters) {
    return new DefaultEndpointMatcher("GET", toPathSegments(OF53), toParametersMap(parameters));
  }

  public static DefaultEndpointMatcher of54(String... parameters) {
    return new DefaultEndpointMatcher("GET", Lists.newArrayList("api", "offers", "export", "async", "file", "{file_id}"),
        toParametersMap(parameters));
  }

  public static DefaultEndpointMatcher p11(String... parameters) {
    return new DefaultEndpointMatcher("GET", toPathSegments(P11), toParametersMap(parameters));
  }
  private static List<String> toPathSegments(MiraklApiEndpoint miraklApiEndpoint) {
    List<String> pathSegments = new ArrayList<>();
    pathSegments.add("api");
    pathSegments.addAll(Arrays.asList(miraklApiEndpoint.getPaths()));
    return pathSegments;
  }

  private static Map<String, String> toParametersMap(String[] varargs) {
    Map<String, String> queryParams = new HashMap<>();
    if (varargs.length % 2 != 0) {
      throw new IllegalStateException("Invalid required param count");
    }
    for (int i = 0; i < varargs.length - 1; i = i + 2) {
      queryParams.put(varargs[i], varargs[i + 1]);
    }
    return queryParams;
  }

}
