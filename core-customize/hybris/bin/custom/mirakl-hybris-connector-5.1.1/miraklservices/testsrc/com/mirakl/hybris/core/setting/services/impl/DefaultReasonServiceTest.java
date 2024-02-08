package com.mirakl.hybris.core.setting.services.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.reason.MiraklGenericReason;
import com.mirakl.client.mmp.domain.reason.MiraklReason;
import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.reason.MiraklGetReasonsRequest;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultReasonServiceTest {

  private static final Locale LOCALE = Locale.FRANCE;

  @InjectMocks
  private DefaultReasonService reasonService;

  @Captor
  private ArgumentCaptor<MiraklGetReasonsRequest> requestArgumentCaptor;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklApi;
  @Mock
  private List<MiraklGenericReason> miraklGenericReasons;
  private List<MiraklReason> miraklReasons;

  @Before
  public void setUp() throws Exception {
    miraklReasons = asList( //
        reason("code-1", "label-1", MiraklReasonType.OFFER_MESSAGING), //
        reason("code-2", "label-2", MiraklReasonType.OFFER_MESSAGING), //
        reason("code-5", "label-5", MiraklReasonType.INCIDENT_OPEN), //
        reason("code-3", "label-3", MiraklReasonType.OFFER_MESSAGING), //
        reason("code-4", "label-4", MiraklReasonType.INCIDENT_OPEN));
  }

  @Test
  public void shouldGetReasonsWithNoLocale() throws Exception {
    when(miraklApi.getReasons(requestArgumentCaptor.capture())).thenReturn(miraklReasons);

    List<MiraklReason> reasons = reasonService.getReasons();

    MiraklGetReasonsRequest request = requestArgumentCaptor.getValue();
    assertThat(request.getQueryParams().get("locale")).isNull();
    assertThat(reasons).containsExactly(miraklReasons.toArray());
  }

  @Test
  public void shouldGetReasonsWithSpecifiedLocale() throws Exception {
    when(miraklApi.getReasons(requestArgumentCaptor.capture())).thenReturn(miraklReasons);

    List<MiraklReason> reasons = reasonService.getReasons(LOCALE);

    MiraklGetReasonsRequest request = requestArgumentCaptor.getValue();
    assertThat(request.getQueryParams().get("locale")).isEqualTo(LOCALE.toString());
    assertThat(reasons).containsExactly(miraklReasons.toArray());
  }

  @Test
  public void shouldGetReasonsByTypeWithNoLocale() throws Exception {
    when(miraklApi.getReasons(requestArgumentCaptor.capture())).thenReturn(miraklReasons);

    List<MiraklGenericReason> reasons = reasonService.getReasonsByType(MiraklReasonType.OFFER_MESSAGING);

    MiraklGetReasonsRequest request = requestArgumentCaptor.getValue();
    assertThat(request.getQueryParams().get("locale")).isNull();
    assertThat(reasons).hasSize(3);
  }

  @Test
  public void shouldGetReasonsByTypeWithSpecifiedLocale() throws Exception {
    when(miraklApi.getReasons(requestArgumentCaptor.capture())).thenReturn(miraklReasons);

    List<MiraklGenericReason> reasons = reasonService.getReasonsByType(MiraklReasonType.INCIDENT_OPEN, LOCALE);

    MiraklGetReasonsRequest request = requestArgumentCaptor.getValue();
    assertThat(request.getQueryParams().get("locale")).isEqualTo(LOCALE.toString());
    assertThat(reasons).hasSize(2);
  }

  protected MiraklReason reason(String code, String label, MiraklReasonType type) {
    MiraklReason reason = new MiraklReason();
    reason.setCode(code);
    reason.setLabel(label);
    reason.setType(type);
    return reason;
  }

}
