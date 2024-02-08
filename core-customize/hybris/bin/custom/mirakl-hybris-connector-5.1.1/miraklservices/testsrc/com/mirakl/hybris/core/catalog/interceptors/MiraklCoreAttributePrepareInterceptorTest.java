package com.mirakl.hybris.core.catalog.interceptors;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCoreAttributePrepareInterceptorTest {

  private static final String UID = "uid";
  private static final String ATTRIBUTE_CODE = "attribute-code";
  private static final String CATEGORY_ATTRIBUTE_CODE = "category-attribute-code";
  private static final String ROOT_CATEGORY = "root-category";

  @InjectMocks
  private MiraklCoreAttributePrepareInterceptor interceptor;

  @Mock
  private InterceptorContext context;

  private MiraklCoreAttributeModel coreAttribute;
  private MiraklCategoryCoreAttributeModel categoryCoreAttribute;

  @Before
  public void setUp() throws Exception {
    coreAttribute = new MiraklCoreAttributeModel();
    coreAttribute.setCode(ATTRIBUTE_CODE);
    categoryCoreAttribute = new MiraklCategoryCoreAttributeModel();
    categoryCoreAttribute.setCode(CATEGORY_ATTRIBUTE_CODE);
    categoryCoreAttribute.setRootCategoryCode(ROOT_CATEGORY);

  }

  @Test
  public void shouldPopulateUidForCoreAttributeWhenEmpty() throws Exception {
    interceptor.onPrepare(coreAttribute, context);

    assertThat(coreAttribute.getUid()).isEqualTo(ATTRIBUTE_CODE);
  }

  @Test
  public void shouldPopulateUidForCategoryCoreAttributeWhenEmpty() throws Exception {
    interceptor.onPrepare(categoryCoreAttribute, context);

    assertThat(categoryCoreAttribute.getUid()).isEqualTo(CATEGORY_ATTRIBUTE_CODE + "-" + ROOT_CATEGORY);
  }

  @Test
  public void shouldNotModifyUidWhenNotEmpty() throws Exception {
    coreAttribute.setUid(UID);

    interceptor.onPrepare(coreAttribute, context);

    assertThat(coreAttribute.getUid()).isEqualTo(UID);
  }

}
