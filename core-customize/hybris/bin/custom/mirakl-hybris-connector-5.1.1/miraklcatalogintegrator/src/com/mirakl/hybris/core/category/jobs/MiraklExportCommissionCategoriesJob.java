package com.mirakl.hybris.core.category.jobs;

import static com.mirakl.hybris.core.enums.MiraklExportType.COMMISSION_CATEGORY_EXPORT;
import static java.lang.String.format;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.category.synchro.MiraklCategorySynchroTracking;
import com.mirakl.hybris.core.category.services.CategoryExportService;
import com.mirakl.hybris.core.jobs.services.ExportJobReportService;
import com.mirakl.hybris.core.model.MiraklExportCommissionCategoriesCronJobModel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;


public class MiraklExportCommissionCategoriesJob extends AbstractJobPerformable<MiraklExportCommissionCategoriesCronJobModel> {

  private static final Logger LOG = Logger.getLogger(MiraklExportCommissionCategoriesJob.class);

  protected CategoryExportService categoryExportService;
  protected CommonI18NService commonI18NService;
  protected I18NService i18NService;
  protected ExportJobReportService exportJobReportService;

  @Override
  public PerformResult perform(MiraklExportCommissionCategoriesCronJobModel miraklExportCategoriesCronJob) {
    Locale defaultLocale = getDefaultLocale(miraklExportCategoriesCronJob);

    CategoryModel rootCategory = miraklExportCategoriesCronJob.getRootCategory();
    String rootCategoryName = rootCategory.getName(defaultLocale);

    LOG.info(format("Exporting marketplace categories for root category [%s - %s]", rootCategory.getCode(), rootCategoryName));
    try {
      String synchronizationFileName = miraklExportCategoriesCronJob.getSynchronizationFileName();
      Set<Locale> additionalLocales = getAdditionalLocales(miraklExportCategoriesCronJob);
      MiraklCategorySynchroTracking miraklCategorySynchroTracking = categoryExportService.exportCommissionCategories(rootCategory,
          defaultLocale, synchronizationFileName, additionalLocales);

      String synchroId = miraklCategorySynchroTracking.getSynchroId();
      LOG.info(format("Marketplace categories export for root category [%s - %s] finished. Synchronization tracking id [%s]",
          rootCategory.getCode(), rootCategoryName, synchroId));

      exportJobReportService.createMiraklJobReport(synchroId, COMMISSION_CATEGORY_EXPORT);
      return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

    } catch (MiraklApiException | IOException e) {
      LOG.error("Exception occurred while exporting categories", e);
      return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
    }
  }

  protected Set<Locale> getAdditionalLocales(MiraklExportCommissionCategoriesCronJobModel miraklExportCategoriesCronJob) {
    final Set<Locale> locales = new HashSet<>();

    for (LanguageModel language : emptyIfNull(miraklExportCategoriesCronJob.getAdditionalLanguages())) {
      locales.add(commonI18NService.getLocaleForLanguage(language));
    }
    return locales;
  }

  protected Locale getDefaultLocale(MiraklExportCommissionCategoriesCronJobModel miraklExportCategoriesCronJob) {
    final LanguageModel defaultLanguage = miraklExportCategoriesCronJob.getSessionLanguage();
    if (defaultLanguage == null) {
      Locale currentLocale = i18NService.getCurrentLocale();
      LOG.warn(format("No session language configured for categories export job. Falling back to the current locale [%s]",
          currentLocale));

      return currentLocale;
    }
    return commonI18NService.getLocaleForLanguage(defaultLanguage);
  }

  @Required
  public void setCategoryExportService(CategoryExportService categoryExportService) {
    this.categoryExportService = categoryExportService;
  }

  @Required
  public void setCommonI18NService(CommonI18NService commonI18NService) {
    this.commonI18NService = commonI18NService;
  }

  @Required
  public void setI18NService(I18NService i18NService) {
    this.i18NService = i18NService;
  }

  @Required
  public void setExportJobReportService(ExportJobReportService exportJobReportService) {
    this.exportJobReportService = exportJobReportService;
  }
}
