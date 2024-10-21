package uk.gov.cca.api.web.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.user.operator.domain.CcaOperatorInvitedUserInfoDTO;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDTO;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserRegistrationWithCredentialsDTO;
import uk.gov.cca.api.user.operator.service.CcaOperatorUserAcceptInvitationService;
import uk.gov.cca.api.user.operator.service.CcaOperatorUserActivationService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.netz.api.user.core.domain.dto.TokenDTO;
import uk.gov.netz.api.user.operator.domain.OperatorUserDTO;

@RestController
@RequestMapping(path = "/v1.0/operator-users/registration")
@Tag(name = "Operator users registration")
@SecurityRequirements
@RequiredArgsConstructor
public class OperatorUserRegistrationController {

    private final CcaOperatorUserActivationService ccaOperatorUserActivationService;
    private final CcaOperatorUserAcceptInvitationService ccaOperatorUserAcceptInvitationService;

    @PostMapping(path = "/accept-invitation")
    @Operation(summary = "Accept invitation for operator user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CcaOperatorInvitedUserInfoDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.ACCEPT_OPERATOR_INVITATION_TOKEN_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<CcaOperatorInvitedUserInfoDTO> acceptOperatorInvitation(
            @RequestBody @Valid @Parameter(description = "The invitation token", required = true) TokenDTO invitationTokenDTO) {
        CcaOperatorInvitedUserInfoDTO operatorInvitedUserInfo =
                ccaOperatorUserAcceptInvitationService.acceptInvitation(invitationTokenDTO.getToken());
        return new ResponseEntity<>(operatorInvitedUserInfo, HttpStatus.OK);
    }

    @PutMapping(path = "/accept-authority-and-enable-invited-operator-with-credentials")
    @Operation(summary = "Accept authority and enable operator user from invitation token")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorUserDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.ACCEPT_AUTHORITY_AND_ENABLE_OPERATOR_USER_FROM_INVITATION_WITH_CREDENTIALS_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<CcaOperatorUserDTO> acceptAuthorityAndEnableInvitedOperatorUserWithCredentials(
            @RequestBody @Valid @Parameter(description = "The operator user", required = true)
            CcaOperatorUserRegistrationWithCredentialsDTO ccaOperatorUserRegistrationWithCredentialsDTO) {
        return new ResponseEntity<>(ccaOperatorUserActivationService.acceptAuthorityAndEnableInvitedUserWithCredentials(
                ccaOperatorUserRegistrationWithCredentialsDTO), HttpStatus.OK);
    }

    @PutMapping(path = "/accept-authority-and-set-credentials-to-operator-user")
    @Operation(summary = "Accept authority and set credentials to operator user")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorUserDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.SET_CREDENTIALS_TO_REGISTERED_OPERATOR_USER_FROM_INVITATION_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> acceptAuthorityAndSetCredentialsToOperatorUser(
            @RequestBody @Valid @Parameter(description = "The operator user credentials", required = true)
            InvitedUserCredentialsDTO invitedUserCredentialsDTO) {
        ccaOperatorUserActivationService.acceptAuthorityAndSetCredentialsToUser(invitedUserCredentialsDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
