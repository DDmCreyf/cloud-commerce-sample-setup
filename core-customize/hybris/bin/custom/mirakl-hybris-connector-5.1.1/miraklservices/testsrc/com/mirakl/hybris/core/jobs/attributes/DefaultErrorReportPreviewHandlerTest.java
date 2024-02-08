package com.mirakl.hybris.core.jobs.attributes;

import com.mirakl.hybris.core.jobs.attributes.DefaultErrorReportPreviewHandler;
import com.mirakl.hybris.core.model.MiraklJobReportModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.mirakl.hybris.core.jobs.attributes.DefaultErrorReportPreviewHandler.NO_ERROR_REPORT_FOUND;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultErrorReportPreviewHandlerTest {

  private static final String ERROR_REPORT_PREVIEW = "errorReportPreview";

  @InjectMocks
  private DefaultErrorReportPreviewHandler testObj = new DefaultErrorReportPreviewHandler();

  @Mock
  private MediaService mediaServiceMock;

  @Mock
  private MiraklJobReportModel reportMock;
  @Mock
  private MediaModel errorReportMock;

  @Before
  public void setUp() {
    when(mediaServiceMock.getDataFromMedia(errorReportMock)).thenReturn(ERROR_REPORT_PREVIEW.getBytes());
  }

  @Test
  public void getsErrorReportPreview() {
    when(reportMock.getErrorReport()).thenReturn(errorReportMock);

    String result = testObj.get(reportMock);

    assertThat(result).isEqualTo(ERROR_REPORT_PREVIEW);
    verify(mediaServiceMock).getDataFromMedia(errorReportMock);
  }

  @Test
  public void returnsNoErrorReportFoundIfErrorReportIsNull() {
    when(reportMock.getErrorReport()).thenReturn(null);

    String result = testObj.get(reportMock);

    assertThat(result).isEqualTo(NO_ERROR_REPORT_FOUND);
    verify(mediaServiceMock, never()).getDataFromMedia(any(MediaModel.class));
  }

}
