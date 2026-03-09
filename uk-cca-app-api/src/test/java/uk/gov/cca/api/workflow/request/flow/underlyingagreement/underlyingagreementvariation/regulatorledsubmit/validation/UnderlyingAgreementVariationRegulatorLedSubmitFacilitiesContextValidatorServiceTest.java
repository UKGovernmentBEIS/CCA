package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedSubmitFacilitiesContextValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedSubmitFacilitiesContextValidatorService service;

    @Test
    void validate() {
        final Map<String, LocalDate> facilityChargeStartDateMap = Map.of(
                "facility1", LocalDate.of(2026, 2, 2)
        );
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(
                                Facility.builder().status(FacilityStatus.NEW).facilityItem(FacilityItem.builder().facilityId("facility1").build()).build(),
                                Facility.builder().status(FacilityStatus.NEW).facilityItem(FacilityItem.builder().facilityId("facility2").build()).build()
                        ))
                        .build())
                .build();

        // Invoke
        List<BusinessValidationResult> results = service.validate(container, facilityChargeStartDateMap);

        // Verify
        assertThat(results.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
    }

    @Test
    void validate_empty_charge_date_valid() {
        final Map<String, LocalDate> facilityChargeStartDateMap = Map.of();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(
                                Facility.builder().status(FacilityStatus.NEW).facilityItem(FacilityItem.builder().facilityId("facility1").build()).build(),
                                Facility.builder().status(FacilityStatus.NEW).facilityItem(FacilityItem.builder().facilityId("facility2").build()).build()
                        ))
                        .build())
                .build();

        // Invoke
        List<BusinessValidationResult> results = service.validate(container, facilityChargeStartDateMap);

        // Verify
        assertThat(results.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
    }

    @Test
    void validate_charge_date_not_valid() {
        final Map<String, LocalDate> facilityChargeStartDateMap = Map.of(
                "facility1", LocalDate.of(2026, 2, 2)
        );
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(
                                Facility.builder().status(FacilityStatus.LIVE).facilityItem(FacilityItem.builder().facilityId("facility1").build()).build(),
                                Facility.builder().status(FacilityStatus.NEW).facilityItem(FacilityItem.builder().facilityId("facility2").build()).build()
                        ))
                        .build())
                .build();

        // Invoke
        List<BusinessValidationResult> results = service.validate(container, facilityChargeStartDateMap);

        // Verify
        assertThat(results.stream().filter(r -> !r.isValid()).count()).isEqualTo(1L);
    }

    @Test
    void validate_charge_date_facility_not_exist_not_valid() {
        final Map<String, LocalDate> facilityChargeStartDateMap = Map.of(
                "facility111", LocalDate.of(2026, 2, 2)
        );
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(
                                Facility.builder().status(FacilityStatus.NEW).facilityItem(FacilityItem.builder().facilityId("facility1").build()).build(),
                                Facility.builder().status(FacilityStatus.NEW).facilityItem(FacilityItem.builder().facilityId("facility2").build()).build()
                        ))
                        .build())
                .build();

        // Invoke
        List<BusinessValidationResult> results = service.validate(container, facilityChargeStartDateMap);

        // Verify
        assertThat(results.stream().filter(r -> !r.isValid()).count()).isEqualTo(1L);
    }

    @Test
    void validate_no_active_facility_not_valid() {
        final Map<String, LocalDate> facilityChargeStartDateMap = Map.of();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(
                                Facility.builder().status(FacilityStatus.EXCLUDED).facilityItem(FacilityItem.builder().facilityId("facility1").build()).build(),
                                Facility.builder().status(FacilityStatus.EXCLUDED).facilityItem(FacilityItem.builder().facilityId("facility2").build()).build()
                        ))
                        .build())
                .build();

        // Invoke
        List<BusinessValidationResult> results = service.validate(container, facilityChargeStartDateMap);

        // Verify
        assertThat(results.stream().filter(r -> !r.isValid()).count()).isEqualTo(1L);
    }
}
