package uk.gov.cca.api.web.controller.sectorassociation;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.OK;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;

@Validated
@RestController
@RequestMapping(path = "/v1.0/sector-association/{sectorAssociationId}/performance-data-report/")
@RequiredArgsConstructor
@Tag(name = "Sector-Level Performance Data View Pages")
public class SectorAssociationAccountPerformanceDataReportController {
    
    private final AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;
    
    @PostMapping
    @Operation(summary = "Populates the target period performance data of sector accounts")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SectorAccountPerformanceDataReportListDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorAssociationId")
    public ResponseEntity<SectorAccountPerformanceDataReportListDTO> getSectorAccountPerformanceDataReportList(
            @PathVariable @Parameter(description = "The sector association id") Long sectorAssociationId,
            @RequestBody @Valid @Parameter(description = "The search criteria") SectorAccountPerformanceDataReportSearchCriteria criteria) {
        
        return new ResponseEntity<>(
                accountPerformanceDataStatusQueryService
		                .getSectorAccountPerformanceDataReportList(sectorAssociationId, criteria),
		        HttpStatus.OK);
    }
}
