/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with
 * hybris.
 *
 *
 */
package com.mirakl.hybris.addon.constants;

/**
 * Global class for all Mirakladdon web constants. You can add global constants for your extension into this class.
 */
public final class MirakladdonWebConstants // NOSONAR
{
  private MirakladdonWebConstants() {
    // empty to avoid instantiating this constant class
  }

  public static final String ORDER_PRICES_CHANGED_MESSAGE = "order.prices.changed";
  public static final String ORDER_CONDITIONS_FRONT_VALIDATION = "mirakl.order.condition.front.validation.enabled";
  public static final String ATTACHMENT_MAX_SIZE = "mirakl.attachment.file.max.size.bytes";
  public static final String ATTACHMENT_MAX_SIZE_MESSAGE = "inbox.thread.form.attachment.size";
  public static final String INBOX_TOPIC_CODE_OTHER = "mirakl.inbox.topic.other.code";
  public static final String INBOX_EMPTY_CONSIGNMENT_CODE_MESSAGE = "inbox.thread.form.consignmentcode.empty";
  public static final String INBOX_EMPTY_TO_MESSAGE = "inbox.thread.form.to.empty";
  public static final String INBOX_INVALID_TO_MESSAGE = "inbox.thread.form.to.invalid";
  public static final String INBOX_BODY_SIZE_MESSAGE = "inbox.thread.form.body.size";
  public static final String INBOX_TOPIC_SIZE_MESSAGE = "inbox.thread.form.topic.size";
  public static final String INBOX_TOPIC_EMPTY_MESSAGE = "inbox.thread.form.topic.empty";
  public static final String INBOX_GENERIC_ERROR_MESSAGE = "inbox.thread.form.sending.genericError";
  public static final String INBOX_OPERATOR_AND_SELLER_RECIPIENT = "BOTH";
  public static final int THREAD_TOPIC_MIN_LENGTH = 3;
  public static final int THREAD_TOPIC_MAX_LENGTH = 200;
  public static final int THREAD_BODY_MIN_LENGTH = 3;
  public static final int THREAD_BODY_MAX_LENGTH = 50000;

}
