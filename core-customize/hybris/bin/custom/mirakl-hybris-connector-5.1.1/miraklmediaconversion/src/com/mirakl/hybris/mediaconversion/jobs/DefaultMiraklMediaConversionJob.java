package com.mirakl.hybris.mediaconversion.jobs;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.mediaconversion.job.MediaConversionJob;
import de.hybris.platform.mediaconversion.model.job.MediaConversionCronJobModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;


public class DefaultMiraklMediaConversionJob extends MediaConversionJob {

  protected SearchRestrictionService searchRestrictionService;

  @Override
  public PerformResult perform(final MediaConversionCronJobModel cronJob) {
    searchRestrictionService.enableSearchRestrictions();
    return super.perform(cronJob);
  }

  @Required
  public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService) {
    this.searchRestrictionService = searchRestrictionService;
  }

}
