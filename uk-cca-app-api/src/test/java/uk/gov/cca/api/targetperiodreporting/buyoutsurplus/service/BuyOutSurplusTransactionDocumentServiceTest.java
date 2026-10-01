package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.files.documents.service.storage.FileDocumentStorageService;
import uk.gov.netz.api.token.FileToken;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionDocumentServiceTest {
    
    @Mock
    private BuyOutSurplusQueryService buyOutSurplusQueryService;
    
    @Mock
    private FileDocumentStorageService fileDocumentStorageService;
    
    @InjectMocks
    private BuyOutSurplusTransactionDocumentService service;
    
    @Test
    void generateGetFileDocumentToken_ShouldReturnToken_WhenValidInput() {
        Long transactionId = 123L;
        UUID documentUuid = UUID.randomUUID();
        String documentUuidString = documentUuid.toString();
        FileToken expectedToken = new FileToken(documentUuidString, 1);
        
        when(buyOutSurplusQueryService.existsBuyOutSurplusTransactionByIdAndDocumentId(transactionId, documentUuidString))
                .thenReturn(true);
        when(fileDocumentStorageService.generateGetFileDocumentToken(documentUuidString))
                .thenReturn(expectedToken);
        
        FileToken result = service.generateGetFileDocumentToken(transactionId, documentUuid);
        
        verify(buyOutSurplusQueryService).existsBuyOutSurplusTransactionByIdAndDocumentId(transactionId, documentUuidString);
        verify(fileDocumentStorageService).generateGetFileDocumentToken(documentUuidString);
        assertEquals(expectedToken, result);
    }
}
