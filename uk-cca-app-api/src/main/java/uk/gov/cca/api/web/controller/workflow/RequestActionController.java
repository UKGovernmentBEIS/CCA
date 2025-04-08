package uk.gov.cca.api.web.controller.workflow;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.workflow.request.application.requestaction.RequestActionQueryService;
import uk.gov.netz.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.netz.api.workflow.request.core.domain.dto.RequestActionInfoDTO;

@Validated
@RestController
@RequestMapping(path = "/v1.0/request-actions")
@Tag(name = "Request actions")
@RequiredArgsConstructor
public class RequestActionController {

    private final RequestActionQueryService requestActionQueryService;

    @GetMapping(path = "/{id}")
    @Operation(summary = "Get request action by id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RequestActionDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#requestActionId")
    public ResponseEntity<RequestActionDTO> getRequestActionById(@Parameter(hidden = true) AppUser appUser,
                                                                 @PathVariable("id") @Parameter(description = "The request action id") Long requestActionId) {
        final RequestActionDTO requestAction = requestActionQueryService.getRequestActionById(requestActionId, appUser);
        return new ResponseEntity<>(requestAction, HttpStatus.OK);
    }

    @GetMapping(params = {"requestId"})
    @Operation(summary = "Retrieves the actions associated with the request")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = RequestActionInfoDTO.class))))
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#requestId")
    public ResponseEntity<List<RequestActionInfoDTO>> getRequestActionsByRequestId(@Parameter(hidden = true) AppUser appUser,
                                                                                   @RequestParam("requestId") @Parameter(name = "requestId", description = "The request id") String requestId) {
        List<RequestActionInfoDTO> sortedByDateList =
            requestActionQueryService.getRequestActionsByRequestId(requestId, appUser).stream()
                .sorted(Comparator.comparing(RequestActionInfoDTO::getCreationDate)
                    .reversed())
                .toList();

        return new ResponseEntity<>(sortedByDateList, HttpStatus.OK);
    }
}
