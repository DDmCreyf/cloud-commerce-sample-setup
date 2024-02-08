package com.mirakl.hybris.facades.order.converters.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.message.MiraklMessageDocument;
import com.mirakl.hybris.beans.DocumentData;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MessageDocumentPopulatorTest {

  private static final String DOCUMENT_ID = "1025";
  private static final String DOCUMENT_FILE_NAME = "file_name.pdf";
  private static final Long DOCUMENT_FILE_SIZE = 1978650L;
  private static final String DOCUMENT_FILE_TYPE = "application/PDF";
  private static final Date DOCUMENT_DATE_UPLOADED = new Date(1000);

  @Mock
  private MiraklMessageDocument miraklMessageDocument;

  @InjectMocks
  private MessageDocumentPopulator testObj;

  @Test
  public void populate() {
    when(miraklMessageDocument.getId()).thenReturn(DOCUMENT_ID);
    when(miraklMessageDocument.getDateUploaded()).thenReturn(DOCUMENT_DATE_UPLOADED);
    when(miraklMessageDocument.getFileName()).thenReturn(DOCUMENT_FILE_NAME);
    when(miraklMessageDocument.getFileSize()).thenReturn(DOCUMENT_FILE_SIZE);
    when(miraklMessageDocument.getType()).thenReturn(DOCUMENT_FILE_TYPE);
    DocumentData output = new DocumentData();

    testObj.populate(miraklMessageDocument, output);

    assertThat(output.getCode()).isEqualTo(DOCUMENT_ID);
    assertThat(output.getDateUploaded()).isEqualTo(DOCUMENT_DATE_UPLOADED);
    assertThat(output.getFileName()).isEqualTo(DOCUMENT_FILE_NAME);
    assertThat(output.getFileSize()).isEqualTo(DOCUMENT_FILE_SIZE);
    assertThat(output.getType()).isEqualTo(DOCUMENT_FILE_TYPE);
  }
}
