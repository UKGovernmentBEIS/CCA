package uk.gov.cca.api.web.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.web.orchestrator.account.dto.TargetUnitAccountDetailsResponseDTO;
import uk.gov.cca.api.web.orchestrator.account.service.TargetUnitAccountQueryServiceOrchestrator;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria.SortBy;
import uk.gov.netz.api.account.domain.dto.AccountSearchResults;
import uk.gov.netz.api.account.service.AccountSearchServiceDelegator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;


@Validated
@RestController
@RequestMapping(path = "/v1.0/target-unit-accounts/")
@RequiredArgsConstructor
@Tag(name = "Target Unit Account info view")
public class TargetUnitAccountViewController {

    private final TargetUnitAccountQueryServiceOrchestrator targetUnitAccountQueryServiceOrchestrator;
    private final AccountSearchServiceDelegator accountSearchServiceDelegator;

    @GetMapping
    @Operation(summary = "Retrieves the current user associated accounts")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountSearchResults.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, SECTOR_USER})
    public ResponseEntity<AccountSearchResults> searchUserAccounts(
            @Parameter(hidden = true) AppUser appUser,
            @RequestParam(value = "term", required = false) @Size(min = 3, max = 256) @Parameter(name = "term", description = "The term to search") String term,
            @RequestParam(value = "page") @NotNull @Parameter(name = "page", description = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") Integer page,
            @RequestParam(value = "size") @NotNull @Parameter(name = "size", description = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}") Integer pageSize
    ) {
        return new ResponseEntity<>(
                accountSearchServiceDelegator.getAccountsByUserAndSearchCriteria(
                        appUser,
                        AccountSearchCriteria.builder()
                                .term(term)
                                .paging(PagingRequest.builder().pageNumber(page).pageSize(pageSize).build())
                                .sortBy(SortBy.ACCOUNT_BUSINESS_ID)
                                .direction(Direction.ASC)
                                .build()),
                HttpStatus.OK);
    }

    @GetMapping(path = "/{accountId}")
    @Operation(summary = "Retrieves the target unit account")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TargetUnitAccountDetailsResponseDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<TargetUnitAccountDetailsResponseDTO> getTargetUnitAccountDetailsById(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId) {

        return new ResponseEntity<>(targetUnitAccountQueryServiceOrchestrator.getTargetUnitAccountDetailsById(accountId), HttpStatus.OK);
    }
}

