<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="offer" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/offers" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:choose>
    <c:when test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' or empty topOffer}">
        <product:productPromotionSection product="${product}"/>

        <ycommerce:testId
                code="productDetails_productNamePrice_label_${product.code}">
            <product:productPricePanel product="${product}"/>
        </ycommerce:testId>

        <div class="description">${product.summary}</div>
    </c:when>
    <c:otherwise>
        <div class="bundle">
            <c:forEach items="${topOffer.promotions}" var="promotion">
                <p class="promotion">${promotion.description}</p>
            </c:forEach>
        </div>
        <div>
            <ycommerce:testId
                    code="productDetails_productNamePrice_label_${topOffer.id}">
                <offer:offerPricePanel offer="${topOffer}"/>
            </ycommerce:testId>
        </div>
        <div class="description">
            <c:choose>
                <c:when test="${not empty topOffer.description}">
                    ${topOffer.description}
                </c:when>
                <c:otherwise>
                    ${product.summary}
                </c:otherwise>
            </c:choose>
        </div>
    </c:otherwise>
</c:choose>
