package uk.gov.cca.api.web.controller.workflow;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.workflow.request.core.service.AvailableRequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@RestController
@RequestMapping(path = "/v1.0/requests/available-workflows")
@Tag(name = "Requests")
@RequiredArgsConstructor
public class AvailableRequestController {

    private final AvailableRequestService availableRequestService;

    @GetMapping("/{resourceType}/{resourceId}")
    @Operation(summary = "Get workflows to start a task")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, useReturnTypeSchema = true)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#resourceId", resourceType = "#resourceType")
    public ResponseEntity<Map<String, RequestCreateValidationResult>> getAvailableWorkflows(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("resourceType") @Parameter(name = "resourceType", description = "The resource type associated with given resource id") String resourceType,
            @PathVariable("resourceId") @Parameter(name = "resourceId", description = "The resource id for which the available workflows will be retrieved", required = true) String resourceId) {

        return new ResponseEntity<>(availableRequestService.getAvailableWorkflows(resourceId, resourceType, appUser), HttpStatus.OK);
    }
}
