package com.mirakl.hybris.core.ordersplitting.services;

import java.util.List;
import java.util.Set;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreatedOrders;
import com.mirakl.client.mmp.front.request.order.evaluation.MiraklCreateOrderEvaluation;
import com.mirakl.hybris.core.enums.MarketplaceConsignmentPaymentStatus;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public interface MarketplaceConsignmentService {

  /**
   * Returns a set of created {@link MarketplaceConsignmentModel}s from Mirakl's response
   *
   * @param order order with marketplace entries
   * @param miraklOrders {@link MiraklCreatedOrders}
   * @return set of {@link MarketplaceConsignmentModel}
   */
  Set<MarketplaceConsignmentModel> createMarketplaceConsignments(OrderModel order, MiraklCreatedOrders miraklOrders);

  /**
   * Loads the stored consignment update if any
   *
   * @param consignmentModel
   * @return a {@link MiraklOrder} corresponding to the received update
   */
  MiraklOrder loadConsignmentUpdate(MarketplaceConsignmentModel consignmentModel);

  /**
   * Stores a received debit request within the matching {@link MarketplaceConsignmentModel}
   *
   * @param miraklOrderPayment
   * @return the updated {@link MarketplaceConsignmentModel}
   * @throws UnknownIdentifierException if no matching consignment can be found
   */
  MarketplaceConsignmentModel storeDebitRequest(MiraklOrderPayment miraklOrderPayment);

  /**
   * Loads the stored debit request if any
   *
   * @param consignmentModel
   * @return a {@link MiraklOrderPayment} corresponding to the received debit request
   */
  MiraklOrderPayment loadDebitRequest(MarketplaceConsignmentModel consignmentModel);

  /**
   * Returns the marketplace consignment with the specified code
   *
   * @param code the code of the consignment
   * @return the consignment with the specified code
   * @throws UnknownIdentifierException if no matching consignment can be found
   */
  MarketplaceConsignmentModel getMarketplaceConsignmentForCode(String code);

  /**
   * Sends the client's reception confirmation of the consignment to Mirakl
   *
   * @param code the code of the consignment
   * @param currentCustomer the currently connected customer
   * @return the updated MarketplaceConsignmentModel
   * @throws UnknownIdentifierException if no matching consignment can be found or if the currentCustomer does not own the
   *         consignment
   * @throws IllegalStateException if the consignment can not be received in its current state
   */
  MarketplaceConsignmentModel confirmConsignmentReceptionForCode(String code, UserModel currentCustomer);


  /**
   * Stores a received update and triggers an event for the consignment process (if any)
   *
   * @param miraklOrder the received update
   * @return the updated consignment
   * @throws UnknownIdentifierException if no matching consignment can be found
   */
  MarketplaceConsignmentModel receiveConsignmentUpdate(MiraklOrder miraklOrder);

  /**
   * Send the client's evaluation of the consignment to Mirakl
   *
   * @param code the code of the consignment
   * @param evaluation the filled evaluation request
   * @param currentCustomer the user evaluating the consignment
   */
  void postEvaluation(String code, MiraklCreateOrderEvaluation evaluation, UserModel currentCustomer);

  /**
   * Invokes Mirakl (OR29) in order to cancel a given consignment
   *
   * @param marketplaceConsignment the marketplace consignment to cancel
   */
  void cancelMarketplaceConsignment(MarketplaceConsignmentModel marketplaceConsignment);


  /**
   * Invokes Mirakl (OR29) in order to cancel a given consignment
   *
   * @param consignmentCode the code of the consignment to cancel
   */
  void cancelMarketplaceConsignmentForCode(String consignmentCode);

  /**
   * Returns the product from the given consignment entry code
   *
   * @param consignmentEntryCode The code of the consignment Entry
   * @return The product model
   */
  ProductModel getProductForConsignmentEntry(String consignmentEntryCode);

  /**
   * Returns the consignmentEntry for the given mirakl order line id
   *
   * @param miraklOrderLineId The mirakl order line id
   * @return The consignment entry model
   */
  ConsignmentEntryModel getConsignmentEntryForMiraklLineId(String miraklOrderLineId);

  /**
   * Performs customer access rights check using the search restriction on marketplace consignments
   *
   * @param consignmentCode the code of the marketplace consignment (= Mirakl order)
   * @throws UnknownIdentifierException if the consignment is not owned by the current customer
   */
  void checkUserAccessRightsForConsignment(String consignmentCode);


  /**
   * Returns the Mirakl consignments for the payment statuses.
   *
   * @param paymentStatuses the payment statuses to search
   * @return the consignments.
   */
  List<MarketplaceConsignmentModel> getMarketplaceConsignmentsForPaymentStatuses(
      Set<MarketplaceConsignmentPaymentStatus> paymentStatuses);

  /**
   * Store consignment custom fields.
   *
   * @param customFields the consignment custom fields
   * @param consignment the consignment
   */
  void storeMarketplaceConsignmentCustomFields(List<MiraklAdditionalFieldValue> customFields,
      MarketplaceConsignmentModel consignment);

  /**
   * Load consignment custom fields.
   *
   * @param consignment the consignment
   * @return the consignment custom fields
   */
  List<MiraklAdditionalFieldValue> loadMarketplaceConsignmentCustomFields(MarketplaceConsignmentModel consignment);

  /**
   * Store consignment entry custom fields.
   *
   * @param customFields the consignment entry custom fields
   * @param entry the consignment entry
   */
  void storeMarketplaceConsignmentEntryCustomFields(List<MiraklAdditionalFieldValue> customFields, ConsignmentEntryModel entry);

  /**
   * Load consignment entry custom fields.
   *
   * @param entry the consignment entry
   * @return the consignment entry custom fields
   */
  List<MiraklAdditionalFieldValue> loadMarketplaceConsignmentEntryCustomFields(ConsignmentEntryModel entry);
}
