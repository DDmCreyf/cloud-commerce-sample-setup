package com.mirakl.hybris.mtc.constants;

import java.util.EnumSet;
import java.util.Set;

import com.mirakl.client.mmp.domain.common.country.IsoCountryCode;
import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;

import de.hybris.platform.util.TaxValue;

/**
 * Global class for all Mirakltaxconnector constants. You can add global constants for your extension into this class.
 */
public final class MirakltaxconnectorConstants extends GeneratedMirakltaxconnectorConstants {
  public static final String EXTENSIONNAME = "mirakltaxconnector";
  public static final Set<MiraklIsoCurrencyCode> ALLOWED_MIRAKL_TAX_CONNECTOR_CURRENCIES = EnumSet.of(MiraklIsoCurrencyCode.USD);
  public static final Set<IsoCountryCode> ALLOWED_MIRAKL_TAX_CONNECTOR_SHIP_TO_COUNTRIES = EnumSet.of(IsoCountryCode.USA);
  public static final TaxValue MTC_NO_TAXES = new TaxValue("MTC_NO_TAXES", 0, true, 0, ALLOWED_MIRAKL_TAX_CONNECTOR_CURRENCIES.iterator().next().name());

  private MirakltaxconnectorConstants() {
    // empty to avoid instantiating this constant class
  }
}
