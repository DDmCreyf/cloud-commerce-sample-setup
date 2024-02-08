package com.mirakl.hybris.core.catalog.strategies.impl;

import static org.mockito.internal.util.collections.Sets.newSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.mockito.Mock;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeOwnerStrategy;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.product.strategies.ProductExportAttributeValueFormattingStrategy;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.variants.model.VariantProductModel;

public abstract class AbstractCoreAttributeHandlerTest<T extends MiraklCoreAttributeModel> {

  protected static final String PRODUCT_ITEM_TYPE = "ApparelSizeVariantProduct";
  protected static final String SIZE_ATTRIBUTE_QUALIFIER = "size";
  protected static final String STYLE_ATTRIBUTE_QUALIFIER = "style";
  protected static final String COMPOSED_TYPE_CODE = "ApparelSizeVariantProduct";

  @Mock
  protected TypeService typeService;
  @Mock
  protected ModelService modelService;
  @Mock
  protected CoreAttributeOwnerStrategy coreAttributeOwnerStrategy;
  @Mock
  private ProductExportAttributeValueFormattingStrategy<Object, String> formattingStrategy;
  @Mock
  protected T coreAttribute;
  @Mock
  protected ProductImportData data;
  @Mock
  protected AttributeValueData attributeValue;
  @Mock
  protected ProductImportFileContextData importContext;
  @Mock
  protected VariantProductModel ownerProduct;
  @Mock
  protected ComposedTypeModel composedType;
  @Mock
  protected ProductImportGlobalContextData globalImportContext;

  protected Map<String, Set<String>> attributesPerType = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    attributesPerType.put(COMPOSED_TYPE_CODE, newSet(SIZE_ATTRIBUTE_QUALIFIER, STYLE_ATTRIBUTE_QUALIFIER));
  }

}
