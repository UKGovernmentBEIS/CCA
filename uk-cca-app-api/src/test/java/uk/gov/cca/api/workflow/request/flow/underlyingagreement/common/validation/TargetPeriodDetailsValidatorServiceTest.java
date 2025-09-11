package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TargetPeriodDetailsValidatorServiceTest {

    @InjectMocks
    private TargetPeriodDetailsValidatorService targetPeriodDetailsValidatorService;

    @Test
    void validateCCA2RelatedTargetPeriodsAreEmpty() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder().build())
                .build();

        // Invoke
        BusinessValidationResult result = targetPeriodDetailsValidatorService.validateCCA2RelatedTargetPeriodsAreEmpty(container);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateCCA2RelatedTargetPeriodsAreEmpty_not_valid() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder().build())
                        .targetPeriod6Details(TargetPeriod6Details.builder().build())
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = targetPeriodDetailsValidatorService.validateCCA2RelatedTargetPeriodsAreEmpty(container);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);
    }

    @Test
    void validate() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder().build())
                        .targetPeriod6Details(TargetPeriod6Details.builder().build())
                        .build())
                .build();
        final UnderlyingAgreementContainer originalContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder()
                                .facilityItem(FacilityItem.builder()
                                        .facilityDetails(FacilityDetails.builder()
                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                                                .build())
                                        .build())
                                .build()))
                        .targetPeriod5Details(TargetPeriod5Details.builder().build())
                        .targetPeriod6Details(TargetPeriod6Details.builder().build())
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = targetPeriodDetailsValidatorService.validate(container, originalContainer);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_no_facility_cca2_not_valid() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder().build())
                        .targetPeriod6Details(TargetPeriod6Details.builder().build())
                        .build())
                .build();
        final UnderlyingAgreementContainer originalContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder()
                                .facilityItem(FacilityItem.builder()
                                        .facilityDetails(FacilityDetails.builder()
                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                                .build())
                                        .build())
                                .build()))
                        .targetPeriod5Details(TargetPeriod5Details.builder().build())
                        .targetPeriod6Details(TargetPeriod6Details.builder().build())
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = targetPeriodDetailsValidatorService.validate(container, originalContainer);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);
    }

    @Test
    void validate_empty_periods_original_not_valid() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder().build())
                        .targetPeriod6Details(TargetPeriod6Details.builder().build())
                        .build())
                .build();
        final UnderlyingAgreementContainer originalContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder()
                                .facilityItem(FacilityItem.builder()
                                        .facilityDetails(FacilityDetails.builder()
                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                                                .build())
                                        .build())
                                .build()))
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = targetPeriodDetailsValidatorService.validate(container, originalContainer);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);
    }

    @Test
    void validate_empty_periods_not_valid() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder().build())
                .build();
        final UnderlyingAgreementContainer originalContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder()
                                .facilityItem(FacilityItem.builder()
                                        .facilityDetails(FacilityDetails.builder()
                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                                                .build())
                                        .build())
                                .build()))
                        .targetPeriod5Details(TargetPeriod5Details.builder().build())
                        .targetPeriod6Details(TargetPeriod6Details.builder().build())
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = targetPeriodDetailsValidatorService.validate(container, originalContainer);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);
    }

    @Test
    void validate_no_LIVE_cca2_valid() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder().build())
                .build();
        final UnderlyingAgreementContainer originalContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder()
                                        .facilityItem(FacilityItem.builder()
                                                .facilityDetails(FacilityDetails.builder()
                                                        .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                                        .build())
                                                .build())
                                        .build(),
                                Facility.builder()
                                        .facilityItem(FacilityItem.builder()
                                                .facilityDetails(FacilityDetails.builder()
                                                        .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                                                        .build())
                                                .build())
                                        .build()))
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = targetPeriodDetailsValidatorService.validate(container, originalContainer);

        // Verify
        assertThat(result.isValid()).isTrue();
    }
}
