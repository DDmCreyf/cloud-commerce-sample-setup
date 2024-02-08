package com.mirakl.hybris.core.product.strategies;

import com.mirakl.hybris.core.product.strategies.impl.DefaultMiraklRealtimeOfferPriceRetrievalMode;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.REALTIME_OFFER_PRICE_ACCESS_MODE;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(Parameterized.class)
public class DefaultMiraklRealtimeOfferPriceRetrievalModeTest {

  @InjectMocks
  private DefaultMiraklRealtimeOfferPriceRetrievalMode defaultRealtimeOfferDataRetrievalMode;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;
  private final boolean configurationValue;
  private final boolean expectedIsActive;


  public DefaultMiraklRealtimeOfferPriceRetrievalModeTest(boolean configurationValue, boolean expectedIsActive) {
    this.configurationValue = configurationValue;
    this.expectedIsActive = expectedIsActive;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{{true, true}, {false, false}, {true, true}, {false, false}});
  }

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    when(configurationService.getConfiguration()).thenReturn(configuration);

  }

  @Test
  public void testIsActive() {
    when(configurationService.getConfiguration().getBoolean(REALTIME_OFFER_PRICE_ACCESS_MODE, false)).thenReturn(configurationValue);

    boolean isActive = defaultRealtimeOfferDataRetrievalMode.isActive();

    Assertions.assertThat(isActive).isEqualTo(expectedIsActive);
  }
}
