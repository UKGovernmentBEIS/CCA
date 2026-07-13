package uk.gov.cca.api.web.controller.sectorassociation;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentsUpdateDTO;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationSchemeUpdateService;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.UPDATE_SUBSECTOR_ASSOCIATION_SCHEME_COMMITMENTS_BAD_REQUEST;

@RestController
@RequestMapping(path = "/v1.0/subsector-schemes/{id}")
@RequiredArgsConstructor
@Validated
@Tag(name = "Subsector association scheme details update")
public class SubsectorAssociationSchemeUpdateController {

    private final SubsectorAssociationSchemeUpdateService subsectorAssociationSchemeUpdateService;

    @PostMapping("/target-commitments")
    @Operation(summary = "Update the subsector association scheme commitments")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = UPDATE_SUBSECTOR_ASSOCIATION_SCHEME_COMMITMENTS_BAD_REQUEST, content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#subsectorAssociationSchemeId")
    public ResponseEntity<Void> updateSubsectorAssociationSchemeTargetCommitments(
            @PathVariable("id") @Parameter(description = "The subsector association scheme id", required = true) Long subsectorAssociationSchemeId,
            @RequestBody @Valid @Parameter(description = "The subsector association scheme commitments fields", required = true)
            TargetCommitmentsUpdateDTO targetCommitmentsUpdateDTO) {
        subsectorAssociationSchemeUpdateService
                .updateSubsectorAssociationSchemeTargetCommitments(subsectorAssociationSchemeId, targetCommitmentsUpdateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
