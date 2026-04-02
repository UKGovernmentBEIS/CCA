package uk.gov.cca.api.workflow.request.flow.noncompliance.common.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceClosedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface NonComplianceClosedMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.NON_COMPLIANCE_CLOSED_PAYLOAD)")
    NonComplianceClosedRequestActionPayload toNonComplianceClosedRequestActionPayload(NonComplianceRequestPayload requestPayload);
}
