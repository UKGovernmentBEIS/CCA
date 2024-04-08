package uk.gov.cca.api.workflow.request.flow.rfi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RfiSubmitRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RfiSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RfiMapper {
    
    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.RFI_SUBMITTED_PAYLOAD)")
    RfiSubmittedRequestActionPayload toRfiSubmittedRequestActionPayload(RfiSubmitRequestTaskActionPayload taskActionPayload);
}
