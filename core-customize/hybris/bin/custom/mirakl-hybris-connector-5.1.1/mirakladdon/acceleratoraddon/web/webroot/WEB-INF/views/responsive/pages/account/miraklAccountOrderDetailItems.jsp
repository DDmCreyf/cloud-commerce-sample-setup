<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/orders" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:eval expression="T(com.mirakl.hybris.core.util.HybrisVersionUtils).versionChecker().getVersion()" var="hybrisVersion" />

<c:set var="isMarketplaceConsignment" value="${consignment.marketplaceStatus ne null}"/>
<c:set var="consignmentStatus"
       value="${(consignment.marketplaceStatus ne null)?consignment.marketplaceStatus:consignment.statusDisplay}"/>

<div class="account-orderdetail ${hybrisVersion >= 6.1 ? 'account-consignment' : ''}">
    <ycommerce:testId code="orderDetail_itemList_section">

        <c:if test="${not empty orderData.unconsignedEntries}">
            <order:miraklOrderUnconsignedEntries order="${orderData}" hybrisversion="${hybrisVersion}"/>
        </c:if>

        <c:forEach items="${orderData.consignments}" var="consignment">
            <c:if test="${consignmentStatus eq 'WAITING' or consignmentStatus eq 'PICKPACK' or consignmentStatus eq 'READY'}">
                <div class="productItemListHolder fulfilment-states-${consignment.status.code}">
                    <order:miraklAccountOrderDetailsItem order="${orderData}" consignment="${consignment}" inProgress="true" hybrisversion="${hybrisVersion}"/>
                </div>
            </c:if>
        </c:forEach>

        <c:forEach items="${orderData.consignments}" var="consignment">
            <c:if test="${consignmentStatus ne 'WAITING' and consignmentStatus ne 'PICKPACK' and consignmentStatus ne 'READY'}">
                <div class="productItemListHolder fulfilment-states-${consignment.status.code}">
                    <order:miraklAccountOrderDetailsItem order="${orderData}" consignment="${consignment}" hybrisversion="${hybrisVersion}"/>
                </div>
            </c:if>
        </c:forEach>

    </ycommerce:testId>
</div>
