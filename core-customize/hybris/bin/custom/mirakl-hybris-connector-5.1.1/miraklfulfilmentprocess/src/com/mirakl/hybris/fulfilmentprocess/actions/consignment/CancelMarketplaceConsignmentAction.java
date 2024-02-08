package com.mirakl.hybris.fulfilmentprocess.actions.consignment;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.model.MarketplaceConsignmentProcessModel;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;

public class CancelMarketplaceConsignmentAction extends MiraklSubprocessEndAction {

  @Override
  public void executeAction(MarketplaceConsignmentProcessModel process) {
    MarketplaceConsignmentModel consignment = (MarketplaceConsignmentModel) process.getConsignment();
    consignment.setStatus(ConsignmentStatus.CANCELLED);
    processCancelledEntries(consignment);
    getModelService().save(consignment);

    super.executeAction(process);
  }

  protected void processCancelledEntries(MarketplaceConsignmentModel consignment) {
    for (ConsignmentEntryModel consignmentEntry : consignment.getConsignmentEntries()) {
      AbstractOrderEntryModel orderEntry = consignmentEntry.getOrderEntry();
      orderEntry.setQuantityStatus(OrderEntryStatus.DEAD);
      getModelService().save(orderEntry);
    }
  }

}
