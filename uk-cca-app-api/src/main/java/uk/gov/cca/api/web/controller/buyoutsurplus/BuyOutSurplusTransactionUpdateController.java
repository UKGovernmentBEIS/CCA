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
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdateAmountDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdatePaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.NO_CONTENT;

@RestController
@RequestMapping("/v1.0/buy-out-surplus/transactions/{id}/update")
@RequiredArgsConstructor
@Tag(name = "Buy out surplus transaction update controller")
public class BuyOutSurplusTransactionUpdateController {

	private final BuyOutSurplusTransactionService buyOutSurplusTransactionService;

	@PostMapping(path = "/payment-status")
	@Operation(summary = "Updates the transaction payment status and transaction history")
	@ApiResponse(responseCode = "204", description = NO_CONTENT)
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized(resourceId = "#id")
	public ResponseEntity<Void> updateBuyOutSurplusTransactionPaymentStatus(
			@PathVariable("id") @Parameter(description = "The transaction id") Long id,
			@RequestBody @Valid @Parameter(description = "The Transaction Update Payment Status Info")
			BuyOutSurplusTransactionUpdatePaymentStatusDTO paymentStatusDTO,
			@Parameter(hidden = true) AppUser appUser
	) {

		buyOutSurplusTransactionService.updateTransactionPaymentStatus(id, paymentStatusDTO, appUser);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/amount")
	@Operation(summary = "Updates the transaction buy out or refund amount and transaction history")
	@ApiResponse(responseCode = "204", description = NO_CONTENT)
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized(resourceId = "#id")
	public ResponseEntity<Void> updateBuyOutSurplusTransactionAmount(
			@PathVariable("id") @Parameter(description = "The transaction id") Long id,
			@RequestBody @Valid @Parameter(description = "The Transaction Update Amount")
			BuyOutSurplusTransactionUpdateAmountDTO updateAmountDTO,
			@Parameter(hidden = true) AppUser appUser
	) {

		buyOutSurplusTransactionService.updateTransactionAmount(id, updateAmountDTO, appUser);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
