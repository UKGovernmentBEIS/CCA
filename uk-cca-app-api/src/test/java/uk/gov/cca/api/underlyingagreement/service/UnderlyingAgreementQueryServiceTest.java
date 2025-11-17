package uk.gov.cca.api.underlyingagreement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementDocument;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDocumentDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDocumentDetailsDTO;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementDocumentRepository;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementQueryServiceTest {

    @InjectMocks
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Mock
    private UnderlyingAgreementRepository underlyingAgreementRepository;

    @Mock
    private UnderlyingAgreementDocumentRepository underlyingAgreementDocumentRepository;

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

        UnderlyingAgreementDocument doc = UnderlyingAgreementDocument.builder().build();
        UnderlyingAgreementEntity entity = UnderlyingAgreementEntity.builder()
                .accountId(accountId)
                .underlyingAgreementContainer(UnderlyingAgreementContainer.builder().build())
                .build();
        entity.addUnderlyingAgreementDocument(doc);

        UnderlyingAgreementDTO expectedDto = UnderlyingAgreementDTO.builder()
                .accountId(accountId)
                .underlyingAgreementContainer(UnderlyingAgreementContainer.builder().build())
                .underlyingAgreementDocuments(List.of(UnderlyingAgreementDocumentDTO.builder().build()))
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
    void getConsolidationNumberMap() {
        final long accountId = 1L;

        UnderlyingAgreementDocument cca2Doc = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(SchemeVersion.CCA_2);
        UnderlyingAgreementDocument cca3Doc = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(SchemeVersion.CCA_3);
        UnderlyingAgreementEntity entity = new UnderlyingAgreementEntity();
        entity.addUnderlyingAgreementDocument(cca2Doc);
        entity.addUnderlyingAgreementDocument(cca3Doc);
        Map<SchemeVersion, Integer> expectedMap = Map.of(SchemeVersion.CCA_2, 1, SchemeVersion.CCA_3, 1);

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(entity));

        // Invoke
        Map<SchemeVersion, Integer> result = underlyingAgreementQueryService.getConsolidationNumberMap(accountId);

        // Verify
        assertThat(result).isEqualTo(expectedMap);
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void getConsolidationNumberMapOfActiveSchemes() {
        final long accountId = 1L;

        UnderlyingAgreementDocument doc = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(SchemeVersion.CCA_2);
        UnderlyingAgreementEntity entity = new UnderlyingAgreementEntity();
        entity.addUnderlyingAgreementDocument(doc);

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(entity));

        // Invoke
        underlyingAgreementQueryService.getConsolidationNumberMapOfActiveSchemes(accountId);

        // Verify
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void getUnderlyingAgreementDetailsByAccountId() {
        final long accountId = 1L;
        final long unaId = 1L;

        String fileDocumentUuid = UUID.randomUUID().toString();
        final LocalDateTime activationDate = LocalDateTime.now();
        final LocalDateTime terminationDate = activationDate.plusDays(1);

        UnderlyingAgreementDocument cca2Doc = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(SchemeVersion.CCA_2);
        cca2Doc.setFileDocumentUuid(fileDocumentUuid);
        UnderlyingAgreementDocument cca3Doc = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(SchemeVersion.CCA_3);
        cca3Doc.setTerminatedDate(terminationDate);

        UnderlyingAgreementEntity entity = new UnderlyingAgreementEntity();
        entity.setId(unaId);
        entity.addUnderlyingAgreementDocument(cca2Doc);
        entity.addUnderlyingAgreementDocument(cca3Doc);

        FileInfoDTO fileDocument = FileInfoDTO.builder()
                .name("Underlying agreement")
                .uuid(fileDocumentUuid)
                .build();

        when(underlyingAgreementRepository.findByAccountId(accountId)).thenReturn(Optional.of(entity));
        when(fileDocumentService.getFileInfoDTO(fileDocumentUuid)).thenReturn(fileDocument);

        UnderlyingAgreementDetailsDTO result = underlyingAgreementQueryService.getUnderlyingAgreementDetailsByAccountId(accountId);

        assertThat(result).isEqualTo(UnderlyingAgreementDetailsDTO.builder()
                .underlyingAgreementDocumentMap(Map.of(SchemeVersion.CCA_2, UnderlyingAgreementDocumentDetailsDTO.builder()
                                .activationDate(activationDate.toLocalDate())
                                .fileDocument(fileDocument)
                                .build(),
                        SchemeVersion.CCA_3, UnderlyingAgreementDocumentDetailsDTO.builder()
                                .activationDate(activationDate.toLocalDate())
                                .terminatedDate(terminationDate.toLocalDate())
                                .build()))
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
        final long unaId = 2L;

        UnderlyingAgreementEntity entity = new UnderlyingAgreementEntity();
        entity.setId(unaId);

        when(underlyingAgreementRepository.findByAccountId(accountId)).thenReturn(Optional.of(entity));

        UnderlyingAgreementDetailsDTO result = underlyingAgreementQueryService.getUnderlyingAgreementDetailsByAccountId(accountId);

        assertThat(result).isEqualTo(UnderlyingAgreementDetailsDTO.builder()
                .id(unaId)
                .underlyingAgreementDocumentMap(Map.of())
                .build());

        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
        verifyNoInteractions(fileDocumentService);
    }

    @Test
    void getUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid() {
        final long unaId = 1L;
        final String fileDocumentUuid = UUID.randomUUID().toString();

        UnderlyingAgreementDocument doc = UnderlyingAgreementDocument.builder()
                .consolidationNumber(1)
                .build();

        when(underlyingAgreementDocumentRepository.findUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(unaId, fileDocumentUuid))
                .thenReturn(Optional.of(doc));

        // Invoke
        UnderlyingAgreementDocument result = underlyingAgreementQueryService.getUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(unaId, fileDocumentUuid);

        // Verify
        assertThat(result).isNotNull().isEqualTo(doc);
        verify(underlyingAgreementDocumentRepository, times(1)).findUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(unaId, fileDocumentUuid);
    }

    @Test
    void getUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid_not_found() {
        final long unaId = 1L;
        final String fileDocumentUuid = UUID.randomUUID().toString();

        when(underlyingAgreementDocumentRepository.findUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(unaId, fileDocumentUuid))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                underlyingAgreementQueryService.getUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(unaId, fileDocumentUuid));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(underlyingAgreementDocumentRepository, times(1)).findUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(unaId, fileDocumentUuid);
    }
}
