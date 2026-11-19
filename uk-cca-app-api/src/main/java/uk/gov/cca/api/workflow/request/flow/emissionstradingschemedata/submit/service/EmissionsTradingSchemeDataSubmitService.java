package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.service.EmissionsTradingSchemeDataService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.transform.EmissionsTradingSchemeDataContainerMapper;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.transform.EmissionsTradingSchemeDataSubmittedMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmissionsTradingSchemeDataSubmitService {

    private final RequestService requestService;
    private final EmissionsTradingSchemeDataService emissionsTradingSchemeDataService;

    private static final EmissionsTradingSchemeDataContainerMapper CONTAINER_MAPPER = Mappers
            .getMapper(EmissionsTradingSchemeDataContainerMapper.class);
    private static final EmissionsTradingSchemeDataSubmittedMapper DATA_SUBMITTED_MAPPER =
            Mappers.getMapper(EmissionsTradingSchemeDataSubmittedMapper.class);

    @Transactional
    public void applySave(EmissionsTradingSchemeDataSaveRequestTaskActionPayload actionPayload, RequestTask requestTask) {
        EmissionsTradingSchemeDataSubmitRequestTaskPayload requestTaskPayload =
                (EmissionsTradingSchemeDataSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setEmissionsTradingSchemeDataInputDetails(actionPayload.getEmissionsTradingSchemeDataInputDetails());
        requestTaskPayload.setSectionsCompleted(actionPayload.getSectionsCompleted());
    }

    @Transactional
    public void cancel(final AppUser user, final RequestTask requestTask) {
        final Request request = requestTask.getRequest();

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.EMISSIONS_TRADING_SCHEME_DATA_CANCELLED,
                user.getUserId());
    }

    @Transactional
    public void submit(final AppUser user, RequestTask requestTask, LocalDateTime submissionDate) {
        Request request = requestTask.getRequest();
        EmissionsTradingSchemeDataRequestPayload requestPayload =
                (EmissionsTradingSchemeDataRequestPayload) request.getPayload();
        EmissionsTradingSchemeDataRequestMetadata requestMetadata =
                (EmissionsTradingSchemeDataRequestMetadata) request.getMetadata();
        final EmissionsTradingSchemeDataSubmitRequestTaskPayload taskPayload =
                (EmissionsTradingSchemeDataSubmitRequestTaskPayload) requestTask.getPayload();

        // Update Request and Metadata
        requestMetadata.setSubmittedDate(submissionDate.toLocalDate());
        requestPayload.setMeasurementType(taskPayload.getMeasurementType());
        requestPayload.setTargetYear(taskPayload.getTargetYear());
        requestPayload.setUketsId(taskPayload.getUketsId());
        requestPayload.setEtsDataInputDetails(taskPayload.getEmissionsTradingSchemeDataInputDetails());
        request.setSubmissionDate(submissionDate);

        EmissionsTradingSchemeDataContainer container = CONTAINER_MAPPER
                .toEmissionsTradingSchemeDataContainer(requestPayload.getEtsDataInputDetails(), requestPayload.getMeasurementType(), requestPayload.getUketsId());

        // Update BO and set report version
        final int reportVersion =
                emissionsTradingSchemeDataService.submitEmissionsTradingSchemeData(container, requestPayload.getFacilityId(), requestPayload.getTargetYear());
        requestMetadata.setReportVersion(reportVersion);

        // Create timeline
        EmissionsTradingSchemeDataSubmittedRequestActionPayload actionPayload =
                DATA_SUBMITTED_MAPPER.toEmissionsTradingSchemeDataSubmittedRequestActionPayload(request, container);

        requestService.addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.EMISSIONS_TRADING_SCHEME_DATA_SUBMITTED,
                user.getUserId());
    }
}
