<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="offerErrors" required="true" type="java.util.List" %>

<div class="col-sm-12 row">
    <p style="color: red">
        <spring:theme code="checkout.multi.deliveryMethod.offer.errors"
                      text="Some products cannot be ordered. Please review the reason below:"/>
    </p>
</div>
<c:forEach items="${offerErrors}" var="offerError">
    <div class="row">
        <div class="col-sm-6">
            <c:url value="${offerError.entry.product.url}" var="productUrl"/>
            <a href="${productUrl}">
                <span class="name col-xs-8">${offerError.entry.product.name}</span>
            </a>
            <span class="qty col-xs-4"><spring:theme code="basket.page.qty"/>:&nbsp;${offerError.missingQuantity}</span>
        </div>
        <div class="col-sm-6">
            <p><spring:theme code="${offerError.message}"/></p>
        </div>
    </div>
</c:forEach>
