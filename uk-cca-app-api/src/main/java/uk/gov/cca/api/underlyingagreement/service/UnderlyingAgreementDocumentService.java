package uk.gov.cca.api.underlyingagreement.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import uk.gov.netz.api.files.documents.service.storage.FileDocumentStorageService;
import uk.gov.netz.api.token.FileToken;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementDocumentService {
    
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final FileDocumentStorageService fileDocumentStorageService;

    public FileToken generateGetFileDocumentToken(final Long underlyingAgreementId, final UUID fileDocumentUuid) {
                
        //Validate existence of Underlying Agreement document
        underlyingAgreementQueryService.getUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(
        		underlyingAgreementId, fileDocumentUuid.toString());
        
        return fileDocumentStorageService.generateGetFileDocumentToken(fileDocumentUuid.toString());
    }

}
