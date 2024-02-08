package com.mirakl.hybris.facades.order;

import com.mirakl.hybris.beans.EvaluationData;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public interface MarketplaceConsignmentFacade {

  /**
   * Allows to post an evaluation of a consignment to Mirakl
   *
   * @param consignmentCode The code of the consignment
   * @param evaluationData The filled EvaluationData
   * @param user The logged user
   * @return the evaluationData that was sent
   */
  EvaluationData postEvaluation(String consignmentCode, EvaluationData evaluationData, UserModel user);

  /**
   * Returns the product from the consignment entry
   *
   * @param consignmentEntryCode The code of the consignment Entry
   * @return The product data
   */
  ProductData getProductForConsignmentEntry(String consignmentEntryCode);

  /**
   * Sends the client's reception confirmation of the consignment to Mirakl
   *
   * @param code the code of the consignment
   * @param currentCustomer the currently connected customer
   * @return the updated ConsignmentData
   * @throws UnknownIdentifierException if no matching consignment can be found or if the currentCustomer does not own the
   *         consignment
   * @throws IllegalStateException if the consignment can not be received in its current state
   */
  ConsignmentData confirmConsignmentReceptionForCode(String code, UserModel currentCustomer);

}
