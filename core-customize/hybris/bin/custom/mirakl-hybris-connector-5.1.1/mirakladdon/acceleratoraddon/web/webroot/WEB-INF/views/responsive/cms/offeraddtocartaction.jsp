<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:url value="${url}" var="addToCartUrl"/>
<div id="addToCartTitle" style="display:none">
    <spring:theme code="basket.added.to.basket"/>
</div>
<form:form method="post" id="addToCartForm" class="add_to_cart_form" action="${addToCartUrl}">
    <c:if test="${not empty topOffer.code and not empty topOffer.price}">
        <c:if test="${empty showAddToCart ? true : showAddToCart}">
            <c:set var="buttonType">button</c:set>
            <input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty js-qty-selector-input" value="1">
            <c:choose>
                <c:when test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' or empty topOffer}">
                    <input type="hidden" name="productCodePost" value="${product.code}"/>
                    <c:set var="buttonType">submit</c:set>
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="productCodePost" value="${topOffer.code}"/>
                    <c:if test="${topOffer.quantity gt 0}">
                        <c:set var="buttonType">submit</c:set>
                    </c:if>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${fn:contains(buttonType, 'button')}">
                    <button type="${buttonType}"
                            class="btn btn-primary btn-block js-add-to-cart btn-icon glyphicon-shopping-cart outOfStock"
                            disabled="disabled">
                        <spring:theme code="product.variants.out.of.stock"/>
                    </button>
                </c:when>
                <c:otherwise>
                    <ycommerce:testId code="addToCartButton">
                        <button id="addToCartButton" type="${buttonType}"
                                class="btn btn-primary btn-block js-add-to-cart js-enable-btn btn-icon glyphicon-shopping-cart"
                                disabled="disabled">
                            <spring:theme code="basket.add.to.basket"/>
                        </button>
                    </ycommerce:testId>
                </c:otherwise>
            </c:choose>
        </c:if>
    </c:if>
</form:form>

