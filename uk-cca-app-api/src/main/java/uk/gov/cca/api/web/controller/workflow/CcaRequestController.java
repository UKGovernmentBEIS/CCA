package uk.gov.cca.api.web.controller.workflow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.workflow.request.flow.common.actionhandler.CcaRequestCreateActionHandlerMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.workflow.request.core.domain.dto.*;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.core.transform.RequestSearchCriteriaMapper;

@Validated
@RestController
@RequestMapping(path = "/v1.0/cca-requests")
@Tag(name = "Cca Requests")
@RequiredArgsConstructor
public class CcaRequestController {

    private final CcaRequestCreateActionHandlerMapper ccaRequestCreateActionHandlerMapper;
    private final RequestQueryService requestQueryService;
    private final RequestSearchCriteriaMapper requestSearchCriteriaMapper = Mappers.getMapper(RequestSearchCriteriaMapper.class);

    @PostMapping
    @SuppressWarnings("unchecked")
    @Operation(summary = "Processes a cca request create action")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RequestCreateActionProcessResponseDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.REQUEST_ACTION_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorAssociationId", resourceSubType = "#requestCreateActionProcess.requestType")
    public ResponseEntity<RequestCreateActionProcessResponseDTO> processCcaRequestCreateAction(@Parameter(hidden = true) AppUser appUser,
                                                                                            @RequestParam @NotNull @Parameter(name = "sectorAssociationId", description = "The sector association id") Long sectorAssociationId,
                                                                                            @RequestParam(required = false) @Parameter(name = "accountId", description = "The account id") Long accountId,
                                                                                            @RequestBody @Valid @Parameter(description = "The request create action body", required = true) RequestCreateActionProcessDTO requestCreateActionProcess) {
        String requestId = ccaRequestCreateActionHandlerMapper
                .get(requestCreateActionProcess.getRequestType())
                .process(sectorAssociationId, accountId, requestCreateActionProcess.getRequestType(),
                        requestCreateActionProcess.getRequestCreateActionPayload(), appUser);
        return ResponseEntity.ok(new RequestCreateActionProcessResponseDTO(requestId));
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Get request details by id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RequestDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<RequestDetailsDTO> getCcaRequestDetailsById(
            @PathVariable("id") @Parameter(description = "The sector association id") String id) {
        return new ResponseEntity<>(requestQueryService.findRequestDetailsById(id), HttpStatus.OK);
    }

    @PostMapping("/workflows") //workaround: post instead of get, in order to support posting collection of params (request types)
    @Operation(summary = "Get the workflows for the given search criteria")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RequestDetailsSearchResults.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#criteria.accountId")
    public ResponseEntity<RequestDetailsSearchResults> getCcaRequestDetailsByAccountId(
            @RequestBody @Valid @Parameter(description = "The search criteria", required = true) RequestSearchByAccountCriteria criteria){
        return new ResponseEntity<>(requestQueryService.findRequestDetailsBySearchCriteria(requestSearchCriteriaMapper.toRequestSearchCriteria(criteria)), HttpStatus.OK);
    }
}
