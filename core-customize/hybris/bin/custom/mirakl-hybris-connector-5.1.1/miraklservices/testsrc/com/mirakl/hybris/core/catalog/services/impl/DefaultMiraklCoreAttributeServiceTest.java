package com.mirakl.hybris.core.catalog.services.impl;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.catalog.daos.MiraklCoreAttributeDao;
import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklCoreAttributeServiceTest {

  private static final String ROOT_CATEGORY_CODE = "rootCategoryCode";
  private static final String CATEGORY_ATTRIBUTE_UID = "categoryAttributeUid";
  private static final String UID_ATTRIBUTE1_CODE = "uidAttribute1Code";
  private static final String UID_ATTRIBUTE2_CODE = "uidAttribute2Code";
  private static final String VARIANT_ATTRIBUTE_CODE = "variantAttributeCode";
  private static final String CATEGORY_ATTRIBUTE_CODE = "categoryAttributeCode";

  @Mock
  private CategoryService categoryService;
  @Mock
  private MiraklCoreAttributeDao miraklCoreAttributeDao;
  @Mock
  private MiraklCoreAttributeConfigurationModel coreAttributeConfiguration;
  @Mock
  private MiraklCoreAttributeModel uidAttribute1, uidAttribute2, variantAttribute;
  @Mock
  private MiraklCategoryCoreAttributeModel categoryAttribute;
  @Mock
  private CatalogVersionModel catalogVersion;
  @Mock
  private CategoryModel rootCategory, category1, category2;

  private List<MiraklCoreAttributeModel> coreAttributes;

  @InjectMocks
  private DefaultMiraklCoreAttributeService coreAttributeService;


  @Before
  public void setUp() throws Exception {
    coreAttributes = asList(uidAttribute1, uidAttribute2, variantAttribute, categoryAttribute);
    when(uidAttribute1.isUniqueIdentifier()).thenReturn(true);
    when(uidAttribute2.isUniqueIdentifier()).thenReturn(true);
    when(variantAttribute.isVariant()).thenReturn(true);
    when(categoryAttribute.getRole()).thenReturn(MiraklAttributeRole.CATEGORY_ATTRIBUTE);
    when(uidAttribute1.getCode()).thenReturn(UID_ATTRIBUTE1_CODE);
    when(uidAttribute2.getCode()).thenReturn(UID_ATTRIBUTE2_CODE);
    when(variantAttribute.getCode()).thenReturn(VARIANT_ATTRIBUTE_CODE);
    when(categoryAttribute.getCode()).thenReturn(CATEGORY_ATTRIBUTE_CODE);
  }

  @Test
  public void shouldGetAllUniqueIdentifierCoreAttributes() throws Exception {
    List<MiraklCoreAttributeModel> coreAttributes = asList(mock(MiraklCoreAttributeModel.class));
    when(miraklCoreAttributeDao.findUniqueIdentifierCoreAttributes()).thenReturn(coreAttributes);

    List<MiraklCoreAttributeModel> uniqueIdentifierCoreAttributes = coreAttributeService.getUniqueIdentifierCoreAttributes();

    assertThat(uniqueIdentifierCoreAttributes).isEqualTo(coreAttributes);
  }

  @Test
  public void shouldGetUniqueIdentifierCoreAttributesInACollection() throws Exception {
    List<MiraklCoreAttributeModel> uniqueIdentifierCoreAttributes =
        coreAttributeService.getUniqueIdentifierCoreAttributes(coreAttributes);

    assertThat(uniqueIdentifierCoreAttributes).containsExactly(uidAttribute1, uidAttribute2);
  }

  @Test
  public void shouldGetAllVariantCoreAttributes() throws Exception {
    List<MiraklCoreAttributeModel> coreAttributes = asList(mock(MiraklCoreAttributeModel.class));
    when(miraklCoreAttributeDao.findVariantCoreAttributes()).thenReturn(coreAttributes);

    List<MiraklCoreAttributeModel> variantCoreAttributes = coreAttributeService.getVariantCoreAttributes();

    assertThat(variantCoreAttributes).isEqualTo(coreAttributes);
  }

  @Test
  public void shouldGetVariantCoreAttributesInACollection() throws Exception {
    List<MiraklCoreAttributeModel> variantCoreAttributes = coreAttributeService.getVariantCoreAttributes(coreAttributes);

    assertThat(variantCoreAttributes).containsExactly(variantAttribute);
  }

  @Test
  public void shouldGetAllAttributeForRoleMiraklAttributeRole() throws Exception {
    MiraklAttributeRole role = MiraklAttributeRole.CATEGORY_ATTRIBUTE;
    List<MiraklCoreAttributeModel> coreAttributes = asList(mock(MiraklCoreAttributeModel.class));
    when(miraklCoreAttributeDao.findCoreAttributeByRole(role)).thenReturn(coreAttributes);

    List<MiraklCoreAttributeModel> attributesForRole = coreAttributeService.getAttributesForRole(role);

    assertThat(attributesForRole).isEqualTo(coreAttributes);
  }

  @Test
  public void shouldGetAttributesForRoleMiraklAttributeRoleInACollection() throws Exception {
    List<MiraklCoreAttributeModel> attributesForRole =
        coreAttributeService.getAttributesForRole(MiraklAttributeRole.CATEGORY_ATTRIBUTE, coreAttributes);

    assertThat(attributesForRole).containsOnly(categoryAttribute);
  }

  @Test
  public void shouldGetCategoryCoreAttributeForRole() throws Exception {
    MiraklCategoryCoreAttributeModel categoryCoreAttribute = new MiraklCategoryCoreAttributeModel();
    categoryCoreAttribute.setRole(MiraklAttributeRole.CATEGORY_ATTRIBUTE);
    when(coreAttributeConfiguration.getCoreAttributes())
        .thenReturn(newHashSet(uidAttribute1, uidAttribute2, variantAttribute, categoryCoreAttribute));

    MiraklCategoryCoreAttributeModel result =
        coreAttributeService.getCategoryCoreAttributeForRole(MiraklAttributeRole.CATEGORY_ATTRIBUTE, coreAttributeConfiguration);

    assertThat(result).isEqualTo(categoryCoreAttribute);
  }

  @Test
  public void shouldGetAllCategoryCoreAttributes() throws Exception {
    List<MiraklCategoryCoreAttributeModel> allCategoryCoreAttributes =
        coreAttributeService.getAllCategoryCoreAttributes(coreAttributes);

    assertThat(allCategoryCoreAttributes).containsOnly(categoryAttribute);
  }

  @Test(expected = UnknownIdentifierException.class)
  public void shouldGetCategoryCoreAttributeForRoleThrowExceptionWhenNoResult() throws Exception {
    when(coreAttributeConfiguration.getCoreAttributes()).thenReturn(newHashSet(uidAttribute1, uidAttribute2, variantAttribute));

    coreAttributeService.getCategoryCoreAttributeForRole(MiraklAttributeRole.CATEGORY_ATTRIBUTE, coreAttributeConfiguration);
  }

  @Test(expected = AmbiguousIdentifierException.class)
  public void shouldGetCategoryCoreAttributeForRoleThrowExceptionWhenMultipleResults() throws Exception {
    MiraklCategoryCoreAttributeModel categoryCoreAttribute1 = new MiraklCategoryCoreAttributeModel();
    categoryCoreAttribute1.setRole(MiraklAttributeRole.CATEGORY_ATTRIBUTE);
    MiraklCategoryCoreAttributeModel categoryCoreAttribute2 = new MiraklCategoryCoreAttributeModel();
    categoryCoreAttribute2.setRole(MiraklAttributeRole.CATEGORY_ATTRIBUTE);
    when(coreAttributeConfiguration.getCoreAttributes())
        .thenReturn(newHashSet(uidAttribute1, uidAttribute2, variantAttribute, categoryCoreAttribute1, categoryCoreAttribute2));

    coreAttributeService.getCategoryCoreAttributeForRole(MiraklAttributeRole.CATEGORY_ATTRIBUTE, coreAttributeConfiguration);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldGetCategoryCoreAttributeForRoleThrowExceptionWhenNotInstanceOf() throws Exception {
    MiraklCoreAttributeModel categoryCoreAttribute = new MiraklCoreAttributeModel();
    categoryCoreAttribute.setRole(MiraklAttributeRole.CATEGORY_ATTRIBUTE);
    when(coreAttributeConfiguration.getCoreAttributes())
        .thenReturn(newHashSet(uidAttribute1, uidAttribute2, variantAttribute, categoryCoreAttribute));

    coreAttributeService.getCategoryCoreAttributeForRole(MiraklAttributeRole.CATEGORY_ATTRIBUTE, coreAttributeConfiguration);
  }

  @Test
  public void shouldGetAllCoreAttributes() throws Exception {
    List<MiraklCoreAttributeModel> coreAttributes = asList(mock(MiraklCoreAttributeModel.class));
    when(miraklCoreAttributeDao.find()).thenReturn(coreAttributes);

    List<MiraklCoreAttributeModel> allCoreAttributes = coreAttributeService.getAllCoreAttributes();

    assertThat(allCoreAttributes).isEqualTo(coreAttributes);
  }

  @Test
  public void shouldGetAllCategoryValuesForCategoryCoreAttributes() throws Exception {
    List<MiraklCoreAttributeModel> coreAttributes = asList(uidAttribute1, uidAttribute2, variantAttribute, categoryAttribute);
    when(categoryAttribute.getUid()).thenReturn(CATEGORY_ATTRIBUTE_UID);
    when(categoryAttribute.getRootCategoryCode()).thenReturn(ROOT_CATEGORY_CODE);
    when(categoryService.getCategoryForCode(catalogVersion, ROOT_CATEGORY_CODE)).thenReturn(rootCategory);
    when(rootCategory.getAllSubcategories()).thenReturn(asList(category1, category2));

    Map<String, Set<CategoryModel>> allCategoryValues =
        coreAttributeService.getAllCategoryValuesForCategoryCoreAttributes(coreAttributes, catalogVersion);

    assertThat(allCategoryValues).hasSize(1);
    assertThat(allCategoryValues.get(CATEGORY_ATTRIBUTE_UID)).isNotEmpty();
    assertThat(allCategoryValues.get(CATEGORY_ATTRIBUTE_UID)).containsOnly(category1, category2);
  }

  @Test
  public void shouldGetCoreAttributeCodes() throws Exception {

    Map<String, MiraklCoreAttributeModel> coreAttributeCodes =
        coreAttributeService.getCoreAttributeCodes(newHashSet(uidAttribute1, uidAttribute2, variantAttribute, categoryAttribute));

    assertThat(coreAttributeCodes).hasSize(4);
    assertThat(coreAttributeCodes).includes(entry(UID_ATTRIBUTE1_CODE, uidAttribute1));
    assertThat(coreAttributeCodes).includes(entry(UID_ATTRIBUTE2_CODE, uidAttribute2));
    assertThat(coreAttributeCodes).includes(entry(VARIANT_ATTRIBUTE_CODE, variantAttribute));
    assertThat(coreAttributeCodes).includes(entry(CATEGORY_ATTRIBUTE_CODE, categoryAttribute));
  }


}
