package com.mirakl.hybris.occ.controllers;

import com.mirakl.client.domain.common.FileWithContext;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreadReplyCreated;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.ThreadRequestData;
import com.mirakl.hybris.dto.message.*;
import com.mirakl.hybris.facades.message.MessagingThreadFacade;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.UUID;

import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.DEFAULT_LEVEL;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

/**
 * Web Services Controller to expose the functionality of the {@link MessagingThreadFacade}.
 */
@Controller(value = "miraklMessagesController")
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/messages")
@OpenAPIDefinition(tags =@Tag(name = "Mirakl - Threads and Messages"))
@Tag(name = "Mirakl - Threads and Messages")
public class MiraklMessagesController extends MiraklBaseController {

  private static final Logger LOG = Logger.getLogger(MiraklMessagesController.class);

  @Resource(name = "messagingThreadFacade")
  private MessagingThreadFacade messagingThreadFacade;
  @Resource(name = "miraklCreateThreadMessageDataPopulator")
  private Populator<String, CreateThreadMessageData> createThreadMessageDataPopulator;
  @Resource(name = "miraklReplyThreadMessageDataPopulator")
  private Populator<UUID, CreateThreadMessageData> replyThreadMessageDataPopulator;

  @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
  @RequestMapping(value = "/attachments/{attachmentId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  @ResponseBody
  @Operation(operationId = "getAttachment", summary = "Get an attachment.", description = "Returns the attachment.")
  @ApiBaseSiteIdAndUserIdParam
  public void getAttachment(
      @Parameter(description = "The attachment identifier", required = true) @PathVariable final String attachmentId,
      final HttpServletResponse response) {
    FileWithContext attachment = messagingThreadFacade.downloadThreadAttachment(attachmentId);
    response.setContentType(attachment.getContentType());
    response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFilename() + "\"");
    try (InputStream is = new FileInputStream(attachment.getFile())) {
      IOUtils.copy(is, response.getOutputStream());
      response.flushBuffer();
    } catch (IOException ioe) {
      LOG.error(MessageFormat.format("Error while downloading attachment with id: {0}", attachmentId), ioe);
    }
  }

  @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
  @RequestMapping(value = "/threads/{threadId}", method = RequestMethod.GET)
  @ResponseBody
  @Operation(operationId = "getThreadDetails", summary = "Get the detail of a thread.", description = "Returns the thread detail.")
  @ApiBaseSiteIdAndUserIdParam
  public ThreadDetailsWsDTO getThreadDetails(
      @Parameter(description = "The thread identifier", required = true) @PathVariable final UUID threadId,
      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_LEVEL) final String fields) {
    return getDataMapper().map(messagingThreadFacade.getThreadDetails(threadId), ThreadDetailsWsDTO.class, fields);
  }

  @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
  @RequestMapping(value = "/threads", method = RequestMethod.GET)
  @ResponseBody
  @Operation(operationId = "getThreads", summary = "List threads.", description = "Returns the thread list.")
  @ApiBaseSiteIdAndUserIdParam
  public ThreadListWsDTO getThreads(
      @Parameter(description = "The consignment code") @RequestParam(required = false) final String consignmentCode,
      @Parameter(description = "The requested page token") @RequestParam(required = false) final String pageToken,
      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_LEVEL) final String fields) {
    final ThreadRequestWsDTO threadRequestWsDTO = new ThreadRequestWsDTO();
    threadRequestWsDTO.setConsignmentCode(consignmentCode);
    threadRequestWsDTO.setPageToken(pageToken);
    final ThreadRequestData threadRequestData = getDataMapper().map(threadRequestWsDTO, ThreadRequestData.class);
    return getDataMapper().map(messagingThreadFacade.getThreads(threadRequestData), ThreadListWsDTO.class, fields);
  }

  @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
  @RequestMapping(value = "/threads/{threadId}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  @Operation(operationId = "replyToThread", summary = "Reply to a thread.", description = "Returns the replied thread details.")
  @ApiBaseSiteIdAndUserIdParam
  public MiraklThreadReplyCreatedWsDTO replyToThread(
      @Parameter(description = "The thread identifier", required = true) @PathVariable final UUID threadId,
      @Parameter(description = "Create thread message DTO",
          required = true) @ModelAttribute CreateThreadMessageWsDTO createThreadMessageWsDTO,
      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_LEVEL) final String fields) {
    final CreateThreadMessageData messageData =
        getDataMapper().map(createThreadMessageWsDTO, CreateThreadMessageData.class);
    replyThreadMessageDataPopulator.populate(threadId, messageData);
    final MiraklThreadReplyCreated replyCreated = messagingThreadFacade.replyToThread(threadId, messageData);
    return getDataMapper().map(replyCreated, MiraklThreadReplyCreatedWsDTO.class, fields);
  }

  @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
  @RequestMapping(value = "/consignment/{consignmentCode}", method = RequestMethod.POST,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  @Operation(operationId = "createConsignmentThread", summary = "Create a thread on a consignment.",
      description = "Returns the created thread details.")
  @ApiBaseSiteIdAndUserIdParam
  public MiraklThreadCreatedWsDTO createConsignmentThread(
      @Parameter(description = "The thread consignment code", required = true) @PathVariable final String consignmentCode,
      @Parameter(description = "Create thread message DTO",
          required = true) @ModelAttribute CreateThreadMessageWsDTO createThreadMessageWsDTO,
      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_LEVEL) final String fields) {
    final CreateThreadMessageData messageData =
        getDataMapper().map(createThreadMessageWsDTO, CreateThreadMessageData.class);
    createThreadMessageDataPopulator.populate(consignmentCode, messageData);
    return getDataMapper().map(messagingThreadFacade.createConsignmentThread(consignmentCode, messageData),
        MiraklThreadCreatedWsDTO.class, fields);
  }
}
