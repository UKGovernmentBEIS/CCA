package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UnderlyingAgreementVariationContainerMapper {

    @Mapping(target = "sectorMeasurementType", source = "accountReferenceData.sectorAssociationDetails.measurementType")
    @Mapping(target = "sectorThroughputUnit", source = "accountReferenceData.sectorAssociationDetails.throughputUnit")
    @Mapping(target = "underlyingAgreement", source = "underlyingAgreement.underlyingAgreement")
    UnderlyingAgreementContainer toUnderlyingAgreementContainer(
            UnderlyingAgreementVariationRequestTaskPayload taskPayload);
}
