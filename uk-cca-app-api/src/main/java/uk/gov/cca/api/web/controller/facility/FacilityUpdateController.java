package uk.gov.cca.api.web.controller.facility;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.facility.domain.dto.UpdateFacilitySchemeExitDateDTO;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;

@Validated
@RestController
@RequestMapping(path = "/v1.0/facilities/{facilityId}")
@RequiredArgsConstructor
@Tag(name = "Update Facility Data")
public class FacilityUpdateController {

    private final FacilityDataUpdateService facilityDataUpdateService;

    @PatchMapping(path = "/scheme-exit-date")
    @Operation(summary = "Updates Scheme Exit Date")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_FACILITY_ERROR_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#facilityId")
    public ResponseEntity<Void> updateFacilitySchemeExitDate(
            @PathVariable("facilityId") @Parameter(description = "The facility id") String facilityId,
            @RequestBody @Valid @Parameter(description = "The updated scheme exit date", required = true) UpdateFacilitySchemeExitDateDTO updateFacilitySchemeExitDateDTO) {

        facilityDataUpdateService.updateFacilitySchemeExitDate(facilityId, updateFacilitySchemeExitDateDTO.getSchemeExitDate());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
