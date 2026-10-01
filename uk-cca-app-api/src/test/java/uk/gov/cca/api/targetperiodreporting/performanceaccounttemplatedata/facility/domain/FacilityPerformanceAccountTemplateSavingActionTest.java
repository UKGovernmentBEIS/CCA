package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FacilityPerformanceAccountTemplateSavingActionTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {
        final FacilityPerformanceAccountTemplateSavingAction data = FacilityPerformanceAccountTemplateSavingAction.builder()
                .actionCategoryType(ActionCategoryType.ENERGY_MANAGEMENT)
                .supplyDemandSideMeasure(SupplyDemandSideMeasure.DEMAND_SIDE)
                .savingActionsImplemented("saving")
                .reasonsForImplementation("reasons")
                .implementationDate(LocalDate.of(2026, 1, 1))
                .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED_AND_VARIABLE)
                .energyConsumptionOrCarbonEmissionsImpactedPercentage(BigDecimal.valueOf(20.6))
                .expectedExtentOfChangeImplementedPercentage(BigDecimal.valueOf(20.6))
                .expectedSavingsFromTheChangeImplementedPercentage(BigDecimal.valueOf(20.6))
                .estimatedChangeInEnergyConsumptionPercentage(BigDecimal.valueOf(20.6))
                .notes("notes")
                .build();

        final Set<ConstraintViolation<FacilityPerformanceAccountTemplateSavingAction>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_not_valid() {
        final FacilityPerformanceAccountTemplateSavingAction data = FacilityPerformanceAccountTemplateSavingAction.builder()
                .actionCategoryType(ActionCategoryType.ENERGY_MANAGEMENT)
                .supplyDemandSideMeasure(SupplyDemandSideMeasure.DEMAND_SIDE)
                .savingActionsImplemented("saving")
                .implementationDate(LocalDate.of(2026, 1, 1))
                .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED_AND_VARIABLE)
                .energyConsumptionOrCarbonEmissionsImpactedPercentage(BigDecimal.valueOf(102.6))
                .expectedExtentOfChangeImplementedPercentage(BigDecimal.valueOf(20.6))
                .expectedSavingsFromTheChangeImplementedPercentage(BigDecimal.valueOf(20.6))
                .estimatedChangeInEnergyConsumptionPercentage(BigDecimal.valueOf(20.6))
                .notes("notes")
                .build();

        final Set<ConstraintViolation<FacilityPerformanceAccountTemplateSavingAction>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must be less than or equal to 100",
                        "numeric value out of bounds (<2 digits>.<7 digits> expected)",
                        "{performanceaccounttemplatedata.facility.facilityPerformanceAccountTemplateSavingAction.mandatoryFieldsIncomplete}");
    }

    @Test
    void validate_no_actions_valid() {
        final FacilityPerformanceAccountTemplateSavingAction data = FacilityPerformanceAccountTemplateSavingAction.builder()
                .actionCategoryType(ActionCategoryType.NO_ACTION)
                .notes("notes")
                .build();

        final Set<ConstraintViolation<FacilityPerformanceAccountTemplateSavingAction>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_no_actions_not_valid() {
        final FacilityPerformanceAccountTemplateSavingAction data = FacilityPerformanceAccountTemplateSavingAction.builder()
                .actionCategoryType(ActionCategoryType.NO_ACTION)
                .supplyDemandSideMeasure(SupplyDemandSideMeasure.DEMAND_SIDE)
                .savingActionsImplemented("saving")
                .implementationDate(LocalDate.of(2026, 1, 1))
                .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED_AND_VARIABLE)
                .expectedExtentOfChangeImplementedPercentage(BigDecimal.valueOf(20.6))
                .expectedSavingsFromTheChangeImplementedPercentage(BigDecimal.valueOf(20.6))
                .estimatedChangeInEnergyConsumptionPercentage(BigDecimal.valueOf(20.6))
                .notes("notes")
                .build();

        final Set<ConstraintViolation<FacilityPerformanceAccountTemplateSavingAction>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{performanceaccounttemplatedata.facility.facilityPerformanceAccountTemplateSavingAction.mandatoryFieldsIncomplete}");
    }

    @Test
    void validate_no_actionCategoryType_not_valid() {
        final FacilityPerformanceAccountTemplateSavingAction data = FacilityPerformanceAccountTemplateSavingAction.builder()
                .build();

        final Set<ConstraintViolation<FacilityPerformanceAccountTemplateSavingAction>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }
}
