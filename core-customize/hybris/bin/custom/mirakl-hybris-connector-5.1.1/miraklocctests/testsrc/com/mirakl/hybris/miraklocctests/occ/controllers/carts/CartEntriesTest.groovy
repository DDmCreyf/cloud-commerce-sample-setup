package com.mirakl.hybris.miraklocctests.occ.controllers.carts

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.carts.AbstractCartTest
import spock.lang.Unroll

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_OK

@Unroll
@ManualTest
class CartEntriesTest extends AbstractCartTest {
    static final String OFFER_PREFIX = "offer_"
    static final String TEST_OFFER_1 = "testOffer1"
    static final String TEST_OFFER_2 = "testOffer2"
    static final String TEST_OFFER_4 = "testOffer4"
    static final String TEST_OFFER_UNKNOWN = "testOfferUnknown"
    static final String OFFER_TEST_OFFER_1 = OFFER_PREFIX + TEST_OFFER_1
    static final String OFFER_TEST_OFFER_2 = OFFER_PREFIX + TEST_OFFER_2
    static final String OFFER_TEST_OFFER_4 = OFFER_PREFIX + TEST_OFFER_4
    static final String OFFER_TEST_UNKNOWN_OFFER = OFFER_PREFIX + TEST_OFFER_UNKNOWN

    def "Customer adds offer to cart for shipping when request: #requestFormat and response: #responseFormat"() {
        given: "a registered and logged in customer with cart"
        def val = createAndAuthorizeCustomerWithCart(restClient, responseFormat)
        def customer = val[0]
        def cart = val[1]

        when: "customer decides to add offer to cart"
        def response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/entries',
                query: ['fields': FIELD_SET_LEVEL_FULL],
                body: postBody,
                contentType: responseFormat,
                requestContentType: requestFormat
        )

        then: "a new entry is added to the cart"
        with(response) {
            status == SC_OK
            data.statusCode == CommerceCartModificationStatus.SUCCESS
            data.quantityAdded == 1
        }

        where:
        requestFormat | responseFormat | postBody
        URLENC        | XML            | ['code': OFFER_TEST_OFFER_1, 'qty': 1]
        URLENC        | JSON           | ['code': OFFER_TEST_OFFER_1, 'qty': 1]
        JSON          | JSON           | "{\"product\" : {\"code\" : \"${OFFER_TEST_OFFER_1}\"},\"quantity\" : 1}"
        XML           | XML            | "<?xml version=\"1.0\" encoding=\"UTF-8\"?><orderEntry><product><code>${OFFER_TEST_OFFER_1}</code></product><quantity>1</quantity></orderEntry>"
    }

    def "Customer tries to add product with wrong code when request: #requestFormat and response: #responseFormat"() {
        given: "a registered and logged in customer with cart"
        def val = createAndAuthorizeCustomerWithCart(restClient, responseFormat)
        def customer = val[0]
        def cart = val[1]

        when: "customer tries to add unknown offer to cart"
        def response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/entries',
                query: ['fields': FIELD_SET_LEVEL_FULL],
                body: postBody,
                contentType: responseFormat,
                requestContentType: requestFormat
        )

        then: "UnknownIdentifierError is returned"
        with(response) {
            status == SC_BAD_REQUEST
            data.errors[0].type == 'UnknownIdentifierError'
            data.errors[0].message == "No offer having for id [" + TEST_OFFER_UNKNOWN + "] can be found."
        }

        where:
        requestFormat | responseFormat | postBody
        URLENC        | XML            | ['code': OFFER_TEST_UNKNOWN_OFFER, 'qty': 1]
        URLENC        | JSON           | ['code': OFFER_TEST_UNKNOWN_OFFER, 'qty': 1]
        JSON          | JSON           | "{\"product\" : {\"code\" : \"${OFFER_TEST_UNKNOWN_OFFER}\"},\"quantity\" : 1}"
        XML           | XML            | "<?xml version=\"1.0\" encoding=\"UTF-8\"?><orderEntry><product><code>${OFFER_TEST_UNKNOWN_OFFER}</code></product><quantity>1</quantity></orderEntry>"
    }

    def "Customer tries to add offer with wrong quantity when request: #requestFormat and response: #responseFormat"() {
        given: "a registered and logged in customer with cart"
        def val = createAndAuthorizeCustomerWithCart(restClient, responseFormat)
        def customer = val[0]
        def cart = val[1]

        when: "customer tries to add more offer than available to cart"
        def response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/entries',
                query: ['fields': FIELD_SET_LEVEL_FULL],
                body: postBody,
                contentType: responseFormat,
                requestContentType: requestFormat
        )

        then: "a new entry is added to the cart with less than max"
        with(response) {
            status == SC_OK
            data.statusCode == CommerceCartModificationStatus.LOW_STOCK
            data.quantityAdded == 8
        }

        where:
        requestFormat | responseFormat | postBody
        URLENC        | XML            | ['code': OFFER_TEST_OFFER_2, 'qty': 10]
        URLENC        | JSON           | ['code': OFFER_TEST_OFFER_2, 'qty': 10]
        JSON          | JSON           | "{\"product\" : {\"code\" : \"${OFFER_TEST_OFFER_2}\"},\"quantity\" : 10}"
        XML           | XML            | "<?xml version=\"1.0\" encoding=\"UTF-8\"?><orderEntry><product><code>${OFFER_TEST_OFFER_2}</code></product><quantity>10</quantity></orderEntry>"
    }

    def "Customer tries to add offer with out of stock quantity when request: #requestFormat and response: #responseFormat"() {
        given: "a registered and logged in customer with cart"
        def val = createAndAuthorizeCustomerWithCart(restClient, responseFormat)
        def customer = val[0]
        def cart = val[1]

        when: "customer tries to add more offer than available to cart"
        def response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/entries',
                query: ['fields': FIELD_SET_LEVEL_FULL],
                body: postBody,
                contentType: responseFormat,
                requestContentType: requestFormat
        )

        then: "ProductLowStockException is returned"
        with(response) {
            status == SC_BAD_REQUEST
            data.errors[0].type == 'InsufficientStockError'
            data.errors[0].message == "Product [" + OFFER_TEST_OFFER_4 + "] cannot be shipped - out of stock online"
        }

        where:
        requestFormat | responseFormat | postBody
        URLENC        | XML            | ['code': OFFER_TEST_OFFER_4, 'qty': 2]
        URLENC        | JSON           | ['code': OFFER_TEST_OFFER_4, 'qty': 2]
        JSON          | JSON           | "{\"product\" : {\"code\" : \"${OFFER_TEST_OFFER_4}\"},\"quantity\" : 2}"
        XML           | XML            | "<?xml version=\"1.0\" encoding=\"UTF-8\"?><orderEntry><product><code>${OFFER_TEST_OFFER_4}</code></product><quantity>2</quantity></orderEntry>"
    }
}
