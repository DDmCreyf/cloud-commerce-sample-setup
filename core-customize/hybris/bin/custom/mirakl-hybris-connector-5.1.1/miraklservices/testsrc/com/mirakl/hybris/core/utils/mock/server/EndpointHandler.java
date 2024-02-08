package com.mirakl.hybris.core.utils.mock.server;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.util.UriComponents;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class EndpointHandler implements HttpHandler {

  protected EndpointMatcher uriMatcher;
  protected AtomicInteger calls = new AtomicInteger();

  public EndpointHandler(EndpointMatcher uriMatcher) {
    this.uriMatcher = uriMatcher;
  }

  public boolean isEligible(String method, UriComponents uriComponents) {
    return uriMatcher.matches(method, uriComponents);
  }

  public AtomicInteger getCalls() {
    return calls;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    handle(exchange, calls.addAndGet(1));
  }

  protected abstract void handle(HttpExchange exchange, int calls) throws IOException;
}
