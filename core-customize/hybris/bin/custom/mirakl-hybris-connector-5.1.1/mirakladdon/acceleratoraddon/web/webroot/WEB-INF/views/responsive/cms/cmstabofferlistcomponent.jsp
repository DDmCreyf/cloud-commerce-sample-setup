<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="offers" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/offers" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:if test="${fn:length(offers) > 0}">

    <c:url value="/cart/add" var="addToCartUrl"/>
    <div id="offerstab" class="tabhead">
        <a href=""><spring:theme code="product.offer.tab.name"/> (${fn:length(offers)})</a>
        <span class="glyphicon"></span>
    </div>
    <div class="tabbody" id="tab-offers-body">
        <div class="tab-offers">
            <div class="head hidden-sm hidden-xs">
                <div class="row">
                    <div class="col-md-3"><spring:theme code="product.offer.tab.price"/></div>
                    <div class="col-md-3"><spring:theme code="product.offer.tab.condition"/></div>
                    <div class="col-md-4"><spring:theme code="product.offer.tab.seller"/></div>
                </div>
            </div>
            <c:forEach items="${offers}" var="offer">

                <c:set var="minQty" value="${empty offer.minOrderQuantity ? 1 : offer.minOrderQuantity}"/>
                <c:set var="maxQty" value="${empty offer.maxOrderQuantity || offer.quantity < offer.maxOrderQuantity ? offer.quantity : offer.maxOrderQuantity}"/>
                <c:set var="stepQty" value="${empty offer.packageQuantity ? 1 : offer.packageQuantity}"/>

                <div class="row offer-line">
                    <div class="col-md-6 col-sm-7 col-xs-7">
                        <div class="row">
                            <div class="col-md-6">
                                <b>${offer.price.formattedValue}</b>
                                <c:if test="${offer.price.value != offer.originPrice.value}">
                                    <s>${offer.originPrice.formattedValue}</s>
                                </c:if>
                                <offers:offerTabVolumePrices offer="${offer}"/>
                                <c:if test="${offer.minShippingPrice.value.doubleValue() == 0}">
                                    <div><spring:theme code="product.offer.free.shipping"/></div>
                                </c:if>
                                <c:if test="${offer.minShippingPrice.value.doubleValue() > 0}">
                                    <div>+${offer.minShippingPrice.formattedValue}&nbsp;<spring:theme code="product.offer.paid.shipping"/></div>
                                </c:if>
                                <c:if test="${not empty offer.priceAdditionalInfo}">
                                    <div class="price-additional-info">${offer.priceAdditionalInfo}</div>
                                </c:if>
                                <c:set var="isPromotionsPresent" >
                                    <c:catch var="exception">${offer.promotions}</c:catch>
                                </c:set>
                                <c:if test="${not empty isPromotionsPresent}">
                                    <c:forEach items="${offer.promotions}" var="promotion">
                                        <div class="mirakl-promotion">
                                            <c:url value="${promotion.searchPageUrl}" var="offersForPromotionUrl"/>
                                            <a href="${offersForPromotionUrl}" title="<spring:theme code="product.offer.promotion.link"/>">
                                                <span class="glyphicon glyphicon-gift"></span>
                                                <span>${promotion.description}</span>
                                            </a>
                                        </div>
                                    </c:forEach>
                                </c:if>
                            </div>
                            <div class="col-md-6">
                                <c:if test="${offer.state != null}">${offer.state}<br/></c:if>
                                <c:if test="${offer.description != null}">${offer.description}<br/></c:if>
                                    ${offer.quantity}&nbsp;<spring:theme code="product.offer.in.stock"/>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-sm-5 col-xs-5">
                        <div class="row">
                            <div class="col-md-9">
                                <offers:offerRating offer="${offer}"/>
                            </div>
                            <c:if test="${not empty offer.price}">
                                <div class="col-md-3 js-offer-qty-selector" data-min="${minQty}" data-max="${maxQty}" data-step="${stepQty}" data-validation="${orderConditionValidation}">
                                    <form:form method="post" action="${addToCartUrl}" class="add_to_cart_form">
                                        <input type="hidden" name="productCodePost" value="${offer.code}"/>
                                        <input type="text" name="qty" maxlength="3" class="offer-tab-quantity js-offer-qty-input" value="1"/>
                                        <button type="submit" class="btn-primary">
                                            <span class="glyphicon glyphicon-shopping-cart"></span>
                                        </button>
                                    </form:form>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</c:if>
