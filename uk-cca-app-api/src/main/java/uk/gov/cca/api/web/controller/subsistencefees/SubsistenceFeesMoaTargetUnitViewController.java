package uk.gov.cca.api.web.controller.subsistencefees;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesSearchCriteria;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaFacilityQueryService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaTargetUnitQueryService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;

@RestController
@RequestMapping(path = "/v1.0/subsistence-fees-moa-target-units/")
@RequiredArgsConstructor
@Tag(name = "Subsistence fees MoA target unit info view")
public class SubsistenceFeesMoaTargetUnitViewController {

	private final SubsistenceFeesMoaTargetUnitQueryService subsistenceFeesMoaTargetUnitQueryService;
	private final SubsistenceFeesMoaFacilityQueryService subsistenceFeesMoaFacilityQueryService;
	
	@GetMapping(path = "{moaTargetUnitId}")
    @Operation(summary = "Retrieves the details for the specific MoA target unit id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SubsistenceFeesMoaTargetUnitDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#moaTargetUnitId")
    public ResponseEntity<SubsistenceFeesMoaTargetUnitDetailsDTO> getSubsistenceFeesMoaTargetUnitDetailsById(
            @Parameter(description = "The MoA target unit id") @PathVariable("moaTargetUnitId") Long moaTargetUnitId) {
        return new ResponseEntity<>(subsistenceFeesMoaTargetUnitQueryService.getSubsistenceFeesMoaTargetUnitDetailsById(moaTargetUnitId), HttpStatus.OK);
    }
	
	@PostMapping(path = "{moaTargetUnitId}/facilities")
    @Operation(summary = "Retrieves the subsistence fees MoA target unit facilities")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SubsistenceFeesMoaFacilitySearchResults.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized(resourceId = "#moaTargetUnitId")
    public ResponseEntity<SubsistenceFeesMoaFacilitySearchResults> getSubsistenceFeesMoaFacilities(
    		@Parameter(description = "The moa target unit id") @PathVariable("moaTargetUnitId") Long moaTargetUnitId,
    		@RequestBody @Valid @Parameter(description = "The search criteria", required = true) SubsistenceFeesSearchCriteria criteria) {
        return new ResponseEntity<>(subsistenceFeesMoaFacilityQueryService.getSubsistenceFeesMoaFacilities(moaTargetUnitId, criteria), HttpStatus.OK);
    }
}
