package uk.gov.cca.api.web.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.account.domain.dto.AccountContactDTO;
import uk.gov.cca.api.account.domain.dto.AccountContactVbInfoResponse;
import uk.gov.cca.api.account.service.AccountVbSiteContactService;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.security.Authorized;
import uk.gov.cca.api.web.security.AuthorizedRole;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;

import java.util.List;

import static uk.gov.netz.api.common.domain.RoleType.VERIFIER;

@Validated
@RestController
@RequestMapping(path = "/v1.0/vb-site-contacts")
@RequiredArgsConstructor
@Tag(name = "VB site contacts")
public class VbSiteContactController {

    private final AccountVbSiteContactService accountVbSiteContactService;

    /**
     * Retrieves the accounts verification body site contacts in which Verifier user has access.
     *
     * @param user {@link AppUser}
     * @param page Page number
     * @param pageSize Page size number
     * @return {@link AccountContactVbInfoResponse}
     */
    @AuthorizedRole(roleType = VERIFIER)
    @GetMapping
    @Operation(summary = "Retrieves the accounts and verification body site contact of the accounts")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountContactVbInfoResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<AccountContactVbInfoResponse> getVbSiteContacts(
            @Parameter(hidden = true) AppUser user,
            @RequestParam("page") @Parameter(name = "page", description = "The page number starting from zero")
            @Min(value = 0, message = "{parameter.page.typeMismatch}")
            @NotNull(message = "{parameter.page.typeMismatch}") Integer page,
            @RequestParam("size") @Parameter(name = "size", description = "The page size")
            @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")
            @NotNull(message = "{parameter.pageSize.typeMismatch}") Integer pageSize) {

        return new ResponseEntity<>(accountVbSiteContactService.getAccountsAndVbSiteContacts(user, page, pageSize), HttpStatus.OK);
    }

    /**
     * Updates accounts verification body site contact.
     *
     * @param user {@link AppUser}
     * @param vbSiteContacts List of {@link AccountContactDTO}
     * @return Empty response
     */
    @PostMapping
    @Operation(summary = "Updates verification body site contacts")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_VB_SITE_CONTACTS_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> updateVbSiteContacts(
            @Parameter(hidden = true) AppUser user,
            @RequestBody @Valid @NotEmpty @Parameter(description = "The accounts with updated verification body site contacts", required = true)
                    List<AccountContactDTO> vbSiteContacts) {
        accountVbSiteContactService.updateVbSiteContacts(user, vbSiteContacts);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
