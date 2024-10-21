package uk.gov.cca.api.underlyingagreement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementRepository;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.netz.api.common.exception.BusinessException;

import static uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus.LIVE;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementService {

    private final UnderlyingAgreementRepository underlyingAgreementRepository;
    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;

    @Transactional
    public Long submitUnderlyingAgreement(UnderlyingAgreementContainer container, Long accountId) {
        // Validate
        underlyingAgreementValidatorService.validate(container);

        activateFacilities(container);

        // Submit
        UnderlyingAgreementEntity entity = UnderlyingAgreementEntity
                .createUnderlyingAgreementEntity(container, accountId);

        UnderlyingAgreementEntity submittedEntity = underlyingAgreementRepository.save(entity);

        return submittedEntity.getId();
    }

    @Transactional
    public void updateUnderlyingAgreement(UnderlyingAgreementContainer newContainer, Long accountId) {
        // Validate
        underlyingAgreementValidatorService.validate(newContainer);

        UnderlyingAgreementEntity entity = underlyingAgreementRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        // Update underlying agreement
        entity.setUnderlyingAgreementContainer(newContainer);
        entity.setMeasurementType(newContainer.getSectorMeasurementType());

        doIncrementConsolidationNumber(entity);
    }

    @Transactional
    public void saveFileDocumentUuid(final Long underlyingAgreementId, final String fileDocumentUuid) {
        underlyingAgreementRepository.updateFileDocumentUuid(underlyingAgreementId, fileDocumentUuid);
    }

    private void doIncrementConsolidationNumber(UnderlyingAgreementEntity entity) {
        entity.setConsolidationNumber(entity.getConsolidationNumber() + 1);
    }

    private void activateFacilities(UnderlyingAgreementContainer container) {
        container.getUnderlyingAgreement().getFacilities().forEach(f -> f.setStatus(LIVE));
    }
}
