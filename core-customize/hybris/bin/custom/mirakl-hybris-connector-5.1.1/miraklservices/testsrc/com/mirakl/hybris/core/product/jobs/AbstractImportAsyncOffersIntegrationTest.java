package com.mirakl.hybris.core.product.jobs;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.mirakl.hybris.core.i18n.services.CurrencyService;
import com.mirakl.hybris.core.jobs.dao.MiraklAsyncTaskDao;
import com.mirakl.hybris.core.jobs.services.MiraklAsyncTaskService;
import com.mirakl.hybris.core.model.MiraklImportOfferPollerModel;
import com.mirakl.hybris.core.model.MiraklImportOffersCronJobModel;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.shop.daos.ShopDao;
import com.mirakl.hybris.core.utils.MiraklMockedServerTest;

import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;

public class AbstractImportAsyncOffersIntegrationTest extends MiraklMockedServerTest {

  // Flexible search queries
  protected static final String ALL_ACTIVE_OFFERS_QUERY =
      "SELECT {o.PK} FROM {Offer AS o} WHERE {o.deleted} = false AND {o.active} = true";
  protected static final String ALL_DELETED_OFFERS_QUERY =
      "SELECT {o.PK} FROM {Offer AS o} WHERE {o.deleted} = true AND {o.active} = false";

  protected static final Date DEFAULT_LAST_IMPORT_TIME = new Date();

  protected MiraklImportOffersCronJobModel schedulerJobModel;
  protected MiraklImportOfferPollerModel jobModel;

  @Resource
  protected ModelService modelService;
  @Resource
  protected UserService userService;
  @Resource
  protected CommonI18NService commonI18NService;
  @Resource
  protected CurrencyService currencyService;
  @Resource
  protected MiraklAsyncTaskService miraklAsyncTaskService;
  @Resource
  protected MiraklAsyncTaskDao miraklAsyncTaskDao;
  @Resource
  protected OfferService offerService;
  @Resource
  protected ShopDao shopDao;
  @Resource
  protected FlexibleSearchService flexibleSearchService;

  protected MiraklImportOfferPollerModel buildJobModel(boolean full, boolean cleanupTask) {
    ServicelayerJobModel schedulerJob = modelService.create(ServicelayerJobModel.class);
    schedulerJob.setCode("defaultImportOffersJob");
    schedulerJob.setSpringId("defaultImportOffersJob");
    modelService.save(schedulerJob);

    schedulerJobModel = modelService.create(MiraklImportOffersCronJobModel.class);
    schedulerJobModel.setFullImport(full);
    schedulerJobModel.setJob(schedulerJob);
    schedulerJobModel.setLastImportTime(DEFAULT_LAST_IMPORT_TIME);
    modelService.save(schedulerJobModel);

    jobModel = modelService.create(MiraklImportOfferPollerModel.class);
    jobModel.setCleanupMiraklAsyncTask(cleanupTask);
    jobModel.setCode("testMiraklImportOfferPoller");
    jobModel.setSessionUser(userService.getUserForUID("admin"));
    jobModel.setSessionLanguage(commonI18NService.getLanguage("en"));
    jobModel.setSessionCurrency(currencyService.getCurrencyForCode("EUR"));
    jobModel.setSchedulerInstance(schedulerJobModel);
    jobModel.setLogsDaysOld(1);
    jobModel.setFilesDaysOld(1);
    jobModel.setStartTime(new Date());
    return jobModel;
  }

  protected String saveSampleOffer(String id) {
    OfferModel offer = modelService.create(OfferModel.class);
    offer.setId(id);
    offer.setProductCode(id + "_product_code");
    offer.setActive(true);
    offer.setDeleted(false);
    offer.setQuantity(1000);
    offer.setTotalPrice(new BigDecimal("10000.97"));
    modelService.save(offer);
    return id;
  }

  protected Date date(String s) {
    return DateTime.parse(s, ISODateTimeFormat.dateTimeParser()).toDate();
  }

}
