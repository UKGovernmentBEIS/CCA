package uk.gov.cca.api.web.controller.subsistencefees;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitSearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesSearchCriteria;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaDocumentService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaQueryService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaTargetUnitQueryService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.token.FileToken;

@RestController
@RequestMapping(path = "/v1.0/subsistence-fees-moas/")
@RequiredArgsConstructor
@Tag(name = "Subsistence fees MoA info view")
public class SubsistenceFeesMoaViewController {

	private final SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;
	private final SubsistenceFeesMoaDocumentService subsistenceFeesMoaDocumentService;
	private final SubsistenceFeesMoaTargetUnitQueryService subsistenceFeesMoaTargetUnitQueryService;
	
	@GetMapping(path = "{moaId}")
    @Operation(summary = "Retrieves the details for the specific MoA id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SubsistenceFeesMoaDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#moaId")
    public ResponseEntity<SubsistenceFeesMoaDetailsDTO> getSubsistenceFeesMoaDetailsById(
            @Parameter(description = "The MoA id") @PathVariable("moaId") Long moaId) {
        return new ResponseEntity<>(subsistenceFeesMoaQueryService.getSubsistenceFeesMoaDetailsById(moaId), HttpStatus.OK);
    }
	
	@GetMapping(path = "{moaId}/document")
    @Operation(summary = "Generate the token to get the document that belongs to the provided MoA id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#moaId")
    public ResponseEntity<FileToken> generateGetSubsistenceFeesMoaDocumentToken(
            @PathVariable("moaId") @Parameter(name = "moaId", description = "The subsistence fees MoA id") @NotNull Long moaId,
            @RequestParam("fileDocumentUuid") @Parameter(name = "fileDocumentUuid", description = "The document uuid") @NotNull UUID fileDocumentUuid) {

        FileToken getFileDocumentToken = subsistenceFeesMoaDocumentService.generateGetFileDocumentToken(moaId, fileDocumentUuid);
        return new ResponseEntity<>(getFileDocumentToken, HttpStatus.OK);
    }
	
	@PostMapping(path = "{moaId}/target-units")
    @Operation(summary = "Retrieves the subsistence fees MoA target units")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SubsistenceFeesMoaTargetUnitSearchResults.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized(resourceId = "#moaId")
    public ResponseEntity<SubsistenceFeesMoaTargetUnitSearchResults> getSubsistenceFeesMoaTargetUnits(
    		@Parameter(description = "The moaId") @PathVariable("moaId") Long moaId,
    		@RequestBody @Valid @Parameter(description = "The search criteria", required = true) SubsistenceFeesSearchCriteria criteria) {
        return new ResponseEntity<>(subsistenceFeesMoaTargetUnitQueryService.getSubsistenceFeesMoaTargetUnits(moaId, criteria), HttpStatus.OK);
    }
}
