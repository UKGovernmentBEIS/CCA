package uk.gov.cca.api.web.controller.authorization;

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
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaRoleService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.authorization.core.domain.dto.RoleDTO;

import java.util.List;

import static uk.gov.cca.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.cca.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/cca-authorities")
@Tag(name = "Cca Authorities")
@RequiredArgsConstructor
public class CcaRoleController {

    private final CcaRoleService ccaRoleService;

    /**
     * Returns all sector user roles.
     *
     * @return List of {@link RoleDTO}
     */
    @GetMapping(path = "/sector-association/{sectorId}/sector-user-roles")
    @Operation(summary = "Retrieves the sector user roles")
    @ApiResponse(responseCode = "200", description = OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = RoleDTO.class))))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#sectorId")
    public ResponseEntity<List<RoleDTO>> getSectorUserRoles(
            @PathVariable("sectorId") @Parameter(description = "The sector association id") Long sectorId) {
        return new ResponseEntity<>(ccaRoleService.getSectorUserRoles(), HttpStatus.OK);
    }
}
