package com.mirakl.hybris.advancedpricing.product.utils;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.mirakl.hybris.core.model.OfferPricingModel;
import com.mirakl.hybris.core.product.decorators.MiraklAsyncOfferPriceDecorator;

public class OfferPricingKey {

  protected static final char SEPARATOR_CHAR = '|';
  protected static final char ESCAPING_CHAR = '\\';

  protected String offerId;
  protected String channel;
  protected String customerGroup;
  protected String customerOrg;
  protected Date startDate;
  protected Date endDate;

  private OfferPricingKey() {}

  public OfferPricingKey(OfferPricingModel model) {
    this(model.getOfferId(), model.getChannelCode(), model.getCustomerGroupId(), model.getCustomerOrganizationId(),
        model.getStartDate(), model.getEndDate());
  }

  public OfferPricingKey(String offerId, String channel, String customerGroup, String customerOrg,
      MiraklAsyncOfferPriceDecorator price) {
    this(offerId, channel, customerGroup, customerOrg, price.getStartDate(), price.getEndDate());
  }

  public OfferPricingKey(String offerId, String channel, String customerGroup, String customerOrg, Date startDate, Date endDate) {
    this.offerId = offerId;
    this.channel = channel;
    this.customerGroup = customerGroup;
    this.customerOrg = customerOrg;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public OfferPricingKey(String serializedKey) {
    List<String> predicates = getPredicates(serializedKey);
    if (predicates.size() != 6) {
      throw new IllegalStateException("Incomplete key");
    }
    offerId = unescape(predicates.get(0));
    channel = isNotEmpty(predicates.get(1)) ? unescape(predicates.get(1)) : null;
    customerGroup = isNotEmpty(predicates.get(2)) ? unescape(predicates.get(2)) : null;
    customerOrg = isNotEmpty(predicates.get(3)) ? unescape(predicates.get(3)) : null;
    startDate = isNotEmpty(predicates.get(4)) ? new Date(Long.parseLong(predicates.get(4))) : null;
    endDate = isNotEmpty(predicates.get(5)) ? new Date(Long.parseLong(predicates.get(5))) : null;
  }

  private List<String> getPredicates(String serializedKey) {
    List<String> predicates = new ArrayList<>();
    boolean isEscaped = false;
    StringBuilder builder = new StringBuilder();
    for (int offset = 0; offset < serializedKey.length(); offset++) {
      char c = serializedKey.charAt(offset);
      if (!isEscaped) {
        if (c == ESCAPING_CHAR) {
          isEscaped = true;
        } else if (c == SEPARATOR_CHAR) {
          predicates.add(builder.toString());
          builder.setLength(0);
          continue;
        }
      } else {
        if (c != SEPARATOR_CHAR && c != ESCAPING_CHAR) {
          throw new IllegalStateException("Invalid escaped character: " + c);
        }
        isEscaped = false;
      }
      builder.append(c);
    }
    predicates.add(builder.toString());
    return predicates;
  }

  public static OfferPricingKey newOfferPricingKey(String offerId) {
    OfferPricingKey key = new OfferPricingKey();
    key.offerId = offerId;
    return key;
  }

  public OfferPricingKey channel(String channel) {
    this.channel = channel;
    return this;
  }

  public OfferPricingKey customerGroup(String customerGroup) {
    this.customerGroup = customerGroup;
    return this;
  }

  public OfferPricingKey customerOrg(String customerOrg) {
    this.customerOrg = customerOrg;
    return this;
  }

  public OfferPricingKey startDate(Date startDate) {
    this.startDate = startDate;
    return this;
  }

  public OfferPricingKey endDate(Date endDate) {
    this.endDate = endDate;
    return this;
  }

  public OfferPricingKey startDate(String startDate) {
    this.startDate = DateTime.parse(startDate, ISODateTimeFormat.dateTimeParser()).toDate();
    return this;
  }

  public OfferPricingKey endDate(String endDate) {
    this.endDate = DateTime.parse(endDate, ISODateTimeFormat.dateTimeParser()).toDate();
    return this;
  }

  public String getOfferId() {
    return offerId;
  }

  public String getChannel() {
    return channel;
  }

  public String getCustomerGroup() {
    return customerGroup;
  }

  public String getCustomerOrg() {
    return customerOrg;
  }

  public Date getStartDate() {
    return startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public String serialize() {
    StringBuilder keyBuilder = new StringBuilder();
    keyBuilder.append(escape(offerId)).append(SEPARATOR_CHAR);
    if (isNotEmpty(channel)) {
      keyBuilder.append(escape(channel));
    }
    keyBuilder.append(SEPARATOR_CHAR);
    if (isNotEmpty(customerGroup)) {
      keyBuilder.append(escape(customerGroup));
    }
    keyBuilder.append(SEPARATOR_CHAR);
    if (isNotEmpty(customerOrg)) {
      keyBuilder.append(escape(customerOrg));
    }
    keyBuilder.append(SEPARATOR_CHAR);
    if (startDate != null) {
      keyBuilder.append(startDate.getTime());
    }
    keyBuilder.append(SEPARATOR_CHAR);
    if (endDate != null) {
      keyBuilder.append(endDate.getTime());
    }
    return keyBuilder.toString();
  }

  private String escape(String value) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (c == SEPARATOR_CHAR || c == ESCAPING_CHAR) {
        builder.append(ESCAPING_CHAR);
      }
      builder.append(c);
    }
    return builder.toString();
  }

  private String unescape(String value) {
    StringBuilder builder = new StringBuilder();
    boolean isEscaped = false;
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (!isEscaped && c == ESCAPING_CHAR) {
        isEscaped = true;
      } else if (isEscaped) {
        if (c == SEPARATOR_CHAR || c == ESCAPING_CHAR) {
          builder.append(c);
          isEscaped = false;
        } else {
          throw new IllegalStateException("Invalid escaped character: " + c);
        }
      } else {
        builder.append(c);
      }
    }
    return builder.toString();
  }

  @Override
  public String toString() {
    return "OfferPricingKey{" + "offerId='" + offerId + '\'' + ", channel='" + channel + '\'' + ", customerGroup='"
        + customerGroup + '\'' + ", customerOrg='" + customerOrg + '\'' + ", startDate=" + startDate + ", endDate=" + endDate
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferPricingKey that = (OfferPricingKey) o;
    return Objects.equals(offerId, that.offerId) && Objects.equals(channel, that.channel)
        && Objects.equals(customerGroup, that.customerGroup) && Objects.equals(customerOrg, that.customerOrg)
        && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(offerId, channel, customerGroup, customerOrg, startDate, endDate);
  }
}
