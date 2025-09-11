package uk.gov.cca.api.web.controller.account;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataStatusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataUpdateLockDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataReportDetailsDTO;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.cca.api.web.orchestrator.account.service.TargetUnitAccountPerformanceDataReportServiceOrchestrator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.token.FileToken;

@RestController
@RequestMapping(path = "/v1.0/target-unit-accounts/{accountId}/performance-data-report")
@RequiredArgsConstructor
@Tag(name = "Target Period Performance Data Report of the Account")
public class TargetUnitAccountPerformanceDataReportController {

    private final TargetUnitAccountPerformanceDataReportServiceOrchestrator orchestrator;

    @GetMapping(path = "/status")
    @Operation(summary = "Retrieves the account performance data status info")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountPerformanceDataStatusInfoDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<AccountPerformanceDataStatusInfoDTO> getAccountPerformanceDataStatus(
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId,
            @RequestParam @Parameter(name = "targetPeriodType", description = "The target period business id") TargetPeriodType targetPeriodType,
            @Parameter(hidden = true) AppUser currentUser) {

		return new ResponseEntity<>(
				orchestrator.getAccountPerformanceDataStatusInfo(accountId, targetPeriodType, currentUser),
				HttpStatus.OK);
    }

    @GetMapping(path = "/details")
    @Operation(summary = "Retrieves the latest performance data for the account and the target period.")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountPerformanceDataReportDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<AccountPerformanceDataReportDetailsDTO> getAccountPerformanceDataReportDetails(
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId,
            @RequestParam @Parameter(name = "targetPeriodType", description = "The target period business id")
            TargetPeriodType targetPeriodType
    ) {

		return new ResponseEntity<>(orchestrator.getAccountPerformanceDataReportDetails(accountId, targetPeriodType),
				HttpStatus.OK);
    }

    @PutMapping(path = "/lock")
    @Operation(summary = "Locks/unlocks the account for reporting performance data for the specified target period")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountPerformanceDataUpdateLockDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateAccountPerformanceDataLock(
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId,
            @RequestBody @Valid @Parameter(description = "The update lock info") AccountPerformanceDataUpdateLockDTO updateLockDTO
    ) {
		orchestrator.updateAccountPerformanceDataLock(accountId, updateLockDTO);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/attachment")
    @Operation(summary = "Retrieves the latest performance data file for the account and the target period.")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<FileToken> generateGetAccountPerformanceDataReportAttachmentToken(
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId,
            @RequestParam @Parameter(name = "targetPeriodType", description = "The target period business id")
            TargetPeriodType targetPeriodType,
            @RequestParam("fileAttachmentUuid") @Parameter(name = "fileAttachmentUuid", description = "The attachment uuid") @NotNull UUID fileAttachmentUuid
    ) {

		return new ResponseEntity<>(
				orchestrator.generateGetAccountPerformanceDataReportAttachmentToken(accountId, targetPeriodType, fileAttachmentUuid),
				HttpStatus.OK);
    }
}