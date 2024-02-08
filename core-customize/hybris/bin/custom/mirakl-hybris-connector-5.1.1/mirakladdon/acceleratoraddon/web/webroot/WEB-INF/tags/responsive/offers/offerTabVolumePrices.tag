<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="offer" required="true" type="com.mirakl.hybris.beans.OfferData" %>

<c:if test="${not empty offer.volumePrices}">
<c:set var="expandableVolPriceId" value="volume-price-${offer.id}"/>
&nbsp;<a href="#" class="js-expand-button" data-expandable="${expandableVolPriceId}"><spring:theme code="product.offer.volumePrices.link"/></a>
<div class="offer-volume-prices collapsed" id="${expandableVolPriceId}">
<table>
    <thead>
        <tr>
            <th><spring:theme code="product.volumePrices.column.qa"/></th>
            <th><spring:theme code="product.volumePrices.column.price"/></th>
        </tr>
    </thead>
    <tbody>
    <c:forEach begin="0" end="${fn:length(offer.volumePrices) - 1}" varStatus="loop">
        <c:set var="volPrice" value="${offer.volumePrices[loop.index]}"/>
        <c:set var="volOriginPrice" value="${offer.volumeOriginPrices[loop.index]}"/>
        <tr>
            <td>
            <c:choose>
                <c:when test="${empty volPrice.maxQuantity}">
                    ${volPrice.minQuantity}+
                </c:when>
                <c:otherwise>
                    ${volPrice.minQuantity}-${volPrice.maxQuantity}
                </c:otherwise>
            </c:choose>
            </td>
            <td>
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
</c:if>
