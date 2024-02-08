package com.mirakl.hybris.core.order.populators;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.order.MiraklOrderLine;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklConsignmentEntryModelPopulatorTest {
  @InjectMocks
  private MiraklConsignmentEntryModelPopulator populator;

  @Mock
  private MarketplaceConsignmentService marketplaceConsignmentService;
  @Mock
  private MiraklOrderLine orderLine;
  @Mock
  private ConsignmentEntryModel consignmentEntry;
  @Mock
  private List<MiraklAdditionalFieldValue> additionalFieldValues;

  @Test
  public void shouldPopulateAdditionalFields() throws Exception {
    when(orderLine.getAdditionalFields()).thenReturn(additionalFieldValues);

    populator.populate(orderLine, consignmentEntry);

    verify(marketplaceConsignmentService).storeMarketplaceConsignmentEntryCustomFields(additionalFieldValues, consignmentEntry);
  }

}
