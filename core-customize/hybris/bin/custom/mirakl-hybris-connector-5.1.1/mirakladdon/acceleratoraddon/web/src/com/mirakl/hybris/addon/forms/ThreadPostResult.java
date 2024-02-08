package com.mirakl.hybris.addon.forms;

import java.util.HashMap;
import java.util.Map;

public class ThreadPostResult {

  private boolean submittedSuccessfully;
  private boolean validated;
  private Map<String, String> errorMessages = new HashMap<String, String>();
  private String globalErrorMessage;
  private String threadPageUrl;

  public boolean isSubmittedSuccessfully() {
    return submittedSuccessfully;
  }

  public void setSubmittedSuccessfully(boolean submittedSuccessfully) {
    this.submittedSuccessfully = submittedSuccessfully;
  }

  public boolean isValidated() {
    return validated;
  }

  public void setValidated(boolean validated) {
    this.validated = validated;
  }

  public void setErrorMessages(Map<String, String> errorMessages) {
    this.errorMessages = errorMessages;
  }

  public Map<String, String> getErrorMessages() {
    return errorMessages;
  }

  public String getGlobalErrorMessage() {
    return globalErrorMessage;
  }

  public void setGlobalErrorMessage(String globalErrorMessage) {
    this.globalErrorMessage = globalErrorMessage;
  }

  public String getThreadPageUrl() {
    return threadPageUrl;
  }

  public void setThreadPageUrl(String threadPageUrl) {
    this.threadPageUrl = threadPageUrl;
  }

}
