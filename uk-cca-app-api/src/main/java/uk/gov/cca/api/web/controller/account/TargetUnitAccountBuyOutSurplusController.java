package uk.gov.cca.api.web.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AccountBuyOutSurplusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusExclusionService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;

import java.util.List;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/target-unit-accounts/{accountId}/buy-out-surplus/")
@RequiredArgsConstructor
@Tag(name = "Buy-out and surplus info")
public class TargetUnitAccountBuyOutSurplusController {

	private final BuyOutSurplusExclusionService buyOutSurplusExclusionService;
	private final SurplusService surplusService;
	private final SurplusQueryService surplusQueryService;

	@GetMapping
	@Operation(summary = "Retrieves the buy-out and surplus details for a specific account")
	@ApiResponse(responseCode = "200", description = OK,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountBuyOutSurplusInfoDTO.class))})
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized(resourceId = "#accountId")
	public ResponseEntity<AccountBuyOutSurplusInfoDTO> getBuyOutSurplusInfoByAccountId(
			@PathVariable("accountId") @Parameter(description = "The account id") Long accountId
			) {

		return new ResponseEntity<>(buyOutSurplusExclusionService
				.getBuyOutSurplusInfoByAccountId(accountId),
				HttpStatus.OK);
	}

	@GetMapping(path = "/history")
	@Operation(summary = "Retrieves the surplus history for a specific account and target period")
	@ApiResponse(responseCode = "200", description = OK,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					array = @ArraySchema(schema = @Schema(implementation = SurplusHistoryDTO.class)))})
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized(resourceId = "#accountId")
	public ResponseEntity<List<SurplusHistoryDTO>> getAllSurplusHistoryByTargetPeriodAndAccountId(
			@PathVariable("accountId") @Parameter(description = "The account id") Long accountId,
			@RequestParam(name = "targetPeriod") @Parameter(required = true, description = "The target period business id") TargetPeriodType targetPeriod
			) {

		return new ResponseEntity<>(surplusQueryService
				.getAllSurplusHistoryByAccountIdAndTargetPeriod(targetPeriod, accountId),
				HttpStatus.OK);
	}

	@PostMapping
	@Operation(summary = "Updates the surplus and surplus history for a specific account and target period")
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
	@Authorized(resourceId = "#accountId")
	public ResponseEntity<Void> updateSurplusGained(
			@Parameter(hidden = true) AppUser appUser,
			@PathVariable("accountId") @Parameter(description = "The account id") Long accountId,
			@RequestBody @Valid @Parameter(description = "Updates Surplus info") SurplusUpdateDTO surplusUpdateDTO
			) {

		surplusService.updateSurplusGained(surplusUpdateDTO, accountId, appUser);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping(path = "/exclude")
	@Operation(summary = "Creates the buy-out excluded (on hold) flag for a specific account")
	@ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized(resourceId = "#accountId")
	public ResponseEntity<Void> excludeAccountFromBuyOutSurplus(
			@PathVariable(name = "accountId") @Parameter(description = "The account id") Long accountId) {
		buyOutSurplusExclusionService.excludeAccountFromBuyOutSurplus(accountId);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping(path = "/include")
	@Operation(summary = "Removes the buy-out excluded (on hold) flag for a specific account")
	@ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized(resourceId = "#accountId")
	public ResponseEntity<Void> removeAccountExclusionFromBuyOutSurplus(
			@PathVariable(name = "accountId") @Parameter(description = "The account id") Long accountId
	) {

		buyOutSurplusExclusionService.removeAccountExclusionFromBuyOutSurplus(accountId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
