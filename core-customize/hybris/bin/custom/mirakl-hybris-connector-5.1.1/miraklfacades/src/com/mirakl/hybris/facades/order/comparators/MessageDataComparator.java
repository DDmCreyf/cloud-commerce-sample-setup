package com.mirakl.hybris.facades.order.comparators;

import java.util.Comparator;

import com.mirakl.hybris.beans.MessageData;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class MessageDataComparator implements Comparator<MessageData> {
  @Override
  public int compare(MessageData o1, MessageData o2) {
    return o1.getDateCreated().compareTo(o2.getDateCreated());
  }
}
