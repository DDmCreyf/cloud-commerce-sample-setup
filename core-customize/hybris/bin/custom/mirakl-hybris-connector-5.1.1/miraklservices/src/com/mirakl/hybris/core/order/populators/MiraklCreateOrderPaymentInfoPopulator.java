package com.mirakl.hybris.core.order.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.mirakl.client.mmp.front.domain.order.create.MiraklCreateOrderPaymentInfo;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklCreateOrderPaymentInfoPopulator implements Populator<PaymentInfoModel, MiraklCreateOrderPaymentInfo> {

  @Override
  public void populate(PaymentInfoModel paymentInfoModel, MiraklCreateOrderPaymentInfo miraklCreateOrderPaymentInfo)
      throws ConversionException {
    validateParameterNotNullStandardMessage("paymentInfo", paymentInfoModel);
    validateParameterNotNullStandardMessage("miraklCreateOrderPaymentInfo", miraklCreateOrderPaymentInfo);

    miraklCreateOrderPaymentInfo.setPaymentId(paymentInfoModel.getCode());
    setPaymentType(miraklCreateOrderPaymentInfo, paymentInfoModel);
  }

  protected void setPaymentType(MiraklCreateOrderPaymentInfo miraklCreateOrderPaymentInfo, PaymentInfoModel paymentInfoModel) {
    if (paymentInfoModel instanceof CreditCardPaymentInfoModel) {
      CreditCardType creditCardType = ((CreditCardPaymentInfoModel) paymentInfoModel).getType();
      miraklCreateOrderPaymentInfo.setPaymentType(creditCardType.name());
    }
  }
}
