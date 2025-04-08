package uk.gov.cca.api.web.controller.subsistencefees;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunSearchResults;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaQueryService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunQueryService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;

@RestController
@RequestMapping(path = "/v1.0/subsistence-fees-runs/")
@RequiredArgsConstructor
@Tag(name = "Subsistence fees run info view")
public class SubsistenceFeesRunViewController {

	private final SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;
	private final SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;
	
	@GetMapping
    @Operation(summary = "Retrieves the subsistence fees runs")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SubsistenceFeesRunSearchResults.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<SubsistenceFeesRunSearchResults> getSubsistenceFeesRuns(@Parameter(hidden = true) AppUser appUser,
    		@RequestParam(value = "page") @NotNull @Parameter(name = "page", description = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") Long page,
            @RequestParam(value = "size") @NotNull @Parameter(name = "size", description = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}") Long pageSize) {
        return new ResponseEntity<>(subsistenceFeesRunQueryService.getSubsistenceFeesRuns(appUser,
        		PagingRequest.builder().pageNumber(page).pageSize(pageSize).build()), HttpStatus.OK);
    }
	
	@GetMapping(path = "/{runId}")
    @Operation(summary = "Retrieves the details for the specific run id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SubsistenceFeesRunDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#runId")
    public ResponseEntity<SubsistenceFeesRunDetailsDTO> getSubsistenceFeesRunDetailsById(
            @Parameter(description = "The run id") @PathVariable("runId") Long runId) {
        return new ResponseEntity<>(subsistenceFeesRunQueryService.getSubsistenceFeesRunDetailsById(runId), HttpStatus.OK);
    }
	
	@PostMapping(path = "{runId}/moas")
    @Operation(summary = "Retrieves the subsistence fees run MoAs")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SubsistenceFeesMoaSearchResults.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized(resourceId = "#runId")
    public ResponseEntity<SubsistenceFeesMoaSearchResults> getSubsistenceFeesRunMoas(
    		@Parameter(description = "The runId") @PathVariable("runId") Long runId,
    		@RequestBody @Valid @Parameter(description = "The search criteria", required = true) SubsistenceFeesMoaSearchCriteria criteria) {
        return new ResponseEntity<>(subsistenceFeesMoaQueryService.getSubsistenceFeesRunMoas(runId, criteria), HttpStatus.OK);
    }
}
