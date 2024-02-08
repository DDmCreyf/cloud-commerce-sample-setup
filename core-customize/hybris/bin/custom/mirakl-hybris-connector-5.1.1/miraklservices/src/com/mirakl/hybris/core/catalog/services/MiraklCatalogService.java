package com.mirakl.hybris.core.catalog.services;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com
 * All Rights Reserved. Tous droits réservés.
 */
public interface MiraklCatalogService {

    /**
     * Returns the codes of the Hierarchies in Mirakl
     *
     * @return a Set with the attribute codes
     */
    Set<String> getMiraklCategoryCodes();

    /**
     * Returns the codes of the Attributes in Mirakl
     *
     * @return a Set of Pairs containing the codes of both the Attribute and the hierarchy
     */
    Set<Pair<String, String>> getMiraklAttributeCodes();

    /**
     * Returns the codes of the values in Mirakl
     *
     * @return a Set with the value codes
     */
    Set<Pair<String, String>> getMiraklValueCodes();

}
