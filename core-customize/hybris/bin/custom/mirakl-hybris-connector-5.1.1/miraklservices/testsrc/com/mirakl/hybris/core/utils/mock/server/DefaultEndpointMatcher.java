package com.mirakl.hybris.core.utils.mock.server;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;

public class DefaultEndpointMatcher implements EndpointMatcher {

  protected static final Pattern variablePathSegmentPattern = Pattern.compile("\\{(.*)}");
  protected final String method;
  protected final List<String> apiSegments = new ArrayList<>();
  protected final Map<String, String> params = new HashMap<>();

  public DefaultEndpointMatcher(String method, List<String> apiSegments, Map<String, String> params) {
    this.method = method;
    this.apiSegments.addAll(apiSegments);
    this.params.putAll(params);
  }

  @Override
  public boolean matches(String method, UriComponents uriComponents) {
    return methodMatches(method) //
        && pathMatches(uriComponents.getPathSegments()) //
        && queryParamsMatch(uriComponents.getQueryParams());
  }

  protected boolean methodMatches(String method) {
    return Objects.equals(this.method, method);
  }

  protected boolean pathMatches(List<String> providedSegments) {
    if (providedSegments.size() > apiSegments.size()) {
      return false;
    }
    for (int i = 0; i < apiSegments.size(); i++) {
      String apiSegment = apiSegments.get(i);
      String providedSegment = i < providedSegments.size() ? providedSegments.get(i) : null;
      if (!Objects.equals(apiSegment, providedSegment)) {
        Matcher matcher = variablePathSegmentPattern.matcher(apiSegment);
        if (matcher.matches()) {
          String variableSegmentKey = matcher.group(1);
          String requiredVariableSegment = params.get(variableSegmentKey);
          if (params.containsKey(variableSegmentKey) && !Objects.equals(requiredVariableSegment, providedSegment)) {
            return false;
          }
        } else {
          return false;
        }
      }
    }
    return true;
  }

  protected boolean queryParamsMatch(MultiValueMap<String, String> queryParams) {
    for (Map.Entry<String, List<String>> queryParam : queryParams.entrySet()) {
      String requestedParam = params.get(queryParam.getKey());
      if (params.containsKey(queryParam.getKey()) && !Objects.equals(queryParam.getValue().get(0), requestedParam)) {
        return false;
      }
    }
    return true;
  }


}
