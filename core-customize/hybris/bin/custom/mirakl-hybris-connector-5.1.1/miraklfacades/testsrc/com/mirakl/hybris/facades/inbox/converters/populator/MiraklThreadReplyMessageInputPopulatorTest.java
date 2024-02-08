package com.mirakl.hybris.facades.inbox.converters.populator;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.mirakl.client.mmp.request.order.message.MiraklThreadReplyMessageInput;
import com.mirakl.client.mmp.request.order.message.MiraklThreadReplyMessageInput.Recipient;
import com.mirakl.client.mmp.request.order.message.MiraklThreadTopic;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.ThreadRecipientData;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklThreadReplyMessageInputPopulatorTest {

  @InjectMocks
  private MiraklThreadReplyMessageInputPopulator populator;

  private CreateThreadMessageData messageData;
  private MiraklThreadReplyMessageInput messageInput;

  @Before
  public void setUp() throws Exception {
    messageInput = new MiraklThreadReplyMessageInput();
    messageData = new CreateThreadMessageData();
  }

  @Test
  public void shouldPopulate() {
    messageData.setBody("Message body");
    messageData.setTopic("Topic");
    ThreadRecipientData recipient1 = new ThreadRecipientData();
    ThreadRecipientData recipient2 = new ThreadRecipientData();
    messageData.setTo(Sets.newHashSet(recipient1, recipient2));

    populator.populate(messageData, messageInput);

    assertThat(messageInput.getBody()).isEqualTo(messageData.getBody());
    List<Recipient> to = messageInput.getTo();
    assertThat(to).isNotEmpty();
    assertThat(to).hasSize(messageData.getTo().size());
    MiraklThreadTopic topic = messageInput.getTopic();
    assertThat(topic).isNotNull();
    assertThat(topic.getValue()).isEqualTo(messageData.getTopic());
  }

  @Test
  public void shouldNotPopulateTopicIfEmpty() {
    messageData.setBody("Message body");
    messageData.setTo(Sets.newHashSet(new ThreadRecipientData()));

    populator.populate(messageData, messageInput);

    assertThat(messageInput.getTopic()).isNull();
  }

}
