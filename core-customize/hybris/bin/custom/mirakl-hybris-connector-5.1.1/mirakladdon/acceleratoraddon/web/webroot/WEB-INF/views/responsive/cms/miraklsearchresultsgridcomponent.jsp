<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/product" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/product" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:eval expression="T(com.mirakl.hybris.core.util.HybrisVersionUtils).versionChecker().getVersion()" var="hybrisVersion" />

<c:set value="product-listing product-grid row" var="cssProductRow"/>
<c:set value="col-md-12" var="cssProductListWrapper"/>

<c:if test="${hybrisVersion >= 6.1}">
    <c:set value="product__listing product__grid" var="cssProductRow"/>
    <c:set value="product__list--wrapper" var="cssProductListWrapper"/>
</c:if>

<div class="${cssProductListWrapper}">
    <div class="results">
        <h1><spring:theme code="search.page.searchText" arguments="${searchPageData.freeTextSearch}"/></h1>
    </div>

    <nav:searchSpellingSuggestion spellingSuggestion="${searchPageData.spellingSuggestion}" />

    <nav:pagination top="true"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"  searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>

    <ul class="<c:out value="${cssProductRow} ${displayOfferStates ? '' : 'offer-states-hidden'}"/>">
        <c:forEach items="${searchPageData.results}" var="product" varStatus="status">
            <product:miraklProductListerGridItem product="${product}"/>
        </c:forEach>
    </ul>

    <div id="addToCartTitle" class="display-none">
        <div class="add-to-cart-header">
            <div class="headline">
                <span class="headline-text"><spring:theme code="basket.added.to.basket"/></span>
            </div>
        </div>
    </div>

    <nav:pagination top="false"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"  searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>
</div>
