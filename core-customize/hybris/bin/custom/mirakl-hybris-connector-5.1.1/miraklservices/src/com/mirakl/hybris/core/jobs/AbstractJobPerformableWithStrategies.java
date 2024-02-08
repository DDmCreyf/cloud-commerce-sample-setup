package com.mirakl.hybris.core.jobs;

import static java.lang.String.format;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.product.strategies.PerformJobStrategy;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public abstract class AbstractJobPerformableWithStrategies<K, T extends CronJobModel> extends AbstractJobPerformable<T> {

  private static final Logger LOG = Logger.getLogger(AbstractJobPerformableWithStrategies.class);

  protected Map<K, PerformJobStrategy<T>> performJobStrategies;

  @Override
  public PerformResult perform(final T cronJob) {
    K strategyKey = getStrategyKey(cronJob);
    PerformJobStrategy<T> strategy = performJobStrategies.get(strategyKey);

    if (strategy == null) {
      throw new IllegalStateException(format("Cannot find strategy to execute for key [%s]", strategyKey));
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Started performing the [%s] using the [%s] strategy", cronJob.getClass(), strategy.getClass()));
    }

    return strategy.perform(cronJob);
  }

  /**
   * Returns the key to use to look up the strategy
   * 
   * @param cronJob
   * @return the key associated to the strategy
   */
  protected abstract K getStrategyKey(T cronJob);

  @Required
  public void setPerformJobStrategies(Map<K, PerformJobStrategy<T>> performJobStrategies) {
    this.performJobStrategies = performJobStrategies;
  }
}
