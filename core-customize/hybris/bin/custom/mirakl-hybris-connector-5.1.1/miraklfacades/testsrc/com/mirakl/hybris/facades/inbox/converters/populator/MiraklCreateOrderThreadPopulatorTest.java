package com.mirakl.hybris.facades.inbox.converters.populator;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderThread;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.ThreadRecipientData;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCreateOrderThreadPopulatorTest {

  @InjectMocks
  private MiraklCreateOrderThreadPopulator populator;

  private CreateThreadMessageData createThreadMessageData;
  private MiraklCreateOrderThread createOrderThread;

  @Before
  public void setUp() throws Exception {
    createThreadMessageData = new CreateThreadMessageData();
    createOrderThread = new MiraklCreateOrderThread();
  }

  @Test
  public void shouldPopulate() {
    createThreadMessageData.setBody("Message body");
    ThreadRecipientData recipient1 = new ThreadRecipientData();
    ThreadRecipientData recipient2 = new ThreadRecipientData();
    createThreadMessageData.setTo(Sets.newHashSet(recipient1, recipient2));

    populator.populate(createThreadMessageData, createOrderThread);

    assertThat(createOrderThread.getBody()).isEqualTo(createThreadMessageData.getBody());
    List<String> to = createOrderThread.getTo();
    assertThat(to).isNotEmpty();
    assertThat(to).hasSize(createThreadMessageData.getTo().size());
    assertThat(createThreadMessageData.getTopic()).isEqualTo(createThreadMessageData.getTopic());
  }

}
