package uk.gov.cca.api.web.controller.user;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityDetailsDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserManagementService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.user.operator.domain.OperatorUserDTO;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@RestController
@RequestMapping(path = "/v1.0/sector-users")
@Tag(name = "Sector Users")
@RequiredArgsConstructor
public class SectorUserManagementController {

    private final SectorUserManagementService sectorUserManagementService;

    /**
     * Retrieves info of user by sector association and user id.
     *
     * @param sectorAssociationId sector association id
     * @param userId Keycloak user id
     * @return {@link SectorUserDTO}
     */
    @GetMapping(path = "/sector-association/{sectorAssociationId}/{userId}")
    @Operation(summary = "Retrieves info of sector user by sector association and user id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SectorUserAuthorityDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorAssociationId")
    public ResponseEntity<SectorUserAuthorityDetailsDTO> getSectorUserById(
            @PathVariable("sectorAssociationId") @Parameter(description = "The sector association id") Long sectorAssociationId,
            @PathVariable("userId") @Parameter(description = "The sector user id") String userId) {
        return new ResponseEntity<>(sectorUserManagementService.getSectorUserBySectorAssociationIdAndUserId(sectorAssociationId, userId),
                HttpStatus.OK);
    }

    @GetMapping(path = "/sector-association/{sectorAssociationId}")
    @Operation(summary = "Retrieves info of the logged in sector user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = SectorUserAuthorityDetailsDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<SectorUserAuthorityDetailsDTO> getCurrentSectorUser(@Parameter(hidden = true) AppUser appUser,
                                                             @PathVariable("sectorAssociationId") @Parameter(description = "The sector association id") Long sectorAssociationId) {
        return new ResponseEntity<>(sectorUserManagementService.getSectorUserBySectorAssociationIdAndUserId(sectorAssociationId, appUser.getUserId()), HttpStatus.OK);
    }

    /**
     * Updates operator user by account and user id.
     *
     * @param sectorAssociationId Sector Association id
     * @param userId Keycloak user id
     * @param sectorUserAuthorityDetailsDTO {@link SectorUserDTO}
     * @return {@link OperatorUserDTO}
     */
    @PatchMapping(path = "/sector-association/{sectorAssociationId}/{userId}")
    @Operation(summary = "Updates sector user by sector association and user id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SectorUserAuthorityDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorAssociationId")
    public ResponseEntity<SectorUserAuthorityDetailsDTO> updateSectorUserBySectorAssociationIdAndUserId(
            @PathVariable("sectorAssociationId") @Parameter(description = "The sector association id") Long sectorAssociationId,
            @PathVariable("userId") @Parameter(description = "The sector association id") String userId,
            @RequestBody @Valid @Parameter(description = "The modified sector user", required = true) SectorUserAuthorityDetailsDTO sectorUserAuthorityDetailsDTO) {
        sectorUserManagementService.updateSectorUser(sectorAssociationId, userId, sectorUserAuthorityDetailsDTO);
        return new ResponseEntity<>(sectorUserAuthorityDetailsDTO, HttpStatus.OK);
    }

    /**
     * Updates logged in operator user.
     *
     * @param appUser {@link AppUser}
     * @param sectorUserAuthorityDetailsDTO {@link SectorUserAuthorityDetailsDTO}
     * @return {@link SectorUserAuthorityDetailsDTO}
     */
    @PatchMapping(path = "/sector-association/{sectorAssociationId}/sector-user")
    @Operation(summary = "Updates logged in operator user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SectorUserAuthorityDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = SECTOR_USER)
    public ResponseEntity<SectorUserAuthorityDetailsDTO> updateCurrentSectorUser(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("sectorAssociationId") @Parameter(description = "The sector association id") Long sectorAssociationId,
            @RequestBody @Valid @Parameter(description = "The modified sector user", required = true) SectorUserAuthorityDetailsDTO sectorUserAuthorityDetailsDTO) {
        sectorUserManagementService.updateCurrentSectorUser(appUser, sectorAssociationId, sectorUserAuthorityDetailsDTO);
        return new ResponseEntity<>(sectorUserAuthorityDetailsDTO, HttpStatus.OK);
    }

    /**
     * Resets the 2FA device for a sector user by sector association and user id.
     *
     * @param sectorAssociationId Sector Association id
     * @param userId Keycloak user id
     */
    @PatchMapping(path = "/sector-association/{sectorAssociationId}/{userId}/reset-2fa")
    @Operation(summary = "Resets the 2FA device for a sector user by sector association and user id")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorAssociationId")
    public ResponseEntity<Void> resetSectorUser2Fa(
            @PathVariable("sectorAssociationId") @Parameter(description = "The sector association id") Long sectorAssociationId,
            @PathVariable("userId") @Parameter(description = "The sector user id") String userId) {
        sectorUserManagementService.resetSectorUser2Fa(sectorAssociationId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
