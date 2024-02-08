package com.mirakl.hybris.core.order.strategies.calculation.impl;

import com.google.common.base.Optional;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.product.services.MiraklRealtimePriceService;
import com.mirakl.hybris.core.product.strategies.MiraklRealtimeOfferPriceRetrievalMode;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.FindPriceStrategy;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.PriceValue;
import org.springframework.beans.factory.annotation.Required;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isBlank;

public class DefaultMiraklFindPriceStrategy implements FindPriceStrategy {

  protected SessionService sessionService;
  protected SearchRestrictionService searchRestrictionService;
  protected MiraklPriceService miraklPriceService;
  protected JsonMarshallingService jsonMarshallingService;
  protected ShippingFeeService shippingFeeService;
  protected MiraklRealtimeOfferPriceRetrievalMode realtimeOfferDataRetrievalMode;
  protected MiraklRealtimePriceService realtimePriceService;

  @Override
  public PriceValue findBasePrice(final AbstractOrderEntryModel entry) throws CalculationException {
    final String offerId = entry.getOfferId();
    if (isBlank(offerId)) {
      throw new IllegalArgumentException(format("[%s] should only be used for marketplace order entries. No offer id was present on order entry [pk=%s]", this.getClass().getSimpleName(), entry.getPk()));
    }

    final AbstractOrderModel order = entry.getOrder();

    // Check for the price in SH02 saved response
    PriceValue priceValue = getPriceValue(order.getShippingFeesJSON(), offerId, order);
    if (priceValue != null) {
      return priceValue;
    }

    // Check for the price in the synchronous cart calculation saved response
    priceValue = getPriceValue(order.getCartCalculationJSON(), offerId, order);
    if (priceValue != null) {
      return priceValue;
    }

    if (realtimeOfferDataRetrievalMode.isActive()) {
      //get real-time offer pricing
      priceValue = realtimePriceService.getPriceValue(entry);
    } else {
      // fallback on the offer price
      return getPriceValue(entry);
    }

    return priceValue;
  }

  protected PriceValue getPriceValue(final AbstractOrderEntryModel entry) {
    return sessionService.executeInLocalView(new SessionExecutionBody() {
      @Override
      public Object execute() {
        try {
          searchRestrictionService.disableSearchRestrictions();
          return createPriceValue(entry.getOfferId(), entry.getQuantity(), entry.getOrder().getNet().booleanValue());
        } catch (UnknownIdentifierException e) {
          throw new IllegalStateException(e);
        } finally {
          searchRestrictionService.enableSearchRestrictions();
        }
      }
    });
  }

  protected PriceValue getPriceValue(String shippingFeesJSON, final String offerId, final AbstractOrderModel order) {
    if (shippingFeesJSON != null) {
      MiraklOrderShippingFees shippingFees = jsonMarshallingService.fromJson(shippingFeesJSON, MiraklOrderShippingFees.class);
      Optional<MiraklOrderShippingFeeOffer> offerShippingFee = shippingFeeService.extractShippingFeeOffer(offerId, shippingFees);
      if (offerShippingFee.isPresent()) {
        return new PriceValue(order.getCurrency().getIsocode(), offerShippingFee.get().getPrice().doubleValue(), order.getNet().booleanValue());
      }
    }
    return null;
  }

  /**
   * For backward compatibility
   **/
  protected PriceValue createPriceValue(final String offerId, final long quantity, final boolean net) {
    return miraklPriceService.createPriceValue(offerId, quantity, net);
  }

  @Required
  public void setRealtimeOfferDataRetrievalMode(MiraklRealtimeOfferPriceRetrievalMode realtimeOfferDataRetrievalMode) {
    this.realtimeOfferDataRetrievalMode = realtimeOfferDataRetrievalMode;
  }

  @Required
  public void setRealtimePriceService(MiraklRealtimePriceService realtimePriceService) {
    this.realtimePriceService = realtimePriceService;
  }

  @Required
  public void setSessionService(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Required
  public void setSearchRestrictionService(SearchRestrictionService searchRestrictionService) {
    this.searchRestrictionService = searchRestrictionService;
  }

  @Required
  public void setMiraklPriceService(MiraklPriceService miraklPriceService) {
    this.miraklPriceService = miraklPriceService;
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

  @Required
  public void setShippingFeeService(ShippingFeeService shippingFeeService) {
    this.shippingFeeService = shippingFeeService;
  }

}
