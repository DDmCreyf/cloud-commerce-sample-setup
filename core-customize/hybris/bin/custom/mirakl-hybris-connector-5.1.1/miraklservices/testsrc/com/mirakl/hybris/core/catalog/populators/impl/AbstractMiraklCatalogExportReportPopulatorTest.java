package com.mirakl.hybris.core.catalog.populators.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mci.front.domain.common.AbstractMiraklCatalogImportResult;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.jobs.populators.AbstractMiraklJobReportPopulatorTest;

import de.hybris.bootstrap.annotations.UnitTest;

@Ignore
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractMiraklCatalogExportReportPopulatorTest<T extends AbstractMiraklCatalogImportResult>
    extends AbstractMiraklJobReportPopulatorTest {

  @Test
  public void populate() throws Exception {
    when(getImportResult().getImportStatus()).thenReturn(MiraklProcessTrackingStatus.COMPLETE);
    when(getImportResult().hasErrorReport()).thenReturn(true);

    getPopulator().populate(getImportResult(), target);

    assertThat(target.getHasErrorReport()).isTrue();
    assertThat(target.getStatus()).isEqualTo(MiraklExportStatus.COMPLETE);
  }
  
  abstract protected AbstractMiraklCatalogExportReportPopulator<T> getPopulator();

  abstract protected T getImportResult();

}
