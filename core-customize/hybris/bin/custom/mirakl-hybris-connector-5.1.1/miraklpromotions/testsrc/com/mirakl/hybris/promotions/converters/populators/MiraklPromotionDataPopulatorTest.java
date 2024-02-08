package com.mirakl.hybris.promotions.converters.populators;

import static com.mirakl.hybris.promotions.constants.MiraklpromotionsConstants.SOLR_INDEX_PROMOTION_ID_SEPARATOR;
import static com.mirakl.hybris.promotions.constants.MiraklpromotionsConstants.SOLR_MIRAKL_PROMOTIONS_KEY;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklPromotionData;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.servicelayer.dto.converter.Converter;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklPromotionDataPopulatorTest {

  private static final String PROMOTION_SHOP_ID = "2089";
  private static final String PROMOTION_INTERNAL_ID = "Internal Id";
  private static final String PROMOTION_FACET_URL = "/promotion/facet/whatever";
  private static final String MIRAKL_PROMOTION_DESCRIPTION = "Buy one, get one !";

  @InjectMocks
  private MiraklPromotionDataPopulator testObj;

  @Mock
  private Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;
  @Captor
  private ArgumentCaptor<SolrSearchQueryData> searchStateCaptor;
  @Mock
  private SearchStateData miraklPromotionSearchStateData;
  @Mock
  private MiraklPromotionModel miraklPromotionModel;

  @Before
  public void setUp() throws Exception {
    when(solrSearchStateConverter.convert(any(SolrSearchQueryData.class))).thenReturn(miraklPromotionSearchStateData);
    when(miraklPromotionSearchStateData.getUrl()).thenReturn(PROMOTION_FACET_URL);
    when(miraklPromotionModel.getShopId()).thenReturn(PROMOTION_SHOP_ID);
    when(miraklPromotionModel.getInternalId()).thenReturn(PROMOTION_INTERNAL_ID);
    when(miraklPromotionModel.getPublicDescription()).thenReturn(MIRAKL_PROMOTION_DESCRIPTION);
  }

  @Test
  public void populate() throws Exception {
    MiraklPromotionData output = new MiraklPromotionData();

    testObj.populate(miraklPromotionModel, output);

    verify(solrSearchStateConverter).convert(searchStateCaptor.capture());
    SolrSearchQueryData solrSearchQuery = searchStateCaptor.getValue();
    List<SolrSearchQueryTermData> filterTerms = solrSearchQuery.getFilterTerms();
    assertThat(filterTerms).hasSize(1);
    assertThat(filterTerms.get(0).getKey()).isEqualTo(SOLR_MIRAKL_PROMOTIONS_KEY);
    assertThat(filterTerms.get(0).getValue())
        .isEqualTo(PROMOTION_SHOP_ID + SOLR_INDEX_PROMOTION_ID_SEPARATOR + PROMOTION_INTERNAL_ID);
    assertThat(output.getSearchPageUrl()).isEqualTo(PROMOTION_FACET_URL);
    assertThat(output.getDescription()).isEqualTo(MIRAKL_PROMOTION_DESCRIPTION);
  }

}
