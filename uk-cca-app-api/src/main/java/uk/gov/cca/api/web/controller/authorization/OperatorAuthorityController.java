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
import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityService;
import uk.gov.cca.api.user.operator.domain.OperatorAuthoritiesInfoDTO;
import uk.gov.cca.api.user.operator.service.OperatorUserAuthorityInfoService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.web.orchestrator.authorization.dto.AccountOperatorAuthorityUpdateWrapperDTO;
import uk.gov.cca.api.web.orchestrator.authorization.service.AccountOperatorUserAuthorityUpdateOrchestrator;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.authorization.core.domain.AppUser;


@Validated
@RestController
@RequestMapping(path = "/v1.0/operator-authorities")
@Tag(name = "Operator Authorities")
@RequiredArgsConstructor
public class OperatorAuthorityController {

    private final AccountOperatorUserAuthorityUpdateOrchestrator accountOperatorUserAuthorityUpdateOrchestrator;
    private final CcaOperatorAuthorityService ccaOperatorAuthorityService;
    private final OperatorUserAuthorityInfoService operatorUserAuthorityInfoService;

    @GetMapping(path = "/account/{accountId}")
    @Operation(summary = "Retrieves the authorities of type OPERATOR for the given account id along with the account contact types")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorAuthoritiesInfoDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<OperatorAuthoritiesInfoDTO> getAccountOperatorAuthorities(
            @Parameter(hidden = true) AppUser currentUser,
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId) {
        return new ResponseEntity<>(
                operatorUserAuthorityInfoService.getOperatorAuthoritiesInfo(currentUser, accountId),
                HttpStatus.OK);
    }

    @PatchMapping(path = "/account/{accountId}")
    @Operation(summary = "Updates authorities for users of type OPERATOR for the given account id")

    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_ACCOUNT_OPERATOR_AUTHORITY_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateAccountOperatorAuthorities(
            @PathVariable("accountId") @Parameter(description = "The account id")
            Long accountId,
            @RequestBody @Valid @Parameter(description = "The account operator authorities to update", required = true)
            AccountOperatorAuthorityUpdateWrapperDTO accountOperatorAuthorityUpdateWrapperDTO) {

        accountOperatorUserAuthorityUpdateOrchestrator.updateAccountOperatorAuthorities(
                accountOperatorAuthorityUpdateWrapperDTO.getAccountOperatorAuthorityUpdateList(),
                accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/account/{accountId}/{userId}")
    @Operation(summary = "Deletes authority from the account")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.DELETE_ACCOUNT_OPERATOR_AUTHORITY_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> deleteAccountOperatorAuthority(
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId,
            @PathVariable("userId") @Parameter(description = "The user id") String userId) {
        ccaOperatorAuthorityService.deleteAccountOperatorAuthority(userId, accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
