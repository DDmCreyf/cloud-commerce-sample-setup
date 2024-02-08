package com.mirakl.hybris.core.category.populators;

import static com.mirakl.hybris.core.enums.MiraklCategoryExportHeader.*;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hybris.platform.category.model.CategoryModel;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.CategoryService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CommissionCategoryPopulatorTest {

  private static final String CATEGORY_CODE_VALUE = "categoryCode";
  private static final String CATEGORY_NAME_VALUE = "categoryName";
  private static final String SUPER_CATEGORY_CODE_2 = "superCategoryCode2";

  @InjectMocks
  private CommissionCategoryPopulator testObj;

  @Mock
  private CategoryService categoryService;

  @Mock
  private CategoryModel categoryMock;
  @Mock
  private CategoryModel superCategory1, superCategory2;
  @Mock
  private CategoryModel rootCategory, otherRootCategory;
  @Mock
  private Pair<CategoryModel, Collection<CategoryModel>> categoryPair;

  @Before
  public void setUp() {
    when(categoryPair.getKey()).thenReturn(categoryMock);
    when(categoryPair.getValue()).thenReturn(asList(categoryMock, rootCategory, superCategory2));

    when(categoryMock.getCode()).thenReturn(CATEGORY_CODE_VALUE);
    when(categoryMock.getName(any(Locale.class))).thenReturn(CATEGORY_NAME_VALUE);

    when(categoryMock.getSupercategories()).thenReturn(asList(superCategory1, superCategory2));
    when(superCategory2.getCode()).thenReturn(SUPER_CATEGORY_CODE_2);
  }

  @Test
  public void populateSubCategoryWithFirstSuperCategory() {
    Map<String, String> result = new HashMap<>();

    testObj.populate(categoryPair, result, Collections.singleton(Locale.ENGLISH));

    assertThat(result).isNotEmpty();
    assertThat(result.keySet()).containsOnly(CATEGORY_CODE.getCode(), PARENT_CODE.getCode(), CATEGORY_LABEL.getCode(), CATEGORY_LABEL.getCode(Locale.ENGLISH));

    assertThat(result.get(CATEGORY_CODE.getCode())).isEqualTo(CATEGORY_CODE_VALUE);
    assertThat(result.get(CATEGORY_LABEL.getCode(Locale.ENGLISH))).isEqualTo(CATEGORY_NAME_VALUE);
    assertThat(result.get(PARENT_CODE.getCode())).isEqualTo(SUPER_CATEGORY_CODE_2);
  }

  @Test
  public void populateRootCategoryWithNoSuperCategories() {
    when(categoryMock.getSupercategories()).thenReturn(Collections.<CategoryModel>emptyList());

    Map<String, String> result = new HashMap<>();
    testObj.populate(categoryPair, result, Collections.singleton(Locale.ENGLISH));

    assertThat(result).isNotEmpty();
    assertThat(result.keySet()).containsOnly(CATEGORY_CODE.getCode(), PARENT_CODE.getCode(), CATEGORY_LABEL.getCode(), CATEGORY_LABEL.getCode(Locale.ENGLISH));

    assertThat(result.get(CATEGORY_CODE.getCode())).isEqualTo(CATEGORY_CODE_VALUE);
    assertThat(result.get(CATEGORY_LABEL.getCode(Locale.ENGLISH))).isEqualTo(CATEGORY_NAME_VALUE);
    assertThat(result.get(PARENT_CODE.getCode())).isEqualTo(EMPTY);
  }

  @Test
  public void populateRootCategoryWithNoParentCategoryIfSuperCategoryIsNotInExportedCategories() {
    when(categoryMock.getSupercategories()).thenReturn(Collections.singletonList(superCategory1));

    Map<String, String> result = new HashMap<>();

    testObj.populate(categoryPair, result, Collections.singleton(Locale.ENGLISH));

    assertThat(result).isNotEmpty();
    assertThat(result.keySet()).containsOnly(CATEGORY_CODE.getCode(), PARENT_CODE.getCode(), CATEGORY_LABEL.getCode(), CATEGORY_LABEL.getCode(Locale.ENGLISH));

    assertThat(result.get(CATEGORY_CODE.getCode())).isEqualTo(CATEGORY_CODE_VALUE);
    assertThat(result.get(CATEGORY_LABEL.getCode(Locale.ENGLISH))).isEqualTo(CATEGORY_NAME_VALUE);
    assertThat(result.get(PARENT_CODE.getCode())).isEqualTo(EMPTY);
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfSourceIsNull() {
    testObj.populate(null, Collections.<String, String>emptyMap(), Collections.singleton(Locale.ENGLISH));
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfTargetIsNull() {
    testObj.populate(categoryPair, null, Collections.singleton(Locale.ENGLISH));
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfCategoryPairKeyIsNull() {
    testObj.populate(Pair.<CategoryModel, Collection<CategoryModel>>of(null, Collections.<CategoryModel>emptyList()),
        Collections.<String, String>emptyMap(), Collections.singleton(Locale.ENGLISH));
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfCategoryPairValueIsNull() {
    testObj.populate(Pair.<CategoryModel, Collection<CategoryModel>>of(rootCategory, null),
        Collections.<String, String>emptyMap(), Collections.singleton(Locale.ENGLISH));
  }
}
