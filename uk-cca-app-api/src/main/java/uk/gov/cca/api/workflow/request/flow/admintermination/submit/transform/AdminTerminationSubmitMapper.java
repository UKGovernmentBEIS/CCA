package uk.gov.cca.api.workflow.request.flow.admintermination.submit.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AdminTerminationSubmitMapper {

    @Mapping(target = "adminTerminationAttachments", source = "adminTerminationSubmitAttachments")
    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.ADMIN_TERMINATION_SUBMIT_PAYLOAD)")
    AdminTerminationSubmitRequestTaskPayload toApplicationSubmitRequestTaskPayload(AdminTerminationRequestPayload requestPayload);
}
