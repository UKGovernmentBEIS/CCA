package uk.gov.cca.api.underlyingagreement.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.service.SchemeTerminationHelper;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementDocument;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductStatus;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementDocumentRepository;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementRepository;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.cca.api.account.domain.AccountSearchKey.ACCOUNT_NAME;
import static uk.gov.cca.api.account.domain.AccountSearchKey.FACILITY_ID;
import static uk.gov.cca.api.account.domain.AccountSearchKey.POST_CODE;
import static uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus.LIVE;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementService {

    private final UnderlyingAgreementRepository underlyingAgreementRepository;
    private final UnderlyingAgreementDocumentRepository underlyingAgreementDocumentRepository;
    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    private final UnderlyingAgreementSchemeVersionsHelperService underlyingAgreementSchemeVersionsHelperService;
    private final SchemeTerminationHelper schemeTerminationHelper;

    /**
     * Submits a new UNA with new documents.
     *
     * @param container The una
     * @param accountId The account id
     * @param underlyingAgreementValidationContext UNA validation context
     * @return Persisted UNA
     */
    @Transactional
    public UnderlyingAgreementEntity submitUnderlyingAgreement(UnderlyingAgreementContainer container, Long accountId,
    		UnderlyingAgreementValidationContext underlyingAgreementValidationContext) {
        // Validate
        underlyingAgreementValidatorService.validate(container, underlyingAgreementValidationContext);

        activateFacilities(container);

        // Update status of variable energy product to 'LIVE'
        activateVariableEnergyProducts(container);

        // Create documents for applicable scheme version(s) and submit
        Set<SchemeVersion> schemeVersions = underlyingAgreementSchemeVersionsHelperService
        		.calculateSchemeVersionsFromActiveFacilities(container.getUnderlyingAgreement().getFacilities());
        
        UnderlyingAgreementEntity entity = UnderlyingAgreementEntity.builder()
        		.accountId(accountId)
        		.underlyingAgreementContainer(container)
        		.build();
        schemeVersions.forEach(version -> 
        	entity.addUnderlyingAgreementDocument(UnderlyingAgreementDocument.createUnderlyingAgreementDocument(version)));

        return underlyingAgreementRepository.save(entity);
    }

    /**
     * Updates the UNA and documents termination date if needed.
     * For true flag document versions are properly updated.
     *
     * @param newContainer The update UNA
     * @param accountId The account id
     * @param underlyingAgreementValidationContext UNA validation context
     */
    @Transactional
    public void updateUnderlyingAgreement(UnderlyingAgreementContainer newContainer, Long accountId,
                                          UnderlyingAgreementValidationContext underlyingAgreementValidationContext,
                                          boolean updateDocuments) {
        // Validate
        underlyingAgreementValidatorService.validate(newContainer, underlyingAgreementValidationContext);

        // Update status of facilities
        activateFacilities(newContainer);

        // Update status of variable energy product to 'LIVE'
        activateVariableEnergyProducts(newContainer);

        UnderlyingAgreementEntity entity = underlyingAgreementRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        // Get versions of documents to be changed
        Set<SchemeVersion> proposedActiveSchemeVersions = underlyingAgreementSchemeVersionsHelperService
                .calculateSchemeVersionsFromActiveFacilities(newContainer.getUnderlyingAgreement().getFacilities());

        // Persisted versions
        Set<SchemeVersion> persistedSchemeVersions = entity.getUnderlyingAgreementDocuments().stream()
                .map(UnderlyingAgreementDocument::getSchemeVersion)
                .collect(Collectors.toSet());

        // persistedSchemeVersions - proposedActiveSchemeVersions -> Update as terminated
        SetUtils.difference(persistedSchemeVersions, proposedActiveSchemeVersions).forEach(version -> {
            UnderlyingAgreementDocument doc = entity.getDocumentForSchemeVersion(version);
            terminateDocumentWithDate(doc, LocalDateTime.now());
        });

        // Update underlying agreement documents
        if(updateDocuments) {
            // proposedActiveSchemeVersions - persistedSchemeVersions -> Create new document
            SetUtils.difference(proposedActiveSchemeVersions, persistedSchemeVersions).forEach(version ->
                    entity.addUnderlyingAgreementDocument(UnderlyingAgreementDocument.createUnderlyingAgreementDocument(version)));

            // Find common between proposedActiveSchemeVersions and persistedSchemeVersions -> Update document
            SetUtils.intersection(persistedSchemeVersions, proposedActiveSchemeVersions).forEach(version ->
                    updateUnderlyingAgreementDocument(entity.getDocumentForSchemeVersion(version)));
        }

        // Update underlying agreement
        entity.setUnderlyingAgreementContainer(newContainer);
    }

	@Transactional
    public void saveFileDocumentUuid(final Long underlyingAgreementDocumentId, final String fileDocumentUuid) {
		underlyingAgreementDocumentRepository.updateFileDocumentUuid(underlyingAgreementDocumentId, fileDocumentUuid);
    }

    @Transactional
    public void updateFileDocumentById(final Long underlyingAgreementDocumentId, final String fileDocumentUuid) {
        UnderlyingAgreementDocument document = underlyingAgreementDocumentRepository.findById(underlyingAgreementDocumentId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        document.setFileDocumentUuid(fileDocumentUuid);
        updateUnderlyingAgreementDocument(document);
    }


    /**
     * A Utility method that constructs the search keywords for the account
     *
     * @param newAccountName Updated account name
     * @param unaContainer Updated UNA
     * @return Keywords
     */
    public Map<String, String> createSearchKeywordsForAccount(final String newAccountName, final UnderlyingAgreementContainer unaContainer) {
        final Map<String, String> postCodesByFacilityId = unaContainer.getUnderlyingAgreement().getFacilities().stream()
                .collect(Collectors.toMap(
                        f -> f.getFacilityItem().getFacilityId(),
                        f -> f.getFacilityItem().getFacilityDetails().getFacilityAddress().getPostcode()));

        return Stream.of(
                        Map.of(ACCOUNT_NAME.name(), newAccountName),
                        postCodesByFacilityId.keySet().stream().collect(Collectors.toMap(FACILITY_ID::concat, Function.identity())),
                        postCodesByFacilityId.entrySet().stream().collect(Collectors.toMap(entry -> POST_CODE.concat(entry.getKey()), Map.Entry::getValue)))
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Transactional
    public void terminateActiveUnaDocuments(Long accountId, LocalDateTime terminatedDate) {
        UnderlyingAgreementEntity entity = findUnderlyingAgreementEntity(accountId);

        entity.getUnderlyingAgreementDocuments()
                .stream()
                .forEach(doc -> terminateDocumentWithDate(doc, terminatedDate));
    }


	public void terminateUnderlyingAgreementDocument(UnderlyingAgreementDocument document, LocalDateTime terminatedDate) {
        document.setTerminatedDate(terminatedDate);
    }

	public UnderlyingAgreementEntity findUnderlyingAgreementEntity(Long accountId) {
		return underlyingAgreementRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
	}

    private void activateFacilities(UnderlyingAgreementContainer container) {
        container.getUnderlyingAgreement().getFacilities().forEach(f -> f.setStatus(LIVE));
    }

    private void activateVariableEnergyProducts(UnderlyingAgreementContainer container) {
        container.getUnderlyingAgreement().getFacilities().forEach(f -> Optional.ofNullable(f.getFacilityItem().getCca3BaselineAndTargets())
                .flatMap(baselineAndTargets -> Optional.ofNullable(baselineAndTargets.getFacilityBaselineEnergyConsumption()))
                .ifPresent(baselineEnergyConsumption -> baselineEnergyConsumption.getVariableEnergyConsumptionDataByProduct()
                        .forEach(p -> p.setProductStatus(ProductStatus.LIVE))));
    }

    private void updateUnderlyingAgreementDocument(UnderlyingAgreementDocument document) {
        if (document == null) {
            throw new BusinessException(RESOURCE_NOT_FOUND);
        }

        document.setActivationDate(LocalDateTime.now());
        document.setConsolidationNumber(document.getConsolidationNumber() + 1);
        document.setTerminatedDate(null);
    }
    
    private void terminateDocumentWithDate(UnderlyingAgreementDocument doc, LocalDateTime terminatedDate) {
		if(doc != null && doc.getTerminatedDate() == null) {
			LocalDateTime effectiveTerminationDate = schemeTerminationHelper.resolveTerminationDate(
					Set.of(doc.getSchemeVersion()), terminatedDate);
			terminateUnderlyingAgreementDocument(doc, effectiveTerminationDate);
		}
	}
}
