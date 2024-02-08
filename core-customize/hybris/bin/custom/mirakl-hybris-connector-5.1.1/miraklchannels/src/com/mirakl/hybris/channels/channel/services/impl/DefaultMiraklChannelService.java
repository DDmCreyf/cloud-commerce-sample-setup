package com.mirakl.hybris.channels.channel.services.impl;

import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.MIRAKL_CHANNELS_ENABLED;
import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.MIRAKL_CHANNEL_SESSION_ATTRIBUTE;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DuplicateKeyException;

import com.mirakl.hybris.channels.channel.daos.MiraklChannelDao;
import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Utilities;

public class DefaultMiraklChannelService implements MiraklChannelService {

  private static final Logger LOG = Logger.getLogger(DefaultMiraklChannelService.class);

  protected ModelService modelService;
  protected SessionService sessionService;
  protected ConfigurationService configurationService;
  protected MiraklChannelDao miraklChannelDao;

  @Override
  public MiraklChannelModel getMiraklChannelForCode(String code) {
    List<MiraklChannelModel> channels = miraklChannelDao.find(Collections.singletonMap(MiraklChannelModel.CODE, code));

    if (isEmpty(channels)) {
      return null;
    }

    return channels.get(0);
  }

  @Override
  public MiraklChannelModel createMiraklChannel(String code, String label) {
    MiraklChannelModel channel = modelService.create(MiraklChannelModel.class);
    channel.setCode(code);
    channel.setLabel(label);
    modelService.save(channel);

    return channel;
  }

  @Override
  public MiraklChannelModel createMiraklChannelSilently(String code, String label) {
    try {
      return createMiraklChannel(code, label);
    } catch (Exception e) {
      if (isUniqueConstraintErrorAsRootCause(e)) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(String.format("Prevented concurrency issue while persisting channel '%s - %s'", code, label));
        }
        return Objects.requireNonNull(getMiraklChannelForCode(code));
      } else {
        throw e;
      }
    }
  }

  // This method is already present in modelService in the latest Hybris versions.
  // We implemented it here for backward compatibility.
  protected boolean isUniqueConstraintErrorAsRootCause(Exception e) {
    return Utilities.getRootCauseOfType(e, SQLIntegrityConstraintViolationException.class) != null
        || isSpringDuplicateKeyException(e) || isSpringConcurrencyException(e) || isHanaConstraintViolation(e)
        || isSQLServerConstraintViolation(e) || isUniqueAttributeInterceptorException(e);
  }

  protected boolean isSQLServerConstraintViolation(Exception e) {
    Throwable root = Utilities.getRootCauseOfName(e, "com.microsoft.sqlserver.jdbc.SQLServerException");
    return root != null && root.getMessage().contains("unique");
  }

  protected boolean isUniqueAttributeInterceptorException(Exception e) {
    return Utilities.getRootCauseOfName(e,
        "de.hybris.platform.servicelayer.interceptor.impl.UniqueAttributesInterceptor.AmbiguousUniqueKeysException") != null;
  }

  protected boolean isHanaConstraintViolation(Exception e) {
    Throwable root = Utilities.getRootCauseOfName(e, "com.sap.db.jdbc.exceptions.JDBCDriverException");
    return root != null && root.getMessage().contains("check_unique");
  }

  protected boolean isSpringDuplicateKeyException(Exception e) {
    return Utilities.getRootCauseOfType(e, DuplicateKeyException.class) != null;
  }

  protected boolean isSpringConcurrencyException(Exception e) {
    return Utilities.getRootCauseOfType(e, ConcurrencyFailureException.class) != null;
  }

  @Override
  public MiraklChannelModel getCurrentMiraklChannel() {
    return (MiraklChannelModel) sessionService.getAttribute(MIRAKL_CHANNEL_SESSION_ATTRIBUTE);
  }

  @Override
  public void setCurrentMiraklChannel(MiraklChannelModel miraklChannel) {
    sessionService.setAttribute(MIRAKL_CHANNEL_SESSION_ATTRIBUTE, miraklChannel);
  }

  @Override
  public boolean isMiraklChannelsEnabled() {
    return configurationService.getConfiguration().getBoolean(MIRAKL_CHANNELS_ENABLED, false);
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setSessionService(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setMiraklChannelDao(MiraklChannelDao miraklChannelDao) {
    this.miraklChannelDao = miraklChannelDao;
  }

}
