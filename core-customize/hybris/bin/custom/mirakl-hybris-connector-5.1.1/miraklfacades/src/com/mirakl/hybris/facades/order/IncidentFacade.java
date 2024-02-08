package com.mirakl.hybris.facades.order;

import java.util.List;

import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.MessageData;
import com.mirakl.hybris.beans.ReasonData;
import com.mirakl.hybris.facades.setting.ReasonFacade;

public interface IncidentFacade {

  /**
   * Get the reasons with the given type as defined in Mirakl
   *
   * @param wantedType the type of reason desired (ex: INCIDENT_OPEN, MESSAGING, ...)
   * @return a list of Reasons
   * @deprecated use {@link ReasonFacade#getReasons(MiraklReasonType)} instead
   */
  @Deprecated
  List<ReasonData> getReasons(MiraklReasonType wantedType);

  /**
   * Open an incident for the designated Consignment Entry Code
   *
   * @param consignmentEntryCode The code of the consignment entry (= Mirakl Order Entry id)
   * @param reasonCode The reason code of the incident
   */
  void openIncident(String consignmentEntryCode, String reasonCode);

  /**
   * Open an incident for the designated Consignment Entry Code
   *
   * @param consignmentEntryCode The code of the consignment entry (= Mirakl Order Entry id)
   * @param reasonCode The reason code of the incident
   * @param createThreadMessageData The message the user wants to post with the incident
   */
  void openIncident(String consignmentEntryCode, String reasonCode, CreateThreadMessageData createThreadMessageData);

  /**
   * Open an incident for the designated Consignment Entry Code
   *
   * @param consignmentEntryCode The code of the consignment entry (= Mirakl Order Entry id)
   * @param reasonCode The reason code of the incident
   * @param message The message the user wants to post with the incident
   *
   * @deprecated use {@link #openIncident(String, String, CreateThreadMessageData)} instead
   */
  @Deprecated
  void openIncident(String consignmentEntryCode, String reasonCode, MessageData message);


  /**
   * Close an incident for the designated Consignment Entry Code
   *
   * @param consignmentEntryCode The code of the consignment entry (= Mirakl Order Entry id)
   * @param reasonCode The reason code of the incident
   */
  void closeIncident(String consignmentEntryCode, String reasonCode);

  /**
   * Close an incident for the designated Consignment Entry Code
   *
   * @param consignmentEntryCode The code of the consignment entry (= Mirakl Order Entry id)
   * @param reasonCode The reason code of the incident
   * @param createThreadMessageData The message the user wants to post with the incident
   */
  void closeIncident(String consignmentEntryCode, String reasonCode, CreateThreadMessageData createThreadMessageData);

  /**
   * Close an incident for the designated Consignment Entry Code
   *
   * @param consignmentEntryCode The code of the consignment entry (= Mirakl Order Entry id)
   * @param reasonCode The reason code of the incident
   * @param message The message the user wants to post with the incident
   *
   * @deprecated use {@link #closeIncident(String, String, CreateThreadMessageData)} instead
   */
  @Deprecated
  void closeIncident(String consignmentEntryCode, String reasonCode, MessageData message);
}
