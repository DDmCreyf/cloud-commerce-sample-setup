package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.utils.MiraklStreamTestUtils.getMiraklStream;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetIntegrationGlobalError;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncDetail;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncError;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncReport;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncResult;
import com.mirakl.client.mci.request.product.MiraklProductDataSheetSyncReportRequest;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.jobs.populators.AbstractMiraklJobReportPopulatorTest;
import com.mirakl.hybris.core.model.MiraklExportProductDataSheetJobReportModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import shaded.com.fasterxml.jackson.databind.ObjectMapper;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductDataSheetExportReportPopulatorTest extends AbstractMiraklJobReportPopulatorTest {

  @InjectMocks
  private ProductDataSheetExportReportPopulator exportReportPopulator;

  @Mock
  private ProductService productService;
  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private Converter<MiraklProductDataSheetSyncDetail, MiraklProductDataSheetSyncDetail> syncDetailErrorConverter;
  @Mock
  private MiraklProductDataSheetSyncResult result;
  @Mock
  private MiraklProductDataSheetSyncReport report;
  @Mock
  private MiraklProductDataSheetSyncDetail syncDetail1, syncDetail2, syncDetail3WithErrors;
  @Mock
  private MiraklProductDataSheetIntegrationGlobalError globalError1, globalError2;
  @Mock
  private MiraklProductDataSheetSyncError syncError;

  private MiraklExportProductDataSheetJobReportModel target;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    target = new MiraklExportProductDataSheetJobReportModel();
    target.setJobId(JOB_ID);
    target.setReportType(MiraklExportType.PRODUCT_DATASHEET_EXPORT);
    when(mciApi.getProductDataSheetsSynchronizationReport(new MiraklProductDataSheetSyncReportRequest(JOB_ID)))
        .thenReturn(report);
  }


  @Test
  public void populate() {
    when(result.getStatus()).thenReturn(MiraklProcessTrackingStatus.COMPLETE);
    when(report.getProcessedItems()).thenReturn(getMiraklStream(syncDetail1, syncDetail2));
    when(report.getGlobalErrors()).thenReturn(getMiraklStream(globalError1, globalError2));

    exportReportPopulator.populate(result, target);

    assertThat(target.getStatus()).isEqualTo(MiraklExportStatus.COMPLETE);
    assertThat(target.getHasErrorReport()).isTrue();
  }

  @Test
  public void populateWithSyncErrors() {
    when(result.getStatus()).thenReturn(MiraklProcessTrackingStatus.COMPLETE);
    when(report.getProcessedItems()).thenReturn(getMiraklStream(syncDetail1, syncDetail2, syncDetail3WithErrors));
    when(syncDetail3WithErrors.getSynchronizationErrors()).thenReturn(Lists.newArrayList(syncError));
    when(report.getGlobalErrors()).thenReturn(getMiraklStream());

    exportReportPopulator.populate(result, target);

    assertThat(target.getStatus()).isEqualTo(MiraklExportStatus.COMPLETE);
    assertThat(target.getHasErrorReport()).isTrue();
  }

  @Test
  public void populateWithGlobalErrors() {
    when(result.getStatus()).thenReturn(MiraklProcessTrackingStatus.COMPLETE);
    when(report.getProcessedItems()).thenReturn(getMiraklStream(syncDetail1, syncDetail2));
    when(report.getGlobalErrors()).thenReturn(getMiraklStream());


    exportReportPopulator.populate(result, target);

    assertThat(target.getStatus()).isEqualTo(MiraklExportStatus.COMPLETE);
    assertThat(target.getHasErrorReport()).isFalse();
  }

}
