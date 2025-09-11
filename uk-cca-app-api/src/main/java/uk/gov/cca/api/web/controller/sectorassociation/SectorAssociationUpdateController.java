package uk.gov.cca.api.web.controller.sectorassociation;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.UPDATE_SECTOR_ASSOCIATION_SECTOR_CONTACT_BAD_REQUEST;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.UPDATE_SECTOR_ASSOCIATION_SECTOR_DETAILS_BAD_REQUEST;

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
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsUpdateDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationUpdateService;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;

@RestController
@RequestMapping(path = "/v1.0/sector-association/{id}")
@RequiredArgsConstructor
@Validated
@Tag(name = "Sector Association Details update")
public class SectorAssociationUpdateController {

    private final SectorAssociationUpdateService sectorAssociationUpdateService;

    @PostMapping("/details")
    @Operation(summary = "Update the sector association sector details")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = UPDATE_SECTOR_ASSOCIATION_SECTOR_DETAILS_BAD_REQUEST, content = {
        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {
        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {
        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {
        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorAssociationId")
    public ResponseEntity<Void> updateSectorAssociationDetails(
        @PathVariable("id") @Parameter(description = "The sector association id", required = true) Long sectorAssociationId,
        @RequestBody @Valid @Parameter(description = "The sector association details fields", required = true)
        SectorAssociationDetailsUpdateDTO sectorAssociationDetailsUpdateDTO) {
        sectorAssociationUpdateService
            .updateSectorAssociationDetails(sectorAssociationId, sectorAssociationDetailsUpdateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/contact")
    @Operation(summary = "Update the sector association sector contact")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = UPDATE_SECTOR_ASSOCIATION_SECTOR_CONTACT_BAD_REQUEST, content = {
        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {
        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {
        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {
        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorAssociationId")
    public ResponseEntity<Void> updateSectorAssociationContact(
        @PathVariable("id") @Parameter(description = "The sector association id", required = true) Long sectorAssociationId,
        @RequestBody @Valid @Parameter(description = "The sector association contact fields", required = true)
        SectorAssociationContactDTO sectorAssociationContactDTO) {
        sectorAssociationUpdateService.updateSectorAssociationContact(sectorAssociationId, sectorAssociationContactDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
