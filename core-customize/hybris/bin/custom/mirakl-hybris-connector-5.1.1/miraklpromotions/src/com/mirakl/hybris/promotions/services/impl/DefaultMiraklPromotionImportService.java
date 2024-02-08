package com.mirakl.hybris.promotions.services.impl;

import static com.google.common.primitives.Ints.checkedCast;
import static com.mirakl.hybris.core.util.PaginationUtils.getNumberOfPages;
import static com.mirakl.hybris.promotions.constants.MiraklpromotionsConstants.DEFAULT_FILE_ENCODING;
import static com.mirakl.hybris.promotions.constants.MiraklpromotionsConstants.OFFER_PROMOTION_MAPPING_HEADER;
import static java.lang.String.format;
import static org.apache.commons.collections4.IterableUtils.isEmpty;
import static org.apache.commons.io.FileUtils.deleteQuietly;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.domain.promotion.MiraklPromotion;
import com.mirakl.client.mmp.front.domain.promotion.MiraklPromotionOffersMapping;
import com.mirakl.client.mmp.front.domain.promotion.MiraklPromotions;
import com.mirakl.client.mmp.front.request.promotion.MiraklGetPromotionsRequest;
import com.mirakl.client.mmp.front.request.promotion.MiraklPromotionOffersMappingRequest;
import com.mirakl.hybris.core.util.PaginationUtils;
import com.mirakl.hybris.core.util.services.CsvService;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;
import com.mirakl.hybris.promotions.services.MiraklPromotionImportService;
import com.mirakl.hybris.promotions.services.MiraklPromotionService;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportConfig.ValidationMode;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.impex.impl.FileBasedImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMiraklPromotionImportService implements MiraklPromotionImportService {

  private static final Logger LOG = Logger.getLogger(DefaultMiraklPromotionImportService.class);

  protected ModelService modelService;
  protected MiraklPromotionService miraklPromotionService;
  protected ConfigurationService configurationService;
  protected ImportService importService;
  protected CsvService csvService;
  protected Converter<MiraklPromotion, MiraklPromotionModel> miraklPromotionConverter;
  protected Converter<MiraklPromotionOffersMapping, Map<String, String>> miraklPromotionOffersMappingImpexConverter;
  protected MiraklMarketplacePlatformFrontApi miraklFrontApi;

  @Override
  public Collection<MiraklPromotionModel> importAllPromotions() {
    int page = 0;
    Integer pagesNeeded = null;
    Set<MiraklPromotionModel> promotionsToSave = new HashSet<>();

    do {
      MiraklPromotions promotions = miraklFrontApi.getPromotions(buildGetPromotionsRequest(page++));
      if (pagesNeeded == null) {
        pagesNeeded = getNumberOfPages(checkedCast(promotions.getTotalCount()), getMaxResultsByPage());
      }
      for (MiraklPromotion miraklPromotion : promotions.getPromotions()) {
        importPromotion(miraklPromotion, promotionsToSave);
      }
    } while (page < pagesNeeded);

    modelService.saveAll(promotionsToSave);

    return promotionsToSave;
  }

  protected void importPromotion(MiraklPromotion miraklPromotion, Set<MiraklPromotionModel> promotionsToSave) {
    try {
      MiraklPromotionModel miraklPromotionModel = getMiraklPromotion(miraklPromotion);
      promotionsToSave.add(miraklPromotionModel);
    } catch (Exception e) {
      LOG.error(format("An error occurred while importing promotion with id:[%s] for shop id:[%s]",
          miraklPromotion.getInternalId(), miraklPromotion.getShopId()), e);
    }
  }

  protected MiraklPromotionModel getMiraklPromotion(MiraklPromotion miraklPromotion) {
    MiraklPromotionModel promotion =
        miraklPromotionService.getPromotion(miraklPromotion.getShopId(), miraklPromotion.getInternalId());
    if (promotion == null) {
      promotion = miraklPromotionConverter.convert(miraklPromotion);
    } else {
      promotion = miraklPromotionConverter.convert(miraklPromotion, promotion);
    }

    return promotion;
  }

  protected MiraklGetPromotionsRequest buildGetPromotionsRequest(int pageNumber) {
    return PaginationUtils.applyMiraklPagination(new MiraklGetPromotionsRequest(), getMaxResultsByPage(),
        pageNumber * getMaxResultsByPage());
  }

  @Override
  public void importPromotionOffersMapping(Date lastUpdate) {
    final MiraklPromotionOffersMappingRequest request = new MiraklPromotionOffersMappingRequest();
    request.setLastRequestDate(lastUpdate);
    Iterable<MiraklPromotionOffersMapping> promotionOfferMapping = miraklFrontApi.exportPromotionOffersMappingAsStream(request);

    if (isEmpty(promotionOfferMapping)) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("No Promotion-Offer mapping to import/update");
      }
      return;
    }

    File promotionMappingFile = null;
    try {
      promotionMappingFile = csvService.createCsvFileWithHeaders(OFFER_PROMOTION_MAPPING_HEADER, promotionOfferMapping,
          miraklPromotionOffersMappingImpexConverter);
      importService.importData(getImpexConfig(promotionMappingFile));
    } catch (IOException e) {
      LOG.error("Unable to import Promotion-Offer mapping", e);
    } finally {
      deleteQuietly(promotionMappingFile);
    }
  }

  protected ImportConfig getImpexConfig(File csvFile) {
    final ImportConfig config = new ImportConfig();
    config.setScript(new FileBasedImpExResource(csvFile, DEFAULT_FILE_ENCODING));
    config.setValidationMode(ValidationMode.RELAXED);

    return config;
  }

  protected int getMaxResultsByPage() {
    return configurationService.getConfiguration().getInt("mirakl.promotions.import.pagesize", 100);
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setMiraklPromotionService(MiraklPromotionService miraklPromotionService) {
    this.miraklPromotionService = miraklPromotionService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setImportService(ImportService importService) {
    this.importService = importService;
  }

  @Required
  public void setCsvService(CsvService csvService) {
    this.csvService = csvService;
  }

  @Required
  public void setMiraklPromotionConverter(Converter<MiraklPromotion, MiraklPromotionModel> miraklPromotionConverter) {
    this.miraklPromotionConverter = miraklPromotionConverter;
  }

  @Required
  public void setMiraklPromotionOffersMappingImpexConverter(
      Converter<MiraklPromotionOffersMapping, Map<String, String>> miraklPromotionOffersMappingImpexConverter) {
    this.miraklPromotionOffersMappingImpexConverter = miraklPromotionOffersMappingImpexConverter;
  }

  @Required
  public void setMiraklFrontApi(MiraklMarketplacePlatformFrontApi miraklFrontApi) {
    this.miraklFrontApi = miraklFrontApi;
  }

}
