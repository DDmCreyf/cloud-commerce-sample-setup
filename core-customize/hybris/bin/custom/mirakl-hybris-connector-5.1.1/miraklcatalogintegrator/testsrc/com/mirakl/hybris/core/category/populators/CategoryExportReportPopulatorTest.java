package com.mirakl.hybris.core.category.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mmp.domain.category.synchro.MiraklCategorySynchroResult;
import com.mirakl.client.mmp.request.catalog.category.MiraklCategorySynchroErrorReportRequest;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.jobs.populators.AbstractMiraklJobReportPopulatorTest;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CategoryExportReportPopulatorTest extends AbstractMiraklJobReportPopulatorTest {

  private static final int LINES_READ = 1;
  private static final int LINES_IN_ERROR = 2;
  private static final int LINES_IN_SUCCESS = 3;
  private static final int CATEGORIES_DELETED = 4;
  private static final int CATEGORIES_INSERTED = 5;
  private static final int CATEGORIES_UPDATED = 6;

  @Mock
  private MiraklCategorySynchroResult synchroResult;

  @InjectMocks
  private CategoryExportReportPopulator exportReportPopulator;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    when(mmpApi.getCategorySynchroErrorReport(new MiraklCategorySynchroErrorReportRequest(JOB_ID)))
        .thenReturn(tempFolder.newFile());
  }

  @Test
  public void populate() throws Exception {
    when(synchroResult.getStatus()).thenReturn(MiraklProcessTrackingStatus.COMPLETE);
    when(synchroResult.hasErrorReport()).thenReturn(true);
    when(synchroResult.getLinesRead()).thenReturn(LINES_READ);
    when(synchroResult.getLinesInError()).thenReturn(LINES_IN_ERROR);
    when(synchroResult.getLinesInSuccess()).thenReturn(LINES_IN_SUCCESS);
    when(synchroResult.getCategoryDeleted()).thenReturn(CATEGORIES_DELETED);
    when(synchroResult.getCategoryInserted()).thenReturn(CATEGORIES_INSERTED);
    when(synchroResult.getCategoryUpdated()).thenReturn(CATEGORIES_UPDATED);

    exportReportPopulator.populate(synchroResult, target);

    assertThat(target.getHasErrorReport()).isTrue();
    assertThat(target.getStatus()).isEqualTo(MiraklExportStatus.COMPLETE);
    assertThat(target.getLinesRead()).isEqualTo(LINES_READ);
    assertThat(target.getLinesInError()).isEqualTo(LINES_IN_ERROR);
    assertThat(target.getLinesInSuccess()).isEqualTo(LINES_IN_SUCCESS);
    assertThat(target.getItemsDeleted()).isEqualTo(CATEGORIES_DELETED);
    assertThat(target.getItemsInserted()).isEqualTo(CATEGORIES_INSERTED);
    assertThat(target.getItemsUpdated()).isEqualTo(CATEGORIES_UPDATED);
  }

}
