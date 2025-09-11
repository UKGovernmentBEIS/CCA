package uk.gov.cca.api.subsistencefees.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.files.evidences.domain.FileEvidence;
import uk.gov.cca.api.files.evidences.service.FileEvidenceService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.token.FileToken;
import uk.gov.netz.api.token.UserFileTokenService;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesMoaFileEvidenceTokenServiceTest {

    @InjectMocks
    private SubsistenceFeesMoaFileEvidenceTokenService fileEvidenceTokenService;

    @Mock
    private SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;

    @Mock
    private FileEvidenceService fileEvidenceService;

    @Mock
    private UserFileTokenService userFileTokenService;

    @Test
    void generateGetFileEvidenceToken() {
        Long moaId = 1L;
        UUID evidenceFileUuid = UUID.randomUUID();
        FileToken fileToken = FileToken.builder()
                .token("token")
                .tokenExpirationMinutes(1L)
                .build();
        AppUser submitter = AppUser.builder().userId("userId").build();
        FileEvidence fileEvidence = FileEvidence.builder()
                .uuid(evidenceFileUuid.toString())
                .status(FileStatus.SUBMITTED)
                .createdBy(submitter.getUserId())
                .build();

        when(fileEvidenceService.getFileEvidenceByUuid(evidenceFileUuid.toString())).thenReturn(fileEvidence);
        when(subsistenceFeesMoaQueryService.getFileEvidenceFilesByMoaId(moaId)).thenReturn(Set.of(evidenceFileUuid));
        when(userFileTokenService.generateGetFileToken(evidenceFileUuid.toString())).thenReturn(fileToken);

        FileToken result = fileEvidenceTokenService.generateGetFileEvidenceToken(moaId, evidenceFileUuid, submitter);

        assertThat(result).isEqualTo(fileToken);
        verify(subsistenceFeesMoaQueryService, times(1)).getFileEvidenceFilesByMoaId(moaId);
        verify(fileEvidenceService, times(1)).getFileEvidenceByUuid(evidenceFileUuid.toString());
        verify(userFileTokenService, times(1)).generateGetFileToken(evidenceFileUuid.toString());
    }

    @Test
    void generateGetFileEvidenceToken_file_evidence_not_found() {
        Long moaId = 1L;
        UUID evidenceFileUuid = UUID.randomUUID();
        AppUser submitter = AppUser.builder().userId("userId").build();

        when(fileEvidenceService.getFileEvidenceByUuid(evidenceFileUuid.toString()))
                .thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, evidenceFileUuid.toString()));
        BusinessException be = assertThrows(BusinessException.class, () ->
            fileEvidenceTokenService.generateGetFileEvidenceToken(moaId, evidenceFileUuid, submitter));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(subsistenceFeesMoaQueryService, never()).getFileEvidenceFilesByMoaId(moaId);
        verify(fileEvidenceService, times(1)).getFileEvidenceByUuid(evidenceFileUuid.toString());
        verifyNoInteractions(userFileTokenService);
    }

    @Test
    void generateGetFileEvidenceToken_file_evidence_pending_with_error() {
        Long resourceId = 1L;
        UUID evidenceFileUuid = UUID.randomUUID();
        AppUser submitter = AppUser.builder().userId("userId").build();
        FileEvidence fileEvidence = FileEvidence.builder()
                .uuid(evidenceFileUuid.toString())
                .status(FileStatus.PENDING)
                .createdBy("userId2")
                .build();

        when(fileEvidenceService.getFileEvidenceByUuid(evidenceFileUuid.toString())).thenReturn(fileEvidence);
        BusinessException be = assertThrows(BusinessException.class, () ->
            fileEvidenceTokenService.generateGetFileEvidenceToken(resourceId, evidenceFileUuid, submitter));

        assertThat(be.getErrorCode()).isEqualTo(CcaErrorCode.SUBMITTER_HAS_NO_ACCESS_TO_FILE_EVIDENCE);

        verify(fileEvidenceService, times(1)).getFileEvidenceByUuid(evidenceFileUuid.toString());
        verifyNoInteractions(userFileTokenService);
    }

    @Test
    void generateGetFileEvidenceToken_evidence_not_valid() {
        Long resourceId = 1L;
        UUID evidenceFileUuid = UUID.randomUUID();
        AppUser submitter = AppUser.builder().userId("userId").build();
        FileEvidence fileEvidence = FileEvidence.builder()
                .uuid(evidenceFileUuid.toString())
                .status(FileStatus.SUBMITTED)
                .createdBy(submitter.getUserId())
                .build();

        when(fileEvidenceService.getFileEvidenceByUuid(evidenceFileUuid.toString())).thenReturn(fileEvidence);
        BusinessException be = assertThrows(BusinessException.class, () ->
            fileEvidenceTokenService.generateGetFileEvidenceToken(resourceId, evidenceFileUuid, submitter));

        assertThat(be.getErrorCode()).isEqualTo(CcaErrorCode.FILE_EVIDENCE_IS_NOT_RELATED_TO_SUBSISTENCE_FEES_MOA);

        verify(fileEvidenceService, never()).existFileEvidence(evidenceFileUuid.toString());
        verifyNoInteractions(userFileTokenService);
    }

    @Test
    void validateFileEvidenceResource() {
        Long moaId = 1L;
        UUID fileEvidenceUuid = UUID.randomUUID();
        Set<UUID> fileEvidenceFiles = Set.of(fileEvidenceUuid);

        when(subsistenceFeesMoaQueryService.getFileEvidenceFilesByMoaId(moaId)).thenReturn(fileEvidenceFiles);

        //invoke
        fileEvidenceTokenService.validateFileEvidenceResource(moaId, fileEvidenceUuid);

        verify(subsistenceFeesMoaQueryService, times(1)).getFileEvidenceFilesByMoaId(moaId);
    }

    @Test
    void validateFileEvidenceResource_throws_exception() {
        Long moaId = 1L;
        UUID fileEvidenceUuid = UUID.randomUUID();
        Set<UUID> fileEvidenceFiles = Set.of();

        when(subsistenceFeesMoaQueryService.getFileEvidenceFilesByMoaId(moaId)).thenReturn(fileEvidenceFiles);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> fileEvidenceTokenService.validateFileEvidenceResource(moaId, fileEvidenceUuid));
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.FILE_EVIDENCE_IS_NOT_RELATED_TO_SUBSISTENCE_FEES_MOA);

        verify(subsistenceFeesMoaQueryService, times(1)).getFileEvidenceFilesByMoaId(moaId);
    }
}
