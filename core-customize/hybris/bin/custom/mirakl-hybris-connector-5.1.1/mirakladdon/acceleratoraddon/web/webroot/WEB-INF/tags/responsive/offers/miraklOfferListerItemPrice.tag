<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>

<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="offers" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/offers" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
    
<c:set var="productOutOfStock" value="${product.stock.stockLevelStatus.code eq 'outOfStock' }"/>
<c:set var="offerInStock" value="${not empty product.offersSummary and product.offersSummary.bestOffer.quantity > 0}"/>
    
<c:choose>
    <c:when test="${productOutOfStock and offerInStock}">
        <ycommerce:testId code="searchPage_price_label_${product.code}">
            <c:set var="bestOffer" value="${product.offersSummary.bestOffer}"/>
            <c:if test="${bestOffer.price.value != bestOffer.originPrice.value}">
                <span class="origin-price-value">${bestOffer.originPrice.formattedValue}</span>&nbsp;
            </c:if>
            <format:price priceData="${bestOffer.price}"/>
        </ycommerce:testId>
    </c:when>
    <c:otherwise>
        <product:productListerItemPrice product="${product}"/>
    </c:otherwise>
</c:choose>

<offers:miraklOfferStatesSummary product="${product}" productOutOfStock="${productOutOfStock}"/>