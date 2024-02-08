package com.mirakl.hybris.core.catalog.strategies;

import com.mirakl.hybris.core.catalog.events.ExportableCategoryEvent;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface WriteCategoryStrategy {

  /**
   * Writes the category in the categories file
   *
   * @param event The event fired when an exportable category is found
   */
  void handleEvent(ExportableCategoryEvent event);

}
