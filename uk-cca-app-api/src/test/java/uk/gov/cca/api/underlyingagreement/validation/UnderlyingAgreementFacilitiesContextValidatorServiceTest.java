package uk.gov.cca.api.underlyingagreement.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITIES;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_SECTION_DATA;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_UNIQUE_FACILITY_ID;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementFacilitiesContextValidatorServiceTest {

	@InjectMocks
    private UnderlyingAgreementFacilitiesContextValidatorService validatorService;

    @Mock
    private DataValidator<Facility> validator;

    @Mock
    private UnderlyingAgreementFacilityValidatorService underlyingAgreementFacilityValidatorService;

    @Test
    void validate() {
        final Facility facility1 = createFacility("facility1");
        final Facility facility2 = createFacility("facility2");
        
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        when(validator.validate(facility1)).thenReturn(Optional.empty());
        when(validator.validate(facility2)).thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility1), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), eq(List.of()));
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility2), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), eq(List.of()));
    }

    @Test
    void validate_not_unique_ids_not_valid() {
        final Facility facility1 = createFacility("facility");
        final Facility facility2 = createFacility("facility");

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        when(validator.validate(facility1)).thenReturn(Optional.empty());
        when(validator.validate(facility2)).thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsExactly(INVALID_UNIQUE_FACILITY_ID.getMessage());
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility1), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), eq(List.of()));
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility2), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), eq(List.of()));
    }

    @Test
    void validate_empty_facilities_not_valid() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of())
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsExactly(INVALID_FACILITIES.getMessage());
        verifyNoInteractions(validator, underlyingAgreementFacilityValidatorService);
    }

    @Test
    void validate_with_data_violation() {
        final Facility facility1 = createFacility("facility1");
        final Facility facility2 = createFacility("facility2");

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        when(validator.validate(facility1)).thenReturn(Optional.of(new BusinessViolation("testClass", "testMessage")));
        when(validator.validate(facility2)).thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsExactly(INVALID_SECTION_DATA.getMessage());
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility1), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), anyList());
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility2), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), anyList());
    }

    private Facility createFacility(String facilityId) {
        return Facility.builder()
                .facilityItem(FacilityItem.builder()
                        .facilityId(facilityId)
                        .facilityDetails(FacilityDetails.builder()
                                .isCoveredByUkets(true)
                                .uketsId(UUID.randomUUID().toString())
                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                                .build())
                        .build())
                .build();
    }
}
