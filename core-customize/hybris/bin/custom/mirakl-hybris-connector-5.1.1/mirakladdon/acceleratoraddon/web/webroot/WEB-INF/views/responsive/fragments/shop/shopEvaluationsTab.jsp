<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="formElement"
           tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="miraklutil" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/util" %>

<c:set var="evaluations" value="${evaluationPageContent.evaluations}"/>
<c:set var="evaluationPageCount" value="${evaluationPageContent.evaluationPageCount}"/>

<ul id="reviews" class="review-list">
    <c:forEach items="${evaluations}" var="evaluation" varStatus="status">
        <li class="review-entry review-separator col-md-12">
            <div class="same-height">
                <div class="col-md-2 shop-grade spaced-content">
                    <table class="padding-0">
                        <tr>
                            <td><b><spring:theme code="shop.review.tab.note"/></b></td>
                            <td class="rating js-miraklRatingCalc shop-grade"
                                data-rating='{"rating":"${evaluation.grade}","total":5}'>
                                <div class="rating-stars">
                                    <span class="js-miraklRatingIcon glyphicon glyphicon-star"></span>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2"><spring:theme code="shop.review.tab.author"/>&nbsp;
                                <b>${evaluation.firstName}&nbsp;${evaluation.lastName}</b>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <span class="date">
                                    <fmt:formatDate type="date" dateStyle="short" value="${evaluation.date}"/>
                                </span>
                            </td>
                        </tr>
                    </table>
                </div>

                <div class="col-md-3 spaced-content light-cell">
                    <ul class="slist">
                        <c:forEach items="${evaluation.assessments}" var="assessment">
                            <li>${assessment.label}:
                                <c:choose>
                                    <c:when test="${assessment.response == 'true'}">
                                        <b class="text-primary"><spring:theme code="yes"/></b>
                                    </c:when>
                                    <c:when test="${assessment.response == 'false'}">
                                        <b class="text-primary"><spring:theme code="no"/></b>
                                    </c:when>
                                    <c:otherwise><b class="text-primary">${assessment.response}</b>/5</c:otherwise>
                                </c:choose>
                            </li>
                        </c:forEach>
                    </ul>
                </div>

                <div class="col-md-7">
                    <div class="content">
                        <span class="stitle"><spring:theme code="shop.review.tab.customer.review"/></span>
                        ${evaluation.comment}
                    </div>
                </div>
            </div>
        </li>
    </c:forEach>
</ul>

<miraklutil:pagination currentPage="${currentPage}" totalPages="${evaluationPageCount}" name="review-pagination-bar"/>
