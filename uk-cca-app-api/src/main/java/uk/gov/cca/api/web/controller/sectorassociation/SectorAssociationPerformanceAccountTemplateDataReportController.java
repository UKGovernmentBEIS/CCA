package uk.gov.cca.api.web.controller.sectorassociation;

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
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportSearchCriteria;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.web.orchestrator.sectorassociation.service.SectorAssociationPerformanceAccountTemplateDataReportServiceOrchestrator;
import uk.gov.netz.api.security.Authorized;

@Validated
@RestController
@RequestMapping(path = "/v1.0/sector-association/{sectorAssociationId}/performance-account-template-data-report")
@RequiredArgsConstructor
@Tag(name = "Sector-Level Performance Account Template Data View Pages")
public class SectorAssociationPerformanceAccountTemplateDataReportController {

	private final SectorAssociationPerformanceAccountTemplateDataReportServiceOrchestrator orchestrator;

	@PostMapping
	@Operation(summary = "Populates the performance account template data of sector accounts")
	@ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SectorPerformanceAccountTemplateDataReportListDTO.class)) })
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)) })
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)) })
	@Authorized(resourceId = "#sectorAssociationId")
	public ResponseEntity<SectorPerformanceAccountTemplateDataReportListDTO> getSectorPerformanceAccountTemplateDataReportList(
			@PathVariable @Parameter(description = "The sector association id") Long sectorAssociationId,
			@RequestBody @Valid @Parameter(description = "The search criteria") SectorPerformanceAccountTemplateDataReportSearchCriteria criteria) {
		return new ResponseEntity<>(
				orchestrator.getSectorPerformanceAccountTemplateDataReportListDTO(sectorAssociationId, criteria),
				HttpStatus.OK);
	}

}
