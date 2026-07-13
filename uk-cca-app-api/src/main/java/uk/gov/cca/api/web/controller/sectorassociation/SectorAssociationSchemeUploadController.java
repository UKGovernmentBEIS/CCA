package uk.gov.cca.api.web.controller.sectorassociation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeDocumentService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.util.FileDtoMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileUuidDTO;
import uk.gov.netz.api.security.AuthorizedRole;

import java.io.IOException;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@RestController
@RequestMapping(path = "/v1.0/sector-scheme-document/upload")
@RequiredArgsConstructor
@Validated
@Tag(name = "Sector scheme document upload")
public class SectorAssociationSchemeUploadController {

    private final SectorAssociationSchemeDocumentService sectorAssociationSchemeDocumentService;
    private final FileDtoMapper fileDtoMapper = Mappers.getMapper(FileDtoMapper.class);

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Upload a sector scheme document file")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileUuidDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPLOAD_SECTOR_ASSOCIATION_SCHEME_DOCUMENT_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<FileUuidDTO> uploadSectorSchemeDocumentFile(
            @Parameter(hidden = true) AppUser authUser,
            @RequestPart("file") @Parameter(description = "The umbrella agreement file", required = true) MultipartFile file) throws IOException {

        final FileDTO fileDTO = fileDtoMapper.toFileDTO(file);
        final String fileUuid = sectorAssociationSchemeDocumentService.createSectorAssociationSchemeDocument(fileDTO, authUser);
        FileUuidDTO fileUuidDTO = FileUuidDTO.builder().uuid(fileUuid).build();

        return new ResponseEntity<>(fileUuidDTO, HttpStatus.OK);
    }
}
