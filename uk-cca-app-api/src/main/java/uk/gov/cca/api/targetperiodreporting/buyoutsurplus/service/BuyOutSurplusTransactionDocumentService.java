package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.documents.service.FileDocumentTokenService;
import uk.gov.netz.api.token.FileToken;

import java.util.UUID;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusTransactionDocumentService {
    
    private final BuyOutSurplusQueryService buyOutSurplusQueryService;
    private final FileDocumentTokenService fileDocumentTokenService;
    
    @Transactional(readOnly = true)
    public FileToken generateGetFileDocumentToken(final Long id, final UUID fileDocumentUuid) {
        
        boolean exists = buyOutSurplusQueryService.existsBuyOutSurplusTransactionByIdAndDocumentId(id, fileDocumentUuid.toString());
        
        if (exists) {
            return fileDocumentTokenService.generateGetFileDocumentToken(fileDocumentUuid.toString());
        } else {
            throw new BusinessException(RESOURCE_NOT_FOUND);
        }
    }
}
