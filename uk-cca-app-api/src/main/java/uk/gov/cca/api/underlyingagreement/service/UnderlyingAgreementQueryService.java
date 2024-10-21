package uk.gov.cca.api.underlyingagreement.service;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.Optional;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.UnderlyingAgreementAuthorityInfoProvider;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDetailsDTO;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementRepository;
import uk.gov.cca.api.underlyingagreement.transform.UnderlyingAgreementMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.documents.service.FileDocumentService;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementQueryService implements UnderlyingAgreementAuthorityInfoProvider {

    private final UnderlyingAgreementRepository underlyingAgreementRepository;
    private final FileDocumentService fileDocumentService;
    
    private static final UnderlyingAgreementMapper UNA_MAPPER = Mappers.getMapper(UnderlyingAgreementMapper.class);

    public UnderlyingAgreementContainer getUnderlyingAgreementContainerByAccountId(Long accountId) {
        UnderlyingAgreementEntity entity = underlyingAgreementRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        return entity.getUnderlyingAgreementContainer();
    }
    
    public UnderlyingAgreementDTO getUnderlyingAgreementByAccountId(final Long accountId) {
        return UNA_MAPPER.toUnderlyingAgreementDTO(
        		underlyingAgreementRepository.findByAccountId(accountId)
        			.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND))
        );
    }

    public int getConsolidationNumber(Long accountId) {
        UnderlyingAgreementEntity entity = underlyingAgreementRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        return entity.getConsolidationNumber();
    }
    
    public UnderlyingAgreementDetailsDTO getUnderlyingAgreementDetailsByAccountId(Long accountId) {
        UnderlyingAgreementEntity entity = underlyingAgreementRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        return UNA_MAPPER.toUnderlyingAgreementInfoDTO(entity, 
                Optional.ofNullable(entity.getFileDocumentUuid()).map(fileDocumentService::getFileInfoDTO).orElse(null));
    }
    
    public UnderlyingAgreementDTO getUnderlyingAgreementByIdAndFileDocumentUuid(final Long id, final String fileDocumentUuid) {
        return UNA_MAPPER.toUnderlyingAgreementDTO(
                underlyingAgreementRepository.findUnderlyingAgreementByIdAndFileDocumentUuid(id, fileDocumentUuid)
                        .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND)));
    }

    @Override
    public Long getUnderlyingAgreementAccountById(Long id) {
        return underlyingAgreementRepository.findUnderlyingAgreementAccountById(id)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
}
