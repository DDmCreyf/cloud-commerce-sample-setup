
package com.mirakl.hybris.core.product.decorators;

import static com.mirakl.hybris.core.util.OpenDateRange.dateRange;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOfferPrice;
import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOfferVolumePrice;
import com.mirakl.hybris.core.util.OpenDateRange;

public class MiraklAsyncOfferPriceDecorator {

  private final MiraklAsyncExportOfferPrice priceEntry;
  private final OpenDateRange discountDateRange;

  public MiraklAsyncOfferPriceDecorator(MiraklAsyncExportOfferPrice wrapped) {
    this.priceEntry = Objects.requireNonNull(wrapped);
    this.discountDateRange = dateRange(priceEntry.getDiscountStartDate(), priceEntry.getDiscountEndDate());
  }

  public List<String> getChannelCodes() {
    ArrayList<String> channels = new ArrayList<>();
    if (priceEntry.getContext() == null || isEmpty(priceEntry.getContext().getChannelCodes())) {
      channels.add(null);
      return channels;
    }
    channels.addAll(priceEntry.getContext().getChannelCodes());
    return channels;
  }

  public List<String> getCustomerGroupIds() {
    ArrayList<String> customerGroupIds = new ArrayList<>();
    if (priceEntry.getContext() != null && !isEmpty(priceEntry.getContext().getCustomerGroupIds())) {
      customerGroupIds.addAll(priceEntry.getContext().getCustomerGroupIds());
    }
    return customerGroupIds;
  }

  public List<String> getCustomerOrganizationIds() {
    ArrayList<String> CustomerOrganizationIds = new ArrayList<>();
    if (priceEntry.getContext() != null && !isEmpty(priceEntry.getContext().getCustomerOrganizationIds())) {
      CustomerOrganizationIds.addAll(priceEntry.getContext().getCustomerOrganizationIds());
    }
    return CustomerOrganizationIds;
  }

  public boolean isDefault() {
    return !isChannelSpecific() && !isAdvanced();
  }

  public boolean isChannelSpecific() {
    return priceEntry.getContext() != null && isNotEmpty(priceEntry.getContext().getChannelCodes());
  }

  public boolean isAdvanced() {
    return priceEntry.getContext() != null //
        && (isNotEmpty(priceEntry.getContext().getCustomerGroupIds()) //
            || isNotEmpty(priceEntry.getContext().getCustomerOrganizationIds()) //
            || priceEntry.getContext().getStartDate() != null //
            || priceEntry.getContext().getEndDate() != null);
  }

  public boolean isCustomerSpecific() {
    return priceEntry.getContext() != null //
        && (isNotEmpty(priceEntry.getContext().getCustomerGroupIds()) //
            || isNotEmpty(priceEntry.getContext().getCustomerOrganizationIds()));
  }

  public boolean hasActiveDiscount() {
    return priceEntry.getDiscountPrice() != null && discountDateRange.encloses(new Date());
  }

  public Date getStartDate() {
    return priceEntry.getContext() != null ? priceEntry.getContext().getStartDate() : null;
  }

  public Date getEndDate() {
    return priceEntry.getContext() != null ? priceEntry.getContext().getEndDate() : null;
  }

  public BigDecimal getOriginPrice() {
    return priceEntry.getOriginPrice();
  }

  public BigDecimal getDiscountPrice() {
    return priceEntry.getDiscountPrice();
  }

  public Date getDiscountEndDate() {
    return priceEntry.getDiscountEndDate();
  }

  public Date getDiscountStartDate() {
    return priceEntry.getDiscountStartDate();
  }

  public List<MiraklAsyncExportOfferVolumePrice> getVolumePrices() {
    return priceEntry.getVolumePrices();
  }

  public MiraklAsyncExportOfferPrice getMiraklPriceEntry() {
    return priceEntry;
  }
}
