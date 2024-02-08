package com.mirakl.hybris.promotions.rule.evaluation.actions.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryConsumedRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklRAOConsumptionSupportTest {

	@Spy
	@InjectMocks
	private MiraklRAOConsumptionSupport testObj;

	@Mock
	private OrderEntryRAO orderEntryRAOMock;
	@Mock
	private AbstractRuleActionRAO actionRAOMock;
	@Mock
	private OrderEntryConsumedRAO orderEntryConsumedRAOMock;

	@Test
	public void consumeOrderEntry_shouldReturnNull_WhenOrderEntryRAOIsMarketplace() {
		when(orderEntryRAOMock.getIsMarketplace()).thenReturn(true);

		final OrderEntryConsumedRAO result = testObj.consumeOrderEntry(orderEntryRAOMock, actionRAOMock);

		assertThat(result).isNull();
	}

	@Test
	public void consumeOrderEntry_shouldReturnWhatSuperConsumeOrderEntryReturns_WhenOrderEntryRAOIsNotMarketplace() {
		when(orderEntryRAOMock.getIsMarketplace()).thenReturn(false);
		doReturn(orderEntryConsumedRAOMock).when(testObj).superConsumeOrderEntry(orderEntryRAOMock, actionRAOMock);

		final OrderEntryConsumedRAO result = testObj.consumeOrderEntry(orderEntryRAOMock, actionRAOMock);

		assertThat(result).isEqualTo(orderEntryConsumedRAOMock);
	}
}
