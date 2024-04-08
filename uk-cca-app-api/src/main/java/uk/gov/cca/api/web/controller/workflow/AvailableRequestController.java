package uk.gov.cca.api.web.controller.workflow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.security.Authorized;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.cca.api.workflow.request.core.service.AvailableRequestService;
import uk.gov.cca.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.Map;

@RestController
@RequestMapping(path = "/v1.0/requests/available-workflows")
@Tag(name = "Requests")
@RequiredArgsConstructor
public class AvailableRequestController {

    private final AvailableRequestService availableRequestService;

    @GetMapping("/permit/{accountId}")
    @Operation(summary = "Get workflows to start a task")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, useReturnTypeSchema = true)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Map<RequestCreateActionType, RequestCreateValidationResult>> getAvailableAccountWorkflows(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("accountId") @Parameter(name = "accountId", description = "The account id", required = true) Long accountId) {

        return new ResponseEntity<>(availableRequestService.getAvailableAccountWorkflows(accountId, appUser), HttpStatus.OK);
    }
}
