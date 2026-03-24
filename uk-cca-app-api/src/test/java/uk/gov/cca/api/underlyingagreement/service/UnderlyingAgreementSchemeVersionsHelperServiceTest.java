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

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.service.SchemeTerminationHelper;
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
    private SchemeTerminationHelper schemeTerminationHelper;
    
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

		when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_2))).thenReturn(false);
		when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_3))).thenReturn(false);

        // Invoke
		Set<SchemeVersion> schemeVersions = service.calculateSchemeVersionsFromActiveFacilities(facilities);

        // Verify
        assertThat(schemeVersions).isEqualTo(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3));
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_3));
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_2));
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

        when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_2))).thenReturn(true);
		when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_3))).thenReturn(false);

        // Invoke
		Set<SchemeVersion> schemeVersions = service.calculateSchemeVersionsFromActiveFacilities(facilities);

        // Verify
        assertThat(schemeVersions).isEqualTo(Set.of(SchemeVersion.CCA_3));
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_3));
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_2));
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

        when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_2))).thenReturn(false);
		when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_3))).thenReturn(false);

        // Invoke
		Set<SchemeVersion> schemeVersions = service.calculateTerminatedSchemeVersionsFromFacilities(facilities);

        // Verify
        assertThat(schemeVersions).isEqualTo(Set.of(SchemeVersion.CCA_2));
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_3));
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_2));
    }
	
	@Test
    void shouldShowTp5Tp6() {
		final LocalDate requestCreationDate = LocalDate.now();
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

        when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_2), requestCreationDate)).thenReturn(false);
		when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_3), requestCreationDate)).thenReturn(false);

        // Invoke
		boolean shouldShowTp5Tp6 = service.shouldShowCCA2BaselineAndTargets(container, requestCreationDate);

        // Verify
        assertThat(shouldShowTp5Tp6).isTrue();
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_3), requestCreationDate);
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_2), requestCreationDate);
    }
	
	@Test
    void shouldShowTp5Tp6_only_CCA3_after_end_date() {
		final LocalDate requestCreationDate = LocalDate.now();
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

        when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_2), requestCreationDate)).thenReturn(true);
		when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_3), requestCreationDate)).thenReturn(false);

        // Invoke
		boolean shouldShowTp5Tp6 = service.shouldShowCCA2BaselineAndTargets(container, requestCreationDate);

        // Verify
        assertThat(shouldShowTp5Tp6).isFalse();
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_3), requestCreationDate);
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_2), requestCreationDate);
    }
	
	@Test
    void shouldShowTp5Tp6_no_existing_tp_data() {
		final LocalDate requestCreationDate = LocalDate.now();
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

        when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_2), requestCreationDate)).thenReturn(false);
		when(schemeTerminationHelper.isCca2Terminated(Set.of(SchemeVersion.CCA_3), requestCreationDate)).thenReturn(false);

        // Invoke
		boolean shouldShowTp5Tp6 = service.shouldShowCCA2BaselineAndTargets(container, requestCreationDate);

        // Verify
        assertThat(shouldShowTp5Tp6).isFalse();
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_3), requestCreationDate);
        verify(schemeTerminationHelper, times(1)).isCca2Terminated(Set.of(SchemeVersion.CCA_2), requestCreationDate);
    }
}
