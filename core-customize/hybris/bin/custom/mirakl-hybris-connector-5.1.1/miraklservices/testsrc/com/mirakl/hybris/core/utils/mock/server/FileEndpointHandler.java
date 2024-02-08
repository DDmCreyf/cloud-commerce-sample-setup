package com.mirakl.hybris.core.utils.mock.server;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.sun.net.httpserver.HttpExchange;

import de.hybris.platform.servicelayer.ServicelayerTest;

public class FileEndpointHandler extends EndpointHandler {

  protected final List<File> files = new ArrayList<>();

  public FileEndpointHandler(EndpointMatcher uriMatcher, String filePath, String... additionalFilePaths) {
    super(uriMatcher);
    this.files.add(new File(ServicelayerTest.class.getResource(filePath).getFile()));
    for (String additionalFilePath : additionalFilePaths) {
      this.files.add(new File(ServicelayerTest.class.getResource(additionalFilePath).getFile()));
    }
  }

  @Override
  public void handle(HttpExchange exchange, int calls) throws IOException {
    byte[] response = Files.readAllBytes(Objects.requireNonNull(getFile(calls)).toPath());
    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
    exchange.getResponseBody().write(response);
    exchange.close();
  }

  private File getFile(int calls) {
    return files.get(calls - 1 < files.size() ? calls - 1 : 0);
  }
}
