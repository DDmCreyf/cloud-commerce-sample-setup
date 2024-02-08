package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.enums.MiraklProductExportHeader.ACTIVE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ProductExportActivePopulatorTest {

  @InjectMocks
  private ProductExportActivePopulator populator;

  @Mock
  private ProductModel product;

  @Test
  public void shouldSetApprovedProductsToActive() {
    when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);

    Map<String, String> result = new HashMap<>();
    populator.populate(product, result);

    assertThat(result.get(ACTIVE.getCode()), equalTo(TRUE.toString()));
  }

  @Test
  public void shouldSetNonApprovedProductsToInactive() {
    when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.CHECK);
    Map<String, String> result = new HashMap<>();
    populator.populate(product, result);
    assertThat(result.get(ACTIVE.getCode()), equalTo(FALSE.toString()));

    when(product.getApprovalStatus()).thenReturn(ArticleApprovalStatus.UNAPPROVED);
    result = new HashMap<>();
    populator.populate(product, result);
    assertThat(result.get(ACTIVE.getCode()), equalTo(FALSE.toString()));
  }

}
