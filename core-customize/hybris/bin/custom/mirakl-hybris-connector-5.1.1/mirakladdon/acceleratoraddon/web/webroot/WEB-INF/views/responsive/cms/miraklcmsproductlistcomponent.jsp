<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/product" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:eval expression="T(com.mirakl.hybris.core.util.HybrisVersionUtils).versionChecker().getVersion()" var="hybrisVersion" />

<c:set value="product-listing product-list row" var="cssProductRow"/>

<c:if test="${hybrisVersion >= 6.1}">
    <c:set value="product__listing product__list" var="cssProductRow"/>
</c:if>

<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>

<div class="${cssProductRow}">
    <c:forEach items="${searchPageData.results}" var="product" varStatus="status">
        <product:miraklProductListerItem product="${product}" hybrisVersion="${hybrisVersion}"/>
    </c:forEach>
</div>

<div id="addToCartTitle" style="display:none">
    <div class="add-to-cart-header">
        <div class="headline">
            <span class="headline-text"><spring:theme code="basket.added.to.basket"/></span>
        </div>
    </div>
</div>

<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>

<storepickup:pickupStorePopup/>
