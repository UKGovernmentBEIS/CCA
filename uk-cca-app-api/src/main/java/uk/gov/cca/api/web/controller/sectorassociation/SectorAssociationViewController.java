package uk.gov.cca.api.web.controller.sectorassociation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoDTO;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.web.orchestrator.sectorassociation.dto.SectorAssociationResponseDTO;
import uk.gov.cca.api.web.orchestrator.sectorassociation.service.SectorAssociationQueryServiceOrchestrator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;

import java.util.List;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@RestController
@RequestMapping(path = "/v1.0/sector-association/")
@RequiredArgsConstructor
@Tag(name = "Sector association info view")
public class SectorAssociationViewController {

    private final SectorAssociationQueryServiceOrchestrator sectorAssociationQueryServiceOrchestrator;

    @GetMapping(path = "/{id}")
    @Operation(summary = "Retrieves the sector association info that corresponds to the provided sector id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SectorAssociationResponseDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<SectorAssociationResponseDTO> getSectorAssociationById(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("id") @Parameter(description = "The sector association id") Long id) {
        return new ResponseEntity<>(sectorAssociationQueryServiceOrchestrator.getSectorAssociationById(id, appUser), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Retrieves the sectors and their contacts")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = SectorAssociationInfoDTO.class)))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR, SECTOR_USER, OPERATOR})
    public ResponseEntity<List<SectorAssociationInfoDTO>> getSectorAssociations(@Parameter(hidden = true) AppUser appUser) {
        return new ResponseEntity<>(sectorAssociationQueryServiceOrchestrator.getSectorAssociations(appUser), HttpStatus.OK);
    }
}

