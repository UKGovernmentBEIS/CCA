package uk.gov.cca.api.subsistencefees.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.files.documents.service.FileDocumentTokenService;
import uk.gov.netz.api.token.FileToken;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesMoaDocumentService {

	private final SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;
    private final FileDocumentTokenService fileDocumentTokenService;

    @Transactional(readOnly = true)
    public FileToken generateGetFileDocumentToken(final Long moaId, final UUID fileDocumentUuid) {
                
    	// Verify that MoA exists
    	subsistenceFeesMoaQueryService.getSubsistenceFeesMoaByIdAndFileDocumentUuid(moaId, fileDocumentUuid.toString());
        
        return fileDocumentTokenService.generateGetFileDocumentToken(fileDocumentUuid.toString());
    }
}
