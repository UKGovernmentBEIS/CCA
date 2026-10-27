package uk.gov.cca.api.web.controller.facility;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.dto.FacilityPerformanceAccountTemplateDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.dto.FacilityPerformanceAccountTemplateDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.service.FacilityPerformanceAccountTemplateDataQueryService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;

import java.time.Year;

@RestController
@RequestMapping(path = "/v1.0/facilities/{facilityId}/performance-account-template-data-report")
@RequiredArgsConstructor
@Tag(name = "Target Period Performance Account Template Data Report of the Facility")
public class FacilityPerformanceAccountTemplateDataReportController {

    private final FacilityPerformanceAccountTemplateDataQueryService facilityPerformanceAccountTemplateDataQueryService;

    @GetMapping(path = "/info")
    @Operation(summary = "Retrieves the facility performance account template data report info if exist")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FacilityPerformanceAccountTemplateDataReportInfoDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#facilityId")
    public ResponseEntity<FacilityPerformanceAccountTemplateDataReportInfoDTO> getFacilityPerformanceAccountTemplateDataReportInfo(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("facilityId") @Parameter(description = "The facility id") Long facilityId,
            @RequestParam @Parameter(name = "targetPeriodYear", description = "The reporting year") Year targetPeriodYear) {

        return new ResponseEntity<>(
                facilityPerformanceAccountTemplateDataQueryService
                        .getFacilityPerformanceAccountTemplateDataReportInfo(facilityId, targetPeriodYear),
                HttpStatus.OK);
    }

    @GetMapping(path = "/details")
    @Operation(summary = "Retrieves the facility performance account template data report details")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FacilityPerformanceAccountTemplateDataReportDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#facilityId")
    public ResponseEntity<FacilityPerformanceAccountTemplateDataReportDetailsDTO> getFacilityPerformanceAccountTemplateDataReportDetails(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("facilityId") @Parameter(description = "The facility id") Long facilityId,
            @RequestParam @Parameter(name = "targetPeriodYear", description = "The reporting year") Year targetPeriodYear) {

        return new ResponseEntity<>(
                facilityPerformanceAccountTemplateDataQueryService
                        .getFacilityPerformanceAccountTemplateDataReportDetails(facilityId, targetPeriodYear),
                HttpStatus.OK);
    }
}
