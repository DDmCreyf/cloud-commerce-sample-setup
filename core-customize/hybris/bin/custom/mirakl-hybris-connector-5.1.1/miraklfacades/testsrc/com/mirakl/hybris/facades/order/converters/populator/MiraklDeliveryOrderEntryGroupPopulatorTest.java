package com.mirakl.hybris.facades.order.converters.populator;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.DeliveryOrderEntryGroupData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklDeliveryOrderEntryGroupPopulatorTest {

  private static final String OFFER_ID_1 = "offerId1";
  private static final String OFFER_ID_2 = "offerId2";
  private static final int FIRST_ORDER_ENTRY_NUMBER = 0;
  private static final int SECOND_ORDER_ENTRY_NUMBER = 1;
  private static final int ORDER_ENTRY_WITHOUT_OFFER_NUMBER = 2;
  private static final long FIRST_ORDER_GROUP_QUANTITY = 1;
  private static final long SECOND_ORDER_GROUP_QUANTITY = 2;
  private static final long NO_OFFER_GROUP_QUANTITY = 3;

  @InjectMocks
  private MiraklDeliveryOrderEntryGroupPopulator testObj = new MiraklDeliveryOrderEntryGroupPopulator();

  @Mock
  private ShippingFeeService shippingFeeServiceMock;
  @Mock
  private Populator<MiraklOrderShippingFee, DeliveryOrderEntryGroupData> shippingOptionPopulatorMock;

  @Mock
  private AbstractOrderModel orderMock;
  @Mock
  private AbstractOrderEntryModel firstOrderEntryModelMock, secondOrderEntryModelMock, orderEntryModelWithoutOfferMock;
  @Mock
  private OrderEntryData firstOrderEntryDataMock, secondOrderEntryDataMock, orderEntryModelWithoutOfferDataMock;
  @Mock
  private MiraklOrderShippingFees orderShippingFeesMock;
  @Mock
  private MiraklOrderShippingFee firstShippingFeeMock, secondShippingFeeMock;
  @Mock
  private MiraklOrderShippingFeeOffer firstShippingOfferMock, secondShippingOfferMock;

  @Before
  public void setUp() {
    when(shippingFeeServiceMock.getStoredShippingFees(orderMock)).thenReturn(orderShippingFeesMock);
    when(orderMock.getMarketplaceEntries()).thenReturn(asList(firstOrderEntryModelMock, secondOrderEntryModelMock));
    when(orderMock.getOperatorEntriesForDelivery()).thenReturn(singletonList(orderEntryModelWithoutOfferMock));

    when(orderShippingFeesMock.getOrders()).thenReturn(ImmutableList.of(firstShippingFeeMock, secondShippingFeeMock));
    when(orderMock.getEntries())
        .thenReturn(ImmutableList.of(firstOrderEntryModelMock, secondOrderEntryModelMock, orderEntryModelWithoutOfferMock));

    when(firstOrderEntryModelMock.getOfferId()).thenReturn(OFFER_ID_1);
    when(firstOrderEntryModelMock.getEntryNumber()).thenReturn(FIRST_ORDER_ENTRY_NUMBER);
    when(secondOrderEntryModelMock.getOfferId()).thenReturn(OFFER_ID_2);
    when(secondOrderEntryModelMock.getEntryNumber()).thenReturn(SECOND_ORDER_ENTRY_NUMBER);
    when(orderEntryModelWithoutOfferMock.getEntryNumber()).thenReturn(ORDER_ENTRY_WITHOUT_OFFER_NUMBER);
    when(firstOrderEntryDataMock.getEntryNumber()).thenReturn(FIRST_ORDER_ENTRY_NUMBER);
    when(firstOrderEntryDataMock.getQuantity()).thenReturn(FIRST_ORDER_GROUP_QUANTITY);
    when(secondOrderEntryDataMock.getEntryNumber()).thenReturn(SECOND_ORDER_ENTRY_NUMBER);
    when(secondOrderEntryDataMock.getQuantity()).thenReturn(SECOND_ORDER_GROUP_QUANTITY);
    when(orderEntryModelWithoutOfferDataMock.getEntryNumber()).thenReturn(ORDER_ENTRY_WITHOUT_OFFER_NUMBER);
    when(orderEntryModelWithoutOfferDataMock.getQuantity()).thenReturn(NO_OFFER_GROUP_QUANTITY);

    when(firstShippingFeeMock.getOffers()).thenReturn(singletonList(firstShippingOfferMock));
    when(firstShippingOfferMock.getId()).thenReturn(OFFER_ID_1);

    when(secondShippingFeeMock.getOffers()).thenReturn(singletonList(secondShippingOfferMock));
    when(secondShippingOfferMock.getId()).thenReturn(OFFER_ID_2);
  }

  @Test
  public void populatesOrderEntryGroups() {
    AbstractOrderData abstractOrderData = new AbstractOrderData();
    abstractOrderData.setEntries(asList(firstOrderEntryDataMock, secondOrderEntryDataMock, orderEntryModelWithoutOfferDataMock));

    testObj.populate(orderMock, abstractOrderData);

    List<DeliveryOrderEntryGroupData> result = abstractOrderData.getDeliveryOrderGroups();

    assertThat(result).hasSize(3);

    Optional<DeliveryOrderEntryGroupData> firstGroupData = findGroupForEntry(result, firstOrderEntryDataMock);
    assertThat(firstGroupData.isPresent()).isEqualTo(true);
    assertThat(firstGroupData.get().getQuantity()).isEqualTo(FIRST_ORDER_GROUP_QUANTITY);

    Optional<DeliveryOrderEntryGroupData> secondGroupData = findGroupForEntry(result, secondOrderEntryDataMock);
    assertThat(secondGroupData.isPresent()).isEqualTo(true);
    assertThat(secondGroupData.get().getQuantity()).isEqualTo(SECOND_ORDER_GROUP_QUANTITY);

    Optional<DeliveryOrderEntryGroupData> thirdGroupData = findGroupForEntry(result, orderEntryModelWithoutOfferDataMock);
    assertThat(thirdGroupData.isPresent()).isEqualTo(true);
    assertThat(thirdGroupData.get().getQuantity()).isEqualTo(NO_OFFER_GROUP_QUANTITY);
  }

  @Test
  public void createsOnlyOneDeliveryGroupWithAllEntriesIfNoShippingFeesJSONFound() {
    when(shippingFeeServiceMock.getStoredShippingFees(orderMock)).thenReturn(null);

    AbstractOrderData abstractOrderData = new AbstractOrderData();
    abstractOrderData.setEntries(asList(firstOrderEntryDataMock, secondOrderEntryDataMock));

    testObj.populate(orderMock, abstractOrderData);

    List<DeliveryOrderEntryGroupData> result = abstractOrderData.getDeliveryOrderGroups();

    assertThat(result).hasSize(1);
    DeliveryOrderEntryGroupData groupData = result.get(0);
    assertThat(groupData.getEntries()).containsOnly(firstOrderEntryDataMock, secondOrderEntryDataMock);
    assertThat(groupData.getQuantity()).isEqualTo(NO_OFFER_GROUP_QUANTITY);

    verify(shippingOptionPopulatorMock, never()).populate(any(MiraklOrderShippingFee.class),
        any(DeliveryOrderEntryGroupData.class));
  }

  private Optional<DeliveryOrderEntryGroupData> findGroupForEntry(final List<DeliveryOrderEntryGroupData> groups,
      final OrderEntryData entry) {
    return FluentIterable.from(groups).firstMatch(new Predicate<DeliveryOrderEntryGroupData>() {

      @Override
      public boolean apply(DeliveryOrderEntryGroupData group) {
        return group.getEntries().contains(entry);
      }
    });
  }
}
