package com.mirakl.hybris.core.jobs.dao.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklJobReportDaoTest {

  @InjectMocks
  private DefaultMiraklJobReportDao testObj = new DefaultMiraklJobReportDao();

  @Mock
  private FlexibleSearchService flexibleSearchServiceMock;

  @Mock
  private MiraklJobReportModel jobReportMock;

  @Before
  public void setUp() {
    when(flexibleSearchServiceMock.search(any(FlexibleSearchQuery.class)))
            .thenReturn(new SearchResultImpl<>(Collections.<Object>singletonList(jobReportMock), 1, 0, 0));
  }

  @Test
  public void findsPendingJobReportsForType() {
    List<MiraklJobReportModel> result = testObj.findPendingJobReportsForType(MiraklExportType.ATTRIBUTE_EXPORT);

    assertThat(result).containsOnly(jobReportMock);
  }

  @Test(expected = IllegalArgumentException.class)
  public void findPendingJobReportsForTypeThrowsIllegalArgumentExceptionIfExportTypeIsNull() {
    testObj.findPendingJobReportsForType(null);
  }
}
