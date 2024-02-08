package com.mirakl.hybris.core.order.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeError;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.client.mmp.front.domain.shipping.MiraklShippingFeeType;
import com.mirakl.client.mmp.front.request.shipping.MiraklGetShippingRatesRequest;
import com.mirakl.client.mmp.front.request.shipping.MiraklOfferQuantityShippingTypeTuple;
import com.mirakl.hybris.core.order.factories.MiraklGetShippingRatesRequestFactory;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.order.strategies.ShippingZoneStrategy;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultShippingFeeService implements ShippingFeeService {

  protected ShippingZoneStrategy shippingZoneStrategy;
  protected JsonMarshallingService jsonMarshallingService;
  protected MiraklMarketplacePlatformFrontApi miraklOperatorApi;
  protected MiraklGetShippingRatesRequestFactory shippingRatesRequestFactory;
  protected Converter<Pair<MiraklOrderShippingFee, MiraklOrderShippingFeeOffer>, AbstractOrderEntryModel> orderEntryShippingConverter;

  @Override
  public MiraklOrderShippingFees getShippingFees(AbstractOrderModel order) {
    return getShippingFees(order, shippingZoneStrategy.getShippingZoneCode(order));
  }

  @Override
  public MiraklOrderShippingFees getShippingFees(AbstractOrderModel order, String shippingZoneCode) {
    List<MiraklOfferQuantityShippingTypeTuple> offerTuples = getOfferTuples(order);
    if (isEmpty(offerTuples)) {
      return null;
    }

    MiraklGetShippingRatesRequest request =
        shippingRatesRequestFactory.createShippingRatesRequest(order, offerTuples, shippingZoneCode);
    return miraklOperatorApi.getShippingRates(request);
  }

  @Override
  public Optional<MiraklOrderShippingFeeOffer> extractShippingFeeOffer(final String offerId,
      MiraklOrderShippingFees shippingFees) {
    validateParameterNotNullStandardMessage("offerId", offerId);
    validateParameterNotNullStandardMessage("shippingFees", shippingFees);

    return Iterables.tryFind(extractAllShippingFeeOffers(shippingFees), new Predicate<MiraklOrderShippingFeeOffer>() {
      @Override
      public boolean apply(MiraklOrderShippingFeeOffer offer) {
        return offerId.equals(offer.getId());
      }
    });
  }

  @Override
  public Optional<MiraklOrderShippingFee> extractOrderShippingFeeForOffer(final String offerId,
      final MiraklOrderShippingFees shippingFees) {
    validateParameterNotNullStandardMessage("offerId", offerId);

    return FluentIterable.from(shippingFees.getOrders()).firstMatch(new Predicate<MiraklOrderShippingFee>() {
      @Override
      public boolean apply(MiraklOrderShippingFee orderShippingFee) {
        for (MiraklOrderShippingFeeOffer miraklOrderShippingFeeOffer : orderShippingFee.getOffers()) {
          if (offerId.equals(miraklOrderShippingFeeOffer.getId())) {
            return true;
          }
        }
        return false;
      }
    });
  }

  @Override
  public Optional<MiraklOrderShippingFeeError> extractShippingFeeError(final String offerId,
      MiraklOrderShippingFees shippingFees) {
    validateParameterNotNullStandardMessage("offerId", offerId);
    validateParameterNotNullStandardMessage("shippingFees", shippingFees);

    return Iterables.tryFind(shippingFees.getErrors(), new Predicate<MiraklOrderShippingFeeError>() {
      @Override
      public boolean apply(MiraklOrderShippingFeeError shippingFeeError) {
        return offerId.equals(shippingFeeError.getOfferId());
      }
    });
  }

  @Override
  public MiraklOrderShippingFees getStoredShippingFees(AbstractOrderModel order) {
    validateParameterNotNullStandardMessage("order", order);

    return jsonMarshallingService.fromJson(order.getShippingFeesJSON(), MiraklOrderShippingFees.class);
  }

  @Override
  public MiraklOrderShippingFees getStoredShippingFeesWithCartCalculationFallback(AbstractOrderModel order) {
    MiraklOrderShippingFees miraklOrderShippingFees = getStoredShippingFees(order);
    if (miraklOrderShippingFees == null) {
      miraklOrderShippingFees = jsonMarshallingService.fromJson(order.getCartCalculationJSON(), MiraklOrderShippingFees.class);
    }
    return miraklOrderShippingFees;
  }

  @Override
  public Optional<MiraklOrderShippingFee> extractShippingFeeForShop(MiraklOrderShippingFees shippingFees, final String shopId,
      final Integer leadTimeToShip) {
    validateParameterNotNullStandardMessage("shippingFees", shippingFees);
    validateParameterNotNullStandardMessage("shopId", shopId);
    validateParameterNotNullStandardMessage("leadTimeToShip", leadTimeToShip);

    return Iterables.tryFind(shippingFees.getOrders(), new Predicate<MiraklOrderShippingFee>() {
      @Override
      public boolean apply(MiraklOrderShippingFee shippingFee) {
        return shopId.equals(shippingFee.getShopId()) && leadTimeToShip.equals(shippingFee.getLeadtimeToShip());
      }
    });
  }

  @Override
  public List<MiraklOrderShippingFeeOffer> extractAllShippingFeeOffers(MiraklOrderShippingFees shippingFees) {
    validateParameterNotNullStandardMessage("shippingFees", shippingFees);

    List<MiraklOrderShippingFeeOffer> allShippingOffers = Lists.newArrayList();

    for (MiraklOrderShippingFee miraklOrderShippingFee : shippingFees.getOrders()) {
      allShippingOffers.addAll(miraklOrderShippingFee.getOffers());
    }
    return allShippingOffers;
  }

  @Override
  public void updateSelectedShippingOption(MiraklOrderShippingFee shippingFee, final String shippingOptionCode) {
    validateParameterNotNullStandardMessage("shippingFee", shippingFee);
    validateParameterNotNullStandardMessage("shippingOptionCode", shippingOptionCode);

    Optional<MiraklShippingFeeType> miraklShippingFeeType =
        Iterables.tryFind(shippingFee.getShippingTypes(), new Predicate<MiraklShippingFeeType>() {
          @Override
          public boolean apply(MiraklShippingFeeType shipping) {
            return shippingOptionCode.equals(shipping.getCode());
          }
        });
    if (miraklShippingFeeType.isPresent()) {
      shippingFee.setSelectedShippingType(miraklShippingFeeType.get());
    }
  }

  @Override
  public List<AbstractOrderEntryModel> setLineShippingDetails(AbstractOrderModel order, MiraklOrderShippingFees shippingRates) {
    validateParameterNotNullStandardMessage("shippingRates", shippingRates);

    return setLineShippingDetails(order, shippingRates.getOrders());
  }


  @Override
  public List<AbstractOrderEntryModel> setLineShippingDetails(AbstractOrderModel order,
      List<MiraklOrderShippingFee> orderShippingFees) {
    validateParameterNotNullStandardMessage("order", order);
    validateParameterNotNullStandardMessage("orderShippingFees", orderShippingFees);

    List<AbstractOrderEntryModel> updatedEntries = new ArrayList<>();

    for (AbstractOrderEntryModel orderEntry : order.getMarketplaceEntries()) {
      for (MiraklOrderShippingFee shippingFee : orderShippingFees) {
        Optional<MiraklOrderShippingFeeOffer> miraklOfferOptional = getOfferFromShippingFee(shippingFee, orderEntry.getOfferId());
        if (miraklOfferOptional.isPresent()) {
          orderEntryShippingConverter.convert(Pair.of(shippingFee, miraklOfferOptional.get()), orderEntry);
          updatedEntries.add(orderEntry);
        }
      }
    }

    return updatedEntries;
  }

  protected Optional<MiraklOrderShippingFeeOffer> getOfferFromShippingFee(MiraklOrderShippingFee shippingFee,
      final String offerId) {
    return Iterables.tryFind(shippingFee.getOffers(), new Predicate<MiraklOrderShippingFeeOffer>() {
      @Override
      public boolean apply(MiraklOrderShippingFeeOffer miraklOffer) {
        return offerId.equals(miraklOffer.getId());
      }
    });
  }

  protected List<MiraklOfferQuantityShippingTypeTuple> getOfferTuples(AbstractOrderModel order) {
    if (order.getMarketplaceEntries().size() == 0) {
      return Collections.emptyList();
    }
    List<MiraklOfferQuantityShippingTypeTuple> offerTuples = new ArrayList<>();

    for (AbstractOrderEntryModel entry : order.getMarketplaceEntries()) {
      offerTuples.add(new MiraklOfferQuantityShippingTypeTuple(entry.getOfferId(), entry.getQuantity().intValue(),
          entry.getLineShippingCode()));
    }
    return offerTuples;
  }

  @Override
  public String getShippingFeesAsJson(MiraklOrderShippingFees miraklOrderShippingFees) {
    validateParameterNotNullStandardMessage("miraklOrderShippingFees", miraklOrderShippingFees);

    return jsonMarshallingService.toJson(miraklOrderShippingFees);
  }

  @Required
  public void setShippingZoneStrategy(ShippingZoneStrategy shippingZoneStrategy) {
    this.shippingZoneStrategy = shippingZoneStrategy;
  }

  @Required
  public void setShippingRatesRequestFactory(MiraklGetShippingRatesRequestFactory shippingRatesRequestFactory) {
    this.shippingRatesRequestFactory = shippingRatesRequestFactory;
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

  @Required
  public void setMiraklOperatorApi(MiraklMarketplacePlatformFrontApi miraklOperatorApi) {
    this.miraklOperatorApi = miraklOperatorApi;
  }

  @Required
  public void setOrderEntryShippingConverter(
      Converter<Pair<MiraklOrderShippingFee, MiraklOrderShippingFeeOffer>, AbstractOrderEntryModel> orderEntryShippingConverter) {
    this.orderEntryShippingConverter = orderEntryShippingConverter;
  }


}
