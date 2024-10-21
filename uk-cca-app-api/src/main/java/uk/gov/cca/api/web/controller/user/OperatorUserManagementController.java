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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDetailsDTO;
import uk.gov.cca.api.user.operator.service.CcaOperatorUserManagementService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.netz.api.user.operator.domain.OperatorUserDTO;
import uk.gov.netz.api.user.operator.service.OperatorUserManagementService;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;

@RestController
@RequestMapping(path = "/v1.0/operator-users")
@Tag(name = "Operator Users")
@RequiredArgsConstructor
public class OperatorUserManagementController {

    private final OperatorUserManagementService operatorUserManagementService;

    private final CcaOperatorUserManagementService ccaOperatorUserManagementService;

    /**
     * Retrieves info of user by account and user id.
     *
     * @param accountId Account id
     * @param userId Keycloak user id
     * @return {@link OperatorUserDTO}
     */
    @GetMapping(path = "/account/{accountId}/{userId}")
    @Operation(summary = "Retrieves info of operator user by account and user id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CcaOperatorUserDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<CcaOperatorUserDetailsDTO> getOperatorUserById(
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId,
            @PathVariable("userId") @Parameter(description = "The operator user id") String userId) {
        return new ResponseEntity<>(ccaOperatorUserManagementService.getOperatorUserByAccountIdAndUserId(userId, accountId),
                HttpStatus.OK);
    }

    @GetMapping(path = "/account/{accountId}")
    @Operation(summary = "Retrieves info of the logged in operator user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = CcaOperatorUserDetailsDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<CcaOperatorUserDetailsDTO> getCurrentOperatorUser(@Parameter(hidden = true) AppUser appUser,
                                                                            @PathVariable("accountId") @Parameter(description = "The operator user id") Long accountId) {
        return new ResponseEntity<>(ccaOperatorUserManagementService.getOperatorUserByAccountIdAndUserId(appUser.getUserId(), accountId), HttpStatus.OK);
    }

    /**
     *  Updates logged in operator user
     * @param appUser
     * @param accountId
     * @param ccaOperatorUserDetailsDTO
     * @return
     */
    @PatchMapping(path = "/account/{accountId}")
    @Operation(summary = "Updates logged in operator user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CcaOperatorUserDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = OPERATOR)
    public ResponseEntity<CcaOperatorUserDetailsDTO> updateCurrentOperatorUser(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("accountId") @Parameter(description = "The operator account id") Long accountId,
            @RequestBody @Valid @Parameter(description = "The modified operator user", required = true) CcaOperatorUserDetailsDTO ccaOperatorUserDetailsDTO) {
        ccaOperatorUserManagementService.updateCurrentOperatorUser(appUser,accountId, ccaOperatorUserDetailsDTO);
        return new ResponseEntity<>(ccaOperatorUserDetailsDTO, HttpStatus.OK);
    }

    /**
     * Updates operator user by account and user id
     * @param accountId
     * @param userId
     * @param ccaOperatorUserDetailsDTO
     * @return
     */
    @PatchMapping(path = "/account/{accountId}/{userId}")
    @Operation(summary = "Updates operator user by account and user id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CcaOperatorUserDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<CcaOperatorUserDetailsDTO> updateOperatorUserById(
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId,
            @PathVariable("userId") @Parameter(description = "The operator user id") String userId,
            @RequestBody @Valid @Parameter(description = "The modified operator user", required = true) CcaOperatorUserDetailsDTO ccaOperatorUserDetailsDTO) {
        ccaOperatorUserManagementService.updateOperatorUserByAccountAndUserId(accountId, userId, ccaOperatorUserDetailsDTO);
        return new ResponseEntity<>(ccaOperatorUserDetailsDTO, HttpStatus.OK);
    }
    
    /**
     * Resets the 2FA device for an operator user by account and user id.
     *
     * @param accountId Account id
     * @param userId Keycloak user id
     */
    @PatchMapping(path = "/account/{accountId}/{userId}/reset-2fa")
    @Operation(summary = "Resets the 2FA device for an operator user by account and user id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorUserDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> resetOperator2Fa(
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId,
            @PathVariable("userId") @Parameter(description = "The operator user id") String userId) {
        operatorUserManagementService.resetOperator2Fa(accountId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
