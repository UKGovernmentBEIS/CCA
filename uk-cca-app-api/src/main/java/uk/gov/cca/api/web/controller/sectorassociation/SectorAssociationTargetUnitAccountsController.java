package uk.gov.cca.api.web.controller.sectorassociation;

import java.util.List;

import org.springframework.data.domain.Sort.Direction;
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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoResponseDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountSiteContactDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountSiteContactService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria.SortBy;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.security.Authorized;

@RestController
@RequestMapping(path = "/v1.0/sector-association/{sectorId}/target-unit-accounts/")
@RequiredArgsConstructor
@Tag(name = "Sector association target unit accounts info")
public class SectorAssociationTargetUnitAccountsController {

	private final TargetUnitAccountSiteContactService targetUnitAccountSiteContactService;
	
	@GetMapping
    @Operation(summary = "Retrieves the target unit accounts and their contacts for specified sector")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
    	content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TargetUnitAccountInfoResponseDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
    	content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
    	content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorId")
    public ResponseEntity<TargetUnitAccountInfoResponseDTO> getTargetUnitAccountsWithSiteContacts(
    		@Parameter(hidden = true) AppUser appUser, 
    		@PathVariable("sectorId") @Parameter(description = "The sector association id") Long sectorId,
            @RequestParam("page") @Parameter(name = "page", description = "The page number starting from zero")
            @Min(value = 0, message = "{parameter.page.typeMismatch}")
            @NotNull(message = "{parameter.page.typeMismatch}") Integer page,
            @RequestParam("size") @Parameter(name = "size", description = "The page size")
            @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")
            @NotNull(message = "{parameter.pageSize.typeMismatch}") Integer pageSize) {   	
		return new ResponseEntity<>(targetUnitAccountSiteContactService.getTargetUnitAccountsWithSiteContact(appUser, sectorId,
				AccountSearchCriteria.builder()
						.paging(PagingRequest.builder().pageNumber(page).pageSize(pageSize).build())
						.sortBy(SortBy.ACCOUNT_BUSINESS_ID)
						.direction(Direction.ASC)
						.build()),
			HttpStatus.OK);
    }
	
	@PostMapping
    @Operation(summary = "Updates target unit account site contacts for specified sector")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {
        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {
        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorId")
    public ResponseEntity<Void> updateTargetUnitAccountSiteContacts(
        @Parameter(hidden = true) AppUser user,
        @PathVariable("sectorId") @Parameter(description = "The sector association id") Long sectorId,
        @RequestBody @Valid @NotEmpty @Parameter(description = "The target unit account with updated site contacts", required = true)
        List<TargetUnitAccountSiteContactDTO> siteContacts) {
			targetUnitAccountSiteContactService.updateTargetUnitAccountSiteContacts(user, sectorId, siteContacts);
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
