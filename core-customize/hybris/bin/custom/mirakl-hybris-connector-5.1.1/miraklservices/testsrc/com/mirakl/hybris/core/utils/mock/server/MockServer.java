package com.mirakl.hybris.core.utils.mock.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class MockServer {

  public static final int DEFAULT_MOCK_SERVER_PORT = 5115;

  protected final Logger LOG = Logger.getLogger(getClass());

  protected final HttpServer httpServer;
  protected final List<EndpointHandler> handlers = new ArrayList<>();

  public MockServer() {
    this(DEFAULT_MOCK_SERVER_PORT);
  }

  public MockServer(int port) {
    try {
      httpServer = HttpServer.create(new InetSocketAddress(port), 0);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    httpServer.createContext("/", new HttpHandler() {
      @Override
      public void handle(HttpExchange exchange) throws IOException {
        UriComponents uriComponents = UriComponentsBuilder.fromUri(exchange.getRequestURI()).build();
        for (EndpointHandler handler : handlers) {
          if (handler.isEligible(exchange.getRequestMethod(), uriComponents)) {
            try {
              handler.handle(exchange);
            } catch (Exception e) {
              LOG.error("An error occurred while handling '" + exchange.getRequestURI() + "'", e);
            }
            return;
          }
        }
        byte[] response = ("Unhandled endpoint '" + exchange.getRequestURI() + "'").getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
      }
    });
    httpServer.start();
  }

  public void stop() {
    httpServer.stop(0);
  }

  public MockServer addHandler(EndpointHandler handler) {
    handlers.add(handler);
    return this;
  }

  public MockServer returnsFile(EndpointMatcher matcher, String defaultFilePath, String... filePaths) {
    return addHandler(new FileEndpointHandler(matcher, defaultFilePath, filePaths));
  }

  public List<EndpointHandler> handlers() {
    return handlers;
  }

  public void clearHandlers() {
    handlers.clear();
  }

  public int getPort() {
    return httpServer.getAddress().getPort();
  }
}
