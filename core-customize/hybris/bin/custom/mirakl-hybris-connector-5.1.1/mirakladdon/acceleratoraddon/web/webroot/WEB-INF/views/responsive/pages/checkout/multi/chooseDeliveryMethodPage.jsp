<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="shipping" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/checkout/multi"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

    <div class="row">
        <div class="col-sm-6">
            <div class="checkout-headline">
                <span class="glyphicon glyphicon-lock"></span>
                <spring:theme code="checkout.multi.secure.checkout" text="Secure Checkout"/>
            </div>
            <multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
                <jsp:body>
                    <ycommerce:testId code="checkoutStepTwo">
                        <div class="checkout-shipping">
                            <shipping:deliveryAddress deliveryAddress="${cartData.deliveryAddress}"/>
                            <hr/>
                            <div class="checkout-indent">
                                <div class="checkout-shipping-items">
                                    <c:forEach items="${cartData.deliveryOrderGroups}" var="deliveryGroup" varStatus="loop">
                                        <c:url value="/checkout/multi/mirakl/delivery-method/update"
                                               var="shippingUpdateFormAction"/>
                                        <form:form id="selectDeliveryMethodForm${loop.index}" action="${shippingUpdateFormAction}"
                                                   method="post" modelAttribute="updateShippingOptionForm">
                                            <div class="form-group">
                                                <input type="hidden" name="leadTimeToShip"
                                                       value="${deliveryGroup.leadTimeToShip}"/>
                                                <input type="hidden" name="shopId" value="${deliveryGroup.shopId}"/>
                                                <c:choose>
                                                    <c:when test="${empty deliveryGroup.shopId}">
                                                        <shipping:operatorDeliveryGroupDetails
                                                                deliveryGroup="${deliveryGroup}"
                                                                deliveryMethods="${deliveryMethods}"
                                                                cartData="${cartData}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <shipping:shopDeliveryGroupDetails
                                                                deliveryGroup="${deliveryGroup}"/>
                                                    </c:otherwise>
                                                </c:choose>

                                            </div>
                                        </form:form>
                                        <hr/>
                                    </c:forEach>
                                </div>
                            </div>
                            <c:if test="${not empty offerErrors}">
                                <div class="shipping-errors">
                                    <shipping:offerErrorsDetails offerErrors="${offerErrors}"/>
                                </div>
                            </c:if>
                            <hr/>
                            <div class="row shipping-info">
                                <div class="col-sm-6">
                                    <p class=""><spring:theme code="checkout.multi.deliveryMethod.message"
                                                              text="Items will ship as soon as they are available. <br> See Order Summary for more information."/></p>
                                </div>
                                <div class="col-sm-6">
                                    <b><p><spring:theme code="checkout.multi.deliveryMethod.totalCost"
                                                        text="Total shipping cost"/>:&nbsp;${cartData.deliveryCost.formattedValue}</p>
                                    </b>
                                </div>
                            </div>
                        </div>
                        <c:url value="/checkout/multi/mirakl/delivery-method/next" var="nextStep"/>
                        <a id="deliveryMethodNext" type="button" class="btn btn-primary btn-block checkout-next"
                           href="${nextStep}">
                            <spring:theme
                                    code="checkout.multi.deliveryMethod.continue" text="Next"/></a>
                    </ycommerce:testId>
                </jsp:body>
            </multi-checkout:checkoutSteps>
        </div>


        <div class="col-sm-6 hidden-xs">
            <multi-checkout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true"
                                                 showPaymentInfo="false"
                                                 showTaxEstimate="false" showTax="true"/>
        </div>

        <div class="col-sm-12 col-lg-12">
            <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
                <cms:component component="${feature}"/>
            </cms:pageSlot>
        </div>
    </div>
</template:page>
