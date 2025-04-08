package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.control.DeepClone;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.common.config.MapperConfig;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class, mappingControl = DeepClone.class)
public interface UnderlyingAgreementVariationContainerDeepCloneMapper {

    @Mapping(target = "sectorMeasurementType", source = "accountReferenceData.sectorAssociationDetails.measurementType")
    @Mapping(target = "sectorThroughputUnit", source = "accountReferenceData.sectorAssociationDetails.throughputUnit")
    @Mapping(target = "underlyingAgreement", source = "payload.underlyingAgreementProposed.underlyingAgreement")
    UnderlyingAgreementContainer toUnderlyingAgreementContainer(UnderlyingAgreementVariationRequestPayload payload, AccountReferenceData accountReferenceData);

    @Mapping(target = "underlyingAgreementSectionAttachmentIds", ignore = true)
    UnderlyingAgreement cloneUnderlyingAgreement(UnderlyingAgreement underlyingAgreement);

    @Mapping(target = "facilityItem.attachmentIds", ignore = true)
    Facility cloneFacility(Facility facility);

    @AfterMapping
    default void setActiveFacilities(@MappingTarget UnderlyingAgreementContainer unaContainer) {
        // get active facilities from proposed UnA
        final Set<Facility> activeFacilities = unaContainer.getUnderlyingAgreement().getFacilities()
                .stream()
                .filter(facility -> !facility.getStatus().equals(FacilityStatus.EXCLUDED))
                .collect(Collectors.toSet());

        unaContainer.getUnderlyingAgreement().setFacilities(activeFacilities);
    }

}
