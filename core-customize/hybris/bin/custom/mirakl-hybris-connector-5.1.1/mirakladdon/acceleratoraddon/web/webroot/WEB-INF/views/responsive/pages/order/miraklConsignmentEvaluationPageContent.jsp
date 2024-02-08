<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url value="/" var="homeURL"/>

<template:page pageTitle="${pageTitle}">
    <div class="account-section">

        <div class="back-link">
            <c:url value="/my-account/order/${orderCode}" var="orderUrl"/>
            <a href="${orderUrl}">
                <span class="glyphicon glyphicon-chevron-left"></span>
            </a>
            <span class="label"><spring:theme code="consignment.reception.header"/></span>
        </div>

        <div class="account-section-content">

            <c:if test="${not empty formErrorMessage}">
                <div class="alert alert-danger alert-dismissable">
                    <button class="close" aria-hidden="true" data-dismiss="alert" type="button">x</button>
                    <spring:theme code="${formErrorMessage}"/>
                </div>
            </c:if>

            <div class="write-review js-review-write">
                <c:url value="/my-account/consignment/${consignmentCode}/evaluate" var="evaluateConsignmentActionUrl"/>
                <form:form method="post" action="${evaluateConsignmentActionUrl}" modelAttribute="consignmentEvaluationForm">

                    <p class="reception-confirmation-content">
                        <c:choose>
                            <c:when test="${alreadyReceived ne null}">
                                <spring:theme code="consignment.reception.already.confirmed"/>
                            </c:when>
                            <c:otherwise>
                                <spring:theme code="consignment.reception.confirmed"/>
                            </c:otherwise>
                        </c:choose>
                    </p>

                    <div class="form-group">
                        <label><spring:theme code="consignment.evaluation.form.seller.grade"/></label>
                        <div class="rating rating-set js-miraklRatingCalcSet"
                             data-rating='{"total":5}'>
                            <div class="rating-stars">
                                <span class="js-miraklRatingIcon js-miraklRatingIconSet glyphicon glyphicon-star"></span>
                            </div>
                        </div>
                        <formElement:formInputBox idKey="seller.grade" labelKey="review.rating" path="sellerGrade"
                                                  inputCSS="sr-only js-miraklRatingSetInput" labelCSS="sr-only" mandatory="true"/>
                    </div>

                    <div class="form-group">
                        <formElement:formTextArea idKey="review.comment" labelKey="review.comment" path="comment"
                                                  areaCSS="form-control" mandatory="true"/>
                    </div>

                    <c:forEach items="${miraklAssessments}" var="assessment" varStatus="i">
                        <div class="form-group">
                            <c:choose>
                                <c:when test="${assessment.type eq 'GRADE'}">
                                    <label>${assessment.label}</label>
                                    <div class="rating rating-set js-miraklRatingCalcSet"
                                         data-rating='{"total":5}'>
                                        <div class="rating-stars">
                                            <span class="js-miraklRatingIcon js-miraklRatingIconSet glyphicon glyphicon-star"></span>
                                        </div>
                                    </div>
                                    <formElement:formInputBox idKey="assessment-${assessment.code}" labelKey="${assessment.label}"
                                                              path="assessments[${i.count-1}].response"
                                                              inputCSS="sr-only js-miraklRatingSetInput" labelCSS="sr-only"
                                                              mandatory="true"/>
                                </c:when>
                                <c:otherwise>
                                    <label>${assessment.label}</label><br/>
                                    <label class="radio-inline"><form:radiobutton path="assessments[${i.count-1}].response"
                                                                                  value="true"/> <spring:theme code="consignment.evaluation.form.boolean.assessment.true"/>
                                    </label>
                                    <label class="radio-inline"><form:radiobutton path="assessments[${i.count-1}].response"
                                                                                  value="false"/> <spring:theme code="consignment.evaluation.form.boolean.assessment.false"/>
                                    </label>
                                </c:otherwise>
                            </c:choose>
                            <form:hidden path="assessments[${i.count-1}].code"/>
                        </div>
                    </c:forEach>

                    <button class="btn btn-primary" type="submit" value="<spring:theme code="review.submit"/>">
                        <spring:theme code="review.submit"/>
                    </button>

                </form:form>
            </div>

        </div>

    </div>

</template:page>
