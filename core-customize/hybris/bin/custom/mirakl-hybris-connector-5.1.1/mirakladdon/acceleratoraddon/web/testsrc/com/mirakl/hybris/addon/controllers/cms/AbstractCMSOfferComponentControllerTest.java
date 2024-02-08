package com.mirakl.hybris.addon.controllers.cms;

import com.mirakl.hybris.addon.model.CMSBuyBoxComponentModel;
import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.facades.product.OfferFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.acceleratorservices.util.SpringHelper;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static com.mirakl.hybris.addon.controllers.cms.AbstractCMSOfferComponentController.TOP_OFFER_ATTRIBUTE;
import static com.mirakl.hybris.addon.controllers.cms.CMSBuyBoxComponentController.PRODUCT_ATTRIBUTE;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractCMSOfferComponentControllerTest {

  private static final String REQUEST_CONTEXT_DATA = SpringHelper.class.getName() + ".bean.requestContextData";
  private static final String PRODUCT_CODE = "productCode";

  @InjectMocks
  private TestCMSOfferComponentController<AbstractCMSComponentModel> testObj = new TestCMSOfferComponentController<>();

  @Mock
  private OfferFacade offerFacadeMock;
  @Mock
  private CMSComponentService cmsComponentServiceMock;

  @Mock
  private HttpServletRequest requestMock;
  @Mock
  private Model modelMock;
  @Mock
  private CMSBuyBoxComponentModel buyBoxComponentMock;
  @Mock
  private RequestContextData requestContextDataMock;
  @Mock
  private OfferData offerDataMock;
  @Mock
  private ProductData productDataMock;

  @Before
  public void setUp() {
    when(requestMock.getAttribute(REQUEST_CONTEXT_DATA)).thenReturn(requestContextDataMock);
    when(requestMock.getAttribute(PRODUCT_ATTRIBUTE)).thenReturn(productDataMock);
    when(productDataMock.getCode()).thenReturn(PRODUCT_CODE);
  }

  @Test
  public void fillsModelFromRequestContextData() {
    when(requestContextDataMock.getOffers()).thenReturn(singletonList(offerDataMock));

    testObj.fillModel(requestMock, modelMock, buyBoxComponentMock);

    verify(requestContextDataMock).getOffers();
    verify(modelMock).addAttribute(TOP_OFFER_ATTRIBUTE, offerDataMock);
    verify(requestContextDataMock, never()).setOffers(anyListOf(OfferData.class));
    verify(offerFacadeMock, never()).getOffersForProductCode(anyString());
  }

  @Test
  public void fillsModelFromFacadeIfRequestContextDataOfferListIsEmpty() {
    when(requestContextDataMock.getOffers()).thenReturn(Collections.<OfferData>emptyList());
    when(offerFacadeMock.getOffersForProductCode(PRODUCT_CODE)).thenReturn(singletonList(offerDataMock));

    testObj.fillModel(requestMock, modelMock, buyBoxComponentMock);

    verify(requestContextDataMock).getOffers();
    verify(modelMock).addAttribute(TOP_OFFER_ATTRIBUTE, offerDataMock);
    verify(offerFacadeMock).getOffersForProductCode(PRODUCT_CODE);
    verify(requestContextDataMock).setOffers(singletonList(offerDataMock));
  }

  @Test
  public void fillModelSetsNullAsTopOfferIfNoOffersFound() {
    when(requestContextDataMock.getOffers()).thenReturn(Collections.<OfferData>emptyList());
    when(offerFacadeMock.getOffersForProductCode(PRODUCT_CODE)).thenReturn(Collections.<OfferData>emptyList());

    testObj.fillModel(requestMock, modelMock, buyBoxComponentMock);

    verify(requestContextDataMock).getOffers();
    verify(modelMock).addAttribute(TOP_OFFER_ATTRIBUTE, null);
    verify(offerFacadeMock).getOffersForProductCode(PRODUCT_CODE);
    verify(requestContextDataMock).setOffers(Collections.<OfferData>emptyList());
  }

  @Test(expected = IllegalArgumentException.class)
  public void fillModelThrowsIllegalArgumentExceptionIfNoProductFoundInRequest() {
    when(requestContextDataMock.getOffers()).thenReturn(Collections.<OfferData>emptyList());
    when(requestMock.getAttribute(PRODUCT_ATTRIBUTE)).thenReturn(null);

    testObj.fillModel(requestMock, modelMock, buyBoxComponentMock);
  }

  protected class TestCMSOfferComponentController<N extends AbstractCMSComponentModel>
      extends AbstractCMSOfferComponentController<N> {

    @Override
    protected void fillModel(Model model, List<OfferData> offers) {}
  }
}
