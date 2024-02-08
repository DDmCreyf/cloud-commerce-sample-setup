package com.mirakl.hybris.core.order.strategies.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.order.strategies.EntryMergeStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;

@IntegrationTest
public class EntryMergeStrategyMiraklIntegrationTest extends ServicelayerTest {

  @Resource
  private EntryMergeStrategy entryMergeStrategy;

  @Resource
  private UserService userService;

  private CartModel existingCart, cartToMerge;

  @Before
  public void setUp() throws Exception {
    createCoreData();
    createDefaultCatalog();
    importCsv("/miraklcartmerge/test/testCartMerge.impex", "utf-8");
    existingCart = getCartForUser("customer-1");
    cartToMerge = getCartForUser("anonymous");
  }

  @Test
  public void shouldMergeOperatorProducts() {
    AbstractOrderEntryModel entryToMerge = entryMergeStrategy.getEntryToMerge(existingCart.getEntries(), getEntryForEntryNumber(cartToMerge, 1));

    assertThat(entryToMerge).isNotNull();
    assertThat(entryToMerge).isEqualTo(getEntryForEntryNumber(existingCart, 1));
  }

  @Test
  public void shouldMergeSameOffers() {
    AbstractOrderEntryModel entryToMerge =
        entryMergeStrategy.getEntryToMerge(existingCart.getEntries(), getEntryForEntryNumber(cartToMerge, 2));

    assertThat(entryToMerge).isNotNull();
    assertThat(entryToMerge).isEqualTo(getEntryForEntryNumber(existingCart, 3));
  }

  @Test
  public void shouldNotMergeDifferentOffersForSameProduct() {
    AbstractOrderEntryModel entryToMerge =
        entryMergeStrategy.getEntryToMerge(existingCart.getEntries(), getEntryForEntryNumber(cartToMerge, 3));

    assertThat(entryToMerge).isNull();
  }

  @Test
  public void shouldNotMergeDifferentProducts() {
    AbstractOrderEntryModel entryToMerge =
        entryMergeStrategy.getEntryToMerge(existingCart.getEntries(), getEntryForEntryNumber(cartToMerge, 4));

    assertThat(entryToMerge).isNull();
  }

  @Test
  public void shouldNotMergeDifferentOffers() {
    AbstractOrderEntryModel entryToMerge =
        entryMergeStrategy.getEntryToMerge(existingCart.getEntries(), getEntryForEntryNumber(cartToMerge, 5));

    assertThat(entryToMerge).isNull();
  }

  protected CartModel getCartForUser(String userUid) {
    final UserModel user = userService.getUserForUID(userUid);
    final Collection<CartModel> cartModels = user.getCarts();
    Assert.assertEquals(1, cartModels.size());
    return cartModels.iterator().next();
  }

  protected AbstractOrderEntryModel getEntryForEntryNumber(final AbstractOrderModel order, final int number) {
    final List<AbstractOrderEntryModel> entries = order.getEntries();
    if (entries != null && !entries.isEmpty()) {
      final Integer requestedEntryNumber = Integer.valueOf(number);
      for (final AbstractOrderEntryModel entry : entries) {
        if (entry != null && requestedEntryNumber.equals(entry.getEntryNumber())) {
          return entry;
        }
      }
    }
    return null;
  }
}
