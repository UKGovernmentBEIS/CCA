package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateProcessingService {
    
    private final RequestService requestService;
    private final PerformanceAccountTemplateProcessingExtractAndValidateService extractAndValidateService;
    private final PerformanceAccountTemplateProcessingRequestActionService requestActionService;
    private final PerformanceAccountTemplateDataService dataService;
    
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void doProcess(String requestId, AccountUploadReport accountReport) throws PerformanceAccountTemplateProcessingException, IOException {
        final Request request = requestService.findRequestById(requestId);
        final PerformanceAccountTemplateProcessingRequestMetadata metadata =
                (PerformanceAccountTemplateProcessingRequestMetadata) request.getMetadata();
        
        List<PerformanceAccountTemplateViolation> errors = new ArrayList<>();
        Optional<PerformanceAccountTemplateDataContainer> container = extractAndValidateService.extractAndValidateData(accountReport, errors);
        
        if (container.isPresent()) {
            postProcessingActions(container.get(), metadata, request);
        } else {
            accountReport.getErrorFilenames().add(accountReport.getFile().getName());
            throw new PerformanceAccountTemplateProcessingException(PerformanceAccountTemplateViolation
                    .PerformanceAccountTemplateViolationMessage
                    .PROCESS_EXCEL_FAILED.getMessage(), errors.stream()
                    .map(PerformanceAccountTemplateViolation::toString)
                    .toList());
        }
    }
    
    private void postProcessingActions(PerformanceAccountTemplateDataContainer container, PerformanceAccountTemplateProcessingRequestMetadata metadata, Request request) {
        dataService.submitPerformanceAccountTemplate(container, metadata.getAccountId(), metadata.getTargetPeriodType(), metadata.getTargetPeriodYear(), metadata.getReportVersion());
        requestActionService.addSubmittedAction(request, metadata, container);
        request.setSubmissionDate(LocalDateTime.now());
    }
}
