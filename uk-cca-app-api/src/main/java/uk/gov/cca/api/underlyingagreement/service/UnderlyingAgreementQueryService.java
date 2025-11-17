package uk.gov.cca.api.underlyingagreement.service;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.UnderlyingAgreementAuthorityInfoProvider;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementDocument;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDocumentDetailsDTO;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementDocumentRepository;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementRepository;
import uk.gov.cca.api.underlyingagreement.transform.UnderlyingAgreementMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementQueryService implements UnderlyingAgreementAuthorityInfoProvider {

    private final UnderlyingAgreementRepository underlyingAgreementRepository;
    private final UnderlyingAgreementDocumentRepository underlyingAgreementDocumentRepository;
    private final FileDocumentService fileDocumentService;

    private static final UnderlyingAgreementMapper UNA_MAPPER = Mappers.getMapper(UnderlyingAgreementMapper.class);

    public UnderlyingAgreementContainer getUnderlyingAgreementContainerByAccountId(Long accountId) {
        UnderlyingAgreementEntity entity = getUnderlyingAgreementEntityByAccountId(accountId);

        return entity.getUnderlyingAgreementContainer();
    }

    public UnderlyingAgreementDTO getUnderlyingAgreementByAccountId(final Long accountId) {
        return UNA_MAPPER.toUnderlyingAgreementDTO(
        		underlyingAgreementRepository.findByAccountId(accountId)
        			.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND))
        );
    }

    public Map<SchemeVersion, Integer> getConsolidationNumberMap(Long accountId) {
        UnderlyingAgreementEntity entity = getUnderlyingAgreementEntityByAccountId(accountId);

        return entity.getUnderlyingAgreementDocuments().stream()
        		.collect(Collectors.toMap(
        				UnderlyingAgreementDocument::getSchemeVersion, UnderlyingAgreementDocument::getConsolidationNumber));
    }

    public Map<SchemeVersion, Integer> getConsolidationNumberMapOfActiveSchemes(Long accountId) {
        UnderlyingAgreementEntity entity = getUnderlyingAgreementEntityByAccountId(accountId);

        return entity.getUnderlyingAgreementDocuments().stream()
                .filter(doc -> ObjectUtils.isEmpty(doc.getTerminatedDate()))
                .collect(Collectors.toMap(
                        UnderlyingAgreementDocument::getSchemeVersion, UnderlyingAgreementDocument::getConsolidationNumber));
    }

    @Transactional
    public UnderlyingAgreementDetailsDTO getUnderlyingAgreementDetailsByAccountId(Long accountId) {
        UnderlyingAgreementEntity entity = getUnderlyingAgreementEntityByAccountId(accountId);

        return UNA_MAPPER.toUnderlyingAgreementDetailsDTO(entity.getId(),
        		getUnaDocumentMap(entity.getUnderlyingAgreementDocuments()));
    }

	public UnderlyingAgreementDocument getUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(final Long unaId, final String fileDocumentUuid) {
    	return underlyingAgreementDocumentRepository.findUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(unaId, fileDocumentUuid)
    			.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    @Override
    public Long getUnderlyingAgreementAccountById(Long id) {
        return underlyingAgreementRepository.findUnderlyingAgreementAccountById(id)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    private Map<SchemeVersion, UnderlyingAgreementDocumentDetailsDTO> getUnaDocumentMap(List<UnderlyingAgreementDocument> underlyingAgreementDocuments) {
    	return underlyingAgreementDocuments.stream()
    	.collect(Collectors.toMap(UnderlyingAgreementDocument::getSchemeVersion,
    			doc -> UNA_MAPPER.toUnderlyingAgreementDocumentDetailsDTO(doc, getFileInfoDTO(doc.getFileDocumentUuid()))));
	}
    
    public UnderlyingAgreementEntity getUnderlyingAgreementEntityByAccountId(Long accountId) {
		return underlyingAgreementRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
	}

    private FileInfoDTO getFileInfoDTO(String uuid) {
    	return Optional.ofNullable(uuid).map(fileDocumentService::getFileInfoDTO).orElse(null);
    }
}
