package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineEnergyConsumption;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.netz.api.common.config.MapperConfig;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {AgreementCompositionType .class, VariableEnergyDepictionType .class, CcaRequestActionPayloadType.class})
public interface Cca3ExistingFacilitiesMigrationAccountProcessingMapper {

    @Mapping(target = "targetComposition", expression = "java(toFacilityTargetComposition(facilityMigration))")
    @Mapping(target = "baselineData", expression = "java(toFacilityBaselineData(facilityMigration))")
    @Mapping(target = "facilityBaselineEnergyConsumption", expression = "java(toFacilityBaselineEnergyConsumption(facilityMigration))")
    @Mapping(target = "facilityTargets", expression = "java(toFacilityTargets(facilityMigration))")
    Cca3FacilityBaselineAndTargets toCca3FacilityBaselineAndTargets(Cca3FacilityMigrationData facilityMigration);

    @Mapping(target = "calculatorFile", expression = "java(java.util.UUID.fromString(facilityMigration.getCalculatorFileUuid()))")
    @Mapping(target = "agreementCompositionType", expression = "java(AgreementCompositionType.NOVEM)")
    FacilityTargetComposition toFacilityTargetComposition(Cca3FacilityMigrationData facilityMigration);

    @Mapping(target = "isTwelveMonths", expression = "java(java.lang.Boolean.TRUE)")
    FacilityBaselineData toFacilityBaselineData(Cca3FacilityMigrationData facilityMigration);

    @Mapping(target = "hasVariableEnergy", expression = "java(facilityMigration.getTotalVariableEnergy().compareTo(java.math.BigDecimal.ZERO) != 0)")
    @Mapping(target = "variableEnergyType", expression = "java(facilityMigration.getTotalVariableEnergy().compareTo(java.math.BigDecimal.ZERO) != 0 ? VariableEnergyDepictionType.TOTALS : null)")
    @Mapping(target = "baselineVariableEnergy", expression = "java(facilityMigration.getTotalVariableEnergy().compareTo(java.math.BigDecimal.ZERO) != 0 ? facilityMigration.getTotalVariableEnergy() : null)")
    FacilityBaselineEnergyConsumption toFacilityBaselineEnergyConsumption(Cca3FacilityMigrationData facilityMigration);

    default FacilityTargets toFacilityTargets(Cca3FacilityMigrationData facilityMigration) {
        Map<TargetImprovementType, BigDecimal> improvements = new EnumMap<>(TargetImprovementType.class);
        improvements.put(TargetImprovementType.TP7, facilityMigration.getTp7Improvement());
        improvements.put(TargetImprovementType.TP8, facilityMigration.getTp8Improvement());
        improvements.put(TargetImprovementType.TP9, facilityMigration.getTp9Improvement());

        return FacilityTargets.builder().improvements(improvements).build();
    }

    @Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)")
    Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload toCca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload(
            Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload, List<DefaultNoticeRecipient> defaultContacts);
}
