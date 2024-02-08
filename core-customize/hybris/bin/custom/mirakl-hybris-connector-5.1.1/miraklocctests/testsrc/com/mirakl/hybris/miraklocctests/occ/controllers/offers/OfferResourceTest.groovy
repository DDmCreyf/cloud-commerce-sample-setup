package com.mirakl.hybris.miraklocctests.occ.controllers.offers

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.SC_OK

@ManualTest
@Unroll
class OfferResourceTest extends AbstractSpockFlowTest {
    static final NUMBER_OF_ALL_OFFERS = 3
    static final String USD = "USD"
    static final String SHOP1_NAME = "shop1"
    static final String SHOP1_GRADE = "2.1"
    static final String MIRAKL_PRODUCT4 = 'miraklProduct4'
    static final String OPERATOR_PRODUCT_CODE = '137220'

    def "Search for offers for product miraklProduct4: "() {

        when: "user search for all products"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/offers',
                contentType: format,
                query: [
                        'productCode': MIRAKL_PRODUCT4,
                        'fields'     : level
                ],
                requestContentType: URLENC
        )

        then: "he gets all the requested fields"
        with(response) {
            status == SC_OK
            println data.offers
            data.offers.size() == NUMBER_OF_ALL_OFFERS
            def foundOffer5 = false;
            def foundOffer6 = false;
            def foundOffer7 = false;
            data.offers.eachWithIndex { offer, index ->
                offer.id != null
                if (index == 0) {
                    foundOffer5 = true;
                    verifyMiraklOffer5(index, offer, level)
                }
                if (index == 1) {
                    foundOffer6 = true;
                    verifyMiraklOffer6(index, offer, level)
                }
                if (index == 2) {
                    foundOffer7 = true;
                    verifyMiraklOffer7(index, offer, level)
                }
            }
            foundOffer5 && foundOffer6 && foundOffer7
        }

