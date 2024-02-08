package com.mirakl.hybris.core.shop.services.impl;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.services.ShopService;
import com.mirakl.hybris.core.shop.services.impl.DefaultShopImportService;
import com.mirakl.hybris.core.shop.strategies.ShopValidationStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultShopImportServiceTest {
  private static final int MAX_RESULTS_BY_PAGE = 10;

  @Spy
  @InjectMocks
  private DefaultShopImportService defaultShopImportService;

  @Mock
  private ShopService shopService;

  @Mock
  private Converter<MiraklShop, ShopModel> shopConverter;

  @Mock
  private ModelService modelService;

  @Mock
  private MiraklMarketplacePlatformFrontApi miraklApi;

  @Mock
  private MiraklShops miraklShops;

  @Mock
  private ShopModel mockedShopModel;

  @Mock
  private ShopValidationStrategy mockedValidationStrategy;

  @Captor
  private ArgumentCaptor<MiraklGetShopsRequest> miraklGetShopsRequestArgumentCaptor;

  @Before
  public void setUp() throws Exception {
    doReturn(MAX_RESULTS_BY_PAGE).when(defaultShopImportService).getMaxResultsByPage();
    doReturn(mockedShopModel).when(shopConverter).convert(any(MiraklShop.class));
    when(mockedValidationStrategy.isValid(any(ShopModel.class))).thenReturn(true);
  }

  @Test
  public void shouldImportAllShopsWithNoPagination() {
    when(miraklShops.getShops()).thenReturn(getShopMocks(MAX_RESULTS_BY_PAGE));
    when(miraklApi.getShops(miraklGetShopsRequestArgumentCaptor.capture())).thenReturn(miraklShops);

    Collection<ShopModel> importedShops = defaultShopImportService.importAllShops();

    MiraklGetShopsRequest request = miraklGetShopsRequestArgumentCaptor.getValue();
    assertThat(request.getUpdatedSince(), is(nullValue()));
    assertThat(request.getMax(), equalTo(MAX_RESULTS_BY_PAGE));
    assertThat(importedShops, hasSize(miraklShops.getShops().size()));
    verify(miraklApi).getShops(request);
  }

  @Test
  public void shouldImportModifiedShopsWithNoPagination() {
    when(miraklShops.getShops()).thenReturn(getShopMocks(MAX_RESULTS_BY_PAGE));
    when(miraklApi.getShops(miraklGetShopsRequestArgumentCaptor.capture())).thenReturn(miraklShops);
    Date startingDate = new Date();

    Collection<ShopModel> importedShops = defaultShopImportService.importShopsUpdatedSince(startingDate);

    MiraklGetShopsRequest request = miraklGetShopsRequestArgumentCaptor.getValue();
    assertThat(request.getUpdatedSince(), equalTo(startingDate));
    assertThat(request.getMax(), equalTo(MAX_RESULTS_BY_PAGE));
    assertThat(importedShops, hasSize(miraklShops.getShops().size()));
    verify(miraklApi).getShops(request);
  }

  @Test
  public void shouldHandlePaginationWhenTotalCountIsMultipleOfMaxPerPage() {
    when(miraklShops.getShops()).thenReturn(getShopMocks(MAX_RESULTS_BY_PAGE));
    when(miraklShops.getTotalCount()).thenReturn(MAX_RESULTS_BY_PAGE * 3L);
    when(miraklApi.getShops(miraklGetShopsRequestArgumentCaptor.capture())).thenReturn(miraklShops);

    defaultShopImportService.importAllShops();

    miraklGetShopsRequestArgumentCaptor.getValue();
    verify(miraklApi, times(3)).getShops(Mockito.any(MiraklGetShopsRequest.class));
  }

  @Test
  public void shouldHandlePaginationWhenTotalCountIsNotMultipleOfMaxPerPage() {
    when(miraklShops.getShops()).thenReturn(getShopMocks(MAX_RESULTS_BY_PAGE));
    when(miraklShops.getTotalCount()).thenReturn(MAX_RESULTS_BY_PAGE * 3L + 1);
    when(miraklApi.getShops(miraklGetShopsRequestArgumentCaptor.capture())).thenReturn(miraklShops);

    defaultShopImportService.importAllShops();

    miraklGetShopsRequestArgumentCaptor.getValue();
    verify(miraklApi, times(4)).getShops(Mockito.any(MiraklGetShopsRequest.class));
  }

  @Test
  public void shouldAbortImportWhenShopIsInvalid() {
    singleShopInit();
    when(mockedValidationStrategy.isValid(any(ShopModel.class))).thenReturn(false);

    Collection<ShopModel> shopModels = defaultShopImportService.importAllShops();
    assertThat(shopModels, hasSize(0));
  }

  private void singleShopInit() {
    when(miraklApi.getShops(any(MiraklGetShopsRequest.class))).thenReturn(miraklShops);
    when(miraklShops.getShops()).thenReturn(getShopMocks(1));
    when(miraklShops.getTotalCount()).thenReturn(1L);
  }

  private List<MiraklShop> getShopMocks(int size) {
    List<MiraklShop> mocks = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      mocks.add(mock(MiraklShop.class));
    }
    return mocks;
  }

}
