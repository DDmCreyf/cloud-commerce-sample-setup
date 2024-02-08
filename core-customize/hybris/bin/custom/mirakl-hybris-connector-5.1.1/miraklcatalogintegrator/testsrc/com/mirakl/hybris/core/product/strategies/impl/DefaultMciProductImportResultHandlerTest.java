package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.product.strategies.impl.DefaultMciProductImportResultHandler.PRODUCT_IMPORT_ERROR_LINE_RESULT_HANDLER;
import static com.mirakl.hybris.core.product.strategies.impl.DefaultMciProductImportResultHandler.PRODUCT_IMPORT_SUCCESS_LINE_RESULT_HANDLER;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;

import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mci.front.request.product.MiraklUpdateProductImportStatusRequest;
import com.mirakl.hybris.beans.ProductImportErrorData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportResultData;
import com.mirakl.hybris.beans.ProductImportSuccessData;
import com.mirakl.hybris.core.product.strategies.ProductImportLineResultHandler;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMciProductImportResultHandlerTest {

  private static final String IMPORT_SUCCESS_SUCCESS_FILE_NAME = "import_xxxxx_success.csv";
  private static final String IMPORT_SUCCESS_ERROR_FILE_NAME = "import_xxxxx_error.csv";
  private static final String IMPORT_ID = "123456";

  @InjectMocks
  private DefaultMciProductImportResultHandler testObj;

  private BlockingQueue<ProductImportResultData> importResultQueue;
  private ProductImportResultData terminationSignal;

  @Mock
  private MiraklCatalogIntegrationFrontApi mciApi;
  @Mock
  private BeanFactory beanFactory;
  @Mock
  private ProductImportLineResultHandler<ProductImportSuccessData> successHandler;
  @Mock
  private ProductImportLineResultHandler<ProductImportErrorData> errorHandler;
  @Mock
  private ProductImportFileContextData context;
  @Mock
  private ProductImportErrorData productImportErrorData;
  @Mock
  private ProductImportSuccessData productImportSuccessData;
  @Mock
  private TestingUnknownProductImportData productImportUnknownData;
  @Mock
  private File receivedFile;

  @Before
  public void setUp() throws Exception {
    terminationSignal = new ProductImportSuccessData();
    terminationSignal.setTerminationSignal(true);

    importResultQueue = new ArrayBlockingQueue<>(4);
    importResultQueue.add(productImportSuccessData);
    importResultQueue.add(productImportErrorData);
    importResultQueue.add(productImportUnknownData);
    importResultQueue.add(terminationSignal);

    when(context.getImportResultQueue()).thenReturn(importResultQueue);
    when(context.getMiraklImportId()).thenReturn(IMPORT_ID);

    when(beanFactory.getBean(PRODUCT_IMPORT_SUCCESS_LINE_RESULT_HANDLER, context)).thenReturn(successHandler);
    when(beanFactory.getBean(PRODUCT_IMPORT_ERROR_LINE_RESULT_HANDLER, context)).thenReturn(errorHandler);

    when(successHandler.getFilename()).thenReturn(IMPORT_SUCCESS_SUCCESS_FILE_NAME);
    when(errorHandler.getFilename()).thenReturn(IMPORT_SUCCESS_ERROR_FILE_NAME);
  }

  @Test
  public void shouldHandleImportResults() throws Exception {
    testObj.handleImportResults(context);

    verify(successHandler).handleLineResult(productImportSuccessData);
    verify(errorHandler).handleLineResult(productImportErrorData);
    verify(mciApi).updateProductImportStatus(any(MiraklUpdateProductImportStatusRequest.class));
  }

  public class TestingUnknownProductImportData extends ProductImportResultData {
  }

}
