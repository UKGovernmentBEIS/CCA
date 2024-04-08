package uk.gov.cca.api.web.controller.user;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.feedback.UserFeedbackDto;
import uk.gov.cca.api.feedback.UserFeedbackService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.netz.api.terms.UpdateTermsDTO;
import uk.gov.netz.api.token.FileToken;
import uk.gov.cca.api.user.application.UserService;
import uk.gov.cca.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.cca.api.user.core.service.UserSignatureService;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;
import uk.gov.cca.api.user.operator.domain.OperatorUserDTO;
import uk.gov.cca.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.cca.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;

import java.util.UUID;

/**
 * Controller for users.
 */
@RestController
@RequestMapping(path = "/v1.0/users")
@Tag(name = "Users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAuthService userAuthService;
    private final UserSignatureService userSignatureService;
    private final UserFeedbackService userFeedbackService;

    /**
     * Updates accepted terms and conditions of the logged in user.
     *
     * @param updateTermsDTO a terms transfer object.
     */
    @PatchMapping(path = "/terms-and-conditions")
    @Operation(summary = "Updates accepted terms and conditions of the logged in user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UpdateTermsDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<UpdateTermsDTO> editUserTerms(
            @Parameter(hidden = true) AppUser appUser,
            @RequestBody @Valid @Parameter(description = "The updateTermsDTO", required = true) UpdateTermsDTO updateTermsDTO) {
        userAuthService.updateUserTerms(appUser.getUserId(), updateTermsDTO.getVersion());
        return new ResponseEntity<>(updateTermsDTO, HttpStatus.OK);
    }

    /**
     * Retrieves info of the logged in user.
     *
     * @return {@link ApplicationUserDTO}
     */
    @GetMapping
    @Operation(summary = "Retrieves info of the logged in user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(oneOf = {ApplicationUserDTO.class, OperatorUserDTO.class, RegulatorUserDTO.class, VerifierUserDTO.class}))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<ApplicationUserDTO> getCurrentUser(@Parameter(hidden = true) AppUser appUser) {

        return new ResponseEntity<>(userService.getUserById(appUser.getUserId()), HttpStatus.OK);
    }

    @GetMapping(path = "/signature")
    @Operation(summary = "Generate the token to get the signature of the current user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<FileToken> generateGetCurrentUserSignatureToken(
            @Parameter(hidden = true) AppUser appUser,
            @RequestParam("signatureUuid") @Parameter(name = "signatureUuid", description = "The signature uuid") @NotNull UUID signatureUuid) {
        FileToken getFileToken = userSignatureService.generateSignatureFileToken(appUser.getUserId(), signatureUuid);
        return new ResponseEntity<>(getFileToken, HttpStatus.OK);
    }

    @PostMapping(path = "/feedback")
    @Operation(summary = "Provides the feedback about the service for the logged in user")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> provideUserFeedback(
            @Parameter(hidden = true) AppUser appUser,
            @RequestBody @Valid @Parameter(description = "The user feedback", required = true) UserFeedbackDto userFeedbackDto) {
        String domainUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        userFeedbackService.sendFeedback(domainUrl, userFeedbackDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
