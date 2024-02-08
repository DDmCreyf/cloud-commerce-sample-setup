package com.mirakl.hybris.mtc.populators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.hybris.beans.MiraklTaxValuesData;
import com.mirakl.hybris.mtc.beans.MiraklTaxEstimation;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.TaxValue;

public class MiraklTaxConnectorTaxValuesDataPopulator implements Populator<Pair<MiraklOrderShippingFee,
    AbstractOrderEntryModel>, MiraklTaxValuesData> {

  protected Converter<MiraklTaxEstimation, List<TaxValue>> absoluteTaxValueConverter;

  @Override
  public void populate(Pair<MiraklOrderShippingFee, AbstractOrderEntryModel> source, MiraklTaxValuesData miraklTaxValuesData) throws ConversionException {
    AbstractOrderEntryModel orderEntry = source.getRight();
    MiraklOrderShippingFee shippingFee = source.getLeft();

    if (miraklTaxValuesData.getTaxValues() == null) {
      miraklTaxValuesData.setTaxValues(new ArrayList<>());
    }
    if (miraklTaxValuesData.getShippingTaxValues() == null) {
      miraklTaxValuesData.setShippingTaxValues(new ArrayList<>());
    }

    String currencyIsoCode = orderEntry.getOrder().getCurrency().getIsocode();
    for (MiraklOrderShippingFeeOffer offer : shippingFee.getOffers()) {
      Long quantity = orderEntry.getQuantity();
      if (offer.getId().equals(orderEntry.getOfferId()) && currencyIsoCode != null && quantity != null && quantity != 0L) {
        miraklTaxValuesData.setQuantity(quantity);
        absoluteTaxValueConverter.convert(new MiraklTaxEstimation(offer.getTaxes(), quantity, currencyIsoCode), miraklTaxValuesData.getTaxValues());
        absoluteTaxValueConverter.convert(new MiraklTaxEstimation(offer.getShippingTaxes(), quantity, currencyIsoCode), miraklTaxValuesData.getShippingTaxValues());
        return;
      }
    }
  }

  @Required
  public void setMiraklTaxConnectorAbsoluteTaxValueConverter(Converter<MiraklTaxEstimation, List<TaxValue>> miraklTaxConnectorAbsoluteTaxValueConverter) {
    this.absoluteTaxValueConverter = miraklTaxConnectorAbsoluteTaxValueConverter;
  }

}
