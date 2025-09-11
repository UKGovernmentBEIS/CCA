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

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionSummaryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDocumentService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionFileEvidenceTokenService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.token.FileToken;

import java.util.List;
import java.util.UUID;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping("/v1.0/buy-out-surplus/transactions/{id}")
@RequiredArgsConstructor
@Validated
@Tag(name = "Buy-out and surplus transaction info view")
public class BuyOutSurplusTransactionViewController {

    private final BuyOutSurplusQueryService buyOutSurplusQueryService;
    private final BuyOutSurplusTransactionDocumentService buyOutSurplusTransactionDocumentService;
    private final BuyOutSurplusTransactionFileEvidenceTokenService buyOutSurplusTransactionFileEvidenceTokenService;

    @GetMapping
    @Operation(summary = "Retrieve buy-out and surplus transaction summary information by transaction id")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BuyOutSurplusTransactionSummaryDTO.class)))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<BuyOutSurplusTransactionSummaryDTO> getBuyOutSurplusTransactionSummary(
            @PathVariable @Parameter(name = "id" , description = "The transaction id") Long id) {

        return new ResponseEntity<>(buyOutSurplusQueryService.getBuyOutSurplusTransactionSummary(id), HttpStatus.OK);
    }

    @GetMapping(path = "/details")
    @Operation(summary = "Retrieve buy-out and surplus transaction details by id")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BuyOutSurplusTransactionDetailsDTO.class)))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<BuyOutSurplusTransactionDetailsDTO> getBuyOutSurplusTransactionDetails(
            @PathVariable("id") @Parameter(description = "The transaction id", required = true) Long id) {

        BuyOutSurplusTransactionDetailsDTO result = buyOutSurplusQueryService.getBuyOutSurplusTransactionDetails(id);

        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/document")
    @Operation(summary = "Generate the token to get the document that belongs to the provided Transaction id")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class)))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<FileToken> generateBuyOutSurplusTransactionDocumentToken(
            @PathVariable("id") @Parameter(name = "id", description = "The Transaction id") @NotNull Long id,
            @RequestParam("fileDocumentUuid") @Parameter(name = "fileDocumentUuid", description = "The document uuid") @NotNull UUID fileDocumentUuid) {

        FileToken getFileDocumentToken = buyOutSurplusTransactionDocumentService.generateGetFileDocumentToken(id, fileDocumentUuid);

        return new ResponseEntity<>(getFileDocumentToken, HttpStatus.OK);
    }

    @GetMapping("/history")
    @Operation(summary = "Retrieve buy-out and surplus transaction history changes by transaction id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = BuyOutSurplusTransactionHistoryDTO.class))))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<List<BuyOutSurplusTransactionHistoryDTO>> getBuyOutSurplusTransactionHistory(
            @PathVariable("id") @Parameter(description = "The transaction id", required = true) Long id) {

        List<BuyOutSurplusTransactionHistoryDTO> resultList = buyOutSurplusQueryService.getBuyOutSurplusTransactionHistory(id);

        return ResponseEntity.ok(resultList);
    }

    @GetMapping(path = "/evidence")
    @Operation(summary = "Generate the token to get the evidence file that belongs to the provided buy-out and surplus transaction id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<FileToken> generateBuyOutSurplusTransactionEvidenceFileToken(
            @Parameter(hidden = true) AppUser authUser,
            @PathVariable("id") @Parameter(description = "The transaction id") @NotNull Long id,
            @RequestParam("fileEvidenceUuid") @Parameter(description = "The evidence file uuid") @NotNull UUID fileEvidenceUuid) {

        FileToken token = buyOutSurplusTransactionFileEvidenceTokenService.generateGetFileEvidenceToken(id, fileEvidenceUuid, authUser);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
