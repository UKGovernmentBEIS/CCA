package uk.gov.cca.api.underlyingagreement.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_AFTER_SCHEME_END_DATE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_PREVIOUS_FACILITY_ID;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_PREVIOUS_FACILITY_ID_CCA2_ONLY;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.service.SchemeTerminationHelper;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.FacilityValidationContext;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;


@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementFacilitiesFinalizationValidatorServiceTest {

	@InjectMocks
    private UnderlyingAgreementFacilitiesFinalizationValidatorService validatorService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;
    
    @Mock
    private SchemeTerminationHelper schemeTerminationHelper;
    
    @Test
    void validate_facility_CCA2_excluded_after_end_date_valid() {
        final Facility facility1 = createFacility(FacilityStatus.EXCLUDED, Set.of(SchemeVersion.CCA_2));
        final Facility facility2 = createFacility(FacilityStatus.LIVE, Set.of(SchemeVersion.CCA_3));        
        Map<String, FacilityValidationContext> facilityValidationContextMap = new HashMap<>();
		facilityValidationContextMap.put(facility1.getFacilityItem().getFacilityId(), FacilityValidationContext
				.builder()
				.facilityBusinessId(facility1.getFacilityItem().getFacilityId())
				.closedDate(null)
				.build());

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(Set.of("previousId")))
        		.thenReturn(facilityValidationContextMap);
        when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_2))).thenReturn(true);
        when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_3))).thenReturn(false);

        // Invoke
        BusinessValidationResult result = validatorService.validate(Set.of(facility1, facility2));

        // Verify
        assertThat(result.getViolations()).isEmpty();    
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_2));
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_3));
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(Set.of("previousId"));
    }
    
    @Test
    void validate_facility_CCA2_exists_after_end_date_not_valid() {
        final Facility facility = createFacility(FacilityStatus.LIVE, Set.of(SchemeVersion.CCA_2));
        
        Map<String, FacilityValidationContext> facilityValidationContextMap = new HashMap<>();
		facilityValidationContextMap.put(facility.getFacilityItem().getFacilityId(), FacilityValidationContext
				.builder().facilityBusinessId(facility.getFacilityItem().getFacilityId())
				.closedDate(null)
				.build());

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(Set.of("previousId")))
        		.thenReturn(facilityValidationContextMap);  
        when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_2))).thenReturn(true);
        
        // Invoke
        BusinessValidationResult result = validatorService.validate(Set.of(facility));

        // Verify
        assertThat(result.getViolations()).hasSize(1);
        assertThat(((UnderlyingAgreementViolation) result.getViolations().getFirst()).getMessage()).isEqualTo(INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_AFTER_SCHEME_END_DATE.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(Set.of("previousId"));
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_2));
    }
    
    @Test
    void validate_previous_facility_inactive_not_valid() {
        final String previousFacilityId = "previousFacilityId";
        final Facility facility = createFacility(FacilityStatus.NEW, Set.of(SchemeVersion.CCA_3));
        facility.getFacilityItem().getFacilityDetails().setApplicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP);
        facility.getFacilityItem().getFacilityDetails().setPreviousFacilityId(previousFacilityId);
        
        Map<String, FacilityValidationContext> facilityValidationContextMap = new HashMap<>();
		facilityValidationContextMap.put(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId(),
				FacilityValidationContext.builder()
						.facilityBusinessId(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId())
						.participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
						.closedDate(LocalDate.now())
						.build());

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(Set.of("previousFacilityId")))
        		.thenReturn(facilityValidationContextMap);
        when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_3))).thenReturn(false);

        // Invoke
        BusinessValidationResult result = validatorService.validate(Set.of(facility));

        // Verify
        assertThat(result.getViolations()).hasSize(1);
        assertThat(((UnderlyingAgreementViolation) result.getViolations().getFirst()).getMessage()).isEqualTo(INVALID_PREVIOUS_FACILITY_ID.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(Set.of("previousFacilityId"));
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_3));
    }
    
    @Test
    void validate_previous_facility_cca2_only_after_cca2_end_date_not_valid() {
        final String previousFacilityId = "previousFacilityId";
        final Facility facility = createFacility(FacilityStatus.NEW, Set.of(SchemeVersion.CCA_3));
        facility.getFacilityItem().getFacilityDetails().setApplicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP);
        facility.getFacilityItem().getFacilityDetails().setPreviousFacilityId(previousFacilityId);
        
        Map<String, FacilityValidationContext> facilityValidationContextMap = new HashMap<>();
		facilityValidationContextMap.put(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId(),
				FacilityValidationContext.builder()
						.facilityBusinessId(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId())
						.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
						.build());

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(Set.of("previousFacilityId")))
        		.thenReturn(facilityValidationContextMap);
        when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_2))).thenReturn(true);
        when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_3))).thenReturn(false);

        // Invoke
        BusinessValidationResult result = validatorService.validate(Set.of(facility));

        // Verify
        assertThat(result.getViolations()).hasSize(1);
        assertThat(((UnderlyingAgreementViolation) result.getViolations().getFirst()).getMessage()).isEqualTo(INVALID_PREVIOUS_FACILITY_ID_CCA2_ONLY.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(Set.of("previousFacilityId"));
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_2));
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_3));
    }
    
    private Facility createFacility(FacilityStatus status, Set<SchemeVersion> schemeVersions) {
        return Facility.builder()
                .status(status)
                .facilityItem(FacilityItem.builder()
                        .facilityId(UUID.randomUUID().toString())
                        .facilityDetails(FacilityDetails.builder()
                                .isCoveredByUkets(Boolean.TRUE)
                                .applicationReason(ApplicationReasonType.NEW_AGREEMENT)
                                .participatingSchemeVersions(schemeVersions)
                                .previousFacilityId("previousId")
                                .facilityAddress(FacilityAddressDTO.builder()
                                        .line1("Line 1")
                                        .line2("Line 2")
                                        .city("City")
                                        .county("County")
                                        .postcode("code")
                                        .country("Country")
                                        .build())
                                .build())
                        .build())
                .build();
    }
}
