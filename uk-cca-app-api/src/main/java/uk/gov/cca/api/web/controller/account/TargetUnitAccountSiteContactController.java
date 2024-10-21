package uk.gov.cca.api.web.controller.account;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountSiteContactDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountSiteContactService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@RestController
@RequestMapping(path = "/v1.0/account-site-contacts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Target Unit Accounts site contacts")
public class TargetUnitAccountSiteContactController {

    private final TargetUnitAccountSiteContactService targetUnitAccountSiteContactService;

    @PostMapping(path = "/sector-association/{sectorId}")
    @Operation(summary = "Updates target unit account site contacts")
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
