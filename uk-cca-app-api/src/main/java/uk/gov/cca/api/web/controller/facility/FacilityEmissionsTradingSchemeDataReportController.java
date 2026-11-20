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
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto.EmissionsTradingSchemeDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto.EmissionsTradingSchemeDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.service.EmissionsTradingSchemeDataQueryService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;

import java.time.Year;

@RestController
@RequestMapping(path = "/v1.0/facilities/{facilityId}/emissions-trading-scheme-data-report")
@RequiredArgsConstructor
@Tag(name = "Emissions Trading Scheme Data Report of the Facility")
public class FacilityEmissionsTradingSchemeDataReportController {

    private final EmissionsTradingSchemeDataQueryService emissionsTradingSchemeDataQueryService;

    @GetMapping(path = "/info")
    @Operation(summary = "Retrieves the facility emissions trading scheme data report info if exists")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmissionsTradingSchemeDataReportInfoDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#facilityId")
    public ResponseEntity<EmissionsTradingSchemeDataReportInfoDTO> getEmissionsTradingSchemeDataReportInfo(
            @PathVariable("facilityId") @Parameter(description = "The facility id") Long facilityId,
            @RequestParam @Parameter(name = "targetPeriodYear", description = "The reporting year") Year targetPeriodYear) {

        return new ResponseEntity<>(
                emissionsTradingSchemeDataQueryService.getEmissionsTradingSchemeDataReportInfo(facilityId, targetPeriodYear),
                HttpStatus.OK);
    }

    @GetMapping(path = "/details")
    @Operation(summary = "Retrieves the facility emissions trading scheme data report details")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmissionsTradingSchemeDataReportDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#facilityId")
    public ResponseEntity<EmissionsTradingSchemeDataReportDetailsDTO> getEmissionsTradingSchemeDataReportDetails(
            @PathVariable("facilityId") @Parameter(description = "The facility id") Long facilityId,
            @RequestParam @Parameter(name = "targetPeriodYear", description = "The reporting year") Year targetPeriodYear) {

        return new ResponseEntity<>(
                emissionsTradingSchemeDataQueryService.getEmissionsTradingSchemeDataReportDetails(facilityId, targetPeriodYear),
                HttpStatus.OK);
    }
}
