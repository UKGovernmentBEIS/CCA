package uk.gov.cca.api.underlyingagreement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDetailsDTO;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementQueryServiceTest {

    @InjectMocks
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Mock
    private UnderlyingAgreementRepository underlyingAgreementRepository;
    
    @Mock
    private FileDocumentService fileDocumentService;

    @Test
    void getUnderlyingAgreementContainerByAccountId() {
        final long accountId = 1L;

        UnderlyingAgreementEntity entity = new UnderlyingAgreementEntity();

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(entity));

        // Invoke
        underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId);

        // Verify
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void getUnderlyingAgreementContainerByAccountId_not_found() {
        final long accountId = 1L;

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
    }
    
    @Test
    void getUnderlyingAgreementByAccountId() {
        final long accountId = 1L;

        UnderlyingAgreementEntity entity = UnderlyingAgreementEntity.builder()
        		.accountId(accountId)
        		.underlyingAgreementContainer(UnderlyingAgreementContainer.builder().build())
        		.build();
        entity.setActivationDate(null);
        UnderlyingAgreementDTO expectedDto = UnderlyingAgreementDTO.builder()
        		.accountId(accountId)
        		.consolidationNumber(1)
        		.underlyingAgreementContainer(UnderlyingAgreementContainer.builder().build())
        		.build();

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(entity));

        // Invoke
        UnderlyingAgreementDTO actualDto = underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId);

        // Verify
        assertThat(expectedDto).isEqualTo(actualDto);
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void getUnderlyingAgreementByAccountId_not_found() {
        final long accountId = 1L;

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void getConsolidationNumber() {
        final long accountId = 1L;

        UnderlyingAgreementEntity entity = new UnderlyingAgreementEntity();

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(entity));

        // Invoke
        underlyingAgreementQueryService.getConsolidationNumber(accountId);

        // Verify
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void getConsolidationNumber_not_found() {
        final long accountId = 1L;

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                underlyingAgreementQueryService.getConsolidationNumber(accountId));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
    }
    
    @Test
    void getUnderlyingAgreementDetailsByAccountId() {
        final long accountId = 1L;
        final long unaId = 1L;

        String fileDocumentUuid = UUID.randomUUID().toString();
        final LocalDateTime activationDate = LocalDateTime.now();
        
        UnderlyingAgreementEntity entity = new UnderlyingAgreementEntity();
        entity.setId(unaId);
        entity.setFileDocumentUuid(fileDocumentUuid);
        entity.setActivationDate(activationDate);

        FileInfoDTO fileDocument = FileInfoDTO.builder()
                .name("Underlying agreement")
                .uuid(fileDocumentUuid)
                .build();
        
        when(underlyingAgreementRepository.findByAccountId(accountId)).thenReturn(Optional.of(entity));
        when(fileDocumentService.getFileInfoDTO(fileDocumentUuid)).thenReturn(fileDocument);
        
        UnderlyingAgreementDetailsDTO result = underlyingAgreementQueryService.getUnderlyingAgreementDetailsByAccountId(accountId);
        
        assertThat(result).isEqualTo(UnderlyingAgreementDetailsDTO.builder()
                .activationDate(activationDate.toLocalDate())
                .fileDocument(fileDocument)
                .id(unaId)
                .build());
        
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
        verify(fileDocumentService, times(1)).getFileInfoDTO(fileDocumentUuid);
    }

    @Test
    void getUnderlyingAgreementDetailsByAccountId_not_found() {
        final long accountId = 1L;

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                underlyingAgreementQueryService.getUnderlyingAgreementDetailsByAccountId(accountId));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
    }
    
    @Test
    void getUnderlyingAgreementDetailsByAccountId_no_document_exist() {
        final long accountId = 1L;

        final LocalDateTime activationDate = LocalDateTime.now();
        
        UnderlyingAgreementEntity entity = new UnderlyingAgreementEntity();
        entity.setActivationDate(activationDate);
        
        when(underlyingAgreementRepository.findByAccountId(accountId)).thenReturn(Optional.of(entity));
        
        UnderlyingAgreementDetailsDTO result = underlyingAgreementQueryService.getUnderlyingAgreementDetailsByAccountId(accountId);
        
        assertThat(result).isEqualTo(UnderlyingAgreementDetailsDTO.builder()
                .activationDate(activationDate.toLocalDate())
                .build());
        
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
        verifyNoInteractions(fileDocumentService);
    }

    @Test
    void getUnderlyingAgreementByIdAndFileDocumentUuid() {
        final long unaId = 1L;
        final String fileDocumentUuid = UUID.randomUUID().toString();

        UnderlyingAgreementEntity entity = UnderlyingAgreementEntity.builder()
                .underlyingAgreementContainer(UnderlyingAgreementContainer.builder().build())
                .build();
        entity.setActivationDate(null);
        
        UnderlyingAgreementDTO expectedDto = UnderlyingAgreementDTO.builder()
                .consolidationNumber(1)
                .underlyingAgreementContainer(UnderlyingAgreementContainer.builder().build())
                .build();

        when(underlyingAgreementRepository.findUnderlyingAgreementByIdAndFileDocumentUuid(unaId, fileDocumentUuid))
                .thenReturn(Optional.of(entity));

        // Invoke
        UnderlyingAgreementDTO actualDto = underlyingAgreementQueryService.getUnderlyingAgreementByIdAndFileDocumentUuid(unaId, fileDocumentUuid);

        // Verify
        assertNotNull(actualDto);
        assertThat(expectedDto).isEqualTo(actualDto);
        verify(underlyingAgreementRepository, times(1)).findUnderlyingAgreementByIdAndFileDocumentUuid(unaId, fileDocumentUuid);
    }

    @Test
    void getUnderlyingAgreementByIdAndFileDocumentUuid_not_found() {
        final long unaId = 1L;
        final String fileDocumentUuid = UUID.randomUUID().toString();

        when(underlyingAgreementRepository.findUnderlyingAgreementByIdAndFileDocumentUuid(unaId, fileDocumentUuid))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                underlyingAgreementQueryService.getUnderlyingAgreementByIdAndFileDocumentUuid(unaId, fileDocumentUuid));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(underlyingAgreementRepository, times(1)).findUnderlyingAgreementByIdAndFileDocumentUuid(unaId, fileDocumentUuid);
    }
    
    
    
}
