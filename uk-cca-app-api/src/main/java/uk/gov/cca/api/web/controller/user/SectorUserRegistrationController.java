package uk.gov.cca.api.web.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.user.sectoruser.domain.SectorInvitedUserInfoDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserRegistrationWithCredentialsDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserActivationService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.web.orchestrator.user.service.SectorUserRegistrationOrchestratorService;
import uk.gov.netz.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.netz.api.user.core.domain.dto.TokenDTO;

@RestController
@RequestMapping(path = "/v1.0/sector-users/registration")
@Tag(name = "Sector Users registration")
@RequiredArgsConstructor
public class SectorUserRegistrationController {

    private final SectorUserActivationService sectorUserActivationService;
    private final SectorUserRegistrationOrchestratorService sectorUserRegistrationOrchestratorService;

    @PostMapping(path = "/accept-invitation")
    @Operation(summary = "Accept invitation for sector user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SectorInvitedUserInfoDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.ACCEPT_SECTOR_INVITATION_TOKEN_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<SectorInvitedUserInfoDTO> acceptSectorUserInvitation(
            @RequestBody @Valid @Parameter(description = "The invitation token", required = true)
            TokenDTO invitationTokenDTO) {
        SectorInvitedUserInfoDTO sectorInvitedUserInfo =
                sectorUserRegistrationOrchestratorService.acceptInvitation(invitationTokenDTO.getToken());
        return new ResponseEntity<>(sectorInvitedUserInfo, HttpStatus.OK);
    }

    @PutMapping(path = "/accept-authority-and-enable-invited-sector-user-with-credentials")
    @Operation(summary = "Accept authority and enable sector user from invitation token")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SectorUserDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.ACCEPT_AUTHORITY_AND_ENABLE_SECTOR_USER_FROM_INVITATION_WITH_CREDENTIALS_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<SectorUserDTO> acceptAuthorityAndEnableInvitedUserWithCredentials(
            @RequestBody @Valid @Parameter(description = "The sector user", required = true)
            SectorUserRegistrationWithCredentialsDTO sectorUserRegistrationWithCredentialsDTO) {
        return new ResponseEntity<>(sectorUserActivationService.acceptAuthorityAndEnableInvitedUserWithCredentials(
                sectorUserRegistrationWithCredentialsDTO), HttpStatus.OK);
    }

    @PutMapping(path = "/accept-authority-and-set-credentials-to-sector-user")
    @Operation(summary = "Accept authority and set credentials to sector user")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SectorUserDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.SET_CREDENTIALS_TO_REGISTERED_SECTOR_USER_FROM_INVITATION_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> acceptAuthorityAndSetCredentialsToSectorUser(
            @RequestBody @Valid @Parameter(description = "The sector user credentials", required = true)
            InvitedUserCredentialsDTO invitedUserCredentialsDTO) {
        sectorUserActivationService.acceptAuthorityAndSetCredentialsToUser(invitedUserCredentialsDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}