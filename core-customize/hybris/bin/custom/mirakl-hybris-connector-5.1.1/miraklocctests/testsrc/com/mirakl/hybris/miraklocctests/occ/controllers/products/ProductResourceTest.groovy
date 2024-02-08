package com.mirakl.hybris.miraklocctests.occ.controllers.products

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.SC_OK

@ManualTest
@Unroll
class ProductResourceTest extends AbstractSpockFlowTest {
    static final ALL_PRODUCTS_PAGE_SIZE = 100
    static final NUMBER_OF_ALL_PRODUCTS = 49

    def "Search for all products with offerSummary in FULL: "() {

        when: "user search for all products"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/products/search',
                contentType: format,
                query: [
                        'pageSize': ALL_PRODUCTS_PAGE_SIZE,
                        'fields'  : 'products(code,offersSummary(FULL))'
                ],
                requestContentType: URLENC
        )

        then: "he gets all the requested fields"
        with(response) {
            ALL_PRODUCTS_PAGE_SIZE >= NUMBER_OF_ALL_PRODUCTS
            status == SC_OK
            data.products.size() == NUMBER_OF_ALL_PRODUCTS
            def foundProduct1 = false;
            def foundProduct3 = false;
            data.products.eachWithIndex { product, index ->
                product.code != null
                if (product.code == "miraklProduct1") {
                    foundProduct1 = true;
                    verifyMiraklProduct1(index, product)
                } else if (product.code == "miraklProduct3") {
                    foundProduct3 = true;
                    verifyMiraklProduct3(index, product)
                } else {
                    product.offersSummary == null
                }
            }
            foundProduct1 && foundProduct3
            data.pagination
            data.pagination.currentPage == 0
            data.pagination.pageSize == ALL_PRODUCTS_PAGE_SIZE
            data.pagination.totalResults.toInteger() == NUMBER_OF_ALL_PRODUCTS
            data.pagination.totalPages.toInteger() == Math.ceil(NUMBER_OF_ALL_PRODUCTS / ALL_PRODUCTS_PAGE_SIZE).toInteger()
            response.containsHeader(HEADER_TOTAL_COUNT)
            response.getFirstHeader(HEADER_TOTAL_COUNT).getValue().toInteger() == NUMBER_OF_ALL_PRODUCTS
        }

        where:
        format << [XML, JSON]
    }

    def "Search for all products with offerSummary in DEFAULT: "() {

        when: "user search for all products"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/products/search',
                contentType: format,
                query: [
                        'pageSize': ALL_PRODUCTS_PAGE_SIZE,
                        'fields'  : 'products(code,offersSummary(DEFAULT))'
                ],
                requestContentType: URLENC
        )

        then: "he gets all the requested fields"
        with(response) {
            ALL_PRODUCTS_PAGE_SIZE >= NUMBER_OF_ALL_PRODUCTS
            status == SC_OK
            data.products.size() == NUMBER_OF_ALL_PRODUCTS
            data.products.eachWithIndex { product, index ->
                product.code != null
                product.offersSummary == null
            }
            data.pagination
            data.pagination.currentPage == 0
            data.pagination.pageSize == ALL_PRODUCTS_PAGE_SIZE
            data.pagination.totalResults.toInteger() == NUMBER_OF_ALL_PRODUCTS
            data.pagination.totalPages.toInteger() == Math.ceil(NUMBER_OF_ALL_PRODUCTS / ALL_PRODUCTS_PAGE_SIZE).toInteger()
            response.containsHeader(HEADER_TOTAL_COUNT)
            response.getFirstHeader(HEADER_TOTAL_COUNT).getValue().toInteger() == NUMBER_OF_ALL_PRODUCTS
        }

        where:
        format << [XML, JSON]
    }

    static void verifyMiraklProduct1(index, product) {
        println "${index + 1}. Mirakl product 1 code: ${product.code}"
        println "${index + 1}. Mirakl product 1 offersSummary: ${product.offersSummary}"
        product.offersSummary.offerCount == 1
        product.offersSummary.bestOffer.code == "offer_testOffer1"
        product.offersSummary.bestOffer.minPurchasableQty == 1
        product.offersSummary.bestOffer.price.currencyIso == "USD"
        product.offersSummary.bestOffer.price.value == 999.00
        product.offersSummary.bestOffer.quantity == 3
        product.offersSummary.bestOffer.shopGrade == 2.1
        product.offersSummary.bestOffer.shopId == "shop1"
        product.offersSummary.bestOffer.shopName == "shop1"
        product.offersSummary.bestOffer.stateCode == 1
        product.offersSummary.bestOffer.originPrice.currencyIso == "USD"
        product.offersSummary.bestOffer.originPrice.value == 999.00
        product.offersSummary.bestOffer.totalPrice.currencyIso == "USD"
        product.offersSummary.bestOffer.totalPrice.value == 999.00
        product.offersSummary.bestOffer.stateLabel == "Used - Like New"
        product.offersSummary.bestOffer.allOfferPricingsJSON == "[{\"price\":999.00,\"unit_origin_price\":999.00,\"volume_prices\":[{\"unit_origin_price\":999.00,\"quantity_threshold\":1}]}]"
        product.offersSummary.states.size() == 1
        product.offersSummary.states[0].minPrice.currencyIso == "USD"
        product.offersSummary.states[0].minPrice.value == 999.00
        product.offersSummary.states[0].offerCount == 1
        product.offersSummary.states[0].stateCode == 1
        product.offersSummary.states[0].stateLabel == "Used - Like New"
    }

    static void verifyMiraklProduct3(index, product) {
        println "${index + 1}. Mirakl product 3 code: ${product.code}"
        println "${index + 1}. Mirakl product 3 offersSummary: ${product.offersSummary}"
        product.offersSummary.offerCount == 2
        product.offersSummary.bestOffer.code == "offer_testOffer2"
        product.offersSummary.bestOffer.minPurchasableQty == 1
        product.offersSummary.bestOffer.price.currencyIso == "USD"
        product.offersSummary.bestOffer.price.value == 999.00
        product.offersSummary.bestOffer.quantity == 8
        product.offersSummary.bestOffer.shopGrade == 3.4
        product.offersSummary.bestOffer.shopId == "shop2"
        product.offersSummary.bestOffer.shopName == "shop2"
        product.offersSummary.bestOffer.stateCode == 11
        product.offersSummary.bestOffer.originPrice.currencyIso == "USD"
        product.offersSummary.bestOffer.originPrice.value == 999.00
        product.offersSummary.bestOffer.totalPrice.currencyIso == "USD"
        product.offersSummary.bestOffer.totalPrice.value == 999.00
        product.offersSummary.bestOffer.stateLabel == "New"
        product.offersSummary.bestOffer.allOfferPricingsJSON == "[{\"price\":989.00,\"unit_origin_price\":999.00,\"volume_prices\":[{\"unit_origin_price\":979.00,\"quantity_threshold\":2}]}]"
        product.offersSummary.states.size() == 2
        product.offersSummary.states[0].minPrice.currencyIso == "USD"
        product.offersSummary.states[0].minPrice.value == 999.00
        product.offersSummary.states[0].offerCount == 1
        product.offersSummary.states[0].stateCode == 11
        product.offersSummary.states[0].stateLabel == "New"
        product.offersSummary.states[1].minPrice.currencyIso == "USD"
        product.offersSummary.states[1].minPrice.value == 999.00
        product.offersSummary.states[1].offerCount == 1
        product.offersSummary.states[1].stateCode == 1
        product.offersSummary.states[1].stateLabel == "Used - Like New"
    }

}
