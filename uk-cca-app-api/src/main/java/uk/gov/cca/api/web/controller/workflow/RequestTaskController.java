package uk.gov.cca.api.web.controller.workflow;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.workflow.request.application.task.CcaRequestTaskViewService;
import uk.gov.cca.api.workflow.request.application.task.RequestTaskRecipientsService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.workflow.request.application.taskview.RequestTaskItemDTO;
import uk.gov.netz.api.workflow.request.core.domain.dto.RequestTaskActionProcessDTO;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandlerMapper;

import java.util.List;

@RestController
@RequestMapping(path = "/v1.0/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks")
@Validated
public class RequestTaskController {

    private final CcaRequestTaskViewService ccaRequestTaskViewService;
    private final RequestTaskRecipientsService requestTaskRecipientsService;
    private final RequestTaskActionHandlerMapper requestTaskActionHandlerMapper;

    @GetMapping(path = "/{id}")
    @Operation(summary = "Get task item info by id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RequestTaskItemDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<RequestTaskItemDTO> getTaskItemInfoById(@Parameter(hidden = true) AppUser appUser,
                                                                  @PathVariable("id") @Parameter(description = "The task id") Long taskId) {
        final RequestTaskItemDTO taskItem = ccaRequestTaskViewService.getTaskItemInfo(taskId, appUser);
        return new ResponseEntity<>(taskItem, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/default-recipients")
    @Operation(summary = "Retrieves the target unit account contacts and the sector association contact")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = NoticeRecipientDTO.class))))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<List<NoticeRecipientDTO>> getDefaultNoticeRecipients(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("id") @Parameter(description = "The task id") Long taskId) {
        return new ResponseEntity<>(requestTaskRecipientsService.getDefaultNoticeRecipients(taskId), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @PostMapping(path = "/actions")
    @Operation(summary = "Processes a request task action")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.REQUEST_TASK_ACTION_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#requestTaskActionProcessDTO.requestTaskId")
    public ResponseEntity<Void> processRequestTaskAction(@Parameter(hidden = true) AppUser appUser,
                                                         @RequestBody @Valid @Parameter(description = "The request task action body", required = true)
                                                         RequestTaskActionProcessDTO requestTaskActionProcessDTO) {
        requestTaskActionHandlerMapper
                .get(requestTaskActionProcessDTO.getRequestTaskActionType())
                .process(requestTaskActionProcessDTO.getRequestTaskId(),
                        requestTaskActionProcessDTO.getRequestTaskActionType(),
                        appUser,
                        requestTaskActionProcessDTO.getRequestTaskActionPayload());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
