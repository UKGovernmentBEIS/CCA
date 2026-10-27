package uk.gov.cca.api.web.controller.buyoutsurplus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionReceivedAmountHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailSearchResultsDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailsSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDetailQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionQueryService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;

import java.util.List;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping("/v1.0/buy-out-surplus/facility-transactions/{id}")
@RequiredArgsConstructor
@Validated
@Tag(name = "Buy-out and surplus facility transaction view")
public class BuyOutSurplusFacilityTransactionViewController {

    private final BuyOutSurplusQueryService buyOutSurplusQueryService;
    private final BuyOutSurplusTransactionDetailQueryService buyOutSurplusTransactionDetailQueryService;
    private final BuyOutSurplusTransactionQueryService buyOutSurplusTransactionQueryService;

    @GetMapping
    @Operation(summary = "Retrieve buy-out and surplus transaction information for facility")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FacilityBuyOutSurplusTransactionDTO.class)))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<FacilityBuyOutSurplusTransactionDTO> getFacilityBuyOutSurplusTransaction(
            @PathVariable("id") @Parameter(description = "The transaction id", required = true) Long id) {

        return new ResponseEntity<>(buyOutSurplusQueryService.getFacilityBuyOutSurplusTransaction(id), HttpStatus.OK);
    }

    @PostMapping("/details")
    @Operation(summary = "Retrieve buy-out and surplus transaction details for facilities that match the specified search criteria")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FacilityBuyOutSurplusTransactionDetailSearchResultsDTO.class)))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<FacilityBuyOutSurplusTransactionDetailSearchResultsDTO> getFacilityBuyOutSurplusTransactionDetails(
            @PathVariable("id") @Parameter(description = "The transaction id", required = true) Long id,
            @Valid @RequestBody @Parameter(description = "Search criteria for transaction details", required = true) FacilityBuyOutSurplusTransactionDetailsSearchCriteria searchCriteria) {

        return new ResponseEntity<>(buyOutSurplusTransactionDetailQueryService
                .getBuyOutSurplusTransactionDetailsList(id, searchCriteria), HttpStatus.OK);
    }

    @GetMapping("/received-amount/history")
    @Operation(summary = "Get the received amount history details for the provided transaction id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = BuyOutSurplusTransactionReceivedAmountHistoryDTO.class)))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<List<BuyOutSurplusTransactionReceivedAmountHistoryDTO>> getBuyOutSurplusTransactionReceivedAmountHistory(
            @PathVariable("id") @Parameter(description = "The transaction id", required = true) Long id) {

        return new ResponseEntity<>(buyOutSurplusTransactionQueryService.getBuyOutSurplusTransactionReceivedAmountHistory(id), HttpStatus.OK);
    }
}
