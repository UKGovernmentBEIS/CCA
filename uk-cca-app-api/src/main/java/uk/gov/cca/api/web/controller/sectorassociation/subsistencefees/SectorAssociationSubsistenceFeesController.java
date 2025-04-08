package uk.gov.cca.api.web.controller.sectorassociation.subsistencefees;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResults;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.web.orchestrator.sectorassociation.service.SectorAssociationSubsistenceFeesServiceOrchestrator;
import uk.gov.netz.api.security.Authorized;

@RestController
@RequestMapping(path = "/v1.0/sector-association/{sectorAssociationId}/subsistence-fees")
@RequiredArgsConstructor
@Tag(name = "Sector association subsistence fees")
public class SectorAssociationSubsistenceFeesController {
	
	private final SectorAssociationSubsistenceFeesServiceOrchestrator orchestrator;
	
	@PostMapping(path = "/moas")
    @Operation(summary = "Retrieves the subsistence fees MoAs for the specified sector")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SubsistenceFeesMoaSearchResults.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized(resourceId = "#sectorAssociationId")
    public ResponseEntity<SubsistenceFeesMoaSearchResults> getSectorSubsistenceFeesMoas(
    		@Parameter(description = "The sectorAssociationId") @PathVariable("sectorAssociationId") Long sectorAssociationId,
    		@RequestBody @Valid @Parameter(description = "The search criteria", required = true) SubsistenceFeesMoaSearchCriteria criteria) {
        return new ResponseEntity<>(orchestrator.getSectorSubsistenceFeesMoas(sectorAssociationId, criteria), HttpStatus.OK);
    }
}
