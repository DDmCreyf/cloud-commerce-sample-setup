package com.mirakl.hybris.core.product.strategies.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.daos.impl.DefaultMiraklProductDao;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMciUniqueIdentifierMatchingStrategyTest {
  private static final String UID_VALUE = "uid-value";
  private static final String UID_CODE = "uid-code";
  private static final String CORE_ATTRIBUTE1 = "core-attribute1";
  private static final String CORE_ATTRIBUTE2 = "core-attribute2";
  private static final PK PRODUCT_CATALOG_PK = PK.fromLong(0L);

  @InjectMocks
  private DefaultMciUniqueIdentifierMatchingStrategy uidMatchingStrategy;

  @Mock
  private DefaultMiraklProductDao miraklProductDao;
  @Mock
  private ModelService modelService;
  @Mock
  private ProductImportData data;
  @Mock
  private ProductImportFileContextData context;
  @Mock
  private ProductImportGlobalContextData globalContext;
  @Mock
  private MiraklRawProductModel rawProduct;
  private List<ProductModel> products;

  @Before
  public void setUp() throws Exception {
    products = asList(mock(ProductModel.class), mock(ProductModel.class));
    when(miraklProductDao.find(anyMapOf(String.class, Object.class))).thenReturn(products);
    when(context.getGlobalContext()).thenReturn(globalContext);
    when(globalContext.getProductCatalogVersion()).thenReturn(PRODUCT_CATALOG_PK);
    when(data.getRawProduct()).thenReturn(rawProduct);
    when(rawProduct.getValues()).thenReturn(singletonMap(UID_CODE, UID_VALUE));
  }

  @Test
  public void shouldReturnMatchesWhenUidIsDefined() throws Exception {
    when(globalContext.getUniqueIdentifierCoreAttributes()).thenReturn(Sets.newHashSet(CORE_ATTRIBUTE1, CORE_ATTRIBUTE2));

    Set<ProductModel> matches = uidMatchingStrategy.getMatches(data, context);

    assertThat(matches).containsOnly(products.toArray());
  }

  @Test
  public void shouldReturnEmptyResultWhenNoUidDefined() throws Exception {
    when(globalContext.getUniqueIdentifierCoreAttributes()).thenReturn(Collections.<String>emptySet());

    Set<ProductModel> matches = uidMatchingStrategy.getMatches(data, context);

    assertThat(matches).isEmpty();
  }

}
