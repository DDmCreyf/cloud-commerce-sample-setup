<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>
<%@ taglib prefix="offers" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/offers" %>


<c:set var="minQty" value="${empty topOffer.minOrderQuantity ? 1 : topOffer.minOrderQuantity}"/>
<c:set var="maxQty" value="${empty topOffer.maxOrderQuantity || topOffer.quantity <  topOffer.maxOrderQuantity ? topOffer.quantity : topOffer.maxOrderQuantity}"/>
<c:set var="stepQty" value="${empty topOffer.packageQuantity ? 1 : topOffer.packageQuantity}"/>
<c:set var="effectiveMinQty" value="${minQty % stepQty == 0 ? minQty : minQty + stepQty - minQty % stepQty}"/>

<div class="addtocart-component js-offer-qty-selector" data-min="${minQty}" data-max="${maxQty}" data-step="${stepQty}" data-validation="${orderConditionValidation}">
    <div class="qty-selector input-group">
        <span class="input-group-btn">
            <button class="btn btn-default js-offer-qty-minus" type="button">
                <span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
            </button>
        </span>
        <input type="text" maxlength="3" class="form-control js-offer-qty-input" size="1" value='${effectiveMinQty}' name="pdpAddtoCartInput" id="pdpAddtoCartInput"/>
        <span class="input-group-btn">
            <button class="btn btn-default js-offer-qty-plus" type="button">
                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
            </button>
        </span>
    </div>
    <div class="order-conditions">
    <c:if test="${not empty topOffer.minOrderQuantity}">
        <span style="margin-right: 5px;"><spring:theme code="product.offer.min.qty"/>: <b>${topOffer.minOrderQuantity}</b></span>
    </c:if>
    <c:if test="${not empty topOffer.maxOrderQuantity}">
        <span style="margin-right: 5px;"><spring:theme code="product.offer.max.qty"/>: <b>${topOffer.maxOrderQuantity}</b></span>
    </c:if>
    <c:if test="${not empty topOffer.packageQuantity}">
        <span style="margin-right: 5px;"><spring:theme code="product.offer.package.qty"/>: <b>${topOffer.packageQuantity}</b></span>
    </c:if>
    </div>
    <c:if test="${topOffer.quantity gt 0}">
        <c:set var="offerQuantity">${topOffer.quantity}&nbsp;
            <spring:theme code="product.variants.in.stock"/>
        </c:set>
    </c:if>
    <div class="stock-wrapper clearfix">
        <c:if test="${not empty topOffer.state}">
            ${topOffer.state}&nbsp;-&nbsp;
        </c:if>${offerQuantity}
    </div>
    <spring:theme code="checkout.summary.deliveryMode.soldBy" text="Sold by"/>&nbsp;
    <offers:offerRating offer="${topOffer}"/>
    <div class="actions">
        <action:actions element="div" parentComponent="${component}"/>
    </div>
</div>
