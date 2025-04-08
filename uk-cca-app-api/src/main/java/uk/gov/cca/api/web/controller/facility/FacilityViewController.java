package uk.gov.cca.api.web.controller.facility;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchCriteria;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchResults;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.facility.service.FacilitySearchService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.security.Authorized;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.OK;

@Validated
@RestController
@RequestMapping(path = "/v1.0/facilities/")
@RequiredArgsConstructor
@Tag(name = "Facility info view")
public class FacilityViewController {

    private final FacilitySearchService facilitySearchService;
    private final FacilityDataQueryService facilityDataQueryService;

    @GetMapping(path = "/account/{accountId}")
    @Operation(summary = "Retrieves the current account facilities")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FacilitySearchResults.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<FacilitySearchResults> searchFacilities(
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId,
            @RequestParam(value = "term", required = false) @Size(min = 3, max = 256) @Parameter(name = "term", description = "The term to search") String term,
            @RequestParam(value = "page") @NotNull @Parameter(name = "page", description = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") Long page,
            @RequestParam(value = "size") @NotNull @Parameter(name = "size", description = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}") Long pageSize
    ) {
        return new ResponseEntity<>(
                facilitySearchService.searchFacilities(
                        accountId,
                        FacilitySearchCriteria.builder()
                                .term(term)
                                .paging(PagingRequest.builder().pageNumber(page).pageSize(pageSize).build())
                                .build()), HttpStatus.OK);
    }

    @GetMapping(path = "/{facilityId}")
    @Operation(summary = "Retrieves the facility details")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FacilityDataDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#facilityId")
    public ResponseEntity<FacilityDataDetailsDTO> getFacilityDetailsById(
            @PathVariable("facilityId") @Parameter(description = "The facility id") String facilityId) {
        return new ResponseEntity<>(facilityDataQueryService.getFacilityData(facilityId), HttpStatus.OK);
    }
}
