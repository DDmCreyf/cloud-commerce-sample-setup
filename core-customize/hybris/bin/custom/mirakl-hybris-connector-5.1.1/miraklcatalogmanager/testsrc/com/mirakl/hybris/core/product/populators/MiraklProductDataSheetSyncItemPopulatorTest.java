package com.mirakl.hybris.core.product.populators;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncItem;
import com.mirakl.client.mci.domain.product.MiraklSynchronizedProductDataSheetAcceptance;
import com.mirakl.client.mci.domain.product.MiraklSynchronizedProductDataSheetAcceptanceStatus;
import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.catalog.attributes.McmCoreAttributeHandler;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogService;
import com.mirakl.hybris.core.catalog.strategies.ClassificationAttributeExportEligibilityStrategy;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandlerResolver;
import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.enums.ProductOrigin;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.product.strategies.McmProductAcceptanceStrategy;
import com.mirakl.hybris.core.product.strategies.ProductExportAttributeValueFormattingStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.classification.features.UnlocalizedFeature;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklProductDataSheetSyncItemPopulatorTest {
  private static final Long FEATURE1_VALUE2_EN = 2L;
  private static final Long FEATURE1_VALUE2_FR = 1L;
  private static final String FEATURE2_VALUE = "feature-value";
  private static final String FORMATTED_FEATURE_VALUE_2 = "formatted-feature-value-2";
  private static final String FEATURE1_VALUE1_EN = "feature-value-en";
  private static final String FEATURE1_VALUE1_FR = "feature-value-fr";
  private static final String MIRAKL_PRODUCT_ID = "mirakl-product-id";
  private static final String ATTRIBUTE1_VALUE = "attribute1-value";
  private static final String ATTRIBUTE2_VALUE = "attribute2-value";
  private static final String ATTRIBUTE3_VALUE_EN = "attribute3-value-en";
  private static final String ATTRIBUTE3_VALUE_FR = "attribute3-value-fr";
  private static final String PRODUCT_CODE = "product-code";
  private static final String CODE_ATTRIBUTE1 = "code-attribute1";
  private static final String CODE_ATTRIBUTE2 = "code-attribute2";
  private static final String CODE_ATTRIBUTE3 = "code-attribute3";
  private static final String CODE_ATTRIBUTE3_EN = "code-attribute3 [en]";
  private static final String CODE_ATTRIBUTE3_FR = "code-attribute3 [fr]";
  private static final String CODE_CLASSIFICATION_ATTRIBUTE1 = "code-classification-attribute1";
  private static final String CODE_CLASSIFICATION_ATTRIBUTE1_FR = "code-classification-attribute1 [fr]";
  private static final String CODE_CLASSIFICATION_ATTRIBUTE1_EN = "code-classification-attribute1 [en]";
  private static final String CODE_CLASSIFICATION_ATTRIBUTE2 = "code-classification-attribute2";
  private static final PK attribute1PK = PK.fromLong(1);
  private static final PK attribute2PK = PK.fromLong(2);
  private static final PK attribute3PK = PK.fromLong(3);
  private static final List<Locale> translatableLocales = asList(Locale.FRENCH, Locale.ENGLISH);

  @InjectMocks
  private MiraklProductDataSheetSyncItemPopulator populator;

  @Mock
  private CoreAttributeHandlerResolver coreAttributeHandlerResolver;
  @Mock
  private ModelService modelService;
  @Mock
  private ClassificationService classificationService;
  @Mock
  private MiraklExportCatalogService exportCatalogService;
  @Mock
  private ClassificationAttributeExportEligibilityStrategy attributeExportEligibilityStrategy;
  @Mock
  private ProductExportAttributeValueFormattingStrategy<Object, String> formattingStrategy;
  @Mock
  private McmProductAcceptanceStrategy productAcceptanceStrategy;
  @Mock
  private ProductModel product;
  @Mock
  private ProductDataSheetExportContextData exportContext;
  @Mock
  private MiraklCoreAttributeModel attribute1, attribute2, attribute3;
  @Mock
  private McmCoreAttributeHandler<MiraklCoreAttributeModel> coreAttributeHandler;
  @Mock
  private ClassAttributeAssignmentModel assignment1, assignment2;
  @Mock
  private ClassificationAttributeModel classificationAttribute1, classificationAttribute2;
  @Mock
  private Map<ArticleApprovalStatus, MiraklSynchronizedProductDataSheetAcceptanceStatus> productApprovalStatusMapping;
  @Mock
  private MiraklSynchronizedProductDataSheetAcceptance acceptance;
  @Mock
  private MiraklCatalogSystem miraklCatalogSystem;

  private LocalizedFeature feature1;
  private UnlocalizedFeature feature2;
  private FeatureList featureList;

  @Before
  public void setUp() throws Exception {
    feature1 = new LocalizedFeature(assignment1, null, null);
    feature2 = new UnlocalizedFeature(assignment2, (List<FeatureValue>) null);
    featureList = new FeatureList(feature1, feature2);
    when(product.getCode()).thenReturn(PRODUCT_CODE);
    when(product.getMiraklProductId()).thenReturn(MIRAKL_PRODUCT_ID);
    when(modelService.get(attribute1PK)).thenReturn(attribute1);
    when(modelService.get(attribute2PK)).thenReturn(attribute2);
    when(modelService.get(attribute3PK)).thenReturn(attribute3);
    HashMap<String, PK> coreAttributes = new HashMap<>();
    coreAttributes.put(CODE_ATTRIBUTE1, attribute1PK);
    coreAttributes.put(CODE_ATTRIBUTE2, attribute2PK);
    coreAttributes.put(CODE_ATTRIBUTE3, attribute3PK);
    when(exportContext.getCoreAttributes()).thenReturn(coreAttributes);
    when(exportContext.getMiraklCatalogSystem()).thenReturn(miraklCatalogSystem);
    when(coreAttributeHandlerResolver.determineHandler(attribute1, miraklCatalogSystem)).thenReturn(coreAttributeHandler);
    when(coreAttributeHandlerResolver.determineHandler(attribute2, miraklCatalogSystem)).thenReturn(coreAttributeHandler);
    when(coreAttributeHandlerResolver.determineHandler(attribute3, miraklCatalogSystem)).thenReturn(coreAttributeHandler);
    when(coreAttributeHandler.getValue(product, attribute1, exportContext)).thenReturn(ATTRIBUTE1_VALUE);
    when(coreAttributeHandler.getValue(product, attribute2, exportContext)).thenReturn(ATTRIBUTE2_VALUE);
    when(coreAttributeHandler.getValue(product, attribute3, Locale.FRENCH, exportContext)).thenReturn(ATTRIBUTE3_VALUE_FR);
    when(coreAttributeHandler.getValue(product, attribute3, Locale.ENGLISH, exportContext)).thenReturn(ATTRIBUTE3_VALUE_EN);
    when(classificationService.getFeatures(product)).thenReturn(featureList);
    when(exportContext.getTranslatableLocales()).thenReturn(translatableLocales);
    when(exportCatalogService.formatAttributeExportName(CODE_ATTRIBUTE3, Locale.ENGLISH)).thenReturn(CODE_ATTRIBUTE3_EN);
    when(exportCatalogService.formatAttributeExportName(CODE_ATTRIBUTE3, Locale.FRENCH)).thenReturn(CODE_ATTRIBUTE3_FR);
    when(exportCatalogService.formatAttributeExportName(CODE_CLASSIFICATION_ATTRIBUTE1, Locale.ENGLISH))
    .thenReturn(CODE_CLASSIFICATION_ATTRIBUTE1_EN);
    when(exportCatalogService.formatAttributeExportName(CODE_CLASSIFICATION_ATTRIBUTE1, Locale.FRENCH))
    .thenReturn(CODE_CLASSIFICATION_ATTRIBUTE1_FR);
    when(formattingStrategy.formatValueForExport(FEATURE2_VALUE)).thenReturn(FORMATTED_FEATURE_VALUE_2);

    when(assignment1.getClassificationAttribute()).thenReturn(classificationAttribute1);
    when(classificationAttribute1.getCode()).thenReturn(CODE_CLASSIFICATION_ATTRIBUTE1);

    when(assignment2.getClassificationAttribute()).thenReturn(classificationAttribute2);
    when(classificationAttribute2.getCode()).thenReturn(CODE_CLASSIFICATION_ATTRIBUTE2);
  }


  @Test
  public void populatesMarketplaceProducts() throws Exception {
    when(product.getOrigin()).thenReturn(ProductOrigin.MARKETPLACE);
    MiraklProductDataSheetSyncItem item = new MiraklProductDataSheetSyncItem();

    populator.populate(Pair.of(product, exportContext), item);

    assertThat(item.getProductSku()).isEqualTo(PRODUCT_CODE);
    assertThat(item.getMiraklProductId()).isEqualTo(MIRAKL_PRODUCT_ID);
    assertThat(item.getData()).isNull();
  }

  @Test
  public void populatesOperatorProducts() throws Exception {
    when(product.getOrigin()).thenReturn(ProductOrigin.OPERATOR);
    MiraklProductDataSheetSyncItem item = new MiraklProductDataSheetSyncItem();

    populator.populate(Pair.of(product, exportContext), item);

    assertThat(item.getProductSku()).isEqualTo(PRODUCT_CODE);
    assertThat(item.getMiraklProductId()).isEqualTo(MIRAKL_PRODUCT_ID);
    Map<String, Object> data = item.getData();
    assertThat(data).isNotEmpty();
    assertThat(data).includes(entry(CODE_ATTRIBUTE1, ATTRIBUTE1_VALUE));
    assertThat(data).includes(entry(CODE_ATTRIBUTE2, ATTRIBUTE2_VALUE));
  }

  @Test
  public void populatesOperatorProductsWithLocalizedCoreAttributes() throws Exception {
    when(product.getOrigin()).thenReturn(ProductOrigin.OPERATOR);
    when(attribute3.isLocalized()).thenReturn(true);
    MiraklProductDataSheetSyncItem item = new MiraklProductDataSheetSyncItem();

    populator.populate(Pair.of(product, exportContext), item);

    assertThat(item.getProductSku()).isEqualTo(PRODUCT_CODE);
    assertThat(item.getMiraklProductId()).isEqualTo(MIRAKL_PRODUCT_ID);
    Map<String, Object> data = item.getData();
    assertThat(data).isNotEmpty();
    assertThat(data).includes(entry(CODE_ATTRIBUTE3_EN, ATTRIBUTE3_VALUE_EN));
    assertThat(data).includes(entry(CODE_ATTRIBUTE3_FR, ATTRIBUTE3_VALUE_FR));
  }

  @Test
  public void populatesOperatorProductsWithLocalizedClassificationAttributes() throws Exception {
    when(product.getOrigin()).thenReturn(ProductOrigin.OPERATOR);
    when(assignment1.getLocalized()).thenReturn(true);
    when(assignment2.getLocalized()).thenReturn(false);
    when(attributeExportEligibilityStrategy.isExportableAttribute(assignment1)).thenReturn(true);
    when(attributeExportEligibilityStrategy.isExportableAttribute(assignment2)).thenReturn(true);
    when(assignment1.getMultiValued()).thenReturn(true);
    feature1.addValue(new FeatureValue(FEATURE1_VALUE1_FR), Locale.FRENCH);
    feature1.addValue(new FeatureValue(FEATURE1_VALUE1_EN), Locale.ENGLISH);
    feature1.addValue(new FeatureValue(FEATURE1_VALUE2_FR), Locale.FRENCH);
    feature1.addValue(new FeatureValue(FEATURE1_VALUE2_EN), Locale.ENGLISH);
    feature2.addValue(new FeatureValue(FEATURE2_VALUE));

    MiraklProductDataSheetSyncItem item = new MiraklProductDataSheetSyncItem();

    populator.populate(Pair.of(product, exportContext), item);

    assertThat(item.getProductSku()).isEqualTo(PRODUCT_CODE);
    assertThat(item.getMiraklProductId()).isEqualTo(MIRAKL_PRODUCT_ID);
    Map<String, Object> data = item.getData();
    assertThat(data).isNotEmpty();
    assertThat(data.get(CODE_CLASSIFICATION_ATTRIBUTE1_FR)).isInstanceOf(List.class);
    assertThat((List<?>) data.get(CODE_CLASSIFICATION_ATTRIBUTE1_FR)).containsOnly(FEATURE1_VALUE2_FR.toString(),
        FEATURE1_VALUE1_FR);
    assertThat(data.get(CODE_CLASSIFICATION_ATTRIBUTE1_EN)).isInstanceOf(List.class);
    assertThat((List<?>) data.get(CODE_CLASSIFICATION_ATTRIBUTE1_EN)).containsOnly(FEATURE1_VALUE2_EN.toString(),
        FEATURE1_VALUE1_EN);
    assertThat(data).includes(entry(CODE_CLASSIFICATION_ATTRIBUTE2, FORMATTED_FEATURE_VALUE_2));
  }

  @Test
  public void populatesProductsWithAcceptance() throws Exception {
    when(product.getOrigin()).thenReturn(ProductOrigin.MARKETPLACE);
    when(productAcceptanceStrategy.getAcceptance(product)).thenReturn(acceptance);

    MiraklProductDataSheetSyncItem item = new MiraklProductDataSheetSyncItem();

    populator.populate(Pair.of(product, exportContext), item);

    assertThat(item.getAcceptance()).isEqualTo(acceptance);
  }

}
