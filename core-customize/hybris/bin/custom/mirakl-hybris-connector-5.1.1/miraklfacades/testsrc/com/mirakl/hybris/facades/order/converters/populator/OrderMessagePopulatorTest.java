package com.mirakl.hybris.facades.order.converters.populator;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.MiraklUserType;
import com.mirakl.client.mmp.domain.message.MiraklMessageDocument;
import com.mirakl.client.mmp.domain.message.MiraklMessageUserSender;
import com.mirakl.client.mmp.domain.message.MiraklOrderMessage;
import com.mirakl.hybris.beans.DocumentData;
import com.mirakl.hybris.beans.MessageData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderMessagePopulatorTest {

  private static final String USERNAME = "Username";
  private static final String MESSAGE_SUBJECT = "My parcel is damaged";
  private static final String MESSAGE_BODY = "I would like a refund. Is it possible ?";
  private static final Date MESSAGE_CREATION_DATE = new Date(1000);

  @Mock
  MiraklOrderMessage miraklOrderMessage;

  @Mock
  MiraklMessageUserSender miraklMessageUserSender;

  @Mock
  Converter<MiraklMessageDocument, DocumentData> documentConverter;

  @Mock
  MiraklMessageDocument messageDocument1, messageDocument2;

  @Mock
  DocumentData documentData1, documentData2;

  @InjectMocks
  OrderMessagePopulator testObj;

  @Test
  public void populate() throws Exception {
    MessageData output = new MessageData();
    when(miraklOrderMessage.getUserSender()).thenReturn(miraklMessageUserSender);
    when(miraklMessageUserSender.getName()).thenReturn(USERNAME);
    when(miraklMessageUserSender.getType()).thenReturn(MiraklUserType.CUSTOMER);
    when(miraklOrderMessage.getSubject()).thenReturn(MESSAGE_SUBJECT);
    when(miraklOrderMessage.getBody()).thenReturn(MESSAGE_BODY);
    when(miraklOrderMessage.getDateCreated()).thenReturn(MESSAGE_CREATION_DATE);
    when(miraklOrderMessage.getDocuments()).thenReturn(asList(messageDocument1, messageDocument2));
    when(documentConverter.convertAll(asList(messageDocument1, messageDocument2)))
        .thenReturn(asList(documentData1, documentData2));

    testObj.populate(miraklOrderMessage, output);

    assertThat(output.getAuthor()).isEqualTo(USERNAME);
    assertThat(output.getSubject()).isEqualTo(MESSAGE_SUBJECT);
    assertThat(output.getBody()).isEqualTo(MESSAGE_BODY);
    assertThat(output.getDateCreated()).isEqualTo(MESSAGE_CREATION_DATE);
    assertThat(output.getIsFromCustomer()).isTrue();
    assertThat(output.getDocuments()).containsExactly(documentData1, documentData2);
  }
}
