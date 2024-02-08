package com.mirakl.hybris.core.environment.daos.impl;

import static java.util.Collections.singletonMap;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.mirakl.hybris.core.model.MiraklEnvironmentModel;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;

@IntegrationTest
public class MiraklEnvironmentDaoIntegrationTest extends ServicelayerTest {

  private static final String FRONT_API_KEY_1 = "fbe46af7-4bef-470b-949a-c10d5ce96135";
  private static final String OPERATOR_API_KEY_1 = "fbe46af7-4bef-470b-949a-c10d5ce96135d";
  private static final String API_URL_1 = "https://test1.mirakl.net";
  private static final String FRONT_API_KEY_2 = "fb94dd9f-352a-4674-8226-94a3d716b65";
  private static final String OPERATOR_API_KEY_2 = "fb94dd9f-352a-4674-8226-94a3d716b65e";
  private static final String API_URL_2 = "https://test2.mirakl.net";
  private static final String FRONT_API_KEY_3 = "fb94dd9f-352a-4674-8226-94a3d716b65c";
  private static final String OPERATOR_API_KEY_3 = "fb94dd9f-352a-4674-8226-94a3d716b65";
  private static final String API_URL_3 = "https://test3.mirakl.net";

  @Resource
  private DefaultMiraklEnvironmentDao defaultMiraklEnvironmentDao;

  @Before
  public void setUp() throws ImpExException {
    importCsv("/miraklservices/test/testMiraklEnvironments.impex", "utf-8");
  }

  @Test
  public void findByDefaultWithTrue() {
    List<MiraklEnvironmentModel> result =
        defaultMiraklEnvironmentDao.find(singletonMap(MiraklEnvironmentModel.DEFAULT, Boolean.TRUE));

    assertThat(result).hasSize(1);
    final MiraklEnvironmentModel miraklEnvironment = result.get(0);
    assertThat(miraklEnvironment.getFrontApiKey()).isEqualTo(FRONT_API_KEY_1);
    assertThat(miraklEnvironment.getOperatorApiKey()).isEqualTo(OPERATOR_API_KEY_1);
    assertThat(miraklEnvironment.getApiUrl()).isEqualTo(API_URL_1);
    assertThat(miraklEnvironment.isDefault()).isEqualTo(true);
  }

  @Test
  public void findByDefaultWithFalse() {
    List<MiraklEnvironmentModel> result =
        defaultMiraklEnvironmentDao.find(singletonMap(MiraklEnvironmentModel.DEFAULT, Boolean.FALSE));

    assertThat(result).hasSize(2);
    boolean foundMiraklEnvironmentTwo = false;
    for (MiraklEnvironmentModel miraklEnvironment : result) {
      if (miraklEnvironment.getFrontApiKey().equals(FRONT_API_KEY_2) && !foundMiraklEnvironmentTwo) {
        assertThat(miraklEnvironment.getFrontApiKey()).isEqualTo(FRONT_API_KEY_2);
        assertThat(miraklEnvironment.getOperatorApiKey()).isEqualTo(OPERATOR_API_KEY_2);
        assertThat(miraklEnvironment.getApiUrl()).isEqualTo(API_URL_2);
        assertThat(miraklEnvironment.isDefault()).isEqualTo(false);
        foundMiraklEnvironmentTwo = true;
      } else if (miraklEnvironment.getFrontApiKey().equals(FRONT_API_KEY_3)) {
        assertThat(miraklEnvironment.getFrontApiKey()).isEqualTo(FRONT_API_KEY_3);
        assertThat(miraklEnvironment.getOperatorApiKey()).isEqualTo(OPERATOR_API_KEY_3);
        assertThat(miraklEnvironment.getApiUrl()).isEqualTo(API_URL_3);
        assertThat(miraklEnvironment.isDefault()).isEqualTo(false);
      } else {
        fail("Invalid miraklEnvironment found.");
      }
    }
  }

}
