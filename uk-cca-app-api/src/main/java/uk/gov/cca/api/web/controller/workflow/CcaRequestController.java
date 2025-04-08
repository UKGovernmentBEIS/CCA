package uk.gov.cca.api.web.controller.workflow;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.workflow.request.flow.common.actionhandler.CcaRequestCreateActionHandlerMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.workflow.request.core.domain.dto.RequestCreateActionProcessDTO;
import uk.gov.netz.api.workflow.request.core.domain.dto.RequestCreateActionProcessResponseDTO;

@Validated
@RestController
@RequestMapping(path = "/v1.0/cca-requests")
@Tag(name = "Cca Requests")
@RequiredArgsConstructor
public class CcaRequestController {

    private final CcaRequestCreateActionHandlerMapper ccaRequestCreateActionHandlerMapper;

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
}
