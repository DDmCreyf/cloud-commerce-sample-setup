<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:eval expression="T(com.mirakl.hybris.core.util.HybrisVersionUtils).versionChecker().getVersion()" var="hybrisVersion" />

<c:if test="${not product.multidimensional }">
    <c:url value="/cart/add" var="addToCartUrl"/>
    <form:form id="addToCartForm${product.code}" action="${addToCartUrl}" method="post" class="add_to_cart_form">
        <c:set var="productOutOfStock" value="${product.stock.stockLevelStatus.code eq 'outOfStock' }"/>
        <c:set var="offerAvailable"

               value="${not empty product.offersSummary and product.offersSummary.bestOffer.quantity > 0 and not empty product.offersSummary.bestOffer.price}"/>
        <ycommerce:testId code="addToCartButton">

            <input type="hidden" name="productNamePost" value="${fn:escapeXml(product.name)}"/>

            <c:choose>
                <c:when test="${productOutOfStock and offerAvailable}">
                    <c:if test="${product.offersSummary.bestOffer.minPurchasableQty > 0}">
                        <input type="hidden" name="qty" value="${fn:escapeXml(product.offersSummary.bestOffer.minPurchasableQty)}"/>
                    </c:if>
                    <input type="hidden" name="productCodePost" value="${fn:escapeXml(product.offersSummary.bestOffer.code)}"/>
                    <input type="hidden" name="productPostPrice" value="${fn:escapeXml(product.offersSummary.bestOffer.price.value)}"/>
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="productCodePost" value="${fn:escapeXml(product.code)}"/>
                    <input type="hidden" name="productPostPrice" value="${fn:escapeXml(product.price.value)}"/>
                    <c:if test="${hybrisVersion >= 6.1 and product.configurable}">
                        <c:choose>
                            <c:when test="${hybrisVersion >= 6.2}">
                                <c:url value="${product.url}/configuratorPage/${configuratorType}" var="configureProductUrl"/>
                            </c:when>
                            <c:otherwise>
                                <c:url value="${product.url}/configure/${configuratorType}" var="configureProductUrl"/>
                            </c:otherwise>
                        </c:choose>
                        <button id="configureProduct" type="button" class="btn btn-primary btn-block js-enable-btn
                                <c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' }"> out-of-stock</c:if>"
                                disabled="disabled"
                                onclick="location.href='${fn:escapeXml(configureProductUrl)}'">
                            <spring:theme code="basket.configure.product"/>
                        </button>
                    </c:if>
                </c:otherwise>
            </c:choose>

            <button type="submit"
                    class="btn btn-primary btn-block glyphicon glyphicon-shopping-cart js-enable-btn
                    ${productOutOfStock and not offerAvailable ? 'out-of-stock' : ''}"
                    <c:if test="${productOutOfStock and not offerAvailable}">aria-disabled="true"</c:if>
                    disabled="disabled"></button>
        </ycommerce:testId>
    </form:form>
</c:if>
