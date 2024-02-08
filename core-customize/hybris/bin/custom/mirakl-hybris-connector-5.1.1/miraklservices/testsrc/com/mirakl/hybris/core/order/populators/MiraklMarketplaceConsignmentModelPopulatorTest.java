package com.mirakl.hybris.core.order.populators;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.domain.order.MiraklOrderLine;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklMarketplaceConsignmentModelPopulatorTest {

  private static final String ORDER_LINE_1_ID = "order-line-1-id";

  private static final String ORDER_LINE_2_ID = "order-line-2-id";

  @InjectMocks
  private MiraklMarketplaceConsignmentModelPopulator populator;

  @Mock
  private Converter<MiraklOrderLine, ConsignmentEntryModel> consignmentEntriesConverter;
  @Mock
  private MarketplaceConsignmentService marketplaceConsignmentService;
  @Mock
  private MiraklOrder miraklOrder;
  @Mock
  private MarketplaceConsignmentModel consignment;
  @Mock
  private MiraklOrderLine miraklOrderLine1, miraklOrderLine2;
  @Mock
  private ConsignmentEntryModel consignmentEntry1, consignmentEntry2;
  @Mock
  private List<MiraklAdditionalFieldValue> additionalFieldValues;

  private List<MiraklOrderLine> miraklOrderLines;
  private Set<ConsignmentEntryModel> consignmentEntries;

  @Before
  public void setUp() throws Exception {
    miraklOrderLines = asList(miraklOrderLine1, miraklOrderLine2);
    consignmentEntries = newHashSet(consignmentEntry1, consignmentEntry2);
    when(consignmentEntry1.getMiraklOrderLineId()).thenReturn(ORDER_LINE_1_ID);
    when(consignmentEntry2.getMiraklOrderLineId()).thenReturn(ORDER_LINE_2_ID);
    when(miraklOrderLine1.getId()).thenReturn(ORDER_LINE_1_ID);
    when(miraklOrderLine2.getId()).thenReturn(ORDER_LINE_2_ID);
  }

  @Test
  public void shouldPopulateAdditionalFields() throws Exception {
    when(miraklOrder.getOrderAdditionalFields()).thenReturn(additionalFieldValues);
    when(miraklOrder.getOrderLines()).thenReturn(miraklOrderLines);
    when(consignment.getConsignmentEntries()).thenReturn(consignmentEntries);

    populator.populate(miraklOrder, consignment);

    verify(marketplaceConsignmentService).storeMarketplaceConsignmentCustomFields(additionalFieldValues, consignment);
    verify(consignmentEntriesConverter).convert(miraklOrderLine1, consignmentEntry1);
    verify(consignmentEntriesConverter).convert(miraklOrderLine2, consignmentEntry2);
    verifyNoMoreInteractions(consignmentEntriesConverter);
  }

}
