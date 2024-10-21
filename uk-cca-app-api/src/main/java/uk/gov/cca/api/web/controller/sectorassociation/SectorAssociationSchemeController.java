package uk.gov.cca.api.web.controller.sectorassociation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeDocumentService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.token.FileToken;
import uk.gov.netz.api.security.Authorized;

import java.util.UUID;

@RestController
@RequestMapping(path = "/v1.0/sector-association/{sectorId}/scheme")
@RequiredArgsConstructor
@Tag(name = "Sector association scheme")
public class SectorAssociationSchemeController {

    private final SectorAssociationSchemeService sectorAssociationSchemeService;
    private final SectorAssociationSchemeDocumentService sectorAssociationSchemeDocumentService;

    @GetMapping
    @Operation(summary = "Retrieves the sector association scheme that corresponds to the provided sector id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SectorAssociationSchemeDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.AUTHORITY_USER_NOT_RELATED_TO_CA,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorId")
    public ResponseEntity<SectorAssociationSchemeDTO> getSectorAssociationSchemeBySectorAssociationId(
            @PathVariable("sectorId") @Parameter(description = "The sector association id") Long sectorId) {
        return new ResponseEntity<>(sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(sectorId), HttpStatus.OK);
    }

    @GetMapping(path = "/document")
    @Operation(summary = "Generate the token to get the document of the sector association with the provided sector association id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorId")
    public ResponseEntity<FileToken> generateGetSectorAssociationSchemeDocumentToken(
            @PathVariable("sectorId") @Parameter(description = "The sector association id the document belongs to") @NotNull Long sectorId,
            @RequestParam("documentUuid") @Parameter(name = "documentUuid", description = "The sector association document uuid") @NotNull UUID documentUuid) {
        FileToken getFileToken =
                sectorAssociationSchemeDocumentService.generateDocumentFileToken(sectorId, documentUuid);
        return new ResponseEntity<>(getFileToken, HttpStatus.OK);
    }
}
