package com.mirakl.hybris.core.product.services.impl;

import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.comparators.OfferVolumePricingDataComparator;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.product.services.MiraklProductService;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.product.strategies.OfferPricingSelectionStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.product.impl.DefaultPriceService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.PriceValue;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.*;

import static com.mirakl.hybris.core.util.OpenDateRange.dateRange;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class DefaultMiraklPriceService extends DefaultPriceService implements MiraklPriceService {

  private static final Logger LOG = Logger.getLogger(DefaultMiraklPriceService.class);

  protected MiraklProductService miraklProductService;
  protected OfferService offerService;
  protected UserService userService;
  protected OfferPricingSelectionStrategy offerPricingSelectionStrategy;

  @Override
  public List<PriceInformation> getPriceInformationsForProduct(ProductModel product) {
    if (miraklProductService.isSellableByOperator(product)) {
      return super.getPriceInformationsForProduct(product);
    }

    List<OfferModel> sortedOffers = offerService.getSortedOffersForProductCode(product.getCode());
    if (isEmpty(sortedOffers)) {
      return Collections.emptyList();
    }

    return getPriceInformationsForOffer(sortedOffers.get(0));
  }

  @Override
  public List<PriceInformation> getPriceInformationsForOffer(OfferModel offer) {
    OfferPricingData offerPricing = getOfferPricing(offer);
    if (offerPricing == null) {
      return emptyList();
    }
    List<OfferVolumePricingData> volumePrices = offerPricing.getVolumePrices();
    if (isEmpty(volumePrices) || volumePrices.size() == 1) {
      return asList(
          new PriceInformation(new PriceValue(offer.getCurrency().getIsocode(), offerPricing.getPrice().doubleValue(), isNet())));
    }

    List<PriceInformation> priceInformations = new ArrayList<>();

    for (OfferVolumePricingData volumePricing : volumePrices) {
      HashMap<Object, Object> qualifiers = new HashMap<>();
      if (volumePricing.getQuantityThreshold() != null) {
        qualifiers.put(PriceRow.MINQTD, volumePricing.getQuantityThreshold().longValue());
      }
      priceInformations.add(new PriceInformation(qualifiers,
          new PriceValue(offer.getCurrency().getIsocode(), volumePricing.getPrice().doubleValue(), isNet())));
    }
    return priceInformations;
  }

  @Override
  public OfferVolumePricingData getVolumePriceForQuantity(OfferModel offer, long quantity) {
    OfferPricingData offerPricing = getOfferPricing(offer);
    if (offerPricing == null) {
      return null;
    }
    return getVolumePriceForQuantity(offerPricing, quantity);
  }

  @Override
  public OfferVolumePricingData getVolumePriceForQuantity(OfferPricingData offerPricing, long quantity) {
    validateParameterNotNullStandardMessage("offerPricing", offerPricing);

    computeAndPopulateApplicablePrice(offerPricing);
    if (isEmpty(offerPricing.getVolumePrices())) {
      return null;
    }
    return getVolumePriceForQuantity(quantity, offerPricing.getVolumePrices());
  }

  protected OfferVolumePricingData getVolumePriceForQuantity(long quantity, List<OfferVolumePricingData> volumePrices) {
    OfferVolumePricingData applicableVolumePrice = null;
    for (OfferVolumePricingData volumePrice : volumePrices) {
      if (applicableVolumePrice == null || quantity >= volumePrice.getQuantityThreshold()) {
        applicableVolumePrice = volumePrice;
      }
    }
    return applicableVolumePrice;
  }

  @Override
  public BigDecimal getOfferTotalPrice(OfferModel offer, OfferPricingData offerPricing) {
    return getOfferTotalPrice(offerPricing, offer.getMinShippingPrice());
  }

  @Override
  public BigDecimal getOfferTotalPrice(OfferOverviewData offer, OfferPricingData offerPricing) {
    BigDecimal minShippingPrice = offer.getMinShippingPrice() != null ? offer.getMinShippingPrice().getValue() : null;
    return getOfferTotalPrice(offerPricing, minShippingPrice);
  }

  protected BigDecimal getOfferTotalPrice(OfferPricingData offerPricing, BigDecimal minShippingPrice) {
    validateParameterNotNull(offerPricing, "offerPricing must not be null.");
    if (minShippingPrice != null) {
      return minShippingPrice.add(offerPricing.getPrice());
    }
    return offerPricing.getPrice();
  }

  @Override
  public OfferPricingData getOfferPricing(OfferModel offer) {
    OfferPricingData offerPricing = offerPricingSelectionStrategy.selectApplicableOfferPricing(offer);
    if (offerPricing == null) {
      LOG.debug(format("offerPricing is null for OfferModel [offerId=%s]", offer.getId()));
    }
    return offerPricing;
  }

  @Override
  public OfferPricingData getOfferPricing(OfferOverviewData offer) {
    OfferPricingData offerPricing = offerPricingSelectionStrategy.selectApplicableOfferPricing(offerService.loadAllOfferPricings(offer));
    if (offerPricing == null) {
      LOG.debug(format("offerPricing is null for OfferOverviewData [offerId=%s]", offer.getCode()));
    }
    return offerPricing;
  }

  @Override
  public BigDecimal getOfferUnitPriceForQuantity(OfferModel offer, long quantity) {
    OfferPricingData offerPricing = getOfferPricing(offer);
    if (offerPricing == null) {
      return null;
    }
    OfferVolumePricingData volumePrice = getVolumePriceForQuantity(offerPricing, quantity);
    return volumePrice != null ? volumePrice.getPrice() : offerPricing.getPrice();
  }

  @Override
  public boolean isDiscountedPrice(OfferPricingData offerPricing) {
    return isDiscountedPrice(offerPricing.getUnitDiscountPrice(), offerPricing.getDiscountStartDate(),
        offerPricing.getDiscountEndDate());
  }

  protected boolean isDiscountedPrice(BigDecimal discountPrice, Date discountStartDate, Date discountEndDate) {
    if (discountStartDate == null && discountEndDate == null) {
      return discountPrice != null;
    }
    return discountPrice != null && dateRange(discountStartDate, discountEndDate).encloses(new Date());
  }

  @Override
  public BigDecimal getPrice(OfferPricingData offerPricing) {
    computeAndPopulateApplicablePrice(offerPricing);
    return offerPricing.getPrice();
  }

  @Override
  public void computeAndPopulateApplicablePrice(OfferPricingData offerPricing) {
    if (isNotEmpty(offerPricing.getVolumePrices())) {
      List<OfferVolumePricingData> sortedVolumePrices = new ArrayList<>(offerPricing.getVolumePrices());
      sortedVolumePrices.sort(OfferVolumePricingDataComparator.INSTANCE);
      setApplicableVolumePrices(sortedVolumePrices, offerPricing);
      offerPricing.setPrice(getVolumePriceForQuantity(1, sortedVolumePrices).getPrice());
    } else {
      boolean isDiscountedPrice = isDiscountedPrice(offerPricing.getUnitDiscountPrice(), offerPricing.getDiscountStartDate(),
          offerPricing.getDiscountEndDate());
      offerPricing.setPrice(isDiscountedPrice ? offerPricing.getUnitDiscountPrice() : offerPricing.getUnitOriginPrice());
    }

  }

@Override
public PriceValue createPriceValue(final String offerId, final long quantity, final boolean net) {
    OfferModel offer = offerService.getOfferForIdIgnoreSearchRestrictions(offerId);
    BigDecimal price = getOfferUnitPriceForQuantity(offer, quantity);

  return price != null ? new PriceValue(offer.getCurrency().getIsocode(), price.doubleValue(), net) : null;
  }

  protected void setApplicableVolumePrices(List<OfferVolumePricingData> volumePrices, OfferPricingData offerPricing) {
    BigDecimal currentDiscountPrice = null;
    for (OfferVolumePricingData volumePrice : volumePrices) {
      boolean isDiscounted = isDiscountedPrice(volumePrice.getUnitDiscountPrice(), offerPricing.getDiscountStartDate(),
          offerPricing.getDiscountEndDate()) || currentDiscountPrice != null;
      BigDecimal price;
      if (isDiscounted) {
        if (volumePrice.getUnitDiscountPrice() != null) {
          price = volumePrice.getUnitDiscountPrice();
          currentDiscountPrice = volumePrice.getUnitDiscountPrice();
        } else if (currentDiscountPrice != null) {
          price = volumePrice.getUnitOriginPrice().min(currentDiscountPrice);
        } else {
          price = volumePrice.getUnitOriginPrice();
        }
      } else {
        price = volumePrice.getUnitOriginPrice();
      }
      volumePrice.setPrice(price);

      if (currentDiscountPrice != null && volumePrice.getUnitDiscountPrice() == null
          && currentDiscountPrice.compareTo(volumePrice.getUnitOriginPrice()) < 0) {
        volumePrice.setUnitDiscountPrice(currentDiscountPrice);
      }
    }
  }


  protected boolean isNet() {
    final UserModel currentUser = userService.getCurrentUser();
    final User userItem = getModelService().getSource(currentUser);
    final PriceFactory pricefactory = OrderManager.getInstance().getPriceFactory();
    return Boolean.valueOf(pricefactory.isNetUser(userItem));
  }

  @Required
  public void setMiraklProductService(MiraklProductService miraklProductService) {
    this.miraklProductService = miraklProductService;
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Override
  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Required
  public void setOfferPricingSelectionStrategy(OfferPricingSelectionStrategy offerPricingSelectionStrategy) {
    this.offerPricingSelectionStrategy = offerPricingSelectionStrategy;
  }



}
