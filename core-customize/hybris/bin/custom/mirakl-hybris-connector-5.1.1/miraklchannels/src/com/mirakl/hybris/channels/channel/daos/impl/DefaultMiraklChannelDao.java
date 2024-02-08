package com.mirakl.hybris.channels.channel.daos.impl;

import com.mirakl.hybris.channels.channel.daos.MiraklChannelDao;
import com.mirakl.hybris.channels.model.MiraklChannelModel;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

public class DefaultMiraklChannelDao extends DefaultGenericDao<MiraklChannelModel> implements MiraklChannelDao {

  public DefaultMiraklChannelDao() {
    super(MiraklChannelModel._TYPECODE);
  }

}
