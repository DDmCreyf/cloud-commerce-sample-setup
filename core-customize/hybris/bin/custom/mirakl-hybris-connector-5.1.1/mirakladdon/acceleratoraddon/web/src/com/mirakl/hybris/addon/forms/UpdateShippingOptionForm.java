package com.mirakl.hybris.addon.forms;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

/**
 * Web form holding unqiue pair of shop id and lead time to ship required to update
 * {@link com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee} with new selected shipping option
 */
public class UpdateShippingOptionForm {

  @Min(value = 0, message = "{basket.error.leadTimeToShip.invalid}")
  @Digits(fraction = 0, integer = 10, message = "{basket.error.leadTimeToShip.invalid}")
  private Integer leadTimeToShip;

  private String shopId;

  public Integer getLeadTimeToShip() {
    return leadTimeToShip;
  }

  public void setLeadTimeToShip(Integer leadTimeToShip) {
    this.leadTimeToShip = leadTimeToShip;
  }

  public String getShopId() {
    return shopId;
  }

  public void setShopId(String shopId) {
    this.shopId = shopId;
  }
}
