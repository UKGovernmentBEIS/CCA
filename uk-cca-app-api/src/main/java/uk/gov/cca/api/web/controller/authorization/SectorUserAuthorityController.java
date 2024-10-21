package uk.gov.cca.api.web.controller.authorization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.cca.api.user.sectoruser.domain.SectorUsersAuthoritiesInfoDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserAuthorityInfoService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.web.orchestrator.authorization.service.SectorUserAuthorityUpdateOrchestrator;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityDeletionService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityUpdateWrapperDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;

@Validated
@RestController
@RequestMapping(path = "/v1.0/sector-authorities")
@Tag(name = "Sector Association authorities")
@RequiredArgsConstructor
public class SectorUserAuthorityController {

    private final SectorUserAuthorityInfoService sectorUserAuthorityInfoService;
    private final SectorUserAuthorityDeletionService sectorUserAuthorityDeletionService;
    private final SectorUserAuthorityUpdateOrchestrator sectorUserAuthorityUpdateOrchestrator;


    @GetMapping(path = "/sector-association/{sectorId}")
    @Operation(summary = "Retrieves the users of type SECTOR_USER")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SectorUsersAuthoritiesInfoDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorId")
    public ResponseEntity<SectorUsersAuthoritiesInfoDTO> getSectorUserAuthoritiesBySectorAssociationId(
            @Parameter(hidden = true) AppUser currentUser,
            @PathVariable("sectorId") @Parameter(description = "The sector association id") Long sectorId) {
        return new ResponseEntity<>(
                sectorUserAuthorityInfoService.getSectorUsersAuthoritiesInfo(currentUser, sectorId),
                HttpStatus.OK);
    }

    @DeleteMapping(path = "/sector-association/{sectorId}/{userId}")
    @Operation(summary = "Delete authority from the sector association")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))}
    )
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))}
    )
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))}
    )
    @Authorized(resourceId = "#sectorId")
    public ResponseEntity<Void> deleteSectorUser(
            @PathVariable("userId") String userId,
            @PathVariable("sectorId") Long sectorId
    ) {
    	sectorUserAuthorityDeletionService.deleteSectorUserByUserIdAndSectorAssociation(userId, sectorId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/sector-association/{sectorId}")
    @Operation(summary = "Delete the current sector user")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))}
    )
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))}
    )
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))}
    )
    @Authorized(resourceId = "#sectorId")
    public ResponseEntity<Void> deleteCurrentSectorUser(
            @Parameter(hidden = true) AppUser currentUser,
            @PathVariable("sectorId") Long sectorId
    ) {
    	sectorUserAuthorityDeletionService.deleteSectorUserByUserIdAndSectorAssociation(currentUser.getUserId(), sectorId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping(path = "/sector-association/{sectorId}")
    @Operation(summary = "Update sector user authorities")

    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_ACCOUNT_OPERATOR_AUTHORITY_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorId")
    public ResponseEntity<Void> updateSectorUserAuthorities(
            @PathVariable("sectorId") @Parameter(description = "The sectorId id")
                    Long sectorId,
            @RequestBody @Valid @Parameter(description = "The sector user authority to update", required = true) SectorUserAuthorityUpdateWrapperDTO sectorDto) {

    	sectorUserAuthorityUpdateOrchestrator.updateSectorAuthorities(sectorDto, sectorId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
