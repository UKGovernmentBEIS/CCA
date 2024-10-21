package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UnderlyingAgreementContainerMapper {

    @Mapping(target = "sectorMeasurementType", source = "accountReferenceData.sectorAssociationDetails.measurementType")
    @Mapping(target = "sectorThroughputUnit", source = "accountReferenceData.sectorAssociationDetails.throughputUnit")
    @Mapping(target = "underlyingAgreement", source = "underlyingAgreement.underlyingAgreement")
    UnderlyingAgreementContainer toUnderlyingAgreementContainer(
            UnderlyingAgreementRequestTaskPayload taskPayload);

    @Mapping(target = "sectorMeasurementType", source = "accountReferenceData.sectorAssociationDetails.measurementType")
    @Mapping(target = "sectorThroughputUnit", source = "accountReferenceData.sectorAssociationDetails.throughputUnit")
    @Mapping(target = "underlyingAgreement", source = "payload.underlyingAgreement.underlyingAgreement")
    UnderlyingAgreementContainer toUnderlyingAgreementContainer(UnderlyingAgreementRequestPayload payload, AccountReferenceData accountReferenceData);

    @AfterMapping
    default void removeUnderlyingAgreementRejectedFacilities(@MappingTarget UnderlyingAgreementContainer container,
    		UnderlyingAgreementRequestPayload payload) {
    	Set<String> rejectedFacilityIds = payload.getFacilitiesReviewGroupDecisions().entrySet().stream()
				.filter(entry -> CcaReviewDecisionType.REJECTED.equals(entry.getValue().getType()))
            	.map(Map.Entry::getKey)
            	.collect(Collectors.toSet());
    	container.getUnderlyingAgreement().setFacilities(
    			payload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities().stream()
    			.filter(facility -> !rejectedFacilityIds.contains(facility.getFacilityItem().getFacilityId()))
    			.collect(Collectors.toSet()));
    }
}
