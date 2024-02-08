package com.mirakl.hybris.core.ordersplitting.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.order.MiraklOrderLine;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class MiraklCreateConsignmentEntryPopulatorTest {
  private static final int QUANTITY = 3;
  private static final String ORDER_LINE_ID = "123456";

  @InjectMocks
  private MiraklCreateConsignmentEntryPopulator populator;

  @Mock
  private AbstractOrderEntryModel orderEntryModel;


  @Mock
  private MiraklOrderLine miraklOrderLine;

  @Before
  public void setUp() {
    when(miraklOrderLine.getQuantity()).thenReturn(QUANTITY);
    when(miraklOrderLine.getId()).thenReturn(ORDER_LINE_ID);
  }

  @Test
  public void shouldPopulateConsignmentEntry() {
    ConsignmentEntryModel consignmentEntryModel = new ConsignmentEntryModel();

    populator.populate(Pair.of(orderEntryModel, miraklOrderLine), consignmentEntryModel);

    assertThat(consignmentEntryModel.getQuantity()).isEqualTo(miraklOrderLine.getQuantity());
    assertThat(consignmentEntryModel.getOrderEntry()).isEqualTo(orderEntryModel);
    assertThat(consignmentEntryModel.getMiraklOrderLineId()).isEqualTo(ORDER_LINE_ID);

  }

}
