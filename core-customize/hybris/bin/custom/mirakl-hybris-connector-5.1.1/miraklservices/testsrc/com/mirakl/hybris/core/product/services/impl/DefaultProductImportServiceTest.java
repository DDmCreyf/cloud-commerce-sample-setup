package com.mirakl.hybris.core.product.services.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportErrorData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.beans.ProductImportResultData;
import com.mirakl.hybris.beans.ProductImportSuccessData;
import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;
import com.mirakl.hybris.core.product.strategies.PostProcessProductLineImportStrategy;
import com.mirakl.hybris.core.product.strategies.ProductCreationStrategy;
import com.mirakl.hybris.core.product.strategies.ProductIdentificationStrategy;
import com.mirakl.hybris.core.product.strategies.ProductImportCredentialCheckStrategy;
import com.mirakl.hybris.core.product.strategies.ProductImportValidationStrategy;
import com.mirakl.hybris.core.product.strategies.ProductReceptionCheckStrategy;
import com.mirakl.hybris.core.product.strategies.ProductUpdateStrategy;
import com.mirakl.hybris.core.shop.services.ShopService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductImportServiceTest {

  private static final PK CATALOG_VERSION_PK = PK.fromLong(1L);
  private static final int VARIANT_1_ROW_NUMBER = 15;
  private static final int VARIANT_2_ROW_NUMBER = 18;

  @InjectMocks
  private DefaultProductImportService testObj;

  @Captor
  private ArgumentCaptor<ProductImportSuccessData> importSuccessDataCaptor;
  @Mock
  private ModelService modelService;
  @Mock
  private ShopService shopService;
  @Mock
  private ProductIdentificationStrategy productIdentificationStrategy;
  @Mock
  private ProductImportValidationStrategy productImportValidationStrategy;
  @Mock
  private ProductCreationStrategy productCreationStrategy;
  @Mock
  private ProductUpdateStrategy productUpdateStrategy;
  @Mock
  private ProductImportCredentialCheckStrategy credentialCheckStrategy;
  @Mock
  private PostProcessProductLineImportStrategy postProcessProductLineImportStrategy;
  @Mock
  private Converter<Pair<MiraklRawProductModel, ProductImportFileContextData>, ProductImportData> productImportDataConverter;
  @Mock
  private Converter<ProductImportException, ProductImportErrorData> errorDataConverter;
  @Mock
  private ProductImportFileContextData context;
  @Mock
  private ProductReceptionCheckStrategy productReceptionCheckStrategy;
  @Mock
  private MiraklRawProductModel rawVariant1, rawVariant2;
  @Mock
  private ProductImportGlobalContextData globalContext;
  @Mock
  private ShopModel shop;
  @Mock
  private CatalogVersionModel catalogVersion;
  @Mock
  private MiraklCatalogSystem miraklCatalogSystem;
  @Mock
  private BlockingQueue<ProductImportResultData> importResultQueue;
  @Mock
  private ProductImportData data1, data2;
  @Mock
  private VariantProductModel identifiedProduct1, identifiedProduct2, createdProduct1, createdProduct2, createdProduct1lv2,
      identifiedProduct1lv2;
  @Mock
  private ProductModel rootBaseProduct;
  @Mock
  private Map<String, String> rawVariant1Values, rawVariant2Values;
  @Mock
  private ProductImportErrorData productImportErrorData;

  @Before
  public void setUp() throws Exception {
    testObj.setPostProcessProductLineImportStrategies(singletonList(postProcessProductLineImportStrategy));
    when(globalContext.getProductCatalogVersion()).thenReturn(CATALOG_VERSION_PK);
    when(globalContext.getMiraklCatalogSystem()).thenReturn(miraklCatalogSystem);

    when(productImportDataConverter.convert(Pair.of(rawVariant1, context))).thenReturn(data1);
    when(productImportDataConverter.convert(Pair.of(rawVariant2, context))).thenReturn(data2);

    when(context.getGlobalContext()).thenReturn(globalContext);
    when(context.getImportResultQueue()).thenReturn(importResultQueue);

    when(rawVariant1.getRowNumber()).thenReturn(VARIANT_1_ROW_NUMBER);
    when(rawVariant1.getValues()).thenReturn(rawVariant1Values);
    when(rawVariant2.getRowNumber()).thenReturn(VARIANT_2_ROW_NUMBER);
    when(rawVariant2.getValues()).thenReturn(rawVariant2Values);

    when(data1.getIdentifiedProduct()).thenReturn(identifiedProduct1);
    when(data2.getIdentifiedProduct()).thenReturn(identifiedProduct2);
    when(data1.getRawProduct()).thenReturn(rawVariant1);
    when(data2.getRawProduct()).thenReturn(rawVariant2);

    when(productCreationStrategy.createProduct(data1, context)).thenReturn(createdProduct1);

    when(createdProduct1.getBaseProduct()).thenReturn(createdProduct1lv2);
    when(createdProduct1lv2.getBaseProduct()).thenReturn(rootBaseProduct);
    when(identifiedProduct2.getBaseProduct()).thenReturn(rootBaseProduct);
  }

  @Test
  public void shouldDoNothingWhenProductWhereAlreadyReceived() throws Exception {
    when(productReceptionCheckStrategy.isAlreadyReceived(rawVariant1, context)).thenReturn(true);
    when(productReceptionCheckStrategy.isAlreadyReceived(rawVariant2, context)).thenReturn(true);

    testObj.importProducts(asList(rawVariant1, rawVariant2), context);

    verifyZeroInteractions(productImportDataConverter);
    verifyZeroInteractions(productUpdateStrategy);
    verify(modelService, never()).save(any(Object.class));
  }

  @Test
  public void shouldWriteAnErrorWhenProductAreNotValid() throws Exception {
    Registry.activateMasterTenant();
    doThrow(ProductImportException.class).when(productImportValidationStrategy).validate(rawVariant1, context);
    when(errorDataConverter.convert(any(ProductImportException.class))).thenReturn(productImportErrorData);

    testObj.importProducts(singletonList(rawVariant1), context);

    verify(importResultQueue).put(productImportErrorData);
  }

  @Test
  public void shouldCreateNonIdentifiedProducts() throws Exception {
    Registry.activateMasterTenant();
    when(data1.getIdentifiedProduct()).thenReturn(null);

    testObj.importProducts(singletonList(rawVariant1), context);

    verify(credentialCheckStrategy).checkProductCreationCredentials(data1, context);
    verify(productCreationStrategy).createProduct(data1, context);
    verify(data1).setProductToUpdate(createdProduct1);
  }

  @Test
  public void importProducts() throws Exception {
    Registry.activateMasterTenant();
    when(data1.getIdentifiedProduct()).thenReturn(null);

    testObj.importProducts(asList(rawVariant1, rawVariant2), context);

    verify(credentialCheckStrategy).checkProductUpdateCredentials(data1, context);
    verify(credentialCheckStrategy).checkProductUpdateCredentials(data2, context);
    verify(productUpdateStrategy).applyValues(data1, context);
    verify(productUpdateStrategy).applyValues(data2, context);
    verify(postProcessProductLineImportStrategy).postProcess(data1, rawVariant1, context);
    verify(postProcessProductLineImportStrategy).postProcess(data2, rawVariant2, context);
    verify(modelService, atLeastOnce()).saveAll(anyCollectionOf(ItemModel.class));
    verify(importResultQueue, times(2)).put(importSuccessDataCaptor.capture());
    List<ProductImportSuccessData> importSuccessDataList = importSuccessDataCaptor.getAllValues();
    assertThat(importSuccessDataList.get(0).getRowNumber()).isEqualTo(VARIANT_1_ROW_NUMBER);
    assertThat(importSuccessDataList.get(1).getRowNumber()).isEqualTo(VARIANT_2_ROW_NUMBER);
    assertThat(importSuccessDataList.get(0).getLineValues()).isEqualTo(rawVariant1Values);
    assertThat(importSuccessDataList.get(1).getLineValues()).isEqualTo(rawVariant2Values);
  }

}
