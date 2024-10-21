package uk.gov.cca.api.web.controller.sectorassociation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeDocumentService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(path = "/v1.0/sector-documents/document")
@RequiredArgsConstructor
@Tag(name = "Sector Association Scheme Documents")
@Description("Check ApiSecurityConfig for authorization")
@SecurityRequirements
public class SectorAssociationSchemeDocumentController {

    private final SectorAssociationSchemeDocumentService sectorAssociationSchemeDocumentService;

    @GetMapping(path = "/{token}")
    @Operation(summary = "Get the sector association scheme document resource for the provided file token")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Resource.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Resource> getSectorAssociationSchemeDocument(
            @PathVariable("token") @Parameter(description = "The sector association document token", required = true) @NotEmpty String token) {
        FileDTO file = sectorAssociationSchemeDocumentService.getSectorAssociationSchemeDocumentDTOByToken(token);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.builder("document").filename(file.getFileName(), StandardCharsets.UTF_8).build().toString())
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .body(new ByteArrayResource(file.getFileContent()));
    }
}
