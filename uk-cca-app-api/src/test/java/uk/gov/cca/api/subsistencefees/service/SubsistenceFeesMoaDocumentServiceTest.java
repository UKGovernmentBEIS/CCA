package uk.gov.cca.api.subsistencefees.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.documents.service.FileDocumentTokenService;
import uk.gov.netz.api.token.FileToken;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesMoaDocumentServiceTest {

	@InjectMocks
    private SubsistenceFeesMoaDocumentService subsistenceFeesMoaDocumentService;

    @Mock
    private SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;
    
    @Mock
    private FileDocumentTokenService fileDocumentTokenService;
    
    @Test
    void generateGetFileDocumentToken() {  
        final Long unaId = 1L;
        final UUID fileDocumentUuid = UUID.randomUUID();
        
        final FileToken fileToken = FileToken.builder().token("token").build();
        final SubsistenceFeesMoa moa = SubsistenceFeesMoa.builder().build();
        
        when(subsistenceFeesMoaQueryService.getSubsistenceFeesMoaByIdAndFileDocumentUuid(unaId, fileDocumentUuid.toString())).thenReturn(moa);
        when(fileDocumentTokenService.generateGetFileDocumentToken(fileDocumentUuid.toString())).thenReturn(fileToken);

        final FileToken result = subsistenceFeesMoaDocumentService.generateGetFileDocumentToken(unaId, fileDocumentUuid);

        assertEquals(result, fileToken);
        verify(subsistenceFeesMoaQueryService, times(1)).getSubsistenceFeesMoaByIdAndFileDocumentUuid(unaId, fileDocumentUuid.toString());
        verify(fileDocumentTokenService, times(1)).generateGetFileDocumentToken(fileDocumentUuid.toString());
    }

    @Test
    void generateGetFileDocumentToken_uuid_not_found() {
        final Long unaId = 1L;
        final UUID fileDocumentUuid = UUID.randomUUID();

        when(subsistenceFeesMoaQueryService.getSubsistenceFeesMoaByIdAndFileDocumentUuid(unaId, fileDocumentUuid.toString()))
            .thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> subsistenceFeesMoaDocumentService.generateGetFileDocumentToken(unaId, fileDocumentUuid));

        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(subsistenceFeesMoaQueryService, times(1)).getSubsistenceFeesMoaByIdAndFileDocumentUuid(unaId, fileDocumentUuid.toString());
        verifyNoInteractions(fileDocumentTokenService);
    }
}
