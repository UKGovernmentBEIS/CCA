package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.handler;


import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditDTO;
import uk.gov.cca.api.facilityaudit.service.FacilityAuditService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.transform.PreAuditReviewSubmitMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Map;
import java.util.Set;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@AllArgsConstructor
public class PreAuditReviewSubmitInitializer implements InitializeRequestTaskHandler {

    private final FacilityAuditService facilityAuditService;

    private static final PreAuditReviewSubmitMapper PRE_AUDIT_REVIEW_SUBMIT_MAPPER = Mappers.getMapper(PreAuditReviewSubmitMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final Long facilityId = request.getRequestResources().stream()
                .filter(requestResource -> requestResource.getResourceType().equals(CcaResourceType.FACILITY))
                .map(RequestResource::getResourceId)
                .map(Long::parseLong)
                .findFirst()
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        final FacilityAuditDTO facilityAuditDTO = facilityAuditService.getFacilityAuditByFacilityId(facilityId);

        return PreAuditReviewSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.PRE_AUDIT_REVIEW_SUBMIT_PAYLOAD)
                .preAuditReviewDetails(PRE_AUDIT_REVIEW_SUBMIT_MAPPER.toPreAuditReviewDetails(facilityAuditDTO))
                .sectionsCompleted(Map.of())
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.PRE_AUDIT_REVIEW_SUBMIT);
    }
}
