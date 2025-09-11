package uk.gov.cca.api.underlyingagreement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementRepository;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.cca.api.account.domain.AccountSearchKey.FACILITY_ID;
import static uk.gov.cca.api.account.domain.AccountSearchKey.POST_CODE;
import static uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus.LIVE;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementService {

    private final UnderlyingAgreementRepository underlyingAgreementRepository;
    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;

    @Transactional
    public UnderlyingAgreementEntity submitUnderlyingAgreement(UnderlyingAgreementContainer container, Long accountId, UnderlyingAgreementValidationContext underlyingAgreementValidationContext) {
        // Validate
        underlyingAgreementValidatorService.validate(container, underlyingAgreementValidationContext);

        activateFacilities(container);

        // Submit
        UnderlyingAgreementEntity entity = UnderlyingAgreementEntity
                .createUnderlyingAgreementEntity(container, accountId);

        return underlyingAgreementRepository.save(entity);
    }

    @Transactional
    public void updateUnderlyingAgreement(UnderlyingAgreementContainer newContainer, Long accountId, UnderlyingAgreementValidationContext underlyingAgreementValidationContext) {
        // Validate
        underlyingAgreementValidatorService.validate(newContainer, underlyingAgreementValidationContext);

        activateFacilities(newContainer);

        UnderlyingAgreementEntity entity = underlyingAgreementRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        // Update underlying agreement
        entity.setUnderlyingAgreementContainer(newContainer);
        entity.setActivationDate(LocalDateTime.now());

        doIncrementConsolidationNumber(entity);
    }

    @Transactional
    public void saveFileDocumentUuid(final Long underlyingAgreementId, final String fileDocumentUuid) {
        underlyingAgreementRepository.updateFileDocumentUuid(underlyingAgreementId, fileDocumentUuid);
    }

    /**
     * A Utility method that constructs the search keywords for the account
     *
     * @param unaContainer
     * @return
     */
    public Map<String, String> createSearchKeywordsForAccount(final UnderlyingAgreementContainer unaContainer) {
        final Map<String, String> postCodesByFacilityId = unaContainer.getUnderlyingAgreement().getFacilities().stream()
                .collect(Collectors.toMap(
                        f -> f.getFacilityItem().getFacilityId(),
                        f -> f.getFacilityItem().getFacilityDetails().getFacilityAddress().getPostcode()));

        return Stream.of(
                        postCodesByFacilityId.keySet().stream().collect(Collectors.toMap(FACILITY_ID::concat, Function.identity())),
                        postCodesByFacilityId.entrySet().stream().collect(Collectors.toMap(entry -> POST_CODE.concat(entry.getKey()), Map.Entry::getValue)))
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void doIncrementConsolidationNumber(UnderlyingAgreementEntity entity) {
        entity.setConsolidationNumber(entity.getConsolidationNumber() + 1);
    }

    private void activateFacilities(UnderlyingAgreementContainer container) {
        container.getUnderlyingAgreement().getFacilities().forEach(f -> f.setStatus(LIVE));
    }
}
