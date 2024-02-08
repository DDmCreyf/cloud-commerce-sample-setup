<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/responsive/nav/breadcrumb" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="miraklutil" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/util" %>

<c:url value="/sellers/${shop.id}/reviews/" var="shopReviewsUrl"/>
<c:url value="${shop.offersPageUrl}" var="shopOffersUrl"/>

<template:page pageTitle="${pageTitle}">
    <div class="container__full">
        <div class="product-details">
            <div class="name">${shop.name}
                <c:if test="${shop.premium}">
                    <span class="sku"><spring:theme code="shop.info.premium"/></span>
                </c:if>
            </div>
        </div>

        <div class="row">
            <div class="col-md-3">
                <c:choose>
                    <c:when test="${empty shop.banner}">
                        <div><theme:image code="img.missingShopImage" alt="${fn:escapeXml(shop.name)}" title="${fn:escapeXml(shop.name)}"/></div>
                    </c:when>
                    <c:otherwise>
                        <img src="${shop.banner}" alt="${fn:escapeXml(shop.name)}" title="${fn:escapeXml(shop.name)}"/>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="col-md-4 shop-stats">
                <table>
                    <tr>
                        <td><b><spring:theme code="shop.info.evaluations"/></b></td>
                        <td>
                            <c:if test="${shop.evaluationCount gt 0}">
                                <fmt:formatNumber type="percent" maxFractionDigits="0" value="${shop.grade/5}"/>&nbsp;
                                <spring:theme code="product.offer.based.on"/>
                                &nbsp;${shop.evaluationCount}&nbsp;
                                <spring:theme code="product.offer.evaluations"/>
                            </c:if>
                            <c:if test="${empty shop.evaluationCount or shop.evaluationCount eq 0}">
                                <spring:theme code="product.offer.no.evaluation"/>
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td><b><spring:theme code="shop.info.approval.delay"/></b></td>
                        <td>
                            <c:if test="${not empty shop.approvalDelay}">
                                <miraklutil:durationTime time="${shop.approvalDelay}"/>
                            </c:if>
                            <c:if test="${empty shop.approvalDelay}">
                                <spring:theme code="shop.info.unknown.value"/>
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td><b><spring:theme code="shop.info.approval.rate"/></b></td>
                        <td>
                            <c:if test="${not empty shop.approvalRate}">
                                <fmt:formatNumber type="percent" maxFractionDigits="0" value="${shop.approvalRate}"/>
                            </c:if>
                            <c:if test="${empty shop.approvalRate}">
                                <spring:theme code="shop.info.unknown.value"/>
                            </c:if>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="col-md-5 shop-stats">
                <table>
                    <tr>
                        <td><b><spring:theme code="shop.info.shipping.country"/></b></td>
                        <td>
                            <c:if test="${not empty shop.shippingCountry}">${shop.shippingCountry}</c:if>
                            <c:if test="${empty shop.shippingCountry}">
                                <spring:theme code="shop.info.unknown.value"/>
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td><b><spring:theme code="shop.info.registration.date"/></b></td>
                        <td><fmt:formatDate type="date" dateStyle="short" value="${shop.registrationDate}"/></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <a href="${shopOffersUrl}"><spring:theme code="shop.info.see.offers"/>&nbsp;${shop.name}</a>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

        <div class="row">
            <div class="col-md-12">
                <div class="tabs js-tabs tabs-responsive">

                    <div class="tabhead" id="description">
                        <a href="#description"><spring:theme code="shop.info.tab.description"/></a> <span class="glyphicon"></span>
                    </div>
                    <div class="tabbody">
                        <div class="tab-details">
                            <p>
                                <c:if test="${not empty shop.description}">${shop.description}</c:if>
                                <c:if test="${empty shop.description}">
                                    <spring:theme code="shop.info.tab.description.empty"/>
                                </c:if>
                            </p>
                        </div>
                    </div>

                    <c:set var="isPromotionsPresent" >
                        <c:catch var="exception">${shop.promotions}</c:catch>
                    </c:set>
                    <c:if test="${not empty isPromotionsPresent}">
                        <c:set value="${fn:length(shop.promotions)}" var="promotionCount"/>
                        <c:if test="${promotionCount gt 0}">
                            <div class="tabhead" id="promotions">
                                <a href="#promotions"><spring:theme code="shop.info.tab.promotions" arguments="${promotionCount}"/></a> <span class="glyphicon"></span>
                            </div>
                            <div class="tabbody">
                                <div class="tab-details container-fluid">
                                    <c:forEach items="${shop.promotions}" var="promotion">
                                        <div class="row">
                                            <div class="col-md-12">
                                                <c:url value="${promotion.searchPageUrl}" var="promotionSearchUrl"/>
                                                <c:choose>
                                                    <c:when test="${empty promotion.mediaUrl}">
                                                        <a href="${promotionSearchUrl}" title="${promotion.description}">${promotion.description}</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="${promotionSearchUrl}" title="${promotion.description}">
                                                            <img src="${promotion.mediaUrl}" alt="${promotion.description}" class="img-responsive"/>
                                                        </a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>
                    </c:if>

                    <div class="tabhead" id="return-policy">
                        <a href="#return-policy"><spring:theme code="shop.info.tab.return.policy"/></a> <span
                            class="glyphicon"></span>
                    </div>
                    <div class="tabbody">
                        <div class="tab-details">
                            <p>
                                <c:if test="${not empty shop.returnPolicy}">${shop.returnPolicy}</c:if>
                                <c:if test="${empty shop.returnPolicy}">
                                    <spring:theme code="shop.info.tab.return.policy.empty"/>
                                </c:if>
                            </p>
                        </div>
                    </div>

                    <c:if test="${shop.evaluationCount > 0}">
                        <div class="tabhead" id="mirakltabreviews">
                            <a href="#mirakltabreviews">
                                <spring:theme code="shop.info.tab.evaluations"/>&nbsp;(${shop.evaluationCount})
                            </a>
                            <span class="glyphicon"></span>
                        </div>
                        <div class="tabbody tab-mirakl-review" id="reviewsbody" data-reviewurl="${shopReviewsUrl}"></div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</template:page>
