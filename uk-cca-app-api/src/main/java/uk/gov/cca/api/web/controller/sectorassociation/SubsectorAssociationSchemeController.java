package uk.gov.cca.api.web.controller.sectorassociation;


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
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationSchemeService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;

@RestController
@RequestMapping(path = "/v1.0/sector-association/{sectorId}/subsector-scheme")
@RequiredArgsConstructor
@Tag(name = "Subsector association scheme")
public class SubsectorAssociationSchemeController {

    private final SubsectorAssociationSchemeService subsectorAssociationSchemeService;

    @GetMapping(path = "/{subsectorSchemeId}")
    @Operation(summary = "Retrieves the subsector association scheme that corresponds to the provided subsector scheme id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SubsectorAssociationSchemeDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.AUTHORITY_USER_NOT_RELATED_TO_CA,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorId")
    public ResponseEntity<SubsectorAssociationSchemeDTO> getSubsectorAssociationSchemeBySubsectorAssociationSchemeId(
            @PathVariable("sectorId") @Parameter(description = "The sector association id") Long sectorId,
            @PathVariable("subsectorSchemeId") @Parameter(description = "The subsector association scheme id") Long subsectorSchemeId) {
        return new ResponseEntity<>(subsectorAssociationSchemeService.getSubsectorAssociationSchemeBySubsectorAssociationSchemeId(subsectorSchemeId), HttpStatus.OK);
    }
}
