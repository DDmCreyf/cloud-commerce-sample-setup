package com.mirakl.hybris.mtc.beans;

import java.io.Serializable;
import java.util.List;

import com.mirakl.client.mmp.front.domain.order.create.MiraklOrderTaxEstimation;

/**
 * A bean used to hold the tax values estimation
 */
public class MiraklTaxEstimation implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<MiraklOrderTaxEstimation> taxEstimations;

  private Long quantity;

  private String currencyIsocode;

  public MiraklTaxEstimation() {
  }

  public MiraklTaxEstimation(List<MiraklOrderTaxEstimation> taxEstimations, Long quantity, String currencyIsocode) {
    this.taxEstimations = taxEstimations;
    this.quantity = quantity;
    this.currencyIsocode = currencyIsocode;
  }

  public void setTaxEstimations(final List<MiraklOrderTaxEstimation> taxEstimations) {
    this.taxEstimations = taxEstimations;
  }

  public List<MiraklOrderTaxEstimation> getTaxEstimations() {
    return taxEstimations;
  }

  public void setQuantity(final Long quantity) {
    this.quantity = quantity;
  }

  public Long getQuantity() {
    return quantity;
  }

  public void setCurrencyIsocode(final String currencyIsocode) {
    this.currencyIsocode = currencyIsocode;
  }

  public String getCurrencyIsocode() {
    return currencyIsocode;
  }

}
