package com.mirakl.hybris.facades.setting.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.reason.MiraklGenericReason;
import com.mirakl.client.mmp.domain.reason.MiraklReason;
import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.hybris.beans.ReasonData;
import com.mirakl.hybris.core.setting.services.ReasonService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultReasonFacadeTest {

  private static final Locale LOCALE = Locale.FRANCE;
  private static final String CODE_1 = "code-1";
  private static final String LABEL_1 = "label-1";
  private static final String CODE_2 = "code-2";
  private static final String LABEL_2 = "label-2";

  @InjectMocks
  private DefaultReasonFacade reasonFacade;

  @Mock
  private ReasonService reasonService;
  @Mock
  private Converter<MiraklGenericReason, ReasonData> reasonDataConverter;
  @Mock
  private MiraklReason reason1, reason2;
  @Mock
  private ReasonData reasonData1, reasonData2;
  private List<MiraklGenericReason> reasons;
  private List<ReasonData> reasonData;

  @Before
  public void setUp() throws Exception {
    reasons = asList(reason1, reason2);
    reasonData = asList(reasonData1, reasonData2);
  }

  @Test
  public void shouldGetReasonsByType() throws Exception {
    when(reasonService.getReasonsByType(MiraklReasonType.ORDER_MESSAGING)).thenReturn(reasons);
    when(reasonDataConverter.convertAll(reasons)).thenReturn(reasonData);

    List<ReasonData> reasons = reasonFacade.getReasons(MiraklReasonType.ORDER_MESSAGING);

    assertThat(reasons).containsOnly(reasonData.toArray());
  }

  @Test
  public void shouldGetReasonsByTypeAndLocale() throws Exception {
    when(reasonService.getReasonsByType(MiraklReasonType.ORDER_MESSAGING, LOCALE)).thenReturn(reasons);
    when(reasonDataConverter.convertAll(reasons)).thenReturn(reasonData);

    List<ReasonData> reasons = reasonFacade.getReasons(MiraklReasonType.ORDER_MESSAGING, LOCALE);

    assertThat(reasons).containsOnly(reasonData.toArray());
  }

  @Test
  public void shouldGetReasonsByTypeAsMap() throws Exception {
    when(reasonService.getReasonsByType(MiraklReasonType.ORDER_MESSAGING)).thenReturn(reasons);
    when(reasonDataConverter.convertAll(reasons))
    .thenReturn(Arrays.asList(resaonData(CODE_1, LABEL_1), resaonData(CODE_2, LABEL_2)));

    Map<String, String> reasonsMap = reasonFacade.getReasonsAsMap(MiraklReasonType.ORDER_MESSAGING);

    assertThat(reasonsMap).hasSize(2);
    assertThat(reasonsMap).includes(entry(CODE_1, LABEL_1));
    assertThat(reasonsMap).includes(entry(CODE_2, LABEL_2));
  }

  private ReasonData resaonData(String code, String label) {
    ReasonData reasonData = new ReasonData();
    reasonData.setCode(code);
    reasonData.setLabel(label);
    return reasonData;
  }
}
