package uk.gov.cca.api.web.controller.buyoutsurplus;

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

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailUpdateAmountDTO;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.web.orchestrator.buyoutsurplus.service.BuyOutSurplusTransactionUpdateServiceOrchestrator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.NO_CONTENT;

@RestController
@RequestMapping("/v1.0/buy-out-surplus/transaction-details/{id}/update")
@RequiredArgsConstructor
@Tag(name = "Buy out surplus transaction detail update controller")
public class BuyOutSurplusTransactionDetailUpdateController {

    private final BuyOutSurplusTransactionUpdateServiceOrchestrator buyOutSurplusTransactionUpdateServiceOrchestrator;

    @PostMapping("/amount")
    @Operation(summary = "Updates the transaction detail buy out or refund amount")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_BUY_OUT_SURPLUS_TRANSACTION_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<Void> updateBuyOutSurplusTransactionDetailAmount(
            @PathVariable("id") @Parameter(description = "The transaction detail id") Long id,
            @RequestBody @Valid @Parameter(description = "The Transaction Update Amount")
            BuyOutSurplusTransactionDetailUpdateAmountDTO updateAmountDTO,
            @Parameter(hidden = true) AppUser appUser) {

        buyOutSurplusTransactionUpdateServiceOrchestrator.updateTransactionDetailAmount(id, updateAmountDTO, appUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
