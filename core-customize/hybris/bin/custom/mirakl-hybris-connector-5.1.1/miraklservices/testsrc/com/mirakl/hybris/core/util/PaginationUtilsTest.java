package com.mirakl.hybris.core.util;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class PaginationUtilsTest {

  @Mock
  private Object obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10;

  @Test
  public void getNumberOfPagesWhenTotalIsGreaterThanPageSizeButNotMultiple() {
    int numberOfPages = PaginationUtils.getNumberOfPages(10, 3);
    assertThat(numberOfPages, equalTo(4));
  }

  @Test
  public void getNumberOfPagesWhenTotalIsGreaterThanPageSizeAndMultiple() {
    int numberOfPages = PaginationUtils.getNumberOfPages(9, 3);
    assertThat(numberOfPages, equalTo(3));
  }

  @Test
  public void getNumberOfPagesWhenTotalIsLessPageSize() {
    int numberOfPages = PaginationUtils.getNumberOfPages(2, 3);
    assertThat(numberOfPages, equalTo(1));
  }

  @Test
  public void getNumberOfPagesWhenTotalIsEqualToPageSize() {
    int numberOfPages = PaginationUtils.getNumberOfPages(3, 3);
    assertThat(numberOfPages, equalTo(1));
  }

  @Test
  public void getPageAtTheBeginning() {
    List<Object> page = PaginationUtils.getPage(0, 3, asList(obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10));

    Assertions.assertThat(page).containsExactly(obj1, obj2, obj3);
  }

  @Test
  public void getPageInTheMiddle() {
    List<Object> page = PaginationUtils.getPage(2, 3, asList(obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10));

    Assertions.assertThat(page).containsExactly(obj7, obj8, obj9);
  }

  @Test
  public void getPageAtTheEnd() {
    List<Object> page = PaginationUtils.getPage(3, 3, asList(obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10));

    Assertions.assertThat(page).containsExactly(obj10);
  }

  @Test
  public void getOutOfBoundPageUp() {
    List<Object> page = PaginationUtils.getSafePage(4, 3, asList(obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10));

    Assertions.assertThat(page).isEmpty();
  }

  @Test
  public void getOutOfBoundPageLow() {
    List<Object> page = PaginationUtils.getSafePage(-1, 3, asList(obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10));

    Assertions.assertThat(page).isEmpty();
  }
}
