package com.mirakl.hybris.core.environment.strategies;

import com.mirakl.hybris.core.model.MiraklEnvironmentModel;

public interface MiraklEnvironmentSelectionStrategy {

  /*
  * Returns the current mirakl environment.
  */
  MiraklEnvironmentModel resolveCurrentMiraklEnvironment();

}
