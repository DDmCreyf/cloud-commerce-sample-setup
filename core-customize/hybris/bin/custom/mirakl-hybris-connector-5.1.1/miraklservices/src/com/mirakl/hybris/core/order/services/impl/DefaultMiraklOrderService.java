package com.mirakl.hybris.core.order.services.impl;

import com.google.common.base.Predicate;
import com.mirakl.client.mmp.domain.evaluation.MiraklAssessment;
import com.mirakl.client.mmp.domain.offer.MiraklOffer;
import com.mirakl.client.mmp.domain.offer.price.MiraklOfferPricing;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreateOrder;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreatedOrders;
import com.mirakl.client.mmp.front.domain.order.create.MiraklOfferNotShippable;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.client.mmp.front.request.offer.MiraklGetOfferRequest;
import com.mirakl.client.mmp.front.request.order.worflow.MiraklCreateOrderRequest;
import com.mirakl.client.mmp.front.request.order.worflow.MiraklValidOrderRequest;
import com.mirakl.client.mmp.request.order.evaluation.MiraklGetAssessmentsRequest;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.services.MiraklOrderService;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;

public class DefaultMiraklOrderService implements MiraklOrderService {

  private static final Logger LOG = Logger.getLogger(DefaultMiraklOrderService.class);

  protected ModelService modelService;
  protected JsonMarshallingService jsonMarshallingService;
  protected MiraklMarketplacePlatformFrontApi miraklApi;
  protected Converter<OrderModel, MiraklCreateOrder> miraklCreateOrderConverter;
  protected OfferService offerService;
  protected MiraklPriceService miraklPriceService;
  protected MarketplaceConsignmentService marketplaceConsignmentService;
  protected Converter<MiraklCreatedOrders, AbstractOrderModel> miraklOrderModelConverter;
  protected Converter<MiraklOfferPricing, OfferPricingData> offerPricingToDataConverter;

  @Override
  public MiraklCreatedOrders createMarketplaceOrders(OrderModel order) {
    validateParameterNotNullStandardMessage("order", order);

    MiraklCreateOrder miraklCreateOrder = miraklCreateOrderConverter.convert(order);
    MiraklCreateOrderRequest request = new MiraklCreateOrderRequest(miraklCreateOrder);

    LOG.info(format("Sending order [%s] to Mirakl..", order.getCode()));

    MiraklCreatedOrders createdOrders = miraklApi.createOrder(request);
    storeCreatedOrders(order, createdOrders);

    return createdOrders;
  }

  @Override
  public List<AbstractOrderEntryModel> extractNotShippableEntries(final List<MiraklOfferNotShippable> notShippableOffers,
      AbstractOrderModel order) {
    validateParameterNotNullStandardMessage("notShippableOffers", notShippableOffers);
    validateParameterNotNullStandardMessage("order", order);

    return newArrayList(filter(order.getMarketplaceEntries(), new Predicate<AbstractOrderEntryModel>() {
      @Override
      public boolean apply(AbstractOrderEntryModel orderEntry) {
        for (MiraklOfferNotShippable nonShippableOffer : notShippableOffers) {
          if (nonShippableOffer.getId().equals(orderEntry.getOfferId())) {
            return true;
          }
        }
        return false;
      }
    }));
  }

  @Override
  public String storeCreatedOrders(AbstractOrderModel order, MiraklCreatedOrders createdOrders) {
    validateParameterNotNullStandardMessage("createdOrders", createdOrders);
    validateParameterNotNullStandardMessage("order", order);

    miraklOrderModelConverter.convert(createdOrders, order);
    modelService.save(order);

    return order.getCreatedOrdersJSON();
  }

