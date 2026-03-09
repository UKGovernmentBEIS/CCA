package uk.gov.cca.api.workflow.request.flow.noncompliance.details.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface NonComplianceDetailsSubmitMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.NON_COMPLIANCE_DETAILS_SUBMITTED_PAYLOAD)")
    NonComplianceDetailsSubmittedRequestActionPayload toNonComplianceDetailsSubmittedRequestActionPayload(NonComplianceDetailsSubmitRequestTaskPayload taskPayload);
}
