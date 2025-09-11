package uk.gov.cca.api.web.controller.facility;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.web.orchestrator.facility.service.FacilityIdGeneratorServiceOrchestrator;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.Set;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@RestController
@RequestMapping(path = "/v1.0/facility")
@RequiredArgsConstructor
@Tag(name = "Facility")
public class FacilityController {

    private final FacilityIdGeneratorServiceOrchestrator facilityIdGeneratorServiceOrchestrator;
    private final FacilityDataQueryService facilityDataQueryService;

    @GetMapping(path = "/generate/{accountId}")
    @Operation(summary = "Get the next facility id per sector association id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FacilityDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<FacilityDTO> generateFacilityId(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId) {
        return new ResponseEntity<>(facilityIdGeneratorServiceOrchestrator.generateFacilityId(accountId), HttpStatus.OK);
    }

    @GetMapping("/facilityId")
    @Operation(summary = "Checks if facility ID exists and returns scheme versions")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = SchemeVersion.class))))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR, SECTOR_USER})
    public ResponseEntity<Set<SchemeVersion>> getActiveFacilityParticipatingSchemeVersions(
            @RequestParam("facilityId") @Parameter(name = "facilityId", description = "The facility ID to check") String facilityId) {
        Set<SchemeVersion> schemeVersions = facilityDataQueryService.getActiveFacilityParticipatingSchemeVersions(facilityId);
        return new ResponseEntity<>(schemeVersions, HttpStatus.OK);
    }
}
