package uk.gov.cca.api.web.controller.subsistencefees;

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
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaFacilityMarkingStatusService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;

@RestController
@RequestMapping(path = "/v1.0/subsistence-fees/moa-facilities/{moaFacilityId}")
@RequiredArgsConstructor
@Tag(name = "Subsistence fees MoA facility view")
public class SubsistenceFeesMoaFacilityController {

    private final SubsistenceFeesMoaFacilityMarkingStatusService subsistenceFeesMoaFacilityMarkingStatusService;

    @GetMapping(path = "/marking-history")
    @Operation(summary = "Retrieves the history details for the specific facility id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#moaFacilityId")
    public ResponseEntity<SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO> getSubsistenceFeesMoaFacilityMarkingStatusHistoryInfo(
            @Parameter(description = "The MoA facility id") @PathVariable("moaFacilityId") Long moaFacilityId) {
        return new ResponseEntity<>(subsistenceFeesMoaFacilityMarkingStatusService.getMoaFacilityMarkingStatusHistoryInfo(moaFacilityId), HttpStatus.OK);
    }
}