  @Override
  public MiraklCreatedOrders loadCreatedOrders(AbstractOrderModel order) {
    validateParameterNotNullStandardMessage("order", order);

    if (isBlank(order.getCreatedOrdersJSON())) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(format("No marketplace orders stored for commercial order id [%s].", order.getCode()));
      }
      return null;
    }

    return jsonMarshallingService.fromJson(order.getCreatedOrdersJSON(), MiraklCreatedOrders.class);
  }

  @Override
  public void validateOrder(AbstractOrderModel order) {
    validateParameterNotNullStandardMessage("order", order);

    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Sending validation for order [%s].", order.getCode()));
    }
    miraklApi.validOrder(new MiraklValidOrderRequest(order.getCode()));
  }

  @Override
  public List<MiraklAssessment> getAssessments() {
    return miraklApi.getAssessments(new MiraklGetAssessmentsRequest());
  }

  @Override
  public boolean updateOffersPrice(AbstractOrderModel order, MiraklOrderShippingFees shippingFees) {
    List<ItemModel> modelsToSave = new ArrayList<>();
    Map<String, BigDecimal> updatedOfferPrices = getUpdatedOfferPrices(shippingFees);
    for (AbstractOrderEntryModel marketplaceEntry : order.getMarketplaceEntries()) {
      OfferModel offer;
      try {
        offer = offerService.getOfferForId(marketplaceEntry.getOfferId());
      } catch (UnknownIdentifierException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(format("Offer [%s] is not available anymore", marketplaceEntry.getOfferId()));
        }
        continue;
      }

      BigDecimal updatedOfferPrice = updatedOfferPrices.get(offer.getId());
      BigDecimal cartEntryOfferPrice = BigDecimal.valueOf(marketplaceEntry.getBasePrice());
      if (updatedOfferPrice.compareTo(cartEntryOfferPrice) != 0) {
        BigDecimal savedOfferPrice = miraklPriceService.getOfferUnitPriceForQuantity(offer, marketplaceEntry.getQuantity());
        if (savedOfferPrice.compareTo(updatedOfferPrice) != 0) {
          LOG.info(String.format("The price of offer [%s] has changed [%s -> %s]. Updating models...", offer.getId(),
              cartEntryOfferPrice, updatedOfferPrice));

          MiraklOffer realTimeOffer = miraklApi.getOffer(new MiraklGetOfferRequest(offer.getId()));
          offerService.storeAllOfferPricings(offerPricingToDataConverter.convertAll(realTimeOffer.getAllPrices()), offer);

          modelsToSave.add(offer);
        }
        marketplaceEntry.setBasePrice(updatedOfferPrice.doubleValue());
        modelsToSave.add(marketplaceEntry);
        order.setCalculated(false);
        modelsToSave.add(order);
      }
    }
    if (!isEmpty(modelsToSave)) {
      modelService.saveAll(modelsToSave);
      return true;
    }
    return false;
  }

  protected Map<String, BigDecimal> getUpdatedOfferPrices(MiraklOrderShippingFees shippingFees) {
    Map<String, BigDecimal> updatedOfferPrices = new HashMap<>();
    for (MiraklOrderShippingFee miraklOrder : shippingFees.getOrders()) {
      for (MiraklOrderShippingFeeOffer updatedOffer : miraklOrder.getOffers()) {
        updatedOfferPrices.put(updatedOffer.getId(), updatedOffer.getPrice());
      }
    }
    return updatedOfferPrices;
  }

  @Required
  public void setMiraklApi(MiraklMarketplacePlatformFrontApi miraklApi) {
    this.miraklApi = miraklApi;
  }

  @Required
  public void setMiraklCreateOrderConverter(Converter<OrderModel, MiraklCreateOrder> miraklCreateOrderConverter) {
    this.miraklCreateOrderConverter = miraklCreateOrderConverter;
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setMiraklPriceService(MiraklPriceService miraklPriceService) {
    this.miraklPriceService = miraklPriceService;
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }

  @Required
  public void setMiraklOrderModelConverter(Converter<MiraklCreatedOrders, AbstractOrderModel> miraklOrderModelConverter) {
    this.miraklOrderModelConverter = miraklOrderModelConverter;
  }

  @Required
  public void setOfferPricingToDataConverter(Converter<MiraklOfferPricing, OfferPricingData> offerPricingToDataConverter) {
    this.offerPricingToDataConverter = offerPricingToDataConverter;
  }
}
