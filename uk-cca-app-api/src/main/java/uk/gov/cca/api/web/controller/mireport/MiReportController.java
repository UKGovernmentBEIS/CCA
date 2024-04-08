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
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.security.AuthorizedRole;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.cca.api.mireport.common.MiReportService;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportParams;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportResult;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportSearchResult;
import uk.gov.cca.api.mireport.common.outstandingrequesttasks.OutstandingRequestTasksReportService;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;
import java.util.Set;

import static uk.gov.netz.api.common.domain.RoleType.REGULATOR;

@RestController
@RequestMapping(path = "/v1.0/mireports")
@RequiredArgsConstructor
@Tag(name = "MiReports")
@Validated
public class MiReportController {

    private final MiReportService miReportService;
    private final OutstandingRequestTasksReportService outstandingRequestTasksReportService;

    @GetMapping("types")
    @Operation(summary = "Retrieves the mi report types for current user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = MiReportSearchResult.class))))
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})

    public ResponseEntity<List<MiReportSearchResult>> getCurrentUserMiReports(@Parameter(hidden = true) AppUser appUser) {
        List<MiReportSearchResult> results =
            miReportService.findByCompetentAuthority(appUser.getCompetentAuthority());
        return ResponseEntity.ok(results);
    }

    @PostMapping
    @Operation(summary = "Generates the report identified by the provided report type")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MiReportResult.class)))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,

            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<MiReportResult> generateReport(@Parameter(hidden = true) AppUser appUser,
                                                         @RequestBody
                                                         @Parameter(description = "The parameters based on which the report will be generated", required = true)
                                                         @Valid
                                                         @SpELExpression(expression = "{(#reportType ne 'CUSTOM')}", message = "mireport.type.notSupported")
                                                                 MiReportParams reportParams) {
        MiReportResult reportResult = miReportService.generateReport(appUser.getCompetentAuthority(), reportParams);
        return ResponseEntity.ok(reportResult);
    }

    @PostMapping("/custom")
    @Operation(summary = "Generates custom report")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MiReportResult.class)))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<MiReportResult> generateCustomReport(@Parameter(hidden = true) AppUser appUser,
                                                               @RequestBody
                                                               @Parameter(description = "The parameters based on which the report will be generated", required = true)
                                                               @Valid
                                                               @SpELExpression(expression = "{(#reportType eq 'CUSTOM')}", message = "mireport.type.notSupported")
                                                                       MiReportParams reportParams) {
        MiReportResult reportResult = miReportService.generateReport(appUser.getCompetentAuthority(), reportParams);
        return ResponseEntity.ok(reportResult);
    }

    @GetMapping("/request-task-types")
    @Operation(summary = "Get regulator related request task types")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = RequestTaskType.class))))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {RoleType.REGULATOR})
    public ResponseEntity<Set<RequestTaskType>> getRegulatorRequestTaskTypes(
            @Parameter(hidden = true) AppUser appUser) {
        Set<RequestTaskType> requestTaskTypes =
                outstandingRequestTasksReportService.getRequestTaskTypesByRoleType(appUser.getRoleType());
        return new ResponseEntity<>(requestTaskTypes, HttpStatus.OK);
    }
}
