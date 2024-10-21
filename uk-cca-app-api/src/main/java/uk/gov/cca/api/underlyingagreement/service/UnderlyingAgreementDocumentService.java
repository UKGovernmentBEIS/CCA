package uk.gov.cca.api.underlyingagreement.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.files.documents.service.FileDocumentTokenService;
import uk.gov.netz.api.token.FileToken;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementDocumentService {
    
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final FileDocumentTokenService fileDocumentTokenService;

    public FileToken generateGetFileDocumentToken(final Long underlyingAgreementId, final UUID fileDocumentUuid) {
                
        //Validate existence of Underlying Agreement
        underlyingAgreementQueryService.getUnderlyingAgreementByIdAndFileDocumentUuid(underlyingAgreementId, fileDocumentUuid.toString());
        
        return fileDocumentTokenService.generateGetFileDocumentToken(fileDocumentUuid.toString());
    }

}
