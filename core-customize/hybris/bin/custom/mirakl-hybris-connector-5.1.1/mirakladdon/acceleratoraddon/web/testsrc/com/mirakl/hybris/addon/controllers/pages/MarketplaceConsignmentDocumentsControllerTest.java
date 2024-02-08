package com.mirakl.hybris.addon.controllers.pages;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import com.mirakl.hybris.core.order.services.MiraklDocumentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.mirakl.client.mmp.domain.common.FileWrapper;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MarketplaceConsignmentDocumentsControllerTest {

  private static final String CONSIGNMENT_CODE = "ce003377-4117-4517-a28e-3ee1bc7bf684-A";
  private static final String DOCUMENT_ID = "4510";
  private static final String DOCUMENT_FILE_NAME = "order_invoice.pdf";

  @Mock
  private MiraklDocumentService documentService;

  @Mock
  private MarketplaceConsignmentService marketplaceConsignmentService;

  @Mock
  private HttpServletResponse httpServletResponse;

  @Mock
  private FileWrapper documentFileWrapper;

  @Mock
  private File documentFile;

  @InjectMocks
  private MarketplaceConsignmentDocumentsController testObj;

  @Before
  public void setUp() throws Exception {
    when(documentService.downloadDocument(DOCUMENT_ID)).thenReturn(documentFileWrapper);
    when(documentService.downloadDocumentsForMarketplaceConsignment(CONSIGNMENT_CODE)).thenReturn(documentFileWrapper);
    when(documentFileWrapper.getFile()).thenReturn(documentFile);
    when(documentService.getDocumentFileName(CONSIGNMENT_CODE, DOCUMENT_ID)).thenReturn(DOCUMENT_FILE_NAME);
  }

  @Test
  public void downloadDocuments() throws Exception {
    Resource output = testObj.downloadDocuments(CONSIGNMENT_CODE, DOCUMENT_ID, httpServletResponse);

    verify(marketplaceConsignmentService).checkUserAccessRightsForConsignment(CONSIGNMENT_CODE); // Check the user rights
    verify(documentService).downloadDocument(DOCUMENT_ID);
    assertThat(output.getFile()).isEqualTo(documentFile);
  }

  @Test
  public void downloadDocumentsForMarketplaceConsignment() throws Exception {
    FileSystemResource output = testObj.downloadDocumentsForMarketplaceConsignment(CONSIGNMENT_CODE, httpServletResponse);

    verify(marketplaceConsignmentService).checkUserAccessRightsForConsignment(CONSIGNMENT_CODE); // Check the user rights
    verify(documentService).downloadDocumentsForMarketplaceConsignment(CONSIGNMENT_CODE);
    assertThat(output.getFile()).isEqualTo(documentFile);
  }

  @Test
  public void getDocumentFileName() throws Exception {
    String output = testObj.getDocumentFileName(CONSIGNMENT_CODE, DOCUMENT_ID);

    assertThat(output).isEqualTo(DOCUMENT_FILE_NAME);
  }

  @Test
  public void getArchivedDocumentFileName() throws Exception {
    String output = testObj.getDocumentFileName(CONSIGNMENT_CODE, null);

    assertThat(output).isEqualTo(CONSIGNMENT_CODE + ".zip");
  }

}
