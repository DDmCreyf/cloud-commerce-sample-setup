package com.mirakl.hybris.core.shop.populators;

import static java.util.UUID.nameUUIDFromBytes;
import static org.apache.commons.lang.StringUtils.isBlank;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.shop.MiraklMediaInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.hybris.core.media.services.MiraklMediaService;
import com.mirakl.hybris.core.model.ShopModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ShopMediaPopulator implements Populator<MiraklShop, ShopModel> {

  protected static final String MIRAKL_SHOP_MEDIA_ID_FORMAT = "mp-shop-%s-%s";

  protected MiraklMediaService miraklMediaService;

  @Override
  public void populate(MiraklShop miraklShop, ShopModel shopModel) throws ConversionException {
    MiraklMediaInformation mediaInformation = miraklShop.getMediaInformation();
    if (mediaInformation == null) {
      return;
    }
    if (!isBlank(mediaInformation.getBanner())) {
      shopModel.setBanner(miraklMediaService.downloadMedia(getId(miraklShop.getId(), mediaInformation.getBanner()), //
          mediaInformation.getBanner(), true));
    }
    if (!isBlank(mediaInformation.getLogo())) {
      shopModel.setLogo(miraklMediaService.downloadMedia(getId(miraklShop.getId(), mediaInformation.getLogo()), //
          mediaInformation.getLogo(), true));
    }
  }

  protected String getId(String shopId, String mediaDownloadUrl) {
    return String.format(MIRAKL_SHOP_MEDIA_ID_FORMAT, shopId, nameUUIDFromBytes(mediaDownloadUrl.getBytes()).toString());
  }

  @Required
  public void setMiraklMediaService(MiraklMediaService miraklMediaService) {
    this.miraklMediaService = miraklMediaService;
  }
}
