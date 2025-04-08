package uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.TargetUnitMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaGeneratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaRequestPayload;
import uk.gov.netz.api.common.config.MapperConfig;

import java.time.Year;
import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface TargetUnitMoaMapper {

    @Mapping(target = "moaDocument", source = "requestPayload.targetUnitMoaDocument")
    @Mapping(target = "businessId", source = "metadata.businessId")
    @Mapping(target = "paymentRequestId", source = "metadata.parentRequestId")
    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.TARGET_UNIT_MOA_GENERATED_PAYLOAD)")
    TargetUnitMoaGeneratedRequestActionPayload toGeneratedActionPayload(
            TargetUnitMoaRequestPayload requestPayload, TargetUnitMoaRequestMetadata metadata, Year chargingYear, List<DefaultNoticeRecipient> recipients);
}
