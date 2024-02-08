package com.mirakl.hybris.core.payment.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.List;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultLookupMiraklDebitsToProcessStrategyTest {

  @InjectMocks
  private DefaultLookupMiraklDebitsToProcessStrategy strategy;

  @Mock
  private List<MarketplaceConsignmentModel> consignments;
  @Mock
  private MarketplaceConsignmentService marketplaceConsignmentService;


  @Before
  public void setUp() {
    when(marketplaceConsignmentService.getMarketplaceConsignmentsForPaymentStatuses(any(EnumSet.class))).thenReturn(consignments);
  }

  @Test
  public void lookupDebitsToProcessIsMarketplaceConsignmentServiceGateway() {
    List<MarketplaceConsignmentModel> marketplaceConsignmentModels = strategy.lookupDebitsToProcess();

    assertThat(marketplaceConsignmentModels).isEqualTo(marketplaceConsignmentModels);
  }
}
