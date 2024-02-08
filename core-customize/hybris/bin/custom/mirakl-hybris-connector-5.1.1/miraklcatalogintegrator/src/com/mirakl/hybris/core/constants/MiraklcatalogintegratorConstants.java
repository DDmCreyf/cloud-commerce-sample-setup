/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.mirakl.hybris.core.constants;

/**
 * Global class for all Miraklcatalogintegrator constants. You can add global constants for your extension into this class.
 */
public final class MiraklcatalogintegratorConstants extends GeneratedMiraklcatalogintegratorConstants
{
	public static final String EXTENSIONNAME = "miraklcatalogintegrator";

	private MiraklcatalogintegratorConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension

  public static final String PRODUCTS_IMPORT_STATUSES_PAGESIZE = "mirakl.products.import.statuses.pagesize";
  public static final String DESCRIPTION_MAXLENGTH_CONFIG_KEY = "mirakl.products.export.description.maxlength";
  public static final String PRODUCTS_EXPORT_FILE_MAX_LINE_COUNT = "mirakl.products.export.file.maxlength";
  public final static String WEBSITE_URL_SECURE = "website.url.secure";
  public static final String ALL_BRANDS_CONTEXT_VARIABLE = "allBrands";
  public static final String ALL_CATEGORIES_CONTEXT_VARIABLE = "allCategories";
  public static final String KEY_VALUE_SEPARATOR = "|";
  public static final char COLLECTION_ITEM_SEPARATOR = ',';
}
