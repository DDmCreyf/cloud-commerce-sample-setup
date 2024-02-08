<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="threads" required="true" type="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="miraklutil" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/util"%>

<c:forEach items="${threads}" var="thread">
    <tr class="responsive-table-item">
        <td class="hidden-sm hidden-md hidden-lg">
            <spring:theme code="inbox.threads.list.participants" />
        </td>
        <td class="responsive-table-cell">
            <miraklutil:textCrop text="${fn:escapeXml(thread.currentParticipantsDisplayValue)}" maxLength="45" />
        </td>
        <td class="hidden-sm hidden-md hidden-lg">
            <spring:theme code="inbox.threads.list.entity" />
        </td>
        <td class="responsive-table-cell">
            <spring:theme code="inbox.threads.list.entity.${thread.entityType}" />
            &nbsp;${fn:escapeXml(thread.entityLabel)}
        </td>
        <td class="hidden-sm hidden-md hidden-lg">
            <spring:theme code="inbox.threads.list.topic" />
        </td>
        <td class="status">
            <spring:url value="/my-account/inbox/thread/{threadId}" var="threadDetailsUrl" htmlEscape="false">
                <spring:param name="threadId" value="${thread.id}" />
            </spring:url>
            <a href="${fn:escapeXml(threadDetailsUrl)}" class="responsive-table-link">
                <miraklutil:textCrop text="${fn:escapeXml(thread.topic.displayValue)}" maxLength="50" />
            </a>
        </td>
        <td class="hidden-sm hidden-md hidden-lg">
            <spring:theme code="inbox.threads.list.lastmessagedate" />
        </td>
        <td class="responsive-table-cell">
            <fmt:formatDate value="${thread.dateUpdated}" dateStyle="medium" timeStyle="short" type="both" />
        </td>
    </tr>
</c:forEach>