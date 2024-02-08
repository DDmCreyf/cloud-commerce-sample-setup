package com.mirakl.hybris.core.order.services;

import java.util.List;

import com.google.common.base.Optional;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeError;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.strategies.ShippingZoneStrategy;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * Service providing access and control on the {@link MiraklOrderShippingFees} received from Mirakl
 */
public interface ShippingFeeService {

  /**
   * Retrieves the shipping fees for the marketplace entries of an order. The shipping zone is determined by the
   * {@link ShippingZoneStrategy#getShippingZoneCode(AbstractOrderModel)} method.
   *
   * @param order the order for which the shipping fees are retrieved
   * @return {@link MiraklOrderShippingFeeOffer}
   */
  MiraklOrderShippingFees getShippingFees(AbstractOrderModel order);

  /**
   * Retrieves the shipping fees for the marketplace entries of an order.
   *
   * @param order            the order for which the shipping fees are retrieved
   * @param shippingZoneCode the shipping zone to be used for the cost calculation.
   * @return {@link MiraklOrderShippingFeeOffer}
   */
  MiraklOrderShippingFees getShippingFees(AbstractOrderModel order, String shippingZoneCode);

  /**
   * Extracts specific {@link MiraklOrderShippingFeeOffer} by offer id from the {@link MiraklOrderShippingFees}
   *
   * @param offerId      offer id
   * @param shippingFees {@link MiraklOrderShippingFees}
   * @return {@link MiraklOrderShippingFeeOffer}
   */
  Optional<MiraklOrderShippingFeeOffer> extractShippingFeeOffer(String offerId, MiraklOrderShippingFees shippingFees);


  /**
   * Extracts {@link MiraklOrderShippingFee} by offerId from {@link MiraklOrderShippingFees}. If the
   * {@link MiraklOrderShippingFee} exists, it returns an {@link Optional} of the {@link MiraklOrderShippingFee}. Otherwise, an
   * absent {@link Optional}
   *
   * @param shippingFees {@link MiraklOrderShippingFees}
   * @param offerId      the offerId
   * @return {@link Optional} of the {@link MiraklOrderShippingFee}
   */
  Optional<MiraklOrderShippingFee> extractOrderShippingFeeForOffer(String offerId, MiraklOrderShippingFees shippingFees);

  /**
   * Extracts specific {@link MiraklOrderShippingFeeError} by offer id from the {@link MiraklOrderShippingFees}
   *
   * @param offerId      offer id
   * @param shippingFees {@link MiraklOrderShippingFees}
   * @return {@link MiraklOrderShippingFeeError}
   */
  Optional<MiraklOrderShippingFeeError> extractShippingFeeError(String offerId, MiraklOrderShippingFees shippingFees);

  /**
   * Returns {@link MiraklOrderShippingFees} stored previously on the {@link AbstractOrderModel}
   *
   * @param abstractOrder {@link AbstractOrderModel}
   * @return {@link MiraklOrderShippingFees}
   */
  MiraklOrderShippingFees getStoredShippingFees(AbstractOrderModel abstractOrder);

  /**
   * Returns {@link MiraklOrderShippingFees} stored previously on the {@link AbstractOrderModel}. If no fees were found, a
   * fallback is done on the {@link AbstractOrderModel#getCartCalculationJSON()}
   *
   * @param abstractOrder
   * @return
   */
  MiraklOrderShippingFees getStoredShippingFeesWithCartCalculationFallback(AbstractOrderModel abstractOrder);

  /**
   * Extracts {@link MiraklOrderShippingFee} by the shop id and lead time to ship from {@link MiraklOrderShippingFees}. If the
   * {@link MiraklOrderShippingFee} exists, it returns an {@link Optional} of the {@link MiraklOrderShippingFee}. Otherwise, an
   * absent {@link Optional}
   *
   * @param miraklOrderShippingFees {@link MiraklOrderShippingFees}
   * @param shopId                  shop id
   * @param leadTimeToShip          lead time to ship in days
   * @return {@link Optional} of the {@link MiraklOrderShippingFee}
   */
  Optional<MiraklOrderShippingFee> extractShippingFeeForShop(MiraklOrderShippingFees miraklOrderShippingFees, String shopId,
      Integer leadTimeToShip);

  /**
   * Extracts all {@link MiraklOrderShippingFeeOffer} for all shops from {@link MiraklOrderShippingFees}
   *
   * @param shippingFees {@link MiraklOrderShippingFees}
   * @return list of all {@link MiraklOrderShippingFeeOffer}
   */
  List<MiraklOrderShippingFeeOffer> extractAllShippingFeeOffers(MiraklOrderShippingFees shippingFees);

  /**
   * Updates selected shipping option of the {@link MiraklOrderShippingFee} with the given shipping option code
   *
   * @param shippingFee        {@link MiraklOrderShippingFee}
   * @param shippingOptionCode new selected shipping option code
   */
  void updateSelectedShippingOption(MiraklOrderShippingFee shippingFee, String shippingOptionCode);

  /**
   * Sets line shipping details for all delivery order entries with offers from the {@link MiraklOrderShippingFees}
   *
   * @param abstractOrder order of which entries should be updated with the shipping details
   * @param shippingRates {@link MiraklOrderShippingFees}
   * @return The updated order entries
   */
  List<AbstractOrderEntryModel> setLineShippingDetails(AbstractOrderModel abstractOrder, MiraklOrderShippingFees shippingRates);

  /**
   * Sets line shipping details for entries matching the given list of {@link MiraklOrderShippingFee}
   *
   * @param order             order of which entries should be updated with the shipping details
   * @param orderShippingFees shipping fees to set
   * @return The updated order entries
   */
  List<AbstractOrderEntryModel> setLineShippingDetails(AbstractOrderModel order, List<MiraklOrderShippingFee> orderShippingFees);

  /**
   * Returns JSON representation of {@link MiraklOrderShippingFees}
   *
   * @param miraklOrderShippingFees {@link MiraklOrderShippingFees}
   * @return JSON representation of {@link MiraklOrderShippingFees}
   */
  String getShippingFeesAsJson(MiraklOrderShippingFees miraklOrderShippingFees);
}
