package com.mirakl.hybris.facades.order.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Comparator;
import java.util.List;

import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.message.MiraklOrderMessage;
import com.mirakl.client.mmp.domain.message.MiraklOrderMessages;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderMessage;
import com.mirakl.hybris.beans.MessageData;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.order.services.MarketplaceConsignmentMessagesService;
import com.mirakl.hybris.core.order.strategies.MarketplaceConsignmentMessagesStrategy;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMarketplaceConsignmentMessagesFacadeTest {

  private static final String CONSIGNMENT_CODE = "b3bcad74-1942-43d1-b428-8f7a0351c5cf-A";

  @Mock
  private MarketplaceConsignmentMessagesService marketplaceConsignmentMessagesService;
  @Mock
  private MarketplaceConsignmentMessagesStrategy marketplaceConsignmentMessagesStrategy;
  @Mock
  private MarketplaceConsignmentService marketplaceConsignmentService;
  @Mock
  private Converter<MiraklOrderMessage, MessageData> orderMessageConverter;
  @Mock
  private Converter<MessageData, MiraklCreateOrderMessage> miraklCreateOrderMessageConverter;
  @Mock
  private MarketplaceConsignmentModel marketplaceConsignment;
  @Mock
  private MiraklOrderMessages miraklOrderMessages;
  @Mock
  private MiraklOrderMessage miraklOrderMessage1, miraklOrderMessage2;
  @Mock
  private MessageData message1, message2, messageToSend;
  @Mock
  private MiraklCreateOrderMessage miraklCreateOrderMessage;
  @Mock
  private Comparator<MessageData> messagesComparator;

  @InjectMocks
  private DefaultMarketplaceConsignmentMessagesFacade testObj;

  @Before
  public void setUp() throws Exception {
    when(marketplaceConsignmentService.getMarketplaceConsignmentForCode(CONSIGNMENT_CODE)).thenReturn(marketplaceConsignment);
    when(marketplaceConsignmentMessagesStrategy.canWriteMessages(marketplaceConsignment)).thenReturn(true);
    when(marketplaceConsignmentMessagesService.getMessagesForConsignment(CONSIGNMENT_CODE)).thenReturn(miraklOrderMessages);
    when(miraklOrderMessages.getMessages()).thenReturn(Collections.list(miraklOrderMessage1, miraklOrderMessage2));
    when(orderMessageConverter.convertAll(anyListOf(MiraklOrderMessage.class))).thenReturn(Collections.list(message1, message2));
    when(miraklCreateOrderMessageConverter.convert(messageToSend)).thenReturn(miraklCreateOrderMessage);
  }

  @Test
  public void canWriteMessages() throws Exception {
    boolean output = testObj.canWriteMessages(CONSIGNMENT_CODE);

    verify(marketplaceConsignmentService).getMarketplaceConsignmentForCode(CONSIGNMENT_CODE);
    verify(marketplaceConsignmentMessagesStrategy).canWriteMessages(marketplaceConsignment);
    assertThat(output).isTrue();
  }

  @Test
  public void getMessagesForConsignment() throws Exception {
    List<MessageData> output = testObj.getMessagesForConsignment(CONSIGNMENT_CODE);

    verify(marketplaceConsignmentMessagesService).getMessagesForConsignment(CONSIGNMENT_CODE);
    verify(orderMessageConverter).convertAll(anyListOf(MiraklOrderMessage.class));

    assertThat(output).containsOnly(message2, message1);
  }

  @Test
  public void postMessageForConsignment() throws Exception {
    boolean output = testObj.postMessageForConsignment(CONSIGNMENT_CODE, messageToSend);

    verify(marketplaceConsignmentMessagesStrategy).canWriteMessages(marketplaceConsignment);
    verify(marketplaceConsignmentMessagesService).postMessageForConsignment(CONSIGNMENT_CODE, miraklCreateOrderMessage);
    assertThat(output).isTrue();
  }

  @Test
  public void postMessageForConsignmentWhenCantWriteMessages() throws Exception {
    when(marketplaceConsignmentMessagesStrategy.canWriteMessages(marketplaceConsignment)).thenReturn(false);

    boolean output = testObj.postMessageForConsignment(CONSIGNMENT_CODE, messageToSend);

    verify(marketplaceConsignmentMessagesStrategy).canWriteMessages(marketplaceConsignment);
    assertThat(output).isFalse();
  }
}
