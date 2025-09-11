package uk.gov.cca.api.web.controller.buyoutsurplus;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.AuthorizedRole;

import java.util.List;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@RestController
@RequestMapping(path = "/v1.0/buy-out-surplus/run")
@RequiredArgsConstructor
@Tag(name = "Buy-out and surplus info")
public class BuyOutSurplusRunController {

	private final BuyOutSurplusQueryService buyOutSurplusQueryService;

	@GetMapping(path = "/excluded-accounts")
	@Operation(summary = "Retrieves all the on hold accounts for a specific target period")
	@ApiResponse(responseCode = "200", description = OK,
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					array = @ArraySchema(schema = @Schema(implementation = TargetUnitAccountBusinessInfoDTO.class))))
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ErrorResponse.class))})
	@AuthorizedRole(roleType = REGULATOR)
	public ResponseEntity<List<TargetUnitAccountBusinessInfoDTO>> getExcludedAccountsForBuyOutSurplusRun(
			@RequestParam @Parameter(name = "targetPeriodType", description = "The target period business id") TargetPeriodType targetPeriodType
			) {

		return new ResponseEntity<>(buyOutSurplusQueryService
				.getAllExcludedEligibleAccountsByTargetPeriod(targetPeriodType), HttpStatus.OK);
	}

}
