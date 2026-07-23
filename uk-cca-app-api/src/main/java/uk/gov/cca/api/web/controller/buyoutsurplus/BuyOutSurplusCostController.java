package uk.gov.cca.api.web.controller.buyoutsurplus;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.UPDATE_TARGET_PERIOD_BUY_OUT_COST_BAD_REQUEST;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodBuyOutCostUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodBuyOutDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.AuthorizedRole;

@RestController
@RequestMapping(path = "/v1.0/buy-out-surplus/cost")
@RequiredArgsConstructor
@Tag(name = "Buy-out and surplus cost info")
public class BuyOutSurplusCostController {

	private final TargetPeriodService targetPeriodService;
	
	@GetMapping
	@Operation(summary = "Retrieves the buy-out cost for each target period in the specified scheme")
	@ApiResponse(responseCode = "200", description = OK,
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					array = @ArraySchema(schema = @Schema(implementation = TargetPeriodBuyOutDetailsDTO.class))))
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ErrorResponse.class))})
	@AuthorizedRole(roleType = REGULATOR)
	public ResponseEntity<List<TargetPeriodBuyOutDetailsDTO>> getBuyOutCosts(
			@RequestParam @Parameter(name = "schemeVersion", description = "The scheme version") SchemeVersion schemeVersion
			) {

		return new ResponseEntity<>(targetPeriodService
				.getTargetPeriodBuyOutDetailsBySchemeVersion(schemeVersion), HttpStatus.OK);
	}
	
	@PatchMapping("/{tp}")
	@Operation(summary = "Updates the buy-out cost for the specified target period")
	@ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
	@ApiResponse(responseCode = "400", description = UPDATE_TARGET_PERIOD_BUY_OUT_COST_BAD_REQUEST, 
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, 
					schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
			content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ErrorResponse.class))})
	@AuthorizedRole(roleType = REGULATOR)
	public ResponseEntity<Void> updateBuyOutCost(
			@PathVariable TargetPeriodType tp,
			@Valid @RequestBody @Parameter(name = "buyOutCost", description = "The buy-out cost") TargetPeriodBuyOutCostUpdateDTO buyOutCostDTO
			) {
		targetPeriodService.updateBuyOutCost(tp, buyOutCostDTO);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
