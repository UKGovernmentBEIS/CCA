package uk.gov.cca.api.web.controller.facility;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditUpdateDTO;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditViewDTO;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.web.orchestrator.facility.service.FacilityAuditServiceOrchestrator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Validated
@RestController
@RequestMapping(path = "/v1.0/facilities/{facilityId}/audit/")
@RequiredArgsConstructor
@Tag(name = "Facility Audit Controller")
public class FacilityAuditController {

	private final FacilityAuditServiceOrchestrator facilityAuditServiceOrchestrator;

	@GetMapping
	@Operation(summary = "Retrieves the Facility Audit View by Facility Id")
	@ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
			content = {@Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = FacilityAuditViewDTO.class))})
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
			content = {@Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
			content = {@Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
			content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
	@Authorized(resourceId = "#facilityId")
	public ResponseEntity<FacilityAuditViewDTO> getFacilityAuditViewByFacilityId(@PathVariable @NotNull Long facilityId,
	                                                                             @Parameter(hidden = true) AppUser appUser) {

		return ResponseEntity.ok(facilityAuditServiceOrchestrator.getFacilityAuditViewByFacilityId(facilityId, appUser));
	}

	@PutMapping
	@Operation(summary = "Creates or updates the Facility Audit by Facility Id")
	@ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
			content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
			content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
	@Authorized(resourceId = "#facilityId")
	public ResponseEntity<Void> editFacilityAuditDetailsByFacilityId(@PathVariable @NotNull Long facilityId,
	                                                                 @Parameter(hidden = true) AppUser appUser,
	                                                                 @RequestBody @Valid FacilityAuditUpdateDTO facilityAuditUpdateDTO) {

		facilityAuditServiceOrchestrator
				.createOrUpdateFacilityAuditByFacilityId(facilityId, facilityAuditUpdateDTO, appUser.getUserId());
		return ResponseEntity.noContent().build();
	}
}
