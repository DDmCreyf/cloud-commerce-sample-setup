package com.mirakl.hybris.core.order.services;

import java.util.List;

import com.mirakl.client.mmp.domain.reason.MiraklReason;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderMessage;
import com.mirakl.hybris.core.setting.services.ReasonService;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;

public interface IncidentService {

  /**
   * Gets the reasons defined in Mirakl
   *
   * @return a list of MiraklReason from Mirakl
   * @deprecated use {@link ReasonService#getReasons()} instead
   */
  @Deprecated
  List<MiraklReason> getReasons();

  /**
   * Opens an incident for the designated consignment entry
   *
   * @param consignmentEntryCode The code of the consignment entry
   * @param reasonCode The reason of the incident
   * @return the updated {@link ConsignmentEntryModel}
   */
  ConsignmentEntryModel openIncident(String consignmentEntryCode, String reasonCode);

  /**
   * Opens an incident for the designated consignment entry
   *
   * @param consignmentEntryCode The code of the consignment entry
   * @param reasonCode The reason of the incident
   * @param message The message the user wants to post with the incident
   * @return the updated {@link ConsignmentEntryModel}
   * 
   * @deprecated use com.mirakl.hybris.facades.order.IncidentFacade.openIncident(String, String, CreateThreadMessageData) instead
   */
  @Deprecated
  void openIncident(String consignmentEntryCode, String reasonCode, MiraklCreateOrderMessage message);

  /**
   * Closes an incident for the designated consignment entry
   *
   * @param consignmentEntryCode The code of the consignment entry
   * @param reasonCode The reason for the closing of the incident
   * @return the updated {@link ConsignmentEntryModel}
   */
  ConsignmentEntryModel closeIncident(String consignmentEntryCode, String reasonCode);

  /**
   * Closes an incident for the designated consignment entry
   *
   * @param consignmentEntryCode The code of the consignment entry
   * @param reasonCode The reason for the closing of the incident
   * @param message The message the user wants to post with the incident
   * @return the updated {@link ConsignmentEntryModel}
   * 
   * @deprecated use com.mirakl.hybris.facades.order.IncidentFacade.closeIncident(String, String, CreateThreadMessageData) instead
   */
  @Deprecated
  void closeIncident(String consignmentEntryCode, String reasonCode, MiraklCreateOrderMessage message);

}
