package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacility;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityReferenceDataService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.util.PerformanceDataFacilityUtil;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.transform.PerformanceDataFacilityCalculationMapper;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.transform.PerformanceDataFacilityContainerMapper;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.transform.PerformanceDataFacilityDigitalFormSubmittedMapper;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.transform.PerformanceDataFacilityDigitalFormSubmitMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormSubmitService {

    private final RequestService requestService;
    private final PerformanceDataFacilityReferenceDataService performanceDataFacilityDigitalFormReferenceDataService;
    private final TargetPeriodService targetPeriodService;
    private final PerformanceDataFacilityStatusService performanceDataFacilityStatusService;
    private static final PerformanceDataFacilityContainerMapper CONTAINER_MAPPER = Mappers
            .getMapper(PerformanceDataFacilityContainerMapper.class);
    private static final PerformanceDataFacilityDigitalFormSubmittedMapper DIGITAL_FORM_SUBMITTED_MAPPER = Mappers
            .getMapper(PerformanceDataFacilityDigitalFormSubmittedMapper.class);
    private static final PerformanceDataFacilityDigitalFormSubmitMapper DIGITAL_FORM_SUBMIT_MAPPER = Mappers
            .getMapper(PerformanceDataFacilityDigitalFormSubmitMapper.class);

    @Transactional
    public void applySave(PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload actionPayload, RequestTask requestTask) {
        PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();

        Optional.ofNullable(taskPayload.getPerformanceData()).ifPresentOrElse(
                performanceData -> {
                    performanceData.setEnergyFuelDetails(actionPayload.getEnergyFuelDetails());
                    performanceData.setThroughputDetails(actionPayload.getThroughputDetails());
                },
                () -> taskPayload.setPerformanceData(PerformanceDataFacilityInputData.builder()
                        .energyFuelDetails(actionPayload.getEnergyFuelDetails())
                        .throughputDetails(actionPayload.getThroughputDetails())
                        .build()));

        taskPayload.setSectionsCompleted(actionPayload.getSectionsCompleted());
    }

    @Transactional
    public void cancel(final AppUser user, final RequestTask requestTask) {
        final Request request = requestTask.getRequest();

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CANCELLED,
                user.getUserId());
    }

    @Transactional
    public void refreshBaselineData(RequestTask requestTask) {
        final Long accountId = requestTask.getRequest().getAccountId();
        final PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();
        final FacilityBaseInfoDTO facility = taskPayload.getFacility();

        final PerformanceDataFacilityBaselineAndTargets newOriginalBaselineTargets = performanceDataFacilityDigitalFormReferenceDataService
                .getFacilityOriginalBaselineAndTargets(accountId, facility.getFacilityBusinessId(), taskPayload.getTargetPeriodYear());

        // Update baseline data
        taskPayload.getReferenceData().setBaselineAndTargets(newOriginalBaselineTargets);
        taskPayload.setSectionsCompleted(new HashMap<>());
        Optional.ofNullable(taskPayload.getPerformanceData())
                .ifPresent(performanceData -> performanceData.setCalculatedResults(null));
    }

    @Transactional
    public void markTaskAsExpired(final AppUser user, final RequestTask requestTask, LocalDateTime submissionDate) {
        PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setExpired(true);
        requestTask.getRequest().setSubmissionDate(submissionDate);

        requestService.addActionToRequest(requestTask.getRequest(),
                null,
                CcaRequestActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_EXPIRED,
                user.getUserId());
    }

    @Transactional
    public void calculate(RequestTask requestTask) {
        PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();

        Optional.ofNullable(taskPayload.getPerformanceData()).ifPresent(performanceData -> {
            final List<TargetPeriodDetailsDTO> targetPeriods = targetPeriodService.getTargetPeriodDetailsByTargetPeriodTypes(
                    Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9));
            final PerformanceDataFacilityCalculationParameters calculationParameters = DIGITAL_FORM_SUBMIT_MAPPER
                    .toPerformanceDataFacilityCalculationParameters(taskPayload, targetPeriods);
            PerformanceDataFacilityCalculatedResults calculatedResults = PerformanceDataFacilityCalculationMapper
                    .toPerformanceDataFacilityCalculatedResults(calculationParameters, performanceData);

            taskPayload.getPerformanceData().setCalculatedResults(calculatedResults);
        });
    }

    @Transactional
    public void submit(final AppUser user, RequestTask requestTask, LocalDateTime submissionDate) {
        Request request = requestTask.getRequest();
        PerformanceDataFacilityDigitalFormRequestPayload requestPayload =
                (PerformanceDataFacilityDigitalFormRequestPayload) request.getPayload();
        PerformanceDataFacilityDigitalFormRequestMetadata requestMetadata =
                (PerformanceDataFacilityDigitalFormRequestMetadata) request.getMetadata();
        final PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();

        // Find submission type
        final TargetPeriodDetailsDTO targetPeriod = targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(taskPayload.getTargetPeriodType());
        PerformanceDataSubmissionType submissionType = PerformanceDataFacilityUtil
                .getSubmissionTypeBySubmissionDate(targetPeriod, requestPayload.getReportType(), submissionDate.toLocalDate())
                .orElse(null);

        // Update Request and Metadata
        requestMetadata.setSubmissionType(submissionType);
        requestMetadata.setSubmittedDate(submissionDate.toLocalDate());
        requestPayload.setSubmissionType(submissionType);
        requestPayload.setReferenceData(taskPayload.getReferenceData());
        requestPayload.setPerformanceData(taskPayload.getPerformanceData());
        request.setSubmissionDate(submissionDate);

        PerformanceDataFacilityContainer container = CONTAINER_MAPPER
                .toPerformanceDataFacilityContainer(requestPayload.getReferenceData().getBaselineAndTargets(), requestPayload.getPerformanceData());

        // Update BO and set report version
        final PerformanceDataFacility performanceDataFacility = DIGITAL_FORM_SUBMITTED_MAPPER.toPerformanceDataFacility(requestPayload, container);
        final int reportVersion = performanceDataFacilityStatusService.submitPerformanceData(performanceDataFacility);

        requestMetadata.setReportVersion(reportVersion);
        requestPayload.setReportVersion(reportVersion);

        // Create timeline
        PerformanceDataFacilitySubmittedRequestActionPayload actionPayload = DIGITAL_FORM_SUBMITTED_MAPPER
                .toPerformanceDataFacilitySubmittedRequestActionPayload(request, container);

        requestService.addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.PERFORMANCE_DATA_FACILITY_SUBMITTED,
                user.getUserId());
    }
}
