<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="productOutOfStock" required="true" type="java.lang.Boolean" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:set var="offersCount" value="${not empty product.offersSummary ? product.offersSummary.offerCount : 0}"/>
<c:set var="onlyOneOfferPurchasable" value="${productOutOfStock and offersCount == 1}"/>

<c:if test="${not empty product.offersSummary and product.offersSummary.offerCount > 0 and not onlyOneOfferPurchasable}">
	<ycommerce:testId code="searchPage_offer_states_summary_${product.code}">
		
		<c:url value="${product.url}#offerstab" var="productUrl"/>
		<div class="more-offers">
			<a href="${productUrl}"><spring:theme code="offer.states.morechoices" text="More Buying Choices"/></a>
		</div>
		
		<c:forEach var="offerStateSummary" items="${product.offersSummary.states}">
			<div class="offer-states-prices">
				<format:price priceData="${offerStateSummary.minPrice}"/>&nbsp;&nbsp;
				<spring:theme code="offer.states.availability" 
							  arguments="${offerStateSummary.stateLabel},${offerStateSummary.offerCount}" 
							  text="${offerStateSummary.stateLabel}  (${offerStateSummary.offerCount})"/>
			</div>
		</c:forEach>
		
	</ycommerce:testId>
</c:if>
