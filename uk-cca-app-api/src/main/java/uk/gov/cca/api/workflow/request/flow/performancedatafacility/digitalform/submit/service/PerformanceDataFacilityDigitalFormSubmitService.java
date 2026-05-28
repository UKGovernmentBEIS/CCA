package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacility;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.util.PerformanceDataFacilityUtil;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.transform.PerformanceDataFacilitySubmittedMapper;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.service.PerformanceDataFacilityReferenceDataService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmittedRequestActionPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.HashMap;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormSubmitService {

    private final RequestService requestService;
    private final PerformanceDataFacilityReferenceDataService performanceDataFacilityDigitalFormReferenceDataService;
    private final TargetPeriodService targetPeriodService;
    private final PerformanceDataFacilityStatusService performanceDataFacilityStatusService;
    private static final PerformanceDataFacilitySubmittedMapper MAPPER = Mappers.getMapper(PerformanceDataFacilitySubmittedMapper.class);

    @Transactional
    public void applySave(PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload actionPayload, RequestTask requestTask) {
        PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setPerformanceData(actionPayload.getPerformanceData());
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
    }

    @Transactional
    public void markTaskAsExpired(RequestTask requestTask, LocalDateTime submissionDate) {
        PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setExpired(true);
        requestTask.getRequest().setSubmissionDate(submissionDate);
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

        // Update BO and set report version
        final PerformanceDataFacility performanceDataFacility = MAPPER.toPerformanceDataFacility(requestPayload);
        final int reportVersion = performanceDataFacilityStatusService.submitPerformanceData(performanceDataFacility);

        requestMetadata.setReportVersion(reportVersion);
        requestPayload.setReportVersion(reportVersion);

        // Create timeline
        PerformanceDataFacilitySubmittedRequestActionPayload actionPayload = MAPPER
                .toPerformanceDataFacilitySubmittedRequestActionPayload(request);

        requestService.addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.PERFORMANCE_DATA_FACILITY_SUBMITTED,
                user.getUserId());
    }
}
