package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UnderlyingAgreementContainerMapper {

    @Mapping(target = "sectorMeasurementType", source = "accountReferenceData.sectorAssociationDetails.measurementType")
    @Mapping(target = "sectorThroughputUnit", source = "accountReferenceData.sectorAssociationDetails.throughputUnit")
    @Mapping(target = "underlyingAgreement", source = "underlyingAgreement.underlyingAgreement")
    UnderlyingAgreementContainer toUnderlyingAgreementContainer(
            UnderlyingAgreementRequestTaskPayload taskPayload);

    @Mapping(target = "sectorMeasurementType", source = "accountReferenceData.sectorAssociationDetails.measurementType")
    @Mapping(target = "sectorThroughputUnit", source = "accountReferenceData.sectorAssociationDetails.throughputUnit")
    @Mapping(target = "underlyingAgreement", source = "underlyingAgreementProposed.underlyingAgreement")
    UnderlyingAgreementContainer toUnderlyingAgreementProposedContainer(
            UnderlyingAgreementReviewRequestTaskPayload taskPayload);

    @Mapping(target = "sectorMeasurementType", source = "accountReferenceData.sectorAssociationDetails.measurementType")
    @Mapping(target = "sectorThroughputUnit", source = "accountReferenceData.sectorAssociationDetails.throughputUnit")
    @Mapping(target = "underlyingAgreement", source = "payload.underlyingAgreementProposed.underlyingAgreement")
    UnderlyingAgreementContainer toUnderlyingAgreementContainer(UnderlyingAgreementRequestPayload payload, AccountReferenceData accountReferenceData);

}
