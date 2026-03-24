package uk.gov.cca.api.underlyingagreement.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementDocument;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementSchemeService {

	private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
	private final UnderlyingAgreementService underlyingAgreementService;
	
	@Transactional
    public void migrateUnderlyingAgreementToScheme(UnderlyingAgreementContainer newContainer, Long accountId,
                                                   UnderlyingAgreementValidationContext underlyingAgreementValidationContext) {
        // Validate
        underlyingAgreementValidatorService.validate(newContainer, underlyingAgreementValidationContext);

        UnderlyingAgreementEntity entity = underlyingAgreementService.findUnderlyingAgreementEntity(accountId);

        // Update underlying agreement and create new scheme document
        UnderlyingAgreementDocument unaDocument = UnderlyingAgreementDocument
                .createUnderlyingAgreementDocument(underlyingAgreementValidationContext.getSchemeVersion());
        entity.setUnderlyingAgreementContainer(newContainer);
        entity.addUnderlyingAgreementDocument(unaDocument);
    }
	
	@Transactional
    public void terminateUnaForSchemeVersion(Long accountId, SchemeVersion schemeVersion, LocalDateTime terminatedDate) {
        UnderlyingAgreementEntity entity = underlyingAgreementService.findUnderlyingAgreementEntity(accountId);

        terminateUnaDocumentsForSchemeVersion(schemeVersion, terminatedDate, entity);
        
        // Remove facilities associated with the scheme version
        entity.getUnderlyingAgreementContainer().getUnderlyingAgreement().getFacilities()
        	.removeIf(f ->
        	f.getFacilityItem()
        	.getFacilityDetails()
        	.getParticipatingSchemeVersions()
        	.equals(Set.of(schemeVersion))
        	);
    }
	
	@Transactional
    public void terminateUnaDocumentsForSchemeVersion(Long accountId, SchemeVersion schemeVersion, LocalDateTime terminatedDate) {
        UnderlyingAgreementEntity entity = underlyingAgreementService.findUnderlyingAgreementEntity(accountId);

        terminateUnaDocumentsForSchemeVersion(schemeVersion, terminatedDate, entity);
    }

	private void terminateUnaDocumentsForSchemeVersion(SchemeVersion schemeVersion, LocalDateTime terminatedDate,
			UnderlyingAgreementEntity entity) {

		entity.getUnderlyingAgreementDocuments()
                .stream()
                .filter(document -> document.getSchemeVersion().equals(schemeVersion) 
                		&& ObjectUtils.isEmpty(document.getTerminatedDate()))
                .forEach(document -> underlyingAgreementService.terminateUnderlyingAgreementDocument(document, terminatedDate));
	}
}
