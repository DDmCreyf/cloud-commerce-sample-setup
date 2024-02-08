<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="hybrisVersion" required="true" type="java.lang.Double" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="offers" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/offers" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set value="product-item" var="cssProductItem"/>
<c:set value="thumb" var="cssThumb"/>
<c:set value="name" var="cssName"/>
<c:set value="price-panel" var="cssPricePanel"/>
<c:set value="promo" var="cssPromo"/>
<c:set value="price" var="cssPrice"/>
<c:set value="description" var="cssDescription"/>

<c:choose>
    <c:when test="${hybrisVersion >= 6.1}">
        <c:set value="product__list--item" var="cssProductItem"/>
        <c:set value="product__list--thumb" var="cssThumb"/>
        <c:set value="product__list--name" var="cssName"/>
        <c:set value="product__list--price-panel" var="cssPricePanel"/>
        <c:set value="product__listing--promo" var="cssPromo"/>
        <c:set value="product__listing--price" var="cssPrice"/>
        <c:set value="product__listing--description" var="cssDescription"/>
    </c:when>
</c:choose>

<spring:theme code="text.addToCart" var="addToCartText"/>
<c:url value="${product.url}" var="productUrl"/>

<c:set value="${not empty product.potentialPromotions}" var="hasPromotion"/>

<li class="${cssProductItem}">
    <ycommerce:testId code="test_searchPage_wholeProduct">
        
        <a class="${cssThumb}" href="${fn:escapeXml(productUrl)}" title="${fn:escapeXml(product.name)}">
            <product:productPrimaryImage product="${product}" format="thumbnail"/>
        </a>
        <ycommerce:testId code="searchPage_productName_link_${product.code}">
            <a class="${cssName}" href="${fn:escapeXml(productUrl)}">${ycommerce:sanitizeHTML(product.name)}</a>
        </ycommerce:testId>

        <div class="${cssPricePanel}">
            <c:if test="${not empty product.potentialPromotions}">
                <div class="${cssPromo}">
                    <c:forEach items="${product.potentialPromotions}" var="promotion">
                        ${ycommerce:sanitizeHTML(promotion.description)}
                    </c:forEach>
                </div>
            </c:if>

            <ycommerce:testId code="searchPage_price_label_${product.code}">
                <div class="${cssPrice}">
                    <offers:miraklOfferListerItemPrice product="${product}"/>
                </div>
            </ycommerce:testId>
        </div>

        <c:if test="${not empty product.summary}">
            <div class="${cssDescription}">${ycommerce:sanitizeHTML(product.summary)}</div>
        </c:if>



        <c:set var="product" value="${product}" scope="request"/>
        <c:set var="addToCartText" value="${addToCartText}" scope="request"/>
        <c:set var="addToCartUrl" value="${addToCartUrl}" scope="request"/>

        <div class="addtocart">
            <div id="actions-container-for-${fn:escapeXml(component.uid)}" class="row">
                <action:actions element="div" parentComponent="${component}"  />
            </div>
        </div>

    </ycommerce:testId>
</li>







