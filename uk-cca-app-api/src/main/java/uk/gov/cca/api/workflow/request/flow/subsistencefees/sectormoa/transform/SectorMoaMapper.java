package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SectorMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaGeneratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaRequestPayload;
import uk.gov.netz.api.common.config.MapperConfig;

import java.time.Year;
import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface SectorMoaMapper {

    @Mapping(target = "moaDocument", source = "requestPayload.sectorMoaDocument")
    @Mapping(target = "paymentRequestId", source = "metadata.parentRequestId")
    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.SECTOR_MOA_GENERATED_PAYLOAD)")
    SectorMoaGeneratedRequestActionPayload toGeneratedActionPayload(
            SectorMoaRequestPayload requestPayload, SectorMoaRequestMetadata metadata, Year chargingYear, List<DefaultNoticeRecipient> recipients);
}
