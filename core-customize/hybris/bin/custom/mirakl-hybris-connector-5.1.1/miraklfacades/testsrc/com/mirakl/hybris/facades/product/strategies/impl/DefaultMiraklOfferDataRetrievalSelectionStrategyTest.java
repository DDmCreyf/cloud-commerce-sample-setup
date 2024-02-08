package com.mirakl.hybris.facades.product.strategies.impl;

import com.mirakl.hybris.facades.product.strategies.OfferDataRetrievalStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklOfferDataRetrievalSelectionStrategyTest {

  @Mock
  private ConfigurationService configurationService;
  @Mock
  private LinkedList<OfferDataRetrievalStrategy> offerDataRetrievalStrategies;
  @InjectMocks
  private DefaultMiraklOfferDataRetrievalResolutionStrategy defaultMiraklOfferDataRetrievalResolutionStrategy;
  @Mock
  private OfferDataRetrievalStrategy offerDataRetrievalStrategy1;
  @Mock
  private OfferDataRetrievalStrategy offerDataRetrievalStrategy2;
  @Mock
  private OfferDataRetrievalStrategy offerDataRetrievalStrategy3;

  @Test
  public void shouldSelectTheFirstApplicableStrategy() {
    when(offerDataRetrievalStrategy2.isApplicable()).thenReturn(true);

    offerDataRetrievalStrategies = new LinkedList<>();
    offerDataRetrievalStrategies.add(offerDataRetrievalStrategy2);
    offerDataRetrievalStrategies.add(offerDataRetrievalStrategy3);
    offerDataRetrievalStrategies.add(offerDataRetrievalStrategy1);
    defaultMiraklOfferDataRetrievalResolutionStrategy.setOfferDataRetrievalStrategies(offerDataRetrievalStrategies);

    Assertions.assertThat(defaultMiraklOfferDataRetrievalResolutionStrategy.resolveOfferDataRetrievalStrategy())
        .isEqualTo(offerDataRetrievalStrategy2);

  }


  @Test
  public void shouldReturnNullIfNoStrategy() {
    defaultMiraklOfferDataRetrievalResolutionStrategy.setOfferDataRetrievalStrategies(new LinkedList<>());

    Assertions.assertThat(defaultMiraklOfferDataRetrievalResolutionStrategy.resolveOfferDataRetrievalStrategy()).isNull();
  }

  @Test
  public void shouldReturnNullIfStrategyListIsNull() {
    defaultMiraklOfferDataRetrievalResolutionStrategy.setOfferDataRetrievalStrategies(null);

    Assertions.assertThat(defaultMiraklOfferDataRetrievalResolutionStrategy.resolveOfferDataRetrievalStrategy()).isNull();
  }

  @Test
  public void shouldReturnNullIfNoApplicableStrategy() {
    when(offerDataRetrievalStrategy1.isApplicable()).thenReturn(false);
    when(offerDataRetrievalStrategy2.isApplicable()).thenReturn(false);
    when(offerDataRetrievalStrategy3.isApplicable()).thenReturn(false);

    offerDataRetrievalStrategies = new LinkedList<>();
    offerDataRetrievalStrategies.add(offerDataRetrievalStrategy2);
    offerDataRetrievalStrategies.add(offerDataRetrievalStrategy3);
    offerDataRetrievalStrategies.add(offerDataRetrievalStrategy1);
    defaultMiraklOfferDataRetrievalResolutionStrategy.setOfferDataRetrievalStrategies(offerDataRetrievalStrategies);

    Assertions.assertThat(defaultMiraklOfferDataRetrievalResolutionStrategy.resolveOfferDataRetrievalStrategy()).isNull();
  }
}
