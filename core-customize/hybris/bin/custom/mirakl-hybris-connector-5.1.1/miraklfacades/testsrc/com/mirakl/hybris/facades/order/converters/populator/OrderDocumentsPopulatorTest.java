package com.mirakl.hybris.facades.order.converters.populator;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.mirakl.client.mmp.domain.order.document.MiraklOrderDocument;
import com.mirakl.hybris.beans.DocumentData;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.order.services.MiraklDocumentService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderDocumentsPopulatorTest {

  private static final String CONSIGNMENT_CODE_1 = "consignment-code-1";

  @InjectMocks
  private OrderDocumentsPopulator populator;

  @Mock
  private MiraklDocumentService documentService;
  @Mock
  private Converter<MiraklOrderDocument, DocumentData> marketplaceConsignmentDocumentConverter;
  @Mock
  private OrderModel orderModel;
  @Mock
  private MarketplaceConsignmentModel consignment1;
  @Captor
  private ArgumentCaptor<Collection<String>> consignmentCodesCaptor;
  @Mock
  private MiraklOrderDocument document1;
  @Mock
  private DocumentData documentData1;
  private ConsignmentData consignmentData1;


  @Test
  public void shouldPopulate() {
    when(orderModel.getConsignments()).thenReturn(Sets.newHashSet(consignment1));
    when(consignment1.getCode()).thenReturn(CONSIGNMENT_CODE_1);
    when(documentService.getDocumentsForMarketplaceConsignments(consignmentCodesCaptor.capture()))
        .thenReturn(newHashSet(document1));
    when(marketplaceConsignmentDocumentConverter.convertAll(anyCollectionOf(MiraklOrderDocument.class)))
        .thenReturn(asList(documentData1));

    populator.populate(orderModel, getTarget());

    assertThat(consignmentCodesCaptor.getValue()).containsOnly(CONSIGNMENT_CODE_1);
    assertThat(consignmentData1.getDocuments()).containsExactly(documentData1);
  }


  private OrderData getTarget() {
    OrderData orderData = new OrderData();
    consignmentData1 = new ConsignmentData();
    consignmentData1.setCode(CONSIGNMENT_CODE_1);
    orderData.setConsignments(asList(consignmentData1));
    return orderData;
  }

}
