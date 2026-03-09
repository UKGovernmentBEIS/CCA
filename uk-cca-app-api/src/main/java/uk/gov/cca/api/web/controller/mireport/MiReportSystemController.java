package uk.gov.cca.api.web.controller.mireport;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.mireport.system.outstandingrequesttasks.OutstandingRequestTasksReportService;
import uk.gov.netz.api.mireport.system.MiReportSystemParams;
import uk.gov.netz.api.mireport.system.MiReportSystemResult;
import uk.gov.netz.api.mireport.system.MiReportSystemSearchResult;
import uk.gov.netz.api.mireport.system.MiReportSystemService;
import uk.gov.netz.api.security.AuthorizedRole;

import java.util.List;
import java.util.Set;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@RestController
@RequestMapping(path = "/v1.0/mireports/system")
@RequiredArgsConstructor
@Tag(name = "MiReports system")
@Validated
public class MiReportSystemController {

    private final MiReportSystemService miReportSystemService;
    private final OutstandingRequestTasksReportService outstandingRequestTasksReportService;

    @GetMapping("types")
    @Operation(summary = "Retrieves the mi report types for current user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = MiReportSystemSearchResult.class))))
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<List<MiReportSystemSearchResult>> getCurrentUserMiReports(@Parameter(hidden = true) AppUser appUser) {
        List<MiReportSystemSearchResult> results =
        		miReportSystemService.findByCompetentAuthority(appUser.getCompetentAuthority());
        return ResponseEntity.ok(results);
    }

    @PostMapping
    @Operation(summary = "Generates the system report identified by the provided report type")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MiReportSystemResult.class)))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,

            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<MiReportSystemResult> generateReport(@Parameter(hidden = true) AppUser appUser,
                                                         @RequestBody
                                                         @Parameter(description = "The parameters based on which the report will be generated", required = true)
                                                         @Valid MiReportSystemParams reportParams) {
        MiReportSystemResult reportResult = miReportSystemService.generateReport(appUser.getCompetentAuthority(), reportParams);
        return ResponseEntity.ok(reportResult);
    }

    @GetMapping("/request-task-types")
    @Operation(summary = "Get regulator related request task types")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = String.class))))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<Set<String>> getRegulatorRequestTaskTypes(
            @Parameter(hidden = true) AppUser appUser) {
        Set<String> requestTaskTypes =
                outstandingRequestTasksReportService.getRequestTaskTypesByRoleType(appUser.getRoleType());
        return new ResponseEntity<>(requestTaskTypes, HttpStatus.OK);
    }
}
