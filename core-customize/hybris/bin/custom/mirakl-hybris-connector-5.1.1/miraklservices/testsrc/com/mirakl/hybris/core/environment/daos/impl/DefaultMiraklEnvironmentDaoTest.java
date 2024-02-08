package com.mirakl.hybris.core.environment.daos.impl;

import static java.util.Collections.singletonMap;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
import com.mirakl.hybris.core.model.MiraklEnvironmentModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklEnvironmentDaoTest {

  @InjectMocks
  private DefaultMiraklEnvironmentDao testObj;

  @Mock
  private FlexibleSearchService flexibleSearchService;

  @Mock
  private MiraklEnvironmentModel miraklEnvironmentDefault, miraklEnvironmentDefaultDuplicated, miraklEnvironmentNonDefault;

  @Test
  public void findDefaultMiraklEnvironment() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(ImmutableList.of(miraklEnvironmentDefault), 1, 0, 0));

    List<MiraklEnvironmentModel> miraklEnvironmentList = testObj.find(singletonMap(MiraklEnvironmentModel.DEFAULT, Boolean.TRUE));

    assertThat(miraklEnvironmentList).containsExactly(miraklEnvironmentDefault);
  }

  @Test
  public void findsAllMiraklEnvironments() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(new SearchResultImpl<>(
        ImmutableList.of(miraklEnvironmentDefault, miraklEnvironmentDefaultDuplicated, miraklEnvironmentNonDefault), 3, 0, 0));

    List<MiraklEnvironmentModel> result = testObj.find();

    assertThat(result).containsExactly(miraklEnvironmentDefault, miraklEnvironmentDefaultDuplicated, miraklEnvironmentNonDefault);
  }

  @Test
  public void findNonDefaultMiraklEnvironment() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(ImmutableList.of(miraklEnvironmentNonDefault), 1, 0, 0));

    List<MiraklEnvironmentModel> miraklEnvironmentList =
        testObj.find(singletonMap(MiraklEnvironmentModel.DEFAULT, Boolean.FALSE));

    assertThat(miraklEnvironmentList).containsExactly(miraklEnvironmentNonDefault);
  }

}
