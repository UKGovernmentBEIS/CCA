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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserInvitationService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;

@RestController
@RequestMapping(path = "/v1.0/sector-users/invite")
@Tag(name = "Sector Users invitation")
@RequiredArgsConstructor
public class SectorUserInvitationController {

    private final SectorUserInvitationService sectorUserInvitationService;

    @PostMapping(path = "/sector-association/{sectorAssociationId}")
    @Operation(summary = "Adds a new sector user to a sector association with a specified role.")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorAssociationId")
    public ResponseEntity<Void> inviteUserToSectorAssociation(
            @Parameter(hidden = true) AppUser currentUser,
            @PathVariable("sectorAssociationId") @Parameter(description = "The sector association id") Long sectorAssociationId,
            @RequestBody @Valid @Parameter(description = "The sector user sector association registration info", required = true)
            SectorUserInvitationDTO sectorUserInvitationDTO) {
    	sectorUserInvitationService.inviteUserToSectorAssociation(sectorAssociationId, sectorUserInvitationDTO, currentUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
