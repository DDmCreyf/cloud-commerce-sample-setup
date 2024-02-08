package com.mirakl.hybris.mtc.populators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.domain.order.create.MiraklOrderTaxEstimation;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.hybris.mtc.beans.MiraklTaxEstimation;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.TaxValue;

public class MiraklAbstractOrderEntryTaxesPopulator
    implements Populator<Pair<MiraklOrderShippingFee, MiraklOrderShippingFeeOffer>, AbstractOrderEntryModel> {

  protected Converter<MiraklTaxEstimation, List<TaxValue>> absoluteTaxValueConverter;

  @Override
  public void populate(Pair<MiraklOrderShippingFee, MiraklOrderShippingFeeOffer> shippingFeeOfferPair, AbstractOrderEntryModel orderEntry)
      throws ConversionException {
    String currencyIsocode = orderEntry.getOrder().getCurrency().getIsocode();
    Long quantity = orderEntry.getQuantity();
    if (quantity == null || quantity == 0L) {
      return;
    }
    MiraklOrderShippingFeeOffer miraklOffer = shippingFeeOfferPair.getRight();

    List<TaxValue> taxValues = new ArrayList<>();
    absoluteTaxValueConverter.convert(createMiraklTaxEstimation(miraklOffer.getTaxes(), quantity, currencyIsocode), taxValues);
    absoluteTaxValueConverter.convert(createMiraklTaxEstimation(miraklOffer.getShippingTaxes(), quantity, currencyIsocode), taxValues);
    orderEntry.setTaxValues(taxValues);
  }

  public MiraklTaxEstimation createMiraklTaxEstimation(List<MiraklOrderTaxEstimation> taxes, Long quantity, String currencyIsocode) {
    return new MiraklTaxEstimation(taxes, quantity, currencyIsocode);
  }

  @Required
  public void setMiraklTaxConnectorAbsoluteTaxValueConverter(Converter<MiraklTaxEstimation, List<TaxValue>> miraklTaxConnectorAbsoluteTaxValueConverter) {
    this.absoluteTaxValueConverter = miraklTaxConnectorAbsoluteTaxValueConverter;
  }

}
