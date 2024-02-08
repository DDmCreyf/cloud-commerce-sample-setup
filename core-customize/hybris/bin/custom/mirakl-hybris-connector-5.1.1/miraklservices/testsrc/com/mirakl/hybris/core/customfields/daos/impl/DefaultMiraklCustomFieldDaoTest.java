package com.mirakl.hybris.core.customfields.daos.impl;

import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.mirakl.hybris.core.enums.MiraklCustomFieldLinkedEntity;
import com.mirakl.hybris.core.model.MiraklCustomFieldModel;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;

@IntegrationTest
public class DefaultMiraklCustomFieldDaoTest extends ServicelayerTest {

  private static final String CODE_1 = "code-1";
  private static final String CODE_9 = "code-9";

  @Resource
  private DefaultMiraklCustomFieldDao defaultMiraklCustomFieldDao;

  @Before
  public void setUp() throws Exception {
    importCsv("/miraklservices/test/testMiraklCustomFields.impex", "utf-8");
  }

  @Test
  public void shouldFindCustomFieldByCodeAndEntity() {
    MiraklCustomFieldModel customFieldByCodeAndEntity =
        defaultMiraklCustomFieldDao.findCustomFieldByCodeAndEntity(CODE_1, MiraklCustomFieldLinkedEntity.OFFER);

    ImmutableList<Pair<String, MiraklCustomFieldLinkedEntity>> customFields =
        FluentIterable.from(singletonList(customFieldByCodeAndEntity)).transform(toCustomFieldPK()).toList();
    assertThat(customFields).containsOnly(Pair.of(CODE_1, MiraklCustomFieldLinkedEntity.OFFER));

    customFieldByCodeAndEntity =
        defaultMiraklCustomFieldDao.findCustomFieldByCodeAndEntity(CODE_9, MiraklCustomFieldLinkedEntity.ORDER);

    customFields = FluentIterable.from(singletonList(customFieldByCodeAndEntity)).transform(toCustomFieldPK()).toList();
    assertThat(customFields).containsOnly(Pair.of(CODE_9, MiraklCustomFieldLinkedEntity.ORDER));
  }

  protected Function<MiraklCustomFieldModel, Pair<String, MiraklCustomFieldLinkedEntity>> toCustomFieldPK() {
    return new Function<MiraklCustomFieldModel, Pair<String, MiraklCustomFieldLinkedEntity>>() {
      @Override
      public Pair<String, MiraklCustomFieldLinkedEntity> apply(MiraklCustomFieldModel customField) {
        return Pair.of(customField.getCode(), customField.getEntity());
      }
    };
  }

}


