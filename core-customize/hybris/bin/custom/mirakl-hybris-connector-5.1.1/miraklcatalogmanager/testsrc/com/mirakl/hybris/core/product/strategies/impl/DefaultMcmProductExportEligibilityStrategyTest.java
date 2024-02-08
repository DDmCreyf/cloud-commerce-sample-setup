package com.mirakl.hybris.core.product.strategies.impl;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.enums.ProductOrigin;
import com.mirakl.hybris.core.product.daos.impl.DefaultMiraklProductDao;
import com.mirakl.hybris.core.product.strategies.McmProductAcceptanceStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMcmProductExportEligibilityStrategyTest {

  private static final HashSet<String> VALID_APPROVAL_STATUS_CODES =
      newHashSet(ArticleApprovalStatus.APPROVED.name(), ArticleApprovalStatus.UNAPPROVED.name());
  private static final PK CATALOG_VERSION_PK = PK.fromLong(1L);
  private static final PK CATEGORY1_PK = PK.fromLong(2L);
  private static final PK CATEGORY2_PK = PK.fromLong(3L);
  private static final PK CATEGORY3_PK = PK.fromLong(4L);
  private static final Date LAST_EXPORT_DATE = new Date();
  private static final HashSet<ProductOrigin> PRODUCT_ORIGINS = newHashSet(ProductOrigin.OPERATOR);

  @InjectMocks
  private DefaultMcmProductExportEligibilityStrategy strategy;

  @Mock
  private ModelService modelService;
  @Mock
  private McmProductAcceptanceStrategy productAcceptanceStrategy;
  @Mock
  private DefaultMiraklProductDao customProductDao;
  @Mock
  private CatalogVersionModel catalogVersion;
  @Mock
  private ProductDataSheetExportContextData productExportContext;
  @Mock
  private ProductModel product1, product2, product3, product4;
  @Mock
  private VariantProductModel variantProduct1;
  @Mock
  private CategoryModel category1, category2, category3;
  @Captor
  private ArgumentCaptor<Set<ArticleApprovalStatus>> approvalStatusesCaptor;

  @Before
  public void setUp() {
    when(productExportContext.getProductOrigins()).thenReturn(PRODUCT_ORIGINS);
    when(productExportContext.getProductCatalogVersion()).thenReturn(CATALOG_VERSION_PK);
    when(modelService.get(CATALOG_VERSION_PK)).thenReturn(catalogVersion);
    when(productExportContext.getModifiedAfter()).thenReturn(LAST_EXPORT_DATE);
    when(product1.getSupercategories()).thenReturn(Collections.singleton(category1));
    when(product2.getSupercategories()).thenReturn(Collections.singleton(category2));
    when(product3.getSupercategories()).thenReturn(Lists.newArrayList(category2, category3));
    when(category1.getPk()).thenReturn(CATEGORY1_PK);
    when(category2.getPk()).thenReturn(CATEGORY2_PK);
    when(category3.getPk()).thenReturn(CATEGORY3_PK);
    when(productExportContext.getAllExportableCategories()).thenReturn(Sets.newHashSet(CATEGORY1_PK, CATEGORY3_PK));
    when(variantProduct1.getBaseProduct()).thenReturn(product4);
    when(product4.getSupercategories()).thenReturn(Collections.singleton(category3));
  }

  @Test
  public void shouldReturnEligibleProducts() {
    when(productAcceptanceStrategy.getMappableApprovalStatusCodes()).thenReturn(VALID_APPROVAL_STATUS_CODES);

    when(customProductDao.findModifiedProductsWithNoVariantType(eq(LAST_EXPORT_DATE), eq(catalogVersion), eq(PRODUCT_ORIGINS),
        approvalStatusesCaptor.capture())).thenReturn(asList(product1, product2, product3, variantProduct1));

    Collection<ProductModel> eligibleProducts = strategy.getProductDataSheetsEligibleForExport(productExportContext);

    Set<ArticleApprovalStatus> approvalStatuses = approvalStatusesCaptor.getValue();
    assertThat(approvalStatuses).containsOnly(ArticleApprovalStatus.UNAPPROVED, ArticleApprovalStatus.APPROVED);
    assertThat(eligibleProducts).containsOnly(product1, product3, variantProduct1);
  }

}
