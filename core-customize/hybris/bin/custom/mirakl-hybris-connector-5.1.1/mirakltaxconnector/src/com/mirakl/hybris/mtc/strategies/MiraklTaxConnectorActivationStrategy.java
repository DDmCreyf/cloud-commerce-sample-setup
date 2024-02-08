package com.mirakl.hybris.mtc.strategies;

import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface MiraklTaxConnectorActivationStrategy {

  /**
   * Checks if the Mirakl Tax Connector is enabled in Hybris.
   *
   * @return true if the Mirakl Tax Connector is enabled, false otherwise
   */
  boolean isMiraklTaxConnectorEnabled();


  /**
   * Checks if the Mirakl Tax Connector is enabled in Hybris and the order requires computation.
   *
   * @param order the order
   * @return true if the Mirakl Tax Connector is enabled and the order contains at least one marketplace entry which needs
   * computation, false otherwise
   */
  boolean isMiraklTaxConnectorComputation(AbstractOrderModel order);
}
