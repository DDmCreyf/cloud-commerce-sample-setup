package com.mirakl.hybris.facades.order.converters.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderMessage;
import com.mirakl.hybris.beans.MessageData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCreateOrderMessagePopulatorTest {

  private static final String AUTHOR_ID = "b3bcad74-1942-43d1-b428-8f7a0351c5cf";
  private static final String MESSAGE_SUBJECT = "My parcel is damaged";
  private static final String MESSAGE_BODY = "I would like a refund. Is it possible ?";
  private static final String CUSTOMER_EMAIL = "chuck.norris@email.com";
  private static final String AUTHOR_FULL_NAME = "Chuck Norris";
  private static final String AUTHOR_FIRST_NAME = "Chuck";
  private static final String AUTHOR_LAST_NAME = "Norris";

  @Mock
  private UserService userService;

  @Mock
  private CustomerNameStrategy customerNameStrategy;

  @Mock
  private MessageData messageData;

  @Mock
  private CustomerModel customerModel;

  @InjectMocks
  private MiraklCreateOrderMessagePopulator testObj;

  @Before
  public void setUp() {
    when(messageData.getAuthorId()).thenReturn(AUTHOR_ID);
    when(userService.getUserForUID(AUTHOR_ID)).thenReturn(customerModel);
    when(messageData.getBody()).thenReturn(MESSAGE_BODY);
    when(messageData.getSubject()).thenReturn(MESSAGE_SUBJECT);
    when(customerModel.getContactEmail()).thenReturn(CUSTOMER_EMAIL);
    when(customerModel.getName()).thenReturn(AUTHOR_FULL_NAME);
    when(customerNameStrategy.splitName(anyString())).thenReturn(new String[] {AUTHOR_FIRST_NAME, AUTHOR_LAST_NAME});
  }

  @Test
  public void populate() throws Exception {
    MiraklCreateOrderMessage output = new MiraklCreateOrderMessage();

    testObj.populate(messageData, output);

    assertThat(output.getBody()).isEqualTo(MESSAGE_BODY);
    assertThat(output.getSubject()).isEqualTo(MESSAGE_SUBJECT);
    assertThat(output.getCustomerEmail()).isEqualTo(CUSTOMER_EMAIL);
    assertThat(output.isToCustomer()).isFalse();
    assertThat(output.isToOperator()).isFalse();
    assertThat(output.isToShop()).isTrue();
    assertThat(output.getCustomerFirstname()).isEqualTo(AUTHOR_FIRST_NAME);
    assertThat(output.getCustomerLastname()).isEqualTo(AUTHOR_LAST_NAME);
  }

}
