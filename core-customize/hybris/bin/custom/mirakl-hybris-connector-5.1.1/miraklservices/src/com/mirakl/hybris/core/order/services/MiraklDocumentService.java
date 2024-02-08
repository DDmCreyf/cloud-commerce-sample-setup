package com.mirakl.hybris.core.order.services;

import java.util.Collection;
import java.util.Set;

import com.mirakl.client.domain.common.FileWithContext;
import com.mirakl.client.mmp.domain.common.FileWrapper;
import com.mirakl.client.mmp.domain.order.document.MiraklOrderDocument;

public interface MiraklDocumentService {
  /**
   * Returns the documents attached to the given consignments
   *
   * @param marketplaceConsignmentCodes a collection of marketplace consignments codes (= Mirakl Order ID). Can be empty
   * @return a list of MiraklOrderDocument
   */
  Set<MiraklOrderDocument> getDocumentsForMarketplaceConsignments(Collection<String> marketplaceConsignmentCodes);

  /**
   * Downloads and returns the files packed in a zip archive for the given marketplace consignment code
   *
   * @param marketplaceConsignmentCode the code of the marketplace consignment (= Mirakl Order ID)
   * @return a FileWrapper containing the archive
   */
  FileWrapper downloadDocumentsForMarketplaceConsignment(String marketplaceConsignmentCode);

  /**
   * Downloads and returns the file for the given document ID
   *
   * @param documentId the ID of the document
   * @return a FileWrapper containing the document
   */
  FileWrapper downloadDocument(String documentId);

  /**
   * Returns the name of the document's file in the specified marketplace consignment for a given document ID
   *
   * @param marketplaceConsignmentCode the name of the marketplace consignment containing the document (= Mirakl Order ID)
   * @param documentId the ID of the document
   * @return the name of the file
   */
  String getDocumentFileName(String marketplaceConsignmentCode, String documentId);

  /**
   * Downloads and returns the file for the given attachment id using the Mirakl M13 API
   *
   * @param attachmentId the id of the attachment to download
   * @return a {@link FileWithContext} object containing the file to download
   */
  FileWithContext downloadThreadAttachment(String attachmentId);
}
