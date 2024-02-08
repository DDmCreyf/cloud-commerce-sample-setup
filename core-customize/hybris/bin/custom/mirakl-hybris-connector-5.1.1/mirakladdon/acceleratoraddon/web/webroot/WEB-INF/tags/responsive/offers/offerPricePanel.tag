<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ attribute name="offer" required="true" type="com.mirakl.hybris.beans.OfferData" %>

<c:choose>
    <c:when test="${empty offer.volumePrices}">
        <div class="row">
            <div class="col-sm-4 price">
                <format:fromPrice priceData="${offer.price}"/>
            </div>
            <c:if test="${offer.price.value != offer.originPrice.value}">
                <div class="col-sm-4 price-origin col-sm-pull-1">
                    <format:fromPrice priceData="${offer.originPrice}"/>
                </div>
            </c:if>
            <div class="col-sm-4 price-description col-sm-pull-2">
                ${offer.priceAdditionalInfo}
            </div>
            <div class="col-sm-12">
                +<b><format:fromPrice priceData="${offer.minShippingPrice}"/></b>&nbsp;<spring:theme code="product.offer.paid.shipping"/>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="offer-volume-prices">
            <div class="price-description">
                ${offer.priceAdditionalInfo}
            </div>
            <table class="volume__prices" cellpadding="0" cellspacing="0" border="0">
            <thead>
                <th class="volume__prices-quantity"><spring:theme code="product.volumePrices.column.qa"/></th>
                <th class="volume__price-amount"><spring:theme code="product.volumePrices.column.price"/></th>
            </thead>
            <tbody>
            <c:forEach begin="0" end="${fn:length(offer.volumePrices) - 1}" varStatus="loop">
                <c:set var="volPrice" value="${offer.volumePrices[loop.index]}"/>
                <c:set var="volOriginPrice" value="${offer.volumeOriginPrices[loop.index]}"/>
                <tr>
                    <td class="volume__price-quantity">
                    <c:choose>
                        <c:when test="${empty volPrice.maxQuantity}">
                            ${volPrice.minQuantity}+
                        </c:when>
                        <c:otherwise>
                            ${volPrice.minQuantity}-${volPrice.maxQuantity}
                        </c:otherwise>
                    </c:choose>
                    </td>
                    <td class="volume__price-amount text-right">
                        <c:if test="${volPrice.value ne volOriginPrice.value}">
                            <s>${fn:escapeXml(volOriginPrice.formattedValue)}</s>
                        </c:if>
                        ${fn:escapeXml(volPrice.formattedValue)}
                    </td>
                </tr>
            </c:forEach>
            </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>
