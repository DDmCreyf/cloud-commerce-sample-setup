package com.mirakl.hybris.promotions.converters.populators;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklOrderEntryRaoPopulatorTest {

  private static final String OFFER_ID = "2014";

  @InjectMocks
  private MiraklOrderEntryRaoPopulator testObj;
  @Mock
  private OrderEntryRAO orderEntryRAO;
  @Mock
  private AbstractOrderEntryModel orderEntryModel;

  @Test
  public void shouldBeMarketAsMarketplace() throws Exception {
    when(orderEntryModel.getOfferId()).thenReturn(OFFER_ID);

    testObj.populate(orderEntryModel, orderEntryRAO);

    verify(orderEntryRAO).setIsMarketplace(true);
  }

  @Test
  public void shouldNotBeMarketAsMarketplace() throws Exception {
    testObj.populate(orderEntryModel, orderEntryRAO);

    verify(orderEntryRAO).setIsMarketplace(false);
  }

}
