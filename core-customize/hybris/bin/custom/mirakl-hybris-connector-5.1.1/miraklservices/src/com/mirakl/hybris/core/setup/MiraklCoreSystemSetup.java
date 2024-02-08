package com.mirakl.hybris.core.setup;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.MIRAKL_ENV_FRONTAPIKEY;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.MIRAKL_ENV_OPERATORAPIKEY;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.MIRAKL_ENV_URL;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.constants.MiraklservicesConstants;
import com.mirakl.hybris.core.environment.strategies.MiraklEnvironmentSelectionStrategy;
import com.mirakl.hybris.core.model.MiraklEnvironmentModel;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;


@SystemSetup(extension = MiraklservicesConstants.EXTENSIONNAME)
public class MiraklCoreSystemSetup extends AbstractSystemSetup {

  private static final Logger LOG = Logger.getLogger(MiraklCoreSystemSetup.class);

  private static final String IMPEX_IMPORT_FOLDER = "/miraklservices/import";

  protected ConfigurationService configurationService;
  protected MiraklEnvironmentSelectionStrategy miraklEnvironmentSelectionStrategy;
  protected ModelService modelService;

  @Override
  @SystemSetupParameterMethod
  public List<SystemSetupParameter> getInitializationOptions() {
    final List<SystemSetupParameter> params = new ArrayList<>();
    return params;
  }

  @SystemSetup(type = Type.PROJECT, process = Process.ALL)
  public void createProjectData(final SystemSetupContext context) {
    importImpexFile(context, IMPEX_IMPORT_FOLDER + "/searchrestrictions.impex");
    propertiesToMiraklEnvironmentMigration();
  }

  protected void propertiesToMiraklEnvironmentMigration() {
    final MiraklEnvironmentModel defaultValue = miraklEnvironmentSelectionStrategy.resolveCurrentMiraklEnvironment();
    String operatorKeyFromProperties = configurationService.getConfiguration().getString(MIRAKL_ENV_OPERATORAPIKEY);
    String frontKeyFromProperties = configurationService.getConfiguration().getString(MIRAKL_ENV_FRONTAPIKEY);
    String apiUrlFromProperties = configurationService.getConfiguration().getString(MIRAKL_ENV_URL);

    if (defaultValue == null && operatorKeyFromProperties != null && frontKeyFromProperties != null && apiUrlFromProperties != null) {
      saveNewDefaultEnvironment(frontKeyFromProperties, operatorKeyFromProperties, apiUrlFromProperties);
      LOG.info(
          format("Mirakl environment initialized with the following values {frontApiKey=[%s], operatorApiKey=[%s], apiUrl=[%s]}.",
              frontKeyFromProperties, operatorKeyFromProperties, apiUrlFromProperties));
    }
  }

  protected void saveNewDefaultEnvironment(String frontKey, String operatorKey, String envUrl) {
    MiraklEnvironmentModel newMiraklEnvironment = modelService.create(MiraklEnvironmentModel.class);
    newMiraklEnvironment.setFrontApiKey(frontKey);
    newMiraklEnvironment.setOperatorApiKey(operatorKey);
    newMiraklEnvironment.setApiUrl(envUrl);
    newMiraklEnvironment.setDefault(true);
    modelService.save(newMiraklEnvironment);
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setMiraklEnvironmentSelectionStrategy(MiraklEnvironmentSelectionStrategy miraklEnvironmentSelectionStrategy) {
    this.miraklEnvironmentSelectionStrategy = miraklEnvironmentSelectionStrategy;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }
}
