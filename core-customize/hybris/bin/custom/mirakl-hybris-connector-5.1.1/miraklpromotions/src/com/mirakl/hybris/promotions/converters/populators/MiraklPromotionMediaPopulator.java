package com.mirakl.hybris.promotions.converters.populators;

import static java.util.UUID.nameUUIDFromBytes;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.promotion.MiraklPromotionMedia;
import com.mirakl.client.mmp.front.domain.promotion.MiraklPromotion;
import com.mirakl.hybris.core.media.services.MiraklMediaService;
import com.mirakl.hybris.core.util.strategies.LocaleMappingStrategy;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.I18NService;

public class MiraklPromotionMediaPopulator implements Populator<MiraklPromotion, MiraklPromotionModel> {

  private static final Logger LOG = Logger.getLogger(MiraklPromotionMediaPopulator.class);

  protected static final String MIRAKL_PROMOTION_MEDIA_ID_FORMAT = "mp-promo-%s-%s-%s";

  protected LocaleMappingStrategy localeMappingStrategy;
  protected I18NService i18NService;
  protected MiraklMediaService miraklMediaService;

  @Override
  public void populate(MiraklPromotion miraklPromotion, MiraklPromotionModel miraklPromotionModel) throws ConversionException {
    List<MiraklPromotionMedia> medias = miraklPromotion.getMedias();
    if (isEmpty(medias)) {
      return;
    }
    for (MiraklPromotionMedia media : medias) {
      MediaModel mediaModel =
          miraklMediaService.downloadMedia(getMediaId(miraklPromotion, media), media.getUrl().toString());
      miraklPromotionModel.setMediaUrl(mediaModel != null ? mediaModel.getURL() : null, getMediaLocale(media));
      if (mediaModel == null) {
        LOG.warn(String.format("Unable to get media [url=%s, locale=%s] for Mirakl promotion [id=%s]...",
            media.getUrl().toString(), getMediaLocale(media), miraklPromotion.getInternalId()));
      }
    }
  }

  protected Locale getMediaLocale(MiraklPromotionMedia media) {
    return media.getLocale() != null ? localeMappingStrategy.mapToHybrisLocale(media.getLocale())
        : i18NService.getCurrentLocale();
  }

  protected String getMediaId(MiraklPromotion miraklPromotion, MiraklPromotionMedia media) {
    return String.format(MIRAKL_PROMOTION_MEDIA_ID_FORMAT, miraklPromotion.getInternalId(), media.getLocale().toString(),
        nameUUIDFromBytes(media.getUrl().toString().getBytes()).toString());
  }

  @Required
  public void setLocaleMappingStrategy(LocaleMappingStrategy localeMappingStrategy) {
    this.localeMappingStrategy = localeMappingStrategy;
  }

  @Required
  public void setI18NService(I18NService i18NService) {
    this.i18NService = i18NService;
  }

  @Required
  public void setMiraklMediaService(MiraklMediaService miraklMediaService) {
    this.miraklMediaService = miraklMediaService;
  }
}
