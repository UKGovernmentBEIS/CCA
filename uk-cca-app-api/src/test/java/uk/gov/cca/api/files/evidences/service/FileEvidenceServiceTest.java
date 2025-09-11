package uk.gov.cca.api.files.evidences.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.files.evidences.domain.FileEvidence;
import uk.gov.cca.api.files.evidences.repository.FileEvidenceRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.service.FileScanValidatorService;
import uk.gov.netz.api.files.common.service.FileValidatorService;
import uk.gov.netz.api.token.UserFileTokenService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileEvidenceServiceTest {

    @InjectMocks
    private FileEvidenceService fileEvidenceService;

    @Mock
    private DateService dateService;

    @Mock
    private FileEvidenceRepository fileEvidenceRepository;

    @Mock
    private UserFileTokenService userFileTokenService;

    @Mock
    private FileScanValidatorService fileScanValidator;

    @Spy
    private ArrayList<FileValidatorService> fileValidators;

    @BeforeEach
    void setUp() {
        fileValidators.add(fileScanValidator);
    }

    @Test
    void cleanUpUnusedFiles() {

        final LocalDateTime today = LocalDateTime.of(2022, 1, 2, 3, 4);
        final LocalDateTime yesterday = LocalDateTime.of(2022, 1, 1, 3, 4);

        when(dateService.getLocalDateTime()).thenReturn(today);

        fileEvidenceService.cleanUpUnusedFiles();

        verify(dateService, times(1)).getLocalDateTime();
        verify(fileEvidenceRepository, times(1)).deleteEvidenceFilesByStatusAndDateBefore(FileStatus.PENDING, yesterday);
    }

    @Test
    void createFileEvidence() throws IOException {
        AppUser user = AppUser.builder().firstName("firstName").userId("userId").lastName("lastName").build();
        byte[] contentBytes = "dummycontent".getBytes();
        FileDTO fileDTO = FileDTO.builder()
                .fileName("name")
                .fileSize(contentBytes.length)
                .fileType("application/pdf")
                .fileContent(contentBytes)
                .build();
        FileStatus status = FileStatus.PENDING;

        String fileEvidenceUuid = fileEvidenceService.createFileEvidence(fileDTO, user);

        assertThat(fileEvidenceUuid).isNotNull();
        ArgumentCaptor<FileEvidence> evidenceCaptor = ArgumentCaptor.forClass(FileEvidence.class);
        verify(fileEvidenceRepository, times(1)).save(evidenceCaptor.capture());
        FileEvidence evidenceFileCaptured = evidenceCaptor.getValue();
        assertThat(evidenceFileCaptured.getFileName()).isEqualTo(fileDTO.getFileName());
        assertThat(evidenceFileCaptured.getFileSize()).isEqualTo(fileDTO.getFileSize());
        assertThat(evidenceFileCaptured.getFileType()).isEqualTo(fileDTO.getFileType());
        assertThat(evidenceFileCaptured.getFileContent()).isEqualTo(contentBytes);
        assertThat(evidenceFileCaptured.getCreatedBy()).isEqualTo("userId");
        assertThat(evidenceFileCaptured.getStatus()).isEqualTo(status);
        assertThat(evidenceFileCaptured.getUuid()).isEqualTo(fileEvidenceUuid);

        verify(fileScanValidator, times(1)).validate(fileDTO);
    }

    @Test
    void getFileDTOByToken() {
        String getFileAttachmentToken = "token";
        String fileEvidenceUuid = "fileEvidenceUuid";
        FileEvidence fileEvidence = FileEvidence.builder()
                .fileName("file")
                .fileContent("content".getBytes())
                .fileSize(1L)
                .fileType("type")
                .build();

        when(userFileTokenService.resolveGetFileUuid(getFileAttachmentToken))
                .thenReturn(fileEvidenceUuid);
        when(fileEvidenceRepository.findByUuid(fileEvidenceUuid))
                .thenReturn(Optional.of(fileEvidence));

        FileDTO result = fileEvidenceService.getFileDTOByToken(getFileAttachmentToken);

        assertThat(result.getFileContent()).isEqualTo(fileEvidence.getFileContent());
        assertThat(result.getFileName()).isEqualTo(fileEvidence.getFileName());
        assertThat(result.getFileSize()).isEqualTo(fileEvidence.getFileSize());
        assertThat(result.getFileType()).isEqualTo(fileEvidence.getFileType());

        verify(userFileTokenService, times(1)).resolveGetFileUuid(getFileAttachmentToken);
        verify(fileEvidenceRepository, times(1)).findByUuid(fileEvidenceUuid);
    }

    @Test
    void getFileDTO() {
        String uuid = "uuid";
        FileEvidence fileEvidence = FileEvidence.builder()
                .fileName("name")
                .fileSize(121210)
                .fileType("application/pdf")
                .fileContent(new byte[]{})
                .build();

        when(fileEvidenceRepository.findByUuid(uuid)).thenReturn(Optional.of(fileEvidence));
        FileDTO fileDTO = fileEvidenceService.getFileDTO("uuid");

        assertThat(fileDTO.getFileName()).isEqualTo(fileEvidence.getFileName());
        assertThat(fileDTO.getFileType()).isEqualTo(fileEvidence.getFileType());
        assertThat(fileDTO.getFileContent()).isEqualTo(fileEvidence.getFileContent());
    }


    @Test
    void existsFileEvidences() {
        String uuid = "uuid";

        when(fileEvidenceRepository.countAllByUuidIn(Set.of(uuid))).thenReturn(1L);

        boolean result = fileEvidenceService.existsFileEvidences(Set.of(uuid));

        assertThat(result).isTrue();
    }

    @Test
    void submitFileEvidence() {
        String uuid = "uuid";
        FileEvidence fileEvidence = FileEvidence.builder()
                .uuid(uuid)
                .fileName("name")
                .fileSize(121210)
                .fileType("application/pdf")
                .fileContent(new byte[]{})
                .build();

        when(fileEvidenceRepository.findByUuid(uuid)).thenReturn(Optional.of(fileEvidence));

        fileEvidenceService.submitFileEvidence(uuid);

        verify(fileEvidenceRepository, times(1)).findByUuid(uuid);
    }
}
