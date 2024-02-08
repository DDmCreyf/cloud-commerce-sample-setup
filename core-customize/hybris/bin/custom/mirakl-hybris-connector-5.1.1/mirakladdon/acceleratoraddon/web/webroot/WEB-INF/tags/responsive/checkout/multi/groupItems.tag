<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ attribute name="deliveryGroupEntries" required="true" type="java.util.List" %>

<ul>
    <c:forEach items="${deliveryGroupEntries}" var="entry">
        <c:url value="${entry.product.url}" var="productUrl"/>
        <li class="row">
            <a href="${productUrl}">
                <span class="name col-xs-8">${entry.product.name}</span>
            </a>
            <span class="qty col-xs-4"><spring:theme code="basket.page.qty"/>:&nbsp;${entry.quantity}</span>
        </li>
    </c:forEach>
</ul>