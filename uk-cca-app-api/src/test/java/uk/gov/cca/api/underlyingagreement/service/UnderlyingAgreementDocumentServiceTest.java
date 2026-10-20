package uk.gov.cca.api.underlyingagreement.service;

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

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementDocument;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.documents.service.FileDocumentTokenService;
import uk.gov.netz.api.token.FileToken;


@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementDocumentServiceTest {
	
    @InjectMocks
    private UnderlyingAgreementDocumentService underlyingAgreementDocumentService;

    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Mock
    private FileDocumentTokenService fileDocumentTokenService;
    
    @Test
    void generateGetFileDocumentToken() {
        
        final Long unaId = 1L;
        final UUID fileDocumentUuid = UUID.randomUUID();

        final UnderlyingAgreementDocument underlyingAgreementDocument = UnderlyingAgreementDocument.builder()
            .fileDocumentUuid(fileDocumentUuid.toString())
            .build();

        final FileToken fileToken = FileToken.builder().token("token").build();
        
        when(underlyingAgreementQueryService.getUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(unaId, fileDocumentUuid.toString())).thenReturn(underlyingAgreementDocument);
        when(fileDocumentTokenService.generateGetFileDocumentToken(fileDocumentUuid.toString())).thenReturn(fileToken);

        final FileToken result = underlyingAgreementDocumentService.generateGetFileDocumentToken(unaId, fileDocumentUuid);

        assertEquals(result, fileToken);
        verify(underlyingAgreementQueryService, times(1)).getUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(unaId, fileDocumentUuid.toString());
        verify(fileDocumentTokenService, times(1)).generateGetFileDocumentToken(fileDocumentUuid.toString());
    }

    @Test
    void generateGetFileDocumentToken_uuid_not_found() {
        
        final Long unaId = 1L;
        final UUID fileDocumentUuid = UUID.randomUUID();

        when(underlyingAgreementQueryService.getUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(unaId, fileDocumentUuid.toString()))
            .thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> underlyingAgreementDocumentService.generateGetFileDocumentToken(unaId, fileDocumentUuid));

        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(underlyingAgreementQueryService, times(1)).getUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(unaId, fileDocumentUuid.toString());
        verifyNoInteractions(fileDocumentTokenService);
    }

}
