package uk.gov.cca.api.web.controller.buyoutsurplus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.AuthorizedRole;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@RestController
@RequestMapping("/v1.0/buy-out-surplus/transactions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Buy-out and surplus transactions info view")
public class BuyOutSurplusTransactionsViewController {
    
    private final BuyOutSurplusQueryService buyOutSurplusQueryService;

    @PostMapping
    @Operation(summary = "Retrieve buy-out and surplus transactions that match the specified search criteria")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BuyOutSurplusTransactionsListDTO.class)))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<BuyOutSurplusTransactionsListDTO> getBuyOutSurplusTransactions(
            @Parameter(hidden = true) AppUser appUser,
            @Valid @RequestBody @Parameter(description = "Search criteria for transactions", required = true) BuyOutSurplusTransactionsListSearchCriteria searchCriteria) {
        
        BuyOutSurplusTransactionsListDTO result = buyOutSurplusQueryService
                .getBuyOutSurplusTransactionsList(appUser, searchCriteria);

        return ResponseEntity.ok(result);
    }
}
