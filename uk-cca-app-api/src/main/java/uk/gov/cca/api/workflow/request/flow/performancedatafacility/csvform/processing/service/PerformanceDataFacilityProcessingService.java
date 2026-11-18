package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacility;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityValidator;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityViolation;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.transform.PerformanceDataFacilityCalculationMapper;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.transform.PerformanceDataFacilityContainerMapper;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.validation.PerformanceDataFacilityInputDataValidator;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.utils.PerformanceDataFacilityDataUploadUtility;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingResults;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.transform.PerformanceDataFacilityProcessingInputDataMapper;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.transform.PerformanceDataFacilityProcessingMapper;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.validation.PerformanceDataFacilityProcessingValidator;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityProcessingService {

    private final RequestService requestService;
    private final PerformanceDataFacilityInputDataValidator performanceDataFacilityInputDataValidator;
    private final PerformanceDataFacilityProcessingValidator performanceDataFacilityProcessingValidator;
    private final PerformanceDataFacilityValidator performanceDataFacilityValidator;
    private final PerformanceDataFacilityStatusService performanceDataFacilityStatusService;
    private static final PerformanceDataFacilityContainerMapper CONTAINER_MAPPER = Mappers.getMapper(PerformanceDataFacilityContainerMapper.class);
    private static final PerformanceDataFacilityProcessingMapper MAPPER = Mappers.getMapper(PerformanceDataFacilityProcessingMapper.class);

    @Transactional(rollbackFor = BpmnExecutionException.class, propagation = Propagation.REQUIRES_NEW)
    public PerformanceDataFacilityProcessingResults doProcess(final PerformanceDataFacilityProcessingRequestPayload requestPayload,
                                                              FacilityUploadReport facilityUploadReport) throws BpmnExecutionException {
        try {
            final FacilityDTO facility = requestPayload.getFacility();
            final TargetPeriodYear targetPeriodYear = requestPayload.getTargetPeriodYear();
            final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = requestPayload.getBaselineAndTargets();
            final PerformanceDataFacilityCalculationParameters calculationParameters = MAPPER
                    .toPerformanceDataFacilityCalculationParameters(requestPayload);
            final PerformanceDataFacilityInputData performanceDataInputData = PerformanceDataFacilityProcessingInputDataMapper
                    .toPerformanceDataFacilityInputData(facilityUploadReport.getCsvData(), calculationParameters);

            // Validate facility
            List<BusinessValidationResult> results = performanceDataFacilityInputDataValidator.validateData(performanceDataInputData, calculationParameters);
            results.add(performanceDataFacilityProcessingValidator.validateCsvRules(facilityUploadReport.getCsvData(), calculationParameters));
            results.add(performanceDataFacilityValidator.validateFacilityEligibility(facility, targetPeriodYear));
            results.add(performanceDataFacilityValidator.validateFacilityBaselineDateEligibility(targetPeriodYear, baselineAndTargets));
            results.add(performanceDataFacilityValidator.validateFacilityProductsEligibility(targetPeriodYear, baselineAndTargets));
            results.add(performanceDataFacilityValidator.validateFacilityReportingLock(facility, targetPeriodYear));
            
            boolean isValid = results.stream().allMatch(BusinessValidationResult::isValid);
            if(!isValid) {
                List<String> errors = PerformanceDataFacilityDataUploadUtility.createFacilityUploadReportErrors(results);
                facilityUploadReport.getErrors().addAll(errors);

                return null;
            }

            // Get calculated results
            PerformanceDataFacilityCalculatedResults calculatedResults = PerformanceDataFacilityCalculationMapper
                    .toPerformanceDataFacilityCalculatedResults(calculationParameters, performanceDataInputData);
            performanceDataInputData.setCalculatedResults(calculatedResults);

            // Update facility performance data
            PerformanceDataFacilityContainer container = CONTAINER_MAPPER
                    .toPerformanceDataFacilityContainer(requestPayload.getBaselineAndTargets(), performanceDataInputData);
            PerformanceDataFacility newPerformanceData = MAPPER.toPerformanceDataFacility(requestPayload, container);
            int reportVersion = performanceDataFacilityStatusService.submitPerformanceData(newPerformanceData);

            return PerformanceDataFacilityProcessingResults.builder()
                    .container(container)
                    .reportVersion(reportVersion)
                    .build();

        } catch (Exception e) {
            final String message = e.getMessage() != null
                    ? e.getMessage()
                    : PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_PROCESS_FAILED.getMessage();
            log.error(message, e);
            throw new BpmnExecutionException(message, List.of(message));
        }
    }

    @Transactional
    public void markAsCompleted(final String requestId, FacilityUploadReport facilityUploadReport) {
        final Request request = requestService.findRequestById(requestId);
        final PerformanceDataFacilityProcessingRequestPayload requestPayload =
                (PerformanceDataFacilityProcessingRequestPayload) request.getPayload();

        // Update processing
        final Request parentRequest = requestService.findRequestById(requestPayload.getParentRequestId());
        PerformanceDataFacilityDataProcessingRequestPayload parentRequestPayload =
                (PerformanceDataFacilityDataProcessingRequestPayload) parentRequest.getPayload();
        facilityUploadReport.setSucceeded(facilityUploadReport.getErrors().isEmpty());
        parentRequestPayload.getFacilityReports().put(facilityUploadReport.getFacilityId(), facilityUploadReport);
    }
}
