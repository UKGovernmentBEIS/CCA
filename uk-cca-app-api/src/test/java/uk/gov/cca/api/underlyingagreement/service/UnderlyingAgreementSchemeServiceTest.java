package uk.gov.cca.api.underlyingagreement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementDocument;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementSchemeServiceTest {

	@InjectMocks
    private UnderlyingAgreementSchemeService underlyingAgreementSchemeService;

    @Mock
    private UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    
    @Mock
    private UnderlyingAgreementService underlyingAgreementService;
    
	@Test
    void migrateUnderlyingAgreementToScheme() {
        final long accountId = 1L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer newContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder().build())
                .build();
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(schemeVersion)
                .build();

        UnderlyingAgreementEntity persistentEntity = new UnderlyingAgreementEntity();

        when(underlyingAgreementService.findUnderlyingAgreementEntity(accountId))
                .thenReturn(persistentEntity);

        // Invoke
        underlyingAgreementSchemeService.migrateUnderlyingAgreementToScheme(newContainer, accountId,
                underlyingAgreementValidationContext);

        // Verify
        assertThat(persistentEntity.getUnderlyingAgreementContainer()).isEqualTo(newContainer);
        assertThat(persistentEntity.getUnderlyingAgreementContainer()).isEqualTo(newContainer);
        assertThat(persistentEntity.getDocumentForSchemeVersion(schemeVersion)).isNotNull();

        verify(underlyingAgreementValidatorService, times(1)).validate(newContainer, underlyingAgreementValidationContext);
        verify(underlyingAgreementService, times(1)).findUnderlyingAgreementEntity(accountId);
    }
	
	@Test
    void terminateUnaForSchemeVersion() {
        final long underlyingAgreementId = 1L;
        final long accountId = 2L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;
        final LocalDateTime terminatedDate = LocalDateTime.now();

        UnderlyingAgreementDocument unaDocument = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(schemeVersion);
        UnderlyingAgreementDocument unaDocument2 = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(SchemeVersion.CCA_3);
        UnderlyingAgreementEntity persistentEntity = UnderlyingAgreementEntity.builder()
        		.id(underlyingAgreementId)
        		.accountId(accountId)
        		.underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
        				.underlyingAgreement(
        						UnderlyingAgreement.builder()
        						.facilities(new HashSet<>(Arrays.asList(
        								Facility.builder()
        								.facilityItem(
        										FacilityItem.builder()
        										.facilityDetails(FacilityDetails.builder()
        												.participatingSchemeVersions(Set.of(schemeVersion, SchemeVersion.CCA_3)).build())
        										.build())
        								.build(),Facility.builder()
        								.facilityItem(
        										FacilityItem.builder()
                                                .facilityDetails(FacilityDetails.builder()
                                                		.participatingSchemeVersions(Set.of(schemeVersion))
                                                		.build())
                                                .build())
        								.build())))
        						.build())
        				.build())
        		.build();

        persistentEntity.addUnderlyingAgreementDocument(unaDocument);
        persistentEntity.addUnderlyingAgreementDocument(unaDocument2);

        when(underlyingAgreementService.findUnderlyingAgreementEntity(accountId)).thenReturn(persistentEntity);

        // Invoke
        underlyingAgreementSchemeService.terminateUnaForSchemeVersion(accountId, schemeVersion, terminatedDate);

        // Verify
        assertThat(persistentEntity.getUnderlyingAgreementContainer().getUnderlyingAgreement().getFacilities()).hasSize(1);
        assertThat(persistentEntity.getUnderlyingAgreementContainer().getUnderlyingAgreement().getFacilities().iterator().next()
        		.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions()).isEqualTo(Set.of(schemeVersion, SchemeVersion.CCA_3));
        verify(underlyingAgreementService, times(1)).terminateUnderlyingAgreementDocument(unaDocument, terminatedDate);
        verify(underlyingAgreementService, times(1)).findUnderlyingAgreementEntity(accountId);
    }
	
	@Test
    void terminateUnaDocumentsForSchemeVersion() {
        final long underlyingAgreementId = 1L;
        final long accountId = 2L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;
        final LocalDateTime terminatedDate = LocalDateTime.now();

        UnderlyingAgreementDocument unaDocument = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(schemeVersion);
        UnderlyingAgreementDocument unaDocument2 = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(SchemeVersion.CCA_3);
        UnderlyingAgreementEntity persistentEntity = UnderlyingAgreementEntity.builder()
        		.id(underlyingAgreementId)
        		.accountId(accountId)
        		.underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
        				.underlyingAgreement(
        						UnderlyingAgreement.builder()
        						.facilities(new HashSet<>(Arrays.asList(
        								Facility.builder()
        								.facilityItem(
        										FacilityItem.builder()
        										.facilityDetails(FacilityDetails.builder()
        												.participatingSchemeVersions(Set.of(schemeVersion, SchemeVersion.CCA_3)).build())
        										.build())
        								.build(),Facility.builder()
        								.facilityItem(
        										FacilityItem.builder()
                                                .facilityDetails(FacilityDetails.builder()
                                                		.participatingSchemeVersions(Set.of(schemeVersion))
                                                		.build())
                                                .build())
        								.build())))
        						.build())
        				.build())
        		.build();

        persistentEntity.addUnderlyingAgreementDocument(unaDocument);
        persistentEntity.addUnderlyingAgreementDocument(unaDocument2);

        when(underlyingAgreementService.findUnderlyingAgreementEntity(accountId)).thenReturn(persistentEntity);

        // Invoke
        underlyingAgreementSchemeService.terminateUnaForSchemeVersion(accountId, schemeVersion, terminatedDate);

        // Verify
        verify(underlyingAgreementService, times(1)).terminateUnderlyingAgreementDocument(unaDocument, terminatedDate);
        verify(underlyingAgreementService, times(1)).findUnderlyingAgreementEntity(accountId);
    }
}
