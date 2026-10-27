package uk.gov.cca.api.web.controller.buyoutsurplus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDetailQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDetailFileEvidenceTokenService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.token.FileToken;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1.0/buy-out-surplus/transaction-details/{id}")
@RequiredArgsConstructor
@Validated
@Tag(name = "Buy-out and surplus transaction detail info view")
public class BuyOutSurplusTransactionDetailViewController {

    private final BuyOutSurplusTransactionDetailQueryService buyOutSurplusTransactionDetailQueryService;
    private final BuyOutSurplusTransactionDetailFileEvidenceTokenService buyOutSurplusTransactionFileEvidenceTokenService;

    @GetMapping("/history")
    @Operation(summary = "Retrieve buy-out and surplus transaction history changes by transaction detail id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = BuyOutSurplusTransactionDetailHistoryDTO.class))))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<List<BuyOutSurplusTransactionDetailHistoryDTO>> getBuyOutSurplusTransactionDetailHistory(
            @PathVariable("id") @Parameter(description = "The transaction detail id", required = true) Long id) {

        List<BuyOutSurplusTransactionDetailHistoryDTO> resultList = buyOutSurplusTransactionDetailQueryService
                .getBuyOutSurplusTransactionDetailHistory(id);

        return ResponseEntity.ok(resultList);
    }

    @GetMapping(path = "/evidence")
    @Operation(summary = "Generate the token to get the evidence file that belongs to the provided buy-out and surplus transaction detail id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<FileToken> generateBuyOutSurplusTransactionDetailEvidenceFileToken(
            @Parameter(hidden = true) AppUser authUser,
            @PathVariable("id") @Parameter(description = "The transaction detail id") @NotNull Long id,
            @RequestParam("fileEvidenceUuid") @Parameter(description = "The evidence file uuid") @NotNull UUID fileEvidenceUuid) {

        FileToken token = buyOutSurplusTransactionFileEvidenceTokenService.generateGetFileEvidenceToken(id, fileEvidenceUuid, authUser);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
