package com.mirakl.hybris.core.customfields.jobs;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.hybris.core.customfields.services.CustomFieldImportService;
import com.mirakl.hybris.core.model.MiraklCustomFieldModel;
import com.mirakl.hybris.core.model.MiraklImportCustomFieldsCronJobModel;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class MiraklImportCustomFieldsJob extends AbstractJobPerformable<MiraklImportCustomFieldsCronJobModel> {

  private static final Logger LOG = Logger.getLogger(MiraklImportCustomFieldsJob.class);

  protected CustomFieldImportService customFieldImportService;

  @Override
  public PerformResult perform(MiraklImportCustomFieldsCronJobModel cronJob) {

    Collection<MiraklCustomFieldModel> importedCustomFields;
    LOG.info("Performing a full custom field import");

    try {
      importedCustomFields = customFieldImportService.importCustomFields(cronJob.getEntities());
    } catch (MiraklApiException e) {
      LOG.error("Exception occurred while importing custom fields", e);
      return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
    }

    LOG.info(String.format("Imported %d custom fields", importedCustomFields.size()));

    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  @Required
  public void setCustomFieldImportService(CustomFieldImportService customFieldsImportService) {
    this.customFieldImportService = customFieldsImportService;
  }

}
