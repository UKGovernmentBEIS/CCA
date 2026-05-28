package uk.gov.cca.api.web.controller.performancedata;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportTypeDTO;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataService;
import uk.gov.netz.api.security.AuthorizedRole;

import java.util.List;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.OK;

@Validated
@RestController
@RequestMapping(path = "/v1.0/target-period-reporting/performance-data/")
@RequiredArgsConstructor
@Tag(name = "Performance data reporting view info")
public class PerformanceDataReportingViewController {

    private final PerformanceDataService performanceDataReportService;

    @GetMapping(path = "/available-target-periods")
    @Operation(summary = "Retrieves the eligible target periods for performance data reporting")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = PerformanceDataReportTypeDTO.class)))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {SECTOR_USER})
    public ResponseEntity<List<PerformanceDataReportTypeDTO>> getAvailableTargetPeriodsForPerformanceDataReporting(
            @RequestParam("scheme") @Parameter(description = "The CCA Scheme") @NotNull SchemeVersion scheme) {
        return new ResponseEntity<>(performanceDataReportService.getAvailableTargetPeriodsForPerformanceDataReporting(scheme), HttpStatus.OK);
    }
}
