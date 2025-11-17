package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {CcaRequestActionPayloadType.class})
public interface Cca2ExtensionNoticeAccountProcessingMapper {

    @Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)")
    Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload toCca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload(
            Cca2ExtensionNoticeAccountProcessingRequestPayload payload);
}
