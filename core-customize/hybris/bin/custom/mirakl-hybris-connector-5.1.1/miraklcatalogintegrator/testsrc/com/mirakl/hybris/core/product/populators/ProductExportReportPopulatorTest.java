package com.mirakl.hybris.core.product.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mmp.domain.product.synchro.MiraklProductSynchroResult;
import com.mirakl.client.mmp.request.catalog.product.MiraklProductSynchroErrorReportRequest;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.jobs.populators.AbstractMiraklJobReportPopulatorTest;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductExportReportPopulatorTest extends AbstractMiraklJobReportPopulatorTest {

  private static final int LINES_READ = 1;
  private static final int LINES_IN_ERROR = 2;
  private static final int LINES_IN_SUCCESS = 3;
  private static final int PRODUCTS_DELETED = 4;
  private static final int PRODUCTS_INSERTED = 5;
  private static final int PRODUCTS_UPDATED = 6;

  @Mock
  private MiraklProductSynchroResult synchroResult;

  @InjectMocks
  private ProductExportReportPopulator testObj;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    when(mmpApi.getProductSynchroErrorReport(new MiraklProductSynchroErrorReportRequest(JOB_ID)))
        .thenReturn(tempFolder.newFile());
  }


  @Test
  public void populate() throws Exception {
    when(synchroResult.getStatus()).thenReturn(MiraklProcessTrackingStatus.COMPLETE);
    when(synchroResult.hasErrorReport()).thenReturn(true);
    when(synchroResult.getLinesRead()).thenReturn(LINES_READ);
    when(synchroResult.getLinesInError()).thenReturn(LINES_IN_ERROR);
    when(synchroResult.getLinesInSuccess()).thenReturn(LINES_IN_SUCCESS);
    when(synchroResult.getProductDeleted()).thenReturn(PRODUCTS_DELETED);
    when(synchroResult.getProductInserted()).thenReturn(PRODUCTS_INSERTED);
    when(synchroResult.getProductUpdated()).thenReturn(PRODUCTS_UPDATED);

    testObj.populate(synchroResult, target);

    assertThat(target.getHasErrorReport()).isTrue();
    assertThat(target.getStatus()).isEqualTo(MiraklExportStatus.COMPLETE);
    assertThat(target.getLinesRead()).isEqualTo(LINES_READ);
    assertThat(target.getLinesInError()).isEqualTo(LINES_IN_ERROR);
    assertThat(target.getLinesInSuccess()).isEqualTo(LINES_IN_SUCCESS);
    assertThat(target.getItemsDeleted()).isEqualTo(PRODUCTS_DELETED);
    assertThat(target.getItemsInserted()).isEqualTo(PRODUCTS_INSERTED);
    assertThat(target.getItemsUpdated()).isEqualTo(PRODUCTS_UPDATED);
  }

}
