package com.mirakl.hybris.mtc.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.order.tax.MiraklOrderTaxAmount;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreateOrderOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.hybris.beans.MiraklTaxValuesData;
import com.mirakl.hybris.core.order.populators.MiraklCreateOrderOfferPopulator;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorEmptyTaxesStrategy;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklCreateOrderWithMiraklTaxConnectorOfferPopulator extends MiraklCreateOrderOfferPopulator
    implements Populator<AbstractOrderEntryModel, MiraklCreateOrderOffer> {

  protected Converter<Pair<MiraklOrderShippingFee, AbstractOrderEntryModel>, MiraklTaxValuesData> absoluteTaxValueConverter;
  protected MiraklTaxConnectorEmptyTaxesStrategy miraklTaxConnectorEmptyTaxesStrategy;
  protected Converter<MiraklTaxValuesData, Pair<List<MiraklOrderTaxAmount>, List<MiraklOrderTaxAmount>>> miraklOrderTaxConnectorTaxAmountConverter;

  protected void populateTaxes(MiraklCreateOrderOffer miraklCreateOrderOffer, AbstractOrderEntryModel orderEntry,
      MiraklOrderShippingFee shippingFee) {
    validateParameterNotNullStandardMessage("miraklCreateOrderOffer", miraklCreateOrderOffer);

    if (shippingFee == null || shippingFee.getOffers() == null || orderEntry == null) {
      miraklCreateOrderOffer.setTaxes(Collections.emptyList());
      miraklCreateOrderOffer.setShippingTaxes(Collections.emptyList());
      return;
    }
    MiraklTaxValuesData taxValuesData = absoluteTaxValueConverter.convert(Pair.of(shippingFee, orderEntry));
    miraklTaxConnectorEmptyTaxesStrategy.resetEmptyTaxValues(taxValuesData);
    Pair<List<MiraklOrderTaxAmount>, List<MiraklOrderTaxAmount>> convert = Pair.of(new ArrayList<>(), new ArrayList<>());
    miraklOrderTaxConnectorTaxAmountConverter.convert(taxValuesData, convert);
    miraklCreateOrderOffer.setTaxes(convert.getLeft());
    miraklCreateOrderOffer.setShippingTaxes(convert.getRight());
  }

  @Required
  public void setMiraklTaxConnectorTaxValuesDataConverter(Converter<Pair<MiraklOrderShippingFee, AbstractOrderEntryModel>, MiraklTaxValuesData> miraklTaxConnectorTaxValuesDataConverter) {
    this.absoluteTaxValueConverter = miraklTaxConnectorTaxValuesDataConverter;
  }

  @Required
  public void setMiraklTaxConnectorEmptyTaxesStrategy(MiraklTaxConnectorEmptyTaxesStrategy miraklTaxConnectorEmptyTaxesStrategy) {
    this.miraklTaxConnectorEmptyTaxesStrategy = miraklTaxConnectorEmptyTaxesStrategy;
  }

  @Required
  public void setMiraklOrderTaxConnectorTaxAmountConverter(Converter<MiraklTaxValuesData, Pair<List<MiraklOrderTaxAmount>, List<MiraklOrderTaxAmount>>> miraklOrderTaxConnectorTaxAmountConverter) {
    this.miraklOrderTaxConnectorTaxAmountConverter = miraklOrderTaxConnectorTaxAmountConverter;
  }
}
