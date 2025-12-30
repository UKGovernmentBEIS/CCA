package uk.gov.cca.api.web.controller.account;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataAttachmentService;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportDetailsDTO;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.token.FileToken;

@RestController
@RequestMapping(path = "/v1.0/target-unit-accounts/{accountId}/performance-account-template-data-report")
@RequiredArgsConstructor
@Tag(name = "Target Period Performance Account Template Data Report of the Account")
public class TargetUnitAccountPerformanceAccountTemplateDataReportController {

	private final PerformanceAccountTemplateDataQueryService performanceAccountTemplateDataQueryService;
	private final PerformanceAccountTemplateDataAttachmentService performanceAccountTemplateDataAttachmentService;
	
	@GetMapping(path = "/info")
    @Operation(summary = "Retrieves the performance account template report info if exist")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountPerformanceAccountTemplateDataReportInfoDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<AccountPerformanceAccountTemplateDataReportInfoDTO> getAccountPerformanceAccountTemplateDataReportInfo(
            @PathVariable @Parameter(description = "The account id") Long accountId,
            @RequestParam @Parameter(name = "targetPeriodType", description = "The target period business id") @NotNull TargetPeriodType targetPeriodType) {
		return new ResponseEntity<>(
				performanceAccountTemplateDataQueryService
						.findReportInfoByAccountIdAndTargetPeriod(accountId, targetPeriodType)
						.orElse(null),
				HttpStatus.OK);
    }
	
	@GetMapping(path = "/details")
    @Operation(summary = "Retrieves the performance account template report details")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountPerformanceAccountTemplateDataReportDetailsDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
    		content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<AccountPerformanceAccountTemplateDataReportDetailsDTO> getAccountPerformanceAccountTemplateDataReportDetails(
            @PathVariable @Parameter(description = "The account id") Long accountId,
            @RequestParam @Parameter(name = "targetPeriodType", description = "The target period business id") @NotNull TargetPeriodType targetPeriodType) {
		return new ResponseEntity<>(
				performanceAccountTemplateDataQueryService
						.getReportDetailsByAccountIdAndTargetPeriod(accountId, targetPeriodType),
				HttpStatus.OK);
    }
	
	@GetMapping(path = "/attachment-token")
    @Operation(summary = "Retrieves the attachment token for the account and the target period")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<FileToken> generateGetAccountPerformanceAccountTemplateDataReportAttachmentToken(
            @PathVariable @Parameter(description = "The account id") Long accountId,
            @RequestParam @Parameter(name = "targetPeriodType", description = "The target period type") @NotNull TargetPeriodType targetPeriodType,
            @RequestParam @Parameter(name = "fileAttachmentUuid", description = "The attachment uuid") @NotNull UUID fileAttachmentUuid
    ) {
		return new ResponseEntity<>(
				performanceAccountTemplateDataAttachmentService
						.generateGetAttachmentToken(accountId, targetPeriodType, fileAttachmentUuid),
				HttpStatus.OK);
    }
}
