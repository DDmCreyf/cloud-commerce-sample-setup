package com.mirakl.hybris.addon.forms;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class ThreadMessageForm {

  private String topicCode;
  private String topicCodeDisplayValue;
  private String topicValue;
  private String body;
  private String to;
  private String consignmentCode;
  private List<MultipartFile> files;

  public String getTopicValue() {
    return topicValue;
  }

  public void setTopicValue(String topicValue) {
    this.topicValue = topicValue;
  }

  public String getTopicCode() {
    return topicCode;
  }

  public void setTopicCode(String topicCode) {
    this.topicCode = topicCode;
  }

  public String getTopicCodeDisplayValue() {
    return topicCodeDisplayValue;
  }

  public void setTopicCodeDisplayValue(String topicCodeDisplayValue) {
    this.topicCodeDisplayValue = topicCodeDisplayValue;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getBody() {
    return body;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public String getConsignmentCode() {
    return consignmentCode;
  }

  public void setConsignmentCode(String consignmentCode) {
    this.consignmentCode = consignmentCode;
  }

  public List<MultipartFile> getFiles() {
    return files;
  }

  public void setFiles(List<MultipartFile> files) {
    this.files = files;
  }

}
