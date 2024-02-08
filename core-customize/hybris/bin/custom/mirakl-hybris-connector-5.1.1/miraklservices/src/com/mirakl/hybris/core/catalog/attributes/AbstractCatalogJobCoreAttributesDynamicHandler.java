package com.mirakl.hybris.core.catalog.attributes;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.Collections;
import java.util.Set;

import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public abstract class AbstractCatalogJobCoreAttributesDynamicHandler<T extends CronJobModel>
    extends AbstractDynamicAttributeHandler<Set<MiraklCoreAttributeModel>, T> {

  protected Set<MiraklCoreAttributeModel> getCoreAttributes(MiraklCoreAttributeConfigurationModel coreAttributeConfiguration) {
    if (coreAttributeConfiguration != null && coreAttributeConfiguration.getCoreAttributes() != null) {
      return coreAttributeConfiguration.getCoreAttributes();
    }
    return Collections.emptySet();
  }

  protected void setCoreAttributes(MiraklCoreAttributeConfigurationModel coreAttributeConfiguration,
      Set<MiraklCoreAttributeModel> coreAttributes) {
    if (coreAttributeConfiguration == null) {
      if (!isEmpty(coreAttributes)) {
        throw new UnsupportedOperationException(
            "Impossible to save the specified core attributes ! Add a core attribute configuration to the job first.");
      }
    } else {
      coreAttributeConfiguration.setCoreAttributes(coreAttributes);
    }
  }

}
