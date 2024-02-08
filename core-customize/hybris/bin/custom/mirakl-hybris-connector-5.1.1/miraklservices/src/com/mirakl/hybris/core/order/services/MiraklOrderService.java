package com.mirakl.hybris.core.order.services;

import java.util.List;

import com.mirakl.client.mmp.domain.evaluation.MiraklAssessment;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreatedOrders;
import com.mirakl.client.mmp.front.domain.order.create.MiraklOfferNotShippable;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;

/**
 * Service to manage marketplace order between Mirakl and Operator systems
 */
public interface MiraklOrderService {

  /**
   * Creates marketplace order in Mirakl
   *
   * @param order order with marketplace entries
   * @return {@link MiraklCreatedOrders}
   */
  MiraklCreatedOrders createMarketplaceOrders(OrderModel order);

  /**
   * Returns list of not shippable order entries
   *
   * @param notShippableOffers list of {@link MiraklOfferNotShippable}
   * @param order              order with order offer entries
   * @return not shippable order entries
   */
  List<AbstractOrderEntryModel> extractNotShippableEntries(List<MiraklOfferNotShippable> notShippableOffers,
      AbstractOrderModel order);

  /**
   * Utility method to store the created Marketplace orders as JSON (OR01 payload) in the order.
   *
   * @param order         the order to be updated
   * @param createdOrders the created orders
   * @return the saved payload
   */
  String storeCreatedOrders(AbstractOrderModel order, MiraklCreatedOrders createdOrders);

  /**
   * Utility method to load the created Marketplace orders stored as JSON within an order.
   *
   * @param order the commercial order
   * @return the {@link MiraklCreatedOrders} saved within the order or null if no payload saved
   */
  MiraklCreatedOrders loadCreatedOrders(AbstractOrderModel order);

  /**
   * Validates the Commercial Order by calling OR02.
   *
   * @param order the commercial order to validate
   */
  void validateOrder(AbstractOrderModel order);

  /**
   * Gets the assessments defined in Mirakl
   *
   * @return the assessments
   */
  List<MiraklAssessment> getAssessments();

  /**
   * Updates the cart and the persisted offers with the prices included within shippingFees
   *
   * @param order        the order to update
   * @param shippingFees the offer information from Mirakl (SH02)
   * @return true if any price was modified
   */
  boolean updateOffersPrice(AbstractOrderModel order, MiraklOrderShippingFees shippingFees);


}
