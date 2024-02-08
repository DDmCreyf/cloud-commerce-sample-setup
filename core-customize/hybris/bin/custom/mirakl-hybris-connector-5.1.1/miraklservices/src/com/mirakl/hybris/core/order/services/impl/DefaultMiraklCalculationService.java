package com.mirakl.hybris.core.order.services.impl;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.order.services.MiraklCalculationService;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.order.strategies.calculation.FindPriceStrategy;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.PriceValue;

public class DefaultMiraklCalculationService extends DefaultCalculationService implements MiraklCalculationService {

  protected CommonI18NService commonI18nService;
  protected FindPriceStrategy miraklFindPriceStrategy;

  @Override
  protected PriceValue findBasePrice(AbstractOrderEntryModel entry) throws CalculationException {
    if (isBlank(entry.getOfferId())) {
      return super.findBasePrice(entry);
    }
    return miraklFindPriceStrategy.findBasePrice(entry);
  }

  @Override
  public double calculateOperatorAmount(OrderModel order) {
    if (isEmpty(order.getOperatorEntries())) {
      return 0D;
    }

    Set<MarketplaceConsignmentModel> marketplaceConsignments = order.getMarketplaceConsignments();
    if (isEmpty(marketplaceConsignments) && isNotEmpty(order.getMarketplaceEntries())) {
      throw new IllegalStateException(
          format("Cannot calculate operator amount for order [%s] if marketplace consignments are not created", order.getCode()));
    }

    double marketplaceTotalPrice = 0D;
    for (MarketplaceConsignmentModel marketplaceConsignment : marketplaceConsignments) {
      marketplaceTotalPrice += marketplaceConsignment.getTotalPrice();
    }

    return commonI18nService.roundCurrency(order.getTotalPrice() - marketplaceTotalPrice, order.getCurrency().getDigits());
  }

  @Override
  public double calculateAlreadyCapturedAmount(OrderModel order) {
    double capturedAmount = 0D;
    Set<PaymentTransactionEntryModel> captureTxEntries = getAcceptedCaptureTransactionEntries(order.getPaymentTransactions());
    if (isEmpty(captureTxEntries)) {
      return capturedAmount;
    }

    for (PaymentTransactionEntryModel entry : captureTxEntries) {
      capturedAmount += entry.getAmount().doubleValue();
    }

    return commonI18nService.roundCurrency(capturedAmount, order.getCurrency().getDigits());
  }

  protected Set<PaymentTransactionEntryModel> getAcceptedCaptureTransactionEntries(List<PaymentTransactionModel> paymentTxs) {
    if (isEmpty(paymentTxs)) {
      return emptySet();
    }

    Iterable<PaymentTransactionEntryModel> acceptedCaptureTxEntries =
        concat(transform(paymentTxs, new Function<PaymentTransactionModel, List<PaymentTransactionEntryModel>>() {
          @Override
          public List<PaymentTransactionEntryModel> apply(PaymentTransactionModel paymentTransaction) {
            return from(paymentTransaction.getEntries()).filter(acceptedCapturesPredicate()).toList();
          }
        }));

    return newHashSet(acceptedCaptureTxEntries);
  }

  protected Predicate<PaymentTransactionEntryModel> acceptedCapturesPredicate() {
    return new Predicate<PaymentTransactionEntryModel>() {

      @Override
      public boolean apply(PaymentTransactionEntryModel entry) {
        return (PaymentTransactionType.PARTIAL_CAPTURE.equals(entry.getType()) //
            || PaymentTransactionType.CAPTURE.equals(entry.getType())) //
            && TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus());
      }
    };
  }

  @Required
  public void setCommonI18nService(CommonI18NService commonI18nService) {
    this.commonI18nService = commonI18nService;
  }

  @Required
  public void setMiraklFindPriceStrategy(FindPriceStrategy miraklFindPriceStrategy) {
    this.miraklFindPriceStrategy = miraklFindPriceStrategy;
  }
}