        where:
        format << [XML, XML, XML, JSON, JSON, JSON]
        level << [FIELD_SET_LEVEL_BASIC, FIELD_SET_LEVEL_DEFAULT, FIELD_SET_LEVEL_FULL, FIELD_SET_LEVEL_BASIC,FIELD_SET_LEVEL_DEFAULT, FIELD_SET_LEVEL_FULL]
    }

    def "Search for offers for product without offers: "() {

        when: "user search for all products"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/offers',
                contentType: format,
                query: [
                        'productCode': OPERATOR_PRODUCT_CODE,
                        'fields'     : level
                ],
                requestContentType: URLENC
        )

        then: "he gets all the requested fields"
        with(response) {
            status == SC_OK
            data.offers.isEmpty()
        }

        where:
        format << [XML, XML, XML, JSON, JSON, JSON]
        level << [FIELD_SET_LEVEL_BASIC, FIELD_SET_LEVEL_DEFAULT, FIELD_SET_LEVEL_FULL, FIELD_SET_LEVEL_BASIC,FIELD_SET_LEVEL_DEFAULT, FIELD_SET_LEVEL_FULL]
    }

    static void verifyMiraklOffer5(index, offer, level) {
        println "${index + 1}. Mirakl offer 5 id: ${offer.id}"
        offer.availableEndDate == "2022-01-19T07:21:00+0000"
        offer.availableStartDate == "2020-12-17T02:20:00+0000"
        offer.code == "offer_testOffer5"
        offer.discountEndDate == "2021-01-28T07:23:00+0000"
        offer.discountStartDate == "2020-12-24T05:33:00+0000"
        offer.id == "testOffer5"
        offer.leadTimeToShip == 1
        offer.productCode == "miraklProduct4"
        offer.quantity == 500
        offer.shopId == SHOP1_NAME
        offer.stateLabel == "New"
        offer.minShippingPrice.currencyIso == USD
        offer.minShippingPrice.value.toString() == "2.00000000"
        offer.originPrice.currencyIso == USD
        offer.originPrice.value.toString() == "50.00000000"
        offer.price.currencyIso == USD
        offer.price.value.toString() == "50.00000000"
        offer.totalPrice.currencyIso == USD
        offer.totalPrice.value.toString() == "47.00000000"
        if (level == FIELD_SET_LEVEL_BASIC) {
            offer.priceAdditionalInfo == null
            offer.description == null
            offer.shopName == null
            offer.shopGrade == null
            offer.shopEvaluationCount == null
        }
        if (level == FIELD_SET_LEVEL_BASIC|| level == FIELD_SET_LEVEL_DEFAULT) {
            offer.minShippingPriceAdditional == null
            offer.volumePrices == null
            offer.volumeOriginPrices == null
            offer.packageQuantity == null
            offer.minOrderQuantity == null
            offer.maxOrderQuantity == null
        }
        if (level == FIELD_SET_LEVEL_DEFAULT || FIELD_SET_LEVEL_FULL) {
            offer.priceAdditionalInfo == "Additional pricing information"
            offer.description == "Offer description"
            offer.shopName == SHOP1_NAME
            offer.shopGrade.toString() == SHOP1_GRADE
            offer.shopEvaluationCount == 10
        }
        if (level == FIELD_SET_LEVEL_FULL) {
            offer.minShippingPriceAdditional.currencyIso == USD
            offer.minShippingPriceAdditional.value.toString() == "1.00000000"
            offer.volumeOriginPrices[0].currencyIso == USD
            offer.volumeOriginPrices[0].minQuantity == 1
            offer.volumeOriginPrices[0].maxQuantity == 1
            offer.volumeOriginPrices[0].value.toString() == "50.00"
            offer.volumeOriginPrices[1].currencyIso == USD
            offer.volumeOriginPrices[1].minQuantity == 2
            offer.volumeOriginPrices[1].maxQuantity == 2
            offer.volumeOriginPrices[1].value.toString() == "45.00"
            offer.volumeOriginPrices[2].currencyIso == USD
            offer.volumeOriginPrices[2].minQuantity == 3
            offer.volumeOriginPrices[2].value.toString() == "40.00"
            offer.volumePrices[0].currencyIso == USD
            offer.volumePrices[0].minQuantity == 1
            offer.volumePrices[0].maxQuantity == 1
            offer.volumePrices[0].value.toString() == "45.00"
            offer.volumePrices[1].currencyIso == USD
            offer.volumePrices[1].minQuantity == 2
            offer.volumePrices[1].maxQuantity == 2
            offer.volumePrices[1].value.toString() == "40.00"
            offer.volumePrices[2].currencyIso == USD
            offer.volumePrices[2].minQuantity == 3
            offer.volumePrices[2].maxQuantity == null
            offer.volumePrices[2].value.toString() == "35.00"
            offer.packageQuantity == null
            offer.minOrderQuantity == 1
            offer.maxOrderQuantity == 10
        }
    }

    static void verifyMiraklOffer6(index, offer, level) {
        println "${index + 1}. Mirakl offer 6 id: ${offer.id}"
        offer.availableEndDate == "2023-01-19T07:21:00+0000"
        offer.availableStartDate == "2020-12-18T02:20:00+0000"
        offer.code == "offer_testOffer6"
        offer.discountEndDate == "2021-01-29T07:23:00+0000"
        offer.discountStartDate == "2020-12-24T05:34:00+0000"
        offer.id == "testOffer6"
        offer.leadTimeToShip == 2
        offer.productCode == "miraklProduct4"
        offer.quantity == 400
        offer.shopId == SHOP1_NAME
        offer.stateLabel == "New"
        offer.minShippingPrice.currencyIso == USD
        offer.minShippingPrice.value.toString() == "3.00000000"
        offer.originPrice.currencyIso == USD
        offer.originPrice.value.toString() == "999.00000000"
        offer.price.currencyIso == USD
        offer.price.value.toString() == "999.00000000"
        offer.totalPrice.currencyIso == USD
        offer.totalPrice.value.toString() == "992.00000000"
        if (level == FIELD_SET_LEVEL_BASIC) {
            offer.priceAdditionalInfo == null
            offer.description == null
            offer.shopName == null
            offer.shopGrade == null
            offer.shopEvaluationCount == null
        }
        if (level == FIELD_SET_LEVEL_BASIC || level == FIELD_SET_LEVEL_DEFAULT) {
            offer.minShippingPriceAdditional == null
            offer.volumePrices == null
            offer.volumeOriginPrices == null
            offer.packageQuantity == null
            offer.minOrderQuantity == null
            offer.maxOrderQuantity == null
        }
        if (level == FIELD_SET_LEVEL_DEFAULT || "FULL") {
            offer.priceAdditionalInfo == "Additional pricing information 2"
            offer.description == "Offer description 2"
            offer.shopName == SHOP1_NAME
            offer.shopGrade.toString() == SHOP1_GRADE
            offer.shopEvaluationCount == 10
        }
        if (level == FIELD_SET_LEVEL_FULL) {
            offer.minShippingPriceAdditional.currencyIso == USD
            offer.minShippingPriceAdditional.value.toString() == "0E-8"
            offer.volumeOriginPrices == null
            offer.volumePrices == null
            offer.packageQuantity == null
            offer.minOrderQuantity == 1
            offer.maxOrderQuantity == 20
        }
    }

    static void verifyMiraklOffer7(index, offer, level) {
        println "${index + 1}. Mirakl offer 7 id: ${offer.id}"
        offer.availableEndDate == "2024-01-19T07:21:00+0000"
        offer.availableStartDate == "2020-12-19T02:20:00+0000"
        offer.code == "offer_testOffer7"
        offer.description == "Offer description 3"
        offer.discountEndDate == "2021-01-30T07:23:00+0000"
        offer.discountStartDate == "2020-12-24T05:35:00+0000"
        offer.id == "testOffer7"
        offer.leadTimeToShip == 3
        offer.priceAdditionalInfo == "Additional pricing information 3"
        offer.productCode == "miraklProduct4"
        offer.quantity == 300
        offer.shopId == SHOP1_NAME
        offer.stateLabel == "Used - Like New"
        offer.minShippingPrice.currencyIso == USD
        offer.minShippingPrice.value.toString() == "2.00000000"
        offer.originPrice.currencyIso == USD
        offer.originPrice.value.toString() == "50.00000000"
        offer.price.currencyIso == USD
        offer.price.value.toString() == "50.00000000"
        offer.totalPrice.currencyIso == USD
        offer.totalPrice.value.toString() == "47.00000000"
        if (level == FIELD_SET_LEVEL_BASIC) {
            offer.priceAdditionalInfo == null
            offer.description == null
            offer.shopName == null
            offer.shopGrade == null
            offer.shopEvaluationCount == null
        }
        if (level == FIELD_SET_LEVEL_BASIC || level == FIELD_SET_LEVEL_DEFAULT) {
            offer.minShippingPriceAdditional == null
            offer.volumePrices == null
            offer.volumeOriginPrices == null
            offer.packageQuantity == null
            offer.minOrderQuantity == null
            offer.maxOrderQuantity == null
        }
        if (level == FIELD_SET_LEVEL_DEFAULT || FIELD_SET_LEVEL_FULL) {
            offer.priceAdditionalInfo == "Additional pricing information 3"
            offer.description == "Offer description 3"
            offer.shopName == SHOP1_NAME
            offer.shopGrade.toString() == SHOP1_GRADE
            offer.shopEvaluationCount == 10
        }
        if (level == FIELD_SET_LEVEL_FULL) {
            offer.minShippingPriceAdditional.currencyIso == USD
            offer.minShippingPriceAdditional.value.toString() == "1.00000000"
            offer.volumeOriginPrices[0].currencyIso == USD
            offer.volumeOriginPrices[0].minQuantity == 1
            offer.volumeOriginPrices[0].maxQuantity == 1
            offer.volumeOriginPrices[0].value.toString() == "50.00"
            offer.volumeOriginPrices[1].currencyIso == USD
            offer.volumeOriginPrices[1].minQuantity == 2
            offer.volumeOriginPrices[1].maxQuantity == 2
            offer.volumeOriginPrices[1].value.toString() == "45.00"
            offer.volumeOriginPrices[2].currencyIso == USD
            offer.volumeOriginPrices[2].currencyIso == USD
            offer.volumeOriginPrices[2].minQuantity == 3
            offer.volumeOriginPrices[2].value.toString() == "40.00"
            offer.volumePrices[0].currencyIso == USD
            offer.volumePrices[0].minQuantity == 1
            offer.volumePrices[0].maxQuantity == 1
            offer.volumePrices[0].value.toString() == "45.00"
            offer.volumePrices[1].currencyIso == USD
            offer.volumePrices[1].minQuantity == 2
            offer.volumePrices[1].maxQuantity == 2
            offer.volumePrices[1].value.toString() == "40.00"
            offer.volumePrices[2].currencyIso == USD
            offer.volumePrices[2].minQuantity == 3
            offer.volumePrices[2].maxQuantity == null
            offer.volumePrices[2].value.toString() == "35.00"
            offer.packageQuantity == null
            offer.minOrderQuantity == 2
            offer.maxOrderQuantity == 30
        }
    }

}
