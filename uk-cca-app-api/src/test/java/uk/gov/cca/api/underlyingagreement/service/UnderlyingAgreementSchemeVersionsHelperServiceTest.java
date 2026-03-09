package uk.gov.cca.api.underlyingagreement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.config.Cca2TerminationConfig;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.Targets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementSchemeVersionsHelperServiceTest {

	@InjectMocks
    private UnderlyingAgreementSchemeVersionsHelperService service;

    @Mock
    private Cca2TerminationConfig cca2TerminationConfig;
    
	@Test
    void calculateSchemeVersionsFromActiveFacilities_before_end_date() {
        final Set<Facility> facilities = Set.of(Facility.builder()
				.status(FacilityStatus.LIVE)
				.facilityItem(FacilityItem.builder()
						.facilityDetails(FacilityDetails.builder()
								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3))
								.build())
						.build())
				.build());

		when(cca2TerminationConfig.getTerminationDate()).thenReturn(LocalDate.now().plusDays(1));

        // Invoke
		Set<SchemeVersion> schemeVersions = service.calculateSchemeVersionsFromActiveFacilities(facilities);

        // Verify
        assertThat(schemeVersions).isEqualTo(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3));
        verify(cca2TerminationConfig, times(2)).getTerminationDate();
    }
	
	@Test
    void calculateSchemeVersionsFromActiveFacilities_after_end_date() {
        final Set<Facility> facilities = Set.of(Facility.builder()
				.status(FacilityStatus.LIVE)
				.facilityItem(FacilityItem.builder()
						.facilityDetails(FacilityDetails.builder()
								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3))
								.build())
						.build())
				.build());

		when(cca2TerminationConfig.getTerminationDate()).thenReturn(LocalDate.now().minusDays(1));

        // Invoke
		Set<SchemeVersion> schemeVersions = service.calculateSchemeVersionsFromActiveFacilities(facilities);

        // Verify
        assertThat(schemeVersions).isEqualTo(Set.of(SchemeVersion.CCA_3));
        verify(cca2TerminationConfig, times(2)).getTerminationDate();
    }
	
	@Test
    void calculateTerminatedSchemeVersionsFromFacilities() {
        final Set<Facility> facilities = Set.of(Facility.builder()
				.status(FacilityStatus.EXCLUDED)
				.facilityItem(FacilityItem.builder()
						.facilityDetails(FacilityDetails.builder()
								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
								.build())
						.build())
				.build(), Facility.builder()
				.status(FacilityStatus.LIVE)
				.facilityItem(FacilityItem.builder()
						.facilityDetails(FacilityDetails.builder()
								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
								.build())
						.build())
				.build());

		when(cca2TerminationConfig.getTerminationDate()).thenReturn(LocalDate.now().plusDays(1));

        // Invoke
		Set<SchemeVersion> schemeVersions = service.calculateTerminatedSchemeVersionsFromFacilities(facilities);

        // Verify
        assertThat(schemeVersions).isEqualTo(Set.of(SchemeVersion.CCA_2));
        verify(cca2TerminationConfig, times(1)).getTerminationDate();
    }
	
	@Test
    void shouldShowTp5Tp6() {
        final Set<Facility> facilities = Set.of(Facility.builder()
				.status(FacilityStatus.LIVE)
				.facilityItem(FacilityItem.builder()
						.facilityDetails(FacilityDetails.builder()
								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3))
								.build())
						.build())
				.build());
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
				.underlyingAgreement(UnderlyingAgreement.builder()
						.facilities(facilities)
						.targetPeriod5Details(TargetPeriod5Details.builder().exist(true).build())
						.targetPeriod6Details(TargetPeriod6Details.builder().targets(Targets.builder().build()).build())
						.build())
				.build();	

		when(cca2TerminationConfig.getTerminationDate()).thenReturn(LocalDate.now().plusDays(1));

        // Invoke
		boolean shouldShowTp5Tp6 = service.shouldShowTp5Tp6(container, LocalDate.now());

        // Verify
        assertThat(shouldShowTp5Tp6).isTrue();
        verify(cca2TerminationConfig, times(2)).getTerminationDate();
    }
	
	@Test
    void shouldShowTp5Tp6_only_CCA3_after_end_date() {
        final Set<Facility> facilities = Set.of(Facility.builder()
				.status(FacilityStatus.LIVE)
				.facilityItem(FacilityItem.builder()
						.facilityDetails(FacilityDetails.builder()
								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3))
								.build())
						.build())
				.build());
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
				.underlyingAgreement(UnderlyingAgreement.builder()
						.facilities(facilities)
						.targetPeriod5Details(TargetPeriod5Details.builder().exist(true).build())
						.targetPeriod6Details(TargetPeriod6Details.builder().targets(Targets.builder().build()).build())
						.build())
				.build();	

		when(cca2TerminationConfig.getTerminationDate()).thenReturn(LocalDate.now().minusDays(1));

        // Invoke
		boolean shouldShowTp5Tp6 = service.shouldShowTp5Tp6(container, LocalDate.now());

        // Verify
        assertThat(shouldShowTp5Tp6).isFalse();
        verify(cca2TerminationConfig, times(2)).getTerminationDate();
    }
	
	@Test
    void shouldShowTp5Tp6_no_existing_tp_data() {
        final Set<Facility> facilities = Set.of(Facility.builder()
				.status(FacilityStatus.LIVE)
				.facilityItem(FacilityItem.builder()
						.facilityDetails(FacilityDetails.builder()
								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3))
								.build())
						.build())
				.build());
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
				.underlyingAgreement(UnderlyingAgreement.builder()
						.facilities(facilities)
						.build())
				.build();	

		when(cca2TerminationConfig.getTerminationDate()).thenReturn(LocalDate.now().plusDays(1));

        // Invoke
		boolean shouldShowTp5Tp6 = service.shouldShowTp5Tp6(container, LocalDate.now());

        // Verify
        assertThat(shouldShowTp5Tp6).isFalse();
        verify(cca2TerminationConfig, times(2)).getTerminationDate();
    }
}
