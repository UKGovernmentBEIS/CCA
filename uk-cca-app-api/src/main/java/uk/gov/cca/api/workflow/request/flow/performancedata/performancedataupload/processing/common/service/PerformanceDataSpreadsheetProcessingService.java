package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusService;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceData;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.transform.PerformanceDataMapper;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.transform.PerformanceDataSpreadsheetProcessingMapper;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.validation.PerformanceDataSpreadsheetTemplateValidator;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceDataSpreadsheetProcessingService {

    private final RequestService requestService;
    private final FileAttachmentService fileAttachmentService;
    private final List<PerformanceDataSpreadsheetProcessingExtractDataService> performanceDataSpreadsheetProcessingExtractDataServices;
    private final List<PerformanceDataSpreadsheetTemplateValidator> validators;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final AccountPerformanceDataStatusService accountPerformanceDataStatusService;
    private final AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

    private static final PerformanceDataSpreadsheetProcessingMapper PERFORMANCE_DATA_SPREADSHEET_PROCESSING_MAPPER = Mappers.getMapper(PerformanceDataSpreadsheetProcessingMapper.class);
    private static final PerformanceDataMapper PERFORMANCE_DATA_CONTAINER_MAPPER = Mappers.getMapper(PerformanceDataMapper.class);

    @Transactional(rollbackFor = BpmnExecutionException.class, propagation = Propagation.REQUIRES_NEW)
    public void doProcess(String requestId, TargetUnitAccountUploadReport accountReport) throws BpmnExecutionException {
        try {
            Request request = requestService.findRequestById(requestId);

            // Extract and validate data
            List<BusinessValidationResult> validationResults = processValidations(request, accountReport);

            // Save validation errors
            List<String> errors = validationResults.stream()
                    .map(BusinessValidationResult::getViolations)
                    .flatMap(List::stream)
                    .flatMap(violation -> Arrays.stream(violation.getData()))
                    .map(Object::toString)
                    .toList();
            accountReport.getErrors().addAll(errors);
            
            // Perform post actions after validation success
            if(CollectionUtils.isEmpty(accountReport.getErrors())) {
            	performPostActionsIfExcelValid(request);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BpmnExecutionException(e.getMessage(), List.of(
                    PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.PROCESS_EXCEL_FAILED.getMessage()));
        }
    }

    @Transactional
    public void cleanupFailed(final TargetUnitAccountUploadReport accountReport) {
        fileAttachmentService.deleteFileAttachmentsInBatches(Set.of(accountReport.getFile().getUuid()));
    }

    @SuppressWarnings("unchecked")
    private List<BusinessValidationResult> processValidations(final Request request, TargetUnitAccountUploadReport accountReport) throws Exception {
        PerformanceDataSpreadsheetProcessingRequestPayload requestPayload =
                (PerformanceDataSpreadsheetProcessingRequestPayload) request.getPayload();
        PerformanceDataSpreadsheetProcessingRequestMetadata metadata =
                (PerformanceDataSpreadsheetProcessingRequestMetadata) request.getMetadata();

        final Long accountId = requestPayload.getAccountId();
        final FileDTO accountReportFile = fileAttachmentService
                .getFileDTO(requestPayload.getAccountReportFile().getUuid());
        final TargetPeriodDocumentTemplate templateType = TargetPeriodDocumentTemplate
                .getTargetPeriodDocumentTemplate(metadata.getPerformanceDataTargetPeriodType());

        // Find proper services based on Excel template
        PerformanceDataSpreadsheetProcessingExtractDataService<PerformanceData> extractDataService = performanceDataSpreadsheetProcessingExtractDataServices.stream()
                .filter(service-> service.getDocumentTemplateType().equals(templateType))
                .findFirst().orElseThrow();
        PerformanceDataSpreadsheetTemplateValidator<PerformanceData> validator = validators.stream()
                .filter(service-> service.getDocumentTemplateType().equals(templateType))
                .findFirst().orElseThrow();

        // Extract Excel data
        PerformanceData performanceData = extractDataService.extractData(metadata, accountReportFile);

        // Update payload with performance data
        requestPayload.setPerformanceData(performanceData);

        // Validate data
        List<BusinessValidationResult> validationResults = new ArrayList<>();
        BusinessValidationResult dataValidationResult = validator.validateData(performanceData);
        validationResults.add(dataValidationResult);

        if(dataValidationResult.isValid()) {
            // Calculate functions
            PerformanceDataCalculatedMetrics performanceDataCalculatedMetrics = extractDataService.extractCalculatedData(performanceData);

            // Store calculated results
            requestPayload.setPerformanceDataCalculatedMetrics(performanceDataCalculatedMetrics);
            accountReport.setPerformanceDataCalculatedMetrics(performanceDataCalculatedMetrics);

            // Get reference data
            TargetUnitAccountDetailsDTO accountDetails = accountReferenceDetailsService
                    .getTargetUnitAccountDetails(accountId);
            UnderlyingAgreementDTO underlyingAgreement = underlyingAgreementQueryService
                    .getUnderlyingAgreementByAccountId(accountId);
			PerformanceDataContainer lastUploadedReport = accountPerformanceDataStatusQueryService
					.getLastUploadedPerformanceData(accountId,metadata.getPerformanceDataTargetPeriodType().getReferenceTargetPeriod()).orElse(null);
            PerformanceDataReferenceDetails referenceDetails = PERFORMANCE_DATA_SPREADSHEET_PROCESSING_MAPPER
                    .toPerformanceDataReferenceDetails(accountDetails, underlyingAgreement, performanceDataCalculatedMetrics, metadata, accountReportFile, lastUploadedReport);

            // Validate functions
            validationResults.addAll(validator.validateBusinessData(referenceDetails, performanceData));
        }

        return validationResults;
    }

	private void performPostActionsIfExcelValid(final Request request) {
		final PerformanceDataSpreadsheetProcessingRequestPayload requestPayload = (PerformanceDataSpreadsheetProcessingRequestPayload) request
				.getPayload();
		final PerformanceDataSpreadsheetProcessingRequestMetadata metadata = (PerformanceDataSpreadsheetProcessingRequestMetadata) request
				.getMetadata();
		final Long accountId = requestPayload.getAccountId();
		final TargetPeriodType targetPeriod = metadata.getTargetPeriodDetails().getBusinessId();

		// Update performance data & Lock account
		PerformanceDataContainer container = PERFORMANCE_DATA_CONTAINER_MAPPER.toPerformanceDataContainer(
				requestPayload.getPerformanceData(), requestPayload.getAccountReportFile(), targetPeriod);

		accountPerformanceDataStatusService.submitAccountPerformanceData(container, accountId, targetPeriod,
				metadata.getReportVersion(), metadata.getSubmissionType());

		// Create timeline
		final PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload actionPayload = PERFORMANCE_DATA_SPREADSHEET_PROCESSING_MAPPER
				.toPerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload(metadata, requestPayload);
		requestService.addActionToRequest(request, actionPayload,
				CcaRequestActionType.PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED,
				requestPayload.getSectorUserAssignee());

		// Set submission date for request
		LocalDateTime submissionDate = LocalDateTime.now();
		request.setSubmissionDate(submissionDate);
	}
}
