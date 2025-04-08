package uk.gov.cca.api.web.controller.account.buyoutsurplus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.web.orchestrator.account.service.TargetUnitAccountBuyOutSurplusServiceOrchestrator;
import uk.gov.netz.api.security.Authorized;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/target-unit-accounts/{accountId}/buy-out-surplus/")
@RequiredArgsConstructor
@Tag(name = "Buy-out and surplus info")
public class TargetUnitAccountBuyOutSurplusController {

	private final TargetUnitAccountBuyOutSurplusServiceOrchestrator targetUnitAccountBuyOutSurplusServiceOrchestrator;

	@GetMapping(path = "/excluded")
	@Operation(summary = "Retrieves the buy-out excluded (on hold) flag for a specific account")
	@ApiResponse(responseCode = "200", description = OK)
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized(resourceId = "#accountId")
	public ResponseEntity<Boolean> isAccountExcludedFromBuyOutSurplus(
			@PathVariable("accountId") @Parameter(description = "The account id") Long accountId
			) {

		return new ResponseEntity<>(targetUnitAccountBuyOutSurplusServiceOrchestrator
				.isAccountExcludedFromBuyOutSurplus(accountId),
				HttpStatus.OK);
	}


}
