package com.mirakl.hybris.channels.product.daos.impl;

import static com.mirakl.hybris.core.util.flexiblesearch.impl.Condition.fieldEquals;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Field.field;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Item.item;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Join.entity;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.util.flexiblesearch.QueryDecorator;
import com.mirakl.hybris.core.util.flexiblesearch.impl.QueryBuilder;

public class OfferDaoChannelQueryDecorator implements QueryDecorator {

  protected MiraklChannelService miraklChannelService;

  @Override
  public void decorate(QueryBuilder queryBuilder) {
    MiraklChannelModel currentMiraklChannel = miraklChannelService.getCurrentMiraklChannel();
    if (currentMiraklChannel != null) {
      queryBuilder //
          .join(entity(item(OfferModel._MIRAKLCHANNEL2OFFERREL, "rel")).on(field("rel", "target"), field("o", OfferModel.PK)))//
          .join(entity(item(MiraklChannelModel._TYPECODE, "c")).on(field("rel", "source"), field("c", MiraklChannelModel.PK))) //
          .and(fieldEquals(field("c", MiraklChannelModel.CODE), currentMiraklChannel.getCode()));
    }
  }

  @Required
  public void setMiraklChannelService(MiraklChannelService miraklChannelService) {
    this.miraklChannelService = miraklChannelService;
  }

}
