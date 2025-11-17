package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PreAuditReviewSubmitMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMITTED_PAYLOAD)")
    PreAuditReviewSubmittedRequestActionPayload toPreAuditReviewSubmittedRequestActionPayload(PreAuditReviewSubmitRequestTaskPayload taskPayload);
}
