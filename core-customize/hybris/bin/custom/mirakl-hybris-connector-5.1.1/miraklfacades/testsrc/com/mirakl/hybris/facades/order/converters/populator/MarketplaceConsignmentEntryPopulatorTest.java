package com.mirakl.hybris.facades.order.converters.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.enums.MiraklOrderLineStatus;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

@UnitTest
@RunWith(value = MockitoJUnitRunner.class)
public class MarketplaceConsignmentEntryPopulatorTest {

  public static final String ENTRY_CLOSED_STATUS_LABEL = "entry_closed_status";
  public static final long SHIPPED_QUANTITY = 8;

  @Mock
  private EnumerationService enumerationService;

  @Mock
  private ConsignmentEntryModel consignmentEntryModel;

  @Mock
  private OrderEntryModel orderEntry;

  @Mock
  private ConsignmentModel consignmentModel;

  @InjectMocks
  private MarketplaceConsignmentEntryPopulator testObj;

  @Before
  public void setUp() throws Exception {
    when(consignmentEntryModel.getMiraklOrderLineStatus()).thenReturn(MiraklOrderLineStatus.CLOSED);
    when(enumerationService.getEnumerationName(MiraklOrderLineStatus.CLOSED)).thenReturn(ENTRY_CLOSED_STATUS_LABEL);
    when(consignmentEntryModel.getConsignment()).thenReturn(consignmentModel);
  }

  @Test
  public void testPopulate() throws Exception {
    ConsignmentEntryData target = new ConsignmentEntryData();
    testObj.populate(consignmentEntryModel, target);

    assertThat(target.getMiraklOrderLineStatus()).isEqualTo(MiraklOrderLineStatus.CLOSED);
    assertThat(target.getMiraklOrderLineStatusLabel()).isEqualTo(ENTRY_CLOSED_STATUS_LABEL);
  }
}
