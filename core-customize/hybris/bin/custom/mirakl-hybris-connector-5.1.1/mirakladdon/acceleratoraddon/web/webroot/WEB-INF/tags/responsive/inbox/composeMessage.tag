<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="consignmentCode" required="false"%>
<%@ attribute name="submitMessageURL" required="true"%>
<%@ attribute name="selectableParticipants" required="true" type="java.util.List"%>
<%@ attribute name="thread" required="false" type="com.mirakl.hybris.beans.ThreadData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="miraklFormElement" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/miraklFormElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<form:form method="post" class="messaging-form" id="threadMessageForm" modelAttribute="threadMessageForm" action="${submitMessageURL}" enctype="multipart/form-data">
    <sec:csrfInput />
    <c:if test="${not empty consignmentCode}">
        <div class="form-description">
            <spring:theme code="inbox.thread.form.description" arguments="${consignmentCode}" />
        </div>
    </c:if>
    <br />
    <div class="form-group">
        <formElement:formSelectBox idKey="recipients" labelKey="inbox.thread.form.to" path="to" mandatory="true" skipBlank="false" skipBlankMessageKey="inbox.thread.form.recipients.selection" items="${selectableParticipants}" itemValue="type" itemLabel="displayName" selectCSSClass="form-control" />
    </div>

    <div class="form-group">
    
        <c:choose>
            <c:when test="${!empty reasons}">
                    <formElement:formSelectBox idKey="topicCode" labelKey="inbox.thread.form.topic" path="topicCode" mandatory="false" skipBlank="false" skipBlankMessageKey="inbox.thread.form.topic.selection" items="${reasons}" itemValue="code" itemLabel="label" selectCSSClass="form-control" />
                    <spring:theme code="${placeholder}" var="placeHolderMessage" htmlEscape="false"/>
                    <spring:theme code="inbox.thread.form.topic.other.placeholder" var="topicPlaceholder" />
                    <form:input cssClass="form-control" id="topicValue" path="topicValue" placeholder="${topicPlaceholder}" cssStyle="display: none"/>
                    <form:hidden id="topicCodeDisplayValue" path="topicCodeDisplayValue"/>
                    <input type="hidden" id="topicCodeOther" value="${topicCodeOther}">
            </c:when>
            <c:otherwise>
                <formElement:formInputBox idKey="topicValue" labelKey="inbox.thread.form.topic" path="topicValue" inputCSS="form-control" mandatory="true" />
            </c:otherwise>
        </c:choose>
    </div>

    <div class="form-group">
        <formElement:formTextArea idKey="body" labelKey="inbox.thread.form.message" path="body" areaCSS="form-control" mandatory="true" />
    </div>
    <form:hidden path="consignmentCode" />

    <div class="account-section-content thread-attachments form-group">
        <c:url value="/my-account/inbox/thread/${thread.id}/attachment" var="submitAttachmentURL" />

        <div id="attach-file-alerts" style="display: block;"></div>
        <div class="well well-quaternary well-md">
            <div class="row">
                <div class="col-xs-12 col-sm-12">
                    <span class="font-weight-bold">
                        <spring:theme code="inbox.thread.form.attachment.select.label" />
                    </span>
                    <div class="form-group file-upload">
                        <div class="file-upload__wrapper btn btn-default btn-small" id="chooseFileButton">
                            <span>
                                <spring:theme code="inbox.thread.form.attachment.browse.label" />
                            </span>
                            <input type="file" id="attachments" name="attachments" class="file-upload__input js-attachment-upload__input" data-file-max-size="${fn:escapeXml(attachmentFileMaxSize)}" multiple="multiple" />
                        </div>
                        <button class="remove-attachment" type="button" style="display: none;">
                            <span class="glyphicon glyphicon-trash"></span>
                        </button>
                        <div id="attachment-files"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="form-actions js-attachment-upload">
        <div class="row">
            <c:if test="${not empty thread}">
                <c:url value="/my-account/inbox/thread?consignmentCode=${consignmentCode}" var="newThreadURL" />
                <a id="compose-new-message" role="button" href="${newThreadURL}" class="btn btn-default col-xs-12 col-sm-6 col-md-5 col-lg-4 pull-left">
                    <spring:theme code="inbox.threads.button.newthread" />
                </a>
            </c:if>
            <form:button id="send-message" type="submit" class="btn btn-primary col-xs-12 col-sm-6 col-md-5 col-lg-4 pull-right">
                <spring:theme code="consignment.messages.form.submit" />
            </form:button>
        </div>
    </div>
</form:form>


<fmt:formatNumber var="fileSize" type="number" value="${attachmentFileMaxSize/1000000}" maxFractionDigits="0" groupingUsed="false" />
<div style="display: none">
    <span id="import-attachment-file-max-size-exceeded-error-message">
        <spring:theme code="inbox.thread.form.attachment.size" arguments="${fileSize}" />
    </span>
</div>

<script id="file-attachement-template" type="text/x-jquery-tmpl">
    <div class="file-attachment">
        <button class="remove-attachment" type="button" data-fileid="\${fileId}">&times;</button>
        <span class="glyphicon glyphicon-file" aria-hidden="true"></span>
        <span class="filename">\${fileName}</span>
        <span class="filesize">(\${fileSize})</span>
    </div>
</script>

<common:globalMessagesTemplates />