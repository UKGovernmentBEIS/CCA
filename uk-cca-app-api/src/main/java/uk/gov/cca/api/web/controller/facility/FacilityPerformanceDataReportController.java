package uk.gov.cca.api.web.controller.facility;

import java.time.Year;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataStatusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataUpdateLockDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataUpdateVariationIndicatorDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;

@RestController
@RequestMapping(path = "/v1.0/facilities/{facilityId}/performance-data-report")
@RequiredArgsConstructor
@Tag(name = "Target Period Performance Data Report of the Facility")
public class FacilityPerformanceDataReportController {

	private final PerformanceDataFacilityStatusQueryService performanceDataFacilityStatusQueryService;
	private final PerformanceDataFacilityStatusService performanceDataFacilityStatusService;
	
	@GetMapping(path = "/status")
    @Operation(summary = "Retrieves the facility performance data status info")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = FacilityPerformanceDataStatusInfoDTO.class)))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#facilityId")
    public ResponseEntity<List<FacilityPerformanceDataStatusInfoDTO>> getFacilityPerformanceDataStatus(
            @PathVariable("facilityId") @Parameter(description = "The facility id") Long facilityId,
            @RequestParam @Parameter(name = "targetPeriodType", description = "The target period business id") TargetPeriodType targetPeriodType,
            @RequestParam @Parameter(name = "reportType", description = "The performance data report type") PerformanceDataReportType reportType,
            @Parameter(hidden = true) AppUser currentUser) {

		return new ResponseEntity<>(
				performanceDataFacilityStatusQueryService
						.getFacilityPerformanceDataStatusInfo(facilityId, targetPeriodType, reportType, currentUser),
				HttpStatus.OK);
    }
	
	@GetMapping(path = "/details")
    @Operation(summary = "Retrieves the latest performance data for the facility and the target period year.")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FacilityPerformanceDataReportDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#facilityId")
    public ResponseEntity<FacilityPerformanceDataReportDetailsDTO> getFacilityPerformanceDataReportDetails(
            @PathVariable("facilityId") @Parameter(description = "The facility id") Long facilityId,
            @RequestParam @Parameter(name = "targetPeriodYear", description = "The target period year")
            Year targetPeriodYear
    ) {

		return new ResponseEntity<>(performanceDataFacilityStatusQueryService
				.getFacilityPerformanceDataReportDetails(facilityId, targetPeriodYear),
				HttpStatus.OK);
    }
	
	@PutMapping(path = "/lock")
    @Operation(summary = "Locks/unlocks the facility for reporting performance data for the specified target period year")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FacilityPerformanceDataUpdateLockDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#facilityId")
    public ResponseEntity<Void> updateFacilityPerformanceDataLock(
            @PathVariable("facilityId") @Parameter(description = "The facility id") Long facilityId,
            @RequestBody @Valid @Parameter(description = "The update lock info") FacilityPerformanceDataUpdateLockDTO updateLockDTO
    ) {
		performanceDataFacilityStatusService.updateFacilityPerformanceDataLock(facilityId, updateLockDTO);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
	
	@PutMapping(path = "/variation-indicator")
    @Operation(summary = "Sets the variation indicator for the facility performance data for the specified target period year")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FacilityPerformanceDataUpdateVariationIndicatorDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#facilityId")
    public ResponseEntity<Void> updateFacilityPerformanceDataVariationIndicator(
            @PathVariable("facilityId") @Parameter(description = "The facility id") Long facilityId,
            @RequestBody @Valid @Parameter(description = "The update variation indicator info") 
            FacilityPerformanceDataUpdateVariationIndicatorDTO updateVariationIndicatorDTO
    ) {
		performanceDataFacilityStatusService.updateFacilityPerformanceDataVariationIndicator(facilityId, updateVariationIndicatorDTO);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
