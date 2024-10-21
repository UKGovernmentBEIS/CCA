package uk.gov.cca.api.underlyingagreement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementRepository;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementServiceTest {

    @InjectMocks
    private UnderlyingAgreementService underlyingAgreementService;

    @Mock
    private UnderlyingAgreementRepository underlyingAgreementRepository;

    @Mock
    private UnderlyingAgreementValidatorService underlyingAgreementValidatorService;

    @Test
    void submitUnderlyingAgreement() {
        final long accountId = 1L;
        final MeasurementType measurementType = MeasurementType.ENERGY_GJ;
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
                .facilities(Set.of(Facility.builder().status(FacilityStatus.NEW).build()))
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .sectorMeasurementType(measurementType)
                .underlyingAgreement(underlyingAgreement)
                .build();

        // update facility status to 'LIVE' before save
        activateFacilities(container);

        final UnderlyingAgreementEntity entity = UnderlyingAgreementEntity
                .createUnderlyingAgreementEntity(container, accountId);


        when(underlyingAgreementRepository.save(entity)).thenReturn(entity);

        // Invoke
        underlyingAgreementService.submitUnderlyingAgreement(container, accountId);

        // Verify
        verify(underlyingAgreementValidatorService, times(1)).validate(container);
        verify(underlyingAgreementRepository, times(1)).save(entity);
        assertTrue(container.getUnderlyingAgreement().getFacilities().stream().allMatch(f -> f.getStatus().equals(FacilityStatus.LIVE)));
    }

    @Test
    void updateUnderlyingAgreement() {
        final long accountId = 1L;
        final MeasurementType measurementType = MeasurementType.ENERGY_GJ;
        final UnderlyingAgreementContainer newContainer = UnderlyingAgreementContainer.builder()
                .sectorMeasurementType(measurementType)
                .underlyingAgreement(UnderlyingAgreement.builder().build())
                .build();

        UnderlyingAgreementEntity persistentEntity = new UnderlyingAgreementEntity();

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(persistentEntity));

        // Invoke
        underlyingAgreementService.updateUnderlyingAgreement(newContainer, accountId);

        // Verify
        assertThat(persistentEntity.getUnderlyingAgreementContainer()).isEqualTo(newContainer);
        assertThat(persistentEntity.getMeasurementType()).isEqualTo(measurementType);
        assertThat(persistentEntity.getConsolidationNumber()).isPositive();
        verify(underlyingAgreementValidatorService, times(1)).validate(newContainer);
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void updateUnderlyingAgreement_not_found() {
        final long accountId = 1L;
        final MeasurementType measurementType = MeasurementType.ENERGY_GJ;
        final UnderlyingAgreementContainer newContainer = UnderlyingAgreementContainer.builder()
                .sectorMeasurementType(measurementType)
                .underlyingAgreement(UnderlyingAgreement.builder().build())
                .build();

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                underlyingAgreementService.updateUnderlyingAgreement(newContainer, accountId));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(underlyingAgreementValidatorService, times(1)).validate(newContainer);
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void setFileDocumentUuid() {
        final long underlyingAgreementId = 1L;
        final String fileDocumentUuid = UUID.randomUUID().toString();

        // Invoke
        underlyingAgreementService.saveFileDocumentUuid(underlyingAgreementId, fileDocumentUuid);

        // Verify
        verify(underlyingAgreementRepository, times(1))
                .updateFileDocumentUuid(underlyingAgreementId, fileDocumentUuid);
    }

    private void activateFacilities(UnderlyingAgreementContainer container) {
        container.getUnderlyingAgreement().getFacilities().forEach(f -> f.setStatus(FacilityStatus.LIVE));
    }
}
