package uk.gov.cca.api.web.controller.workflow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.security.Authorized;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.workflow.request.flow.payment.domain.CardPaymentCreateResponseDTO;
import uk.gov.cca.api.workflow.request.flow.payment.domain.CardPaymentProcessResponseDTO;
import uk.gov.cca.api.workflow.request.flow.payment.service.CardPaymentService;

import static uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskActionType.PAYMENT_PAY_BY_CARD;

@RestController
@RequestMapping(path = "/v1.0/tasks-payment")
@RequiredArgsConstructor
@Tag(name = "Payments")
@ConditionalOnProperty(prefix = "govuk-pay", name = "isActive", havingValue = "true")
public class RequestTaskPaymentController {

    private final CardPaymentService cardPaymentService;

    @PostMapping(path = "/{taskId}/create")
    @Operation(summary = "Create card payment for the provided task")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.REQUEST_TASK_CREATE_CARD_PAYMENT_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<CardPaymentCreateResponseDTO> createCardPayment(@Parameter(hidden = true) AppUser appUser,
                                                                          @PathVariable("taskId") @Parameter(description = "The task id") Long taskId) {
        return ResponseEntity.ok(cardPaymentService.createCardPayment(taskId, PAYMENT_PAY_BY_CARD, appUser));
    }

    @PostMapping(path = "/{taskId}/process")
    @Operation(summary = "Process existing card payment that corresponds to the provided task")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.REQUEST_TASK_PROCESS_EXISTING_CARD_PAYMENT_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<CardPaymentProcessResponseDTO> processExistingCardPayment(@Parameter(hidden = true) AppUser appUser,
                                                                                    @PathVariable("taskId") @Parameter(description = "The task id") Long taskId) {
        return ResponseEntity.ok(cardPaymentService.processExistingCardPayment(taskId, PAYMENT_PAY_BY_CARD, appUser));
    }
}
