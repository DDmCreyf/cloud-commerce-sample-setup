<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="hybrisversion" required="true" type="java.lang.Double" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>
<%@ taglib prefix="miraklOrder" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/orders" %>

<c:set value="well well-tertiary" var="cssWell"/>
<c:set value="product-list" var="cssProductList"/>
<c:set value="item-list-header" var="cssItemListHeader"/>

<c:if test="${hybrisversion >= 6.1}">
    <c:set value="item-list" var="cssProductList"/>
    <c:set value="well well-quinary well-xs" var="cssWell"/>
</c:if>
<c:if test="${hybrisversion >= 6.2}">
    <c:set value="item__list" var="cssProductList"/>
    <c:set value="item__list--header" var="cssItemListHeader"/>
</c:if>

<c:forEach items="${order.unconsignedEntries}" var="entry" varStatus="loop">
    <div class="${cssWell}">
        <div class="well-headline orderPending">
            <spring:theme code="text.account.order.unconsignedEntry.status.pending" />
        </div>

        <c:choose>
            <c:when test="${entry.deliveryPointOfService ne null}">
                <div class="well-content col-sm-12 col-md-9">
                    <order:storeAddressItem deliveryPointOfService="${entry.deliveryPointOfService}" />
                </div>
            </c:when>
            <c:otherwise>
                <div class="well-content col-md-5 order-ship-to">
                    <div class="label-order"><spring:theme code="text.account.order.shipto" text="Ship To" /></div>
                    <div class="value-order"><order:addressItem address="${orderData.deliveryAddress}"/></div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <ul class="${cssProductList}">
        <c:if test="${hybrisversion >= 6.1}">
            <li class="hidden-xs hidden-sm">
                <ul class="${cssItemListHeader}">
                    <li class="product-list-header"><spring:theme code="order.consignment.products" /></li>
                </ul>
            </li>
        </c:if>
        <miraklOrder:miraklOrderEntryDetails orderEntry="${entry}" order="${order}" itemIndex="${loop.index}" hybrisversion="${hybrisversion}"/>
    </ul>
</c:forEach>

