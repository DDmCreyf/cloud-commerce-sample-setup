package com.mirakl.hybris.core.environment.services;

import com.mirakl.hybris.core.model.MiraklEnvironmentModel;

/**
 * Service providing access and control on the environments variables to connect with Mirakl
 */
public interface MiraklEnvironmentService {

  /**
   * Returns the Mirakl Environment by if present, null otherwise. An IllegalStateException is thrown if there exists more than
   * one Mirakl Environment by default
   *
   * @return The default Mirakl Environment
   */
  MiraklEnvironmentModel getDefault();

}
