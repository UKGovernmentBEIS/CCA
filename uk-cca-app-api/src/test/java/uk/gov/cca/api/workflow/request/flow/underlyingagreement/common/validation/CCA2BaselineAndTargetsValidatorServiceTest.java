package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementSchemeVersionsHelperService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CCA2BaselineAndTargetsValidatorServiceTest {

    @InjectMocks
    private CCA2BaselineAndTargetsValidatorService cca2BaselineAndTargetsValidatorService;
    
    @Mock
    private UnderlyingAgreementSchemeVersionsHelperService underlyingAgreementSchemeVersionsHelperService;

    @Test
    void validateEmpty() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder().build())
                .build();

        // Invoke
        BusinessValidationResult result = cca2BaselineAndTargetsValidatorService.validateEmpty(container);

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
        BusinessValidationResult result = cca2BaselineAndTargetsValidatorService.validateEmpty(container);

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
                                .status(FacilityStatus.LIVE)
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
        
        when(underlyingAgreementSchemeVersionsHelperService.shouldShowCCA2BaselineAndTargets(originalContainer, null))
        		.thenReturn(true);

        // Invoke
        BusinessValidationResult result = cca2BaselineAndTargetsValidatorService.validate(container, originalContainer, null);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(underlyingAgreementSchemeVersionsHelperService, times(1)).shouldShowCCA2BaselineAndTargets(originalContainer, null);
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
                                .status(FacilityStatus.LIVE)
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
        
        when(underlyingAgreementSchemeVersionsHelperService.shouldShowCCA2BaselineAndTargets(originalContainer, null))
				.thenReturn(false);

        // Invoke
        BusinessValidationResult result = cca2BaselineAndTargetsValidatorService.validate(container, originalContainer, null);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);
        verify(underlyingAgreementSchemeVersionsHelperService, times(1)).shouldShowCCA2BaselineAndTargets(originalContainer, null);
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
                                .status(FacilityStatus.LIVE)
                                .facilityItem(FacilityItem.builder()
                                        .facilityDetails(FacilityDetails.builder()
                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                                                .build())
                                        .build())
                                .build()))
                        .build())
                .build();
        
        when(underlyingAgreementSchemeVersionsHelperService.shouldShowCCA2BaselineAndTargets(originalContainer, null))
				.thenReturn(false);

        // Invoke
        BusinessValidationResult result = cca2BaselineAndTargetsValidatorService.validate(container, originalContainer, null);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);
        verify(underlyingAgreementSchemeVersionsHelperService, times(1)).shouldShowCCA2BaselineAndTargets(originalContainer, null);
    }

    @Test
    void validate_empty_periods_not_valid() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder().build())
                .build();
        final UnderlyingAgreementContainer originalContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder()
                                .status(FacilityStatus.LIVE)
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
        
        when(underlyingAgreementSchemeVersionsHelperService.shouldShowCCA2BaselineAndTargets(originalContainer, null))
				.thenReturn(true);

        // Invoke
        BusinessValidationResult result = cca2BaselineAndTargetsValidatorService.validate(container, originalContainer, null);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSize(2);
        verify(underlyingAgreementSchemeVersionsHelperService, times(1)).shouldShowCCA2BaselineAndTargets(originalContainer, null);
    }
}
