package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UnderlyingAgreementContainerMapper {

	@Mapping(target = "schemeDataMap", source = "accountReferenceData.sectorAssociationDetails.schemeDataMap")
    @Mapping(target = "underlyingAgreement", source = "underlyingAgreement.underlyingAgreement")
    UnderlyingAgreementContainer toUnderlyingAgreementContainer(
            UnderlyingAgreementRequestTaskPayload taskPayload);

	@Mapping(target = "schemeDataMap", source = "accountReferenceData.sectorAssociationDetails.schemeDataMap")
    @Mapping(target = "underlyingAgreement", source = "payload.underlyingAgreementProposed.underlyingAgreement")
    UnderlyingAgreementContainer toUnderlyingAgreementContainer(UnderlyingAgreementRequestPayload payload, AccountReferenceData accountReferenceData);

}
