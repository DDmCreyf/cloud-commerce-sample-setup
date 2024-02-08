package com.mirakl.hybris.addon.controllers.pages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mirakl.client.mmp.domain.common.FileWrapper;
import com.mirakl.hybris.core.order.services.MiraklDocumentService;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.addonsupport.controllers.page.AbstractAddOnPageController;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@Controller
@RequestMapping(value = "/my-account/consignment")
public class MarketplaceConsignmentDocumentsController extends AbstractAddOnPageController {

  protected static final String APPLICATION_CONTENT_TYPE = "application/octet-stream";
  protected static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";

  protected MiraklDocumentService documentService;
  protected MarketplaceConsignmentService marketplaceConsignmentService;

  @ResponseBody
  @RequireHardLogIn
  @RequestMapping(value = "/{consignmentCode:.*}/document/{documentId:.*}", method = RequestMethod.GET)
  public Resource downloadDocuments(@PathVariable("consignmentCode") String consignmentCode,
      @PathVariable("documentId") String documentId, HttpServletResponse response) {
    marketplaceConsignmentService.checkUserAccessRightsForConsignment(consignmentCode);
    FileWrapper fileWrapper = documentService.downloadDocument(documentId);
    response.setContentType(APPLICATION_CONTENT_TYPE);
    response.addHeader(CONTENT_DISPOSITION_HEADER, "attachment; filename=" + getDocumentFileName(consignmentCode, documentId));
    return new FileSystemResource(fileWrapper.getFile());
  }

  @ResponseBody
  @RequireHardLogIn
  @RequestMapping(value = "/{consignmentCode:.*}/documents", method = RequestMethod.GET)
  public FileSystemResource downloadDocumentsForMarketplaceConsignment(@PathVariable("consignmentCode") String consignmentCode,
      HttpServletResponse response) {
    marketplaceConsignmentService.checkUserAccessRightsForConsignment(consignmentCode);
    FileWrapper fileWrapper = documentService.downloadDocumentsForMarketplaceConsignment(consignmentCode);
    response.setContentType(APPLICATION_CONTENT_TYPE);
    response.addHeader(CONTENT_DISPOSITION_HEADER, "attachment; filename=" + getDocumentFileName(consignmentCode));
    return new FileSystemResource(fileWrapper.getFile());
  }

  @ExceptionHandler(UnknownIdentifierException.class)
  public String handleUnknownIdentifierException(final UnknownIdentifierException exception, final HttpServletRequest request) {
    request.setAttribute("message", exception.getMessage());
    return FORWARD_PREFIX + "/404";
  }

  protected String getDocumentFileName(String consignmentCode, String documentId) {
    if (documentId == null) {
      return String.format("%s.zip", consignmentCode);
    }
    return documentService.getDocumentFileName(consignmentCode, documentId);
  }

  protected String getDocumentFileName(String consignmentCode) {
    return getDocumentFileName(consignmentCode, null);
  }

  @Required
  public void setDocumentService(MiraklDocumentService documentService) {
    this.documentService = documentService;
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }
}
