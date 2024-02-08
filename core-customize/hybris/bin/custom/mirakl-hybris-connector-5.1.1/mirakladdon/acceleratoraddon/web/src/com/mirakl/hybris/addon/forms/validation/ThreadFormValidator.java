package com.mirakl.hybris.addon.forms.validation;

import static com.mirakl.hybris.addon.constants.MirakladdonWebConstants.*;
import static java.lang.String.valueOf;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import com.mirakl.hybris.addon.constants.MirakladdonWebConstants;
import com.mirakl.hybris.addon.forms.ThreadMessageForm;
import com.mirakl.hybris.core.enums.MiraklThreadParticipantType;

import de.hybris.platform.servicelayer.config.ConfigurationService;

public class ThreadFormValidator implements Validator {

  public static final String ATTACHMENTS_FIELD = "attachments";
  public static final String TO_FIELD = "to";
  public static final String TOPIC_CODE_FIELD = "topicCode";
  public static final String TOPIC_VALUE_FIELD = "topicValue";
  public static final String BODY_FIELD = "body";

  protected ConfigurationService configurationService;

  @Override
  public boolean supports(final Class<?> aClass) {
    return ThreadMessageForm.class.equals(aClass);
  }

  @Override
  public void validate(final Object target, final Errors errors) {
    ThreadMessageForm threadForm = (ThreadMessageForm) target;

    performGlobalValidations(threadForm, errors);
    validateAttachments(threadForm, errors);
    validateRecipients(threadForm, errors);
    validateTopic(threadForm, errors);
    validateBody(threadForm, errors);
  }

  protected void performGlobalValidations(ThreadMessageForm threadForm, Errors errors) {
    if (isBlank(threadForm.getConsignmentCode())) {
      errors.reject(INBOX_EMPTY_CONSIGNMENT_CODE_MESSAGE);
    }
  }

  protected void validateAttachments(ThreadMessageForm form, final Errors errors) {
    List<MultipartFile> attachments = form.getFiles();
    if (isNotEmpty(attachments)) {
      long totalAttachmentsSize = 0;
      for (MultipartFile attachment : attachments) {
        totalAttachmentsSize += attachment.getSize();
      }

      long fileMaxSize = getFileMaxSize();
      if (totalAttachmentsSize > fileMaxSize) {
        errors.rejectValue(ATTACHMENTS_FIELD, ATTACHMENT_MAX_SIZE_MESSAGE, valueOf(fileMaxSize));
      }
    }
  }

  protected long getFileMaxSize() {
    return configurationService.getConfiguration().getLong(ATTACHMENT_MAX_SIZE, 0);
  }

  protected void validateRecipients(ThreadMessageForm form, Errors errors) {
    if (isBlank(form.getTo())) {
      errors.rejectValue(TO_FIELD, INBOX_EMPTY_TO_MESSAGE);
    } else if (!getPossibleRecipientValues().contains(form.getTo())) {
      errors.rejectValue(TO_FIELD, INBOX_INVALID_TO_MESSAGE);
    }
  }

  protected Set<String> getPossibleRecipientValues() {
    Set<String> possibleRecipients = new HashSet<>();
    possibleRecipients.add(MiraklThreadParticipantType.OPERATOR.getCode());
    possibleRecipients.add(MiraklThreadParticipantType.SHOP.getCode());
    possibleRecipients.add(MirakladdonWebConstants.INBOX_OPERATOR_AND_SELLER_RECIPIENT);

    return possibleRecipients;
  }

  protected void validateTopic(ThreadMessageForm threadForm, Errors errors) {
    String topicValue = threadForm.getTopicValue();
    if (isNotBlank(topicValue)) {
      if (topicValue.length() < THREAD_TOPIC_MIN_LENGTH || topicValue.length() > THREAD_TOPIC_MAX_LENGTH) {
        errors.rejectValue(TOPIC_VALUE_FIELD, INBOX_TOPIC_SIZE_MESSAGE);
      }
      return;
    }
    String topicOtherReasonCode = configurationService.getConfiguration().getString(INBOX_TOPIC_CODE_OTHER);
    if (isBlank(threadForm.getTopicCode()) || isBlank(threadForm.getTopicCodeDisplayValue())) {
      errors.rejectValue(TOPIC_CODE_FIELD, INBOX_TOPIC_EMPTY_MESSAGE);
    } else if (threadForm.getTopicCode().equals(topicOtherReasonCode)) {
      errors.rejectValue(TOPIC_VALUE_FIELD, INBOX_TOPIC_EMPTY_MESSAGE);
    }
  }

  protected void validateBody(ThreadMessageForm threadForm, Errors errors) {
    String body = threadForm.getBody();
    if (isBlank(body) || body.length() < THREAD_BODY_MIN_LENGTH || body.length() > THREAD_TOPIC_MAX_LENGTH) {
      errors.rejectValue(BODY_FIELD, INBOX_BODY_SIZE_MESSAGE);
    }
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }
}
