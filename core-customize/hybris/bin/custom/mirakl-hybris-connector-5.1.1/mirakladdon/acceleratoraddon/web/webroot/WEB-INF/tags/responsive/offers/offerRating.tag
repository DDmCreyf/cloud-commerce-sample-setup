<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="offer" required="true" type="com.mirakl.hybris.beans.OfferData" %>

<c:url value="/sellers/${offer.shopCode}" var="sellerUrl"/>

<div class="row">
    <div class="col-md-5">
        <a class="link-to-shop" href="${sellerUrl}">${offer.shopName}</a>
    </div>
    <div class="col-md-7">
        <div class="rating js-miraklRatingCalc shop-grade"
             data-rating='{"rating":"${offer.shopGrade}","total":5}'>
            <div class="rating-stars">
                <span class="js-miraklRatingIcon glyphicon glyphicon-star"></span>
            </div>
        </div>
    </div>
    <div class="col-md-12 hidden-sm hidden-xs">
        <c:if test="${offer.shopEvaluationCount gt 0}">
            <fmt:formatNumber type="percent" maxFractionDigits="0" value="${offer.shopGrade/5}"/>
            <spring:theme code="product.offer.based.on"/>&nbsp;${offer.shopEvaluationCount}&nbsp;<spring:theme code="product.offer.evaluations"/>
        </c:if>
        <c:if test="${empty offer.shopEvaluationCount or offer.shopEvaluationCount eq 0}">
            <spring:theme code="product.offer.no.evaluation"/>
        </c:if>
    </div>
</div>
