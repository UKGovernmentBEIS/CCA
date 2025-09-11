package uk.gov.cca.api.web.controller.subsistencefees;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaReceivedAmountDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaReceivedAmountInfoDTO;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaFileEvidenceTokenService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaQueryService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaUpdateService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.token.FileToken;

import java.util.UUID;

@RestController
@RequestMapping(path = "/v1.0/subsistence-fees/moas/{moaId}/received-amount")
@RequiredArgsConstructor
@Tag(name = "Subsistence fees MoA received amount controller")
public class SubsistenceFeesMoaReceivedAmountController {

    private final SubsistenceFeesMoaFileEvidenceTokenService fileEvidenceTokenService;
    private final SubsistenceFeesMoaUpdateService subsistenceFeesMoaUpdateService;
    private final SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;

    @GetMapping
    @Operation(summary = "Get the received amount history details for the provided MoA id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SubsistenceFeesMoaReceivedAmountInfoDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#moaId")
    public ResponseEntity<SubsistenceFeesMoaReceivedAmountInfoDTO> getSubsistenceFeesMoaReceivedAmountInfo(
            @PathVariable("moaId") @Parameter(name = "moaId", description = "The subsistence fees MoA id") @NotNull Long moaId) {

        SubsistenceFeesMoaReceivedAmountInfoDTO moaReceivedAmountHistory =
                subsistenceFeesMoaQueryService.getSubsistenceFeesMoaReceivedAmountInfo(moaId);
        return new ResponseEntity<>(moaReceivedAmountHistory, HttpStatus.OK);
    }

    @GetMapping(path = "/evidence")
    @Operation(summary = "Generate the token to get the evidence file that belongs to the provided MoA id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#moaId")
    public ResponseEntity<FileToken> generateGetMoaReceivedAmountEvidenceFileToken(
            @Parameter(hidden = true) AppUser authUser,
            @PathVariable("moaId") @Parameter(name = "moaId", description = "The subsistence fees MoA id") @NotNull Long moaId,
            @RequestParam("fileEvidenceUuid") @Parameter(name = "fileEvidenceUuid", description = "The evidence file uuid") @NotNull UUID fileDocumentUuid) {

        FileToken getFileEvidenceToken = fileEvidenceTokenService.generateGetFileEvidenceToken(moaId, fileDocumentUuid, authUser);
        return new ResponseEntity<>(getFileEvidenceToken, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Update the subsistence fees MoA received amount")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#moaId")
    public ResponseEntity<Void> updateSubsistenceFeesMoaReceivedAmount(
            @Parameter(hidden = true) AppUser submitter,
            @Parameter(description = "The moaId") @PathVariable("moaId") Long moaId,
            @RequestBody @Valid @Parameter(description = "The received amount details", required = true) SubsistenceFeesMoaReceivedAmountDetailsDTO detailsDto) {

        subsistenceFeesMoaUpdateService.updateSubsistenceFeesMoaReceivedAmount(moaId, detailsDto, submitter);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
