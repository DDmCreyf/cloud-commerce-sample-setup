package com.mirakl.hybris.channels.search.facades.solrfacetsearch.indexer.listeners;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.channel.strategies.MiraklChannelResolvingStrategy;

import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchListener;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerListener;
import de.hybris.platform.solrfacetsearch.indexer.IndexerQueryContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerQueryListener;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;

public class MiraklChannelListener implements IndexerQueryListener, IndexerListener, IndexerBatchListener {

  protected MiraklChannelService miraklChannelService;
  protected MiraklChannelResolvingStrategy miraklChannelResolvingStrategy;

  @Override
  public void beforeQuery(IndexerQueryContext paramIndexerQueryContext) throws IndexerException {
    initalizeSession();
  }

  @Override
  public void beforeBatch(IndexerBatchContext paramIndexerBatchContext) throws IndexerException {
    initalizeSession();
  }

  @Override
  public void beforeIndex(IndexerContext paramIndexerContext) throws IndexerException {
    initalizeSession();
  }

  protected void initalizeSession() {
    miraklChannelService.setCurrentMiraklChannel(miraklChannelResolvingStrategy.resolveCurrentChannel());
  }

  @Override
  public void afterQuery(IndexerQueryContext paramIndexerQueryContext) throws IndexerException {
    // Nothing to do here
  }

  @Override
  public void afterQueryError(IndexerQueryContext paramIndexerQueryContext) throws IndexerException {
    // Nothing to do here
  }

  @Override
  public void afterBatch(IndexerBatchContext paramIndexerBatchContext) throws IndexerException {
    // Nothing to do here
  }

  @Override
  public void afterBatchError(IndexerBatchContext paramIndexerBatchContext) throws IndexerException {
    // Nothing to do here
  }

  @Override
  public void afterIndex(IndexerContext paramIndexerContext) throws IndexerException {
    // Nothing to do here
  }

  @Override
  public void afterIndexError(IndexerContext paramIndexerContext) throws IndexerException {
    // Nothing to do here
  }

  @Required
  public void setMiraklChannelService(MiraklChannelService miraklChannelService) {
    this.miraklChannelService = miraklChannelService;
  }

  @Required
  public void setMiraklChannelResolvingStrategy(MiraklChannelResolvingStrategy miraklChannelResolvingStrategy) {
    this.miraklChannelResolvingStrategy = miraklChannelResolvingStrategy;
  }

}
