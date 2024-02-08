package com.mirakl.hybris.core.utils;

import com.mirakl.hybris.core.model.MiraklEnvironmentModel;
import com.mirakl.hybris.core.utils.mock.server.MockServer;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.After;

import javax.annotation.Resource;

/**
 * Allows a server mocking Mirakl APIs to be used in the test
 */
public class MiraklMockedServerTest extends MiraklServiceLayerTest {

  @Resource
  protected ModelService modelService;

  protected MockServer mockServer;

  protected MockServer initMockServer() {
    mockServer = new MockServer();
    modelService.save(buildMiraklEnvironment(mockServer));

    return mockServer;
  }

  @After
  public void tearDown() throws Exception {
    mockServer.stop();
  }


  protected MiraklEnvironmentModel buildMiraklEnvironment(MockServer server) {
    MiraklEnvironmentModel miraklEnvironment = modelService.create(MiraklEnvironmentModel.class);
    miraklEnvironment.setApiUrl("http://localhost:" + server.getPort() + "/api");
    miraklEnvironment.setDefault(true);
    miraklEnvironment.setFrontApiKey("");
    miraklEnvironment.setOperatorApiKey("");
    return miraklEnvironment;
  }



}
