package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.service.SchemeTerminationHelper;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.netz.api.common.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementHandleCca2FacilitiesAfterTerminationDateServiceTest {

	@InjectMocks
    private UnderlyingAgreementHandleCca2FacilitiesAfterTerminationDateService service;
    
    @Mock
    private SchemeTerminationHelper schemeTerminationHelper;
    
    @Test
    void handleCca2FacilitiesAfterTerminationDate_close_LIVE_ignore_NEW_cca2_facilities_after_cca2_termination_date() {
      final LocalDate cca2EndDate = LocalDate.now().minusDays(1);

      final Facility facility1 = Facility.builder()
              .status(FacilityStatus.LIVE)
              .facilityItem(FacilityItem.builder()
                      .facilityDetails(FacilityDetails.builder()
                              .previousFacilityId("Prv1")
                              .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                              .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                              .build())
                      .build())
              .build();
      final Facility facility2 = Facility.builder()
              .status(FacilityStatus.NEW)
              .facilityItem(FacilityItem.builder()
                      .facilityDetails(FacilityDetails.builder()
                              .previousFacilityId("Prv1")
                              .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                              .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                              .build())
                      .build())
              .build();
      final Facility facility3 = Facility.builder()
              .status(FacilityStatus.LIVE)
              .facilityItem(FacilityItem.builder()
                      .facilityDetails(FacilityDetails.builder()
                              .previousFacilityId("Prv1")
                              .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                              .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                              .build())
                      .build())
              .build();
      final UnderlyingAgreement una = UnderlyingAgreement.builder()
	          .facilities(new HashSet<>(Set.of(facility1, facility2, facility3)))
	          .build();
      facility1.setExcludedDate(cca2EndDate);

		when(schemeTerminationHelper.isAfterCca2SchemeTerminationDate()).thenReturn(true);
		when(schemeTerminationHelper.getCca2TerminationDate()).thenReturn(cca2EndDate);
		
		// Invoke
		UnderlyingAgreement result = service.handleCca2FacilitiesAfterTerminationDate(una);

		// Verify
		assertThat(result.getFacilities()).hasSize(2);
		assertThat(result.getFacilities()).containsOnly(facility1, facility3);
		verify(schemeTerminationHelper, times(1)).isAfterCca2SchemeTerminationDate();
		verify(schemeTerminationHelper, times(1)).getCca2TerminationDate();
  }
  
  @Test
  void handleCca2FacilitiesAfterTerminationDate_no_facilities_after_cca2_termination_date() {
      final Facility facility = Facility.builder()
              .status(FacilityStatus.LIVE)
              .facilityItem(FacilityItem.builder()
                      .facilityDetails(FacilityDetails.builder()
                              .previousFacilityId("Prv1")
                              .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                              .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                              .build())
                      .build())
              .build();
      final UnderlyingAgreement una = UnderlyingAgreement.builder()
	          .facilities(new HashSet<>(Set.of(facility)))
	          .build();
      
      when(schemeTerminationHelper.isAfterCca2SchemeTerminationDate()).thenReturn(true);

      // Invoke
      BusinessException businessException = assertThrows(BusinessException.class,
              () -> service.handleCca2FacilitiesAfterTerminationDate(una));

      // Verify
      assertThat(businessException.getErrorCode()).isEqualTo(CcaErrorCode.NO_FACILITIES_FOUND_FOR_CURRENT_SCHEME);
      verify(schemeTerminationHelper, times(1)).isAfterCca2SchemeTerminationDate();
	  verify(schemeTerminationHelper, never()).getCca2TerminationDate();
  }
}
