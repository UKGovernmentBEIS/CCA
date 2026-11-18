package uk.gov.cca.api.files.attachments.service;

import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.files.attachments.repository.CcaFileAttachmentRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaFileAttachmentServiceTest {

    @InjectMocks
    private CcaFileAttachmentService ccaFileAttachmentService;

    @Mock
    private CcaFileAttachmentRepository ccaFileAttachmentRepository;

    @Test
    void getAllByFileNameLikeAndStatus() {
        final String prefixName = "prefix";
        final FileStatus status = FileStatus.PENDING_MIGRATION;
        final List<FileAttachment> attachments = List.of(
                FileAttachment.builder().uuid("uuid").fileName("name").build()
        );

        when(ccaFileAttachmentRepository.findAllByFileNameLikeAndStatus(prefixName, status))
                .thenReturn(attachments);

        // Invoke
        List<FileInfoDTO> result = ccaFileAttachmentService.getAllByFileNameLikeAndStatus(prefixName, status);

        // Verify
        assertThat(result).containsExactlyInAnyOrder(FileInfoDTO.builder().uuid("uuid").name("name").build());
        verify(ccaFileAttachmentRepository, times(1))
                .findAllByFileNameLikeAndStatus(prefixName, status);
    }

    @Test
    void createSystemFileAttachment() throws IOException {
        byte[] fileContent = "fileContent".getBytes();
        final FileDTO fileDTO = FileDTO.builder()
                .fileName("test")
                .fileContent(fileContent)
                .fileType("text/plain")
                .fileSize(fileContent.length)
                .build();
        final FileStatus status = FileStatus.SUBMITTED;
        final String createdBy = "createdBy";

        // Invoke
        String result = ccaFileAttachmentService.createSystemFileAttachment(fileDTO, status, createdBy);

        // Verify
        assertThat(result).isNotNull();
        verify(ccaFileAttachmentRepository, times(1)).save(any());
    }

    @Test
    void createSystemFileAttachments() throws IOException {
        final List<FileInfoDTO> files = List.of(FileInfoDTO.builder().uuid("uuid").build());
        final FileDTO fileDTO = FileDTO.builder()
                .fileName("name")
                .fileType("type")
                .fileContent("test".getBytes())
                .fileSize("test".length())
                .build();
        final FileStatus status = FileStatus.SUBMITTED;

        final FileAttachment attachment = FileAttachment.builder()
                .uuid("uuid")
                .fileType("type")
                .fileName("name")
                .fileContent("test".getBytes())
                .fileSize("test".length())
                .status(status)
                .createdBy("System")
                .build();

        // Invoke
        ccaFileAttachmentService.createSystemFileAttachments(files, fileDTO, status);

        // Verify
        verify(ccaFileAttachmentRepository, times(1)).saveAll(List.of(attachment));
    }

    @Test
    void updateMigrationFileStatusByName() {
        final String fileName = "name";
        final FileStatus status = FileStatus.SUBMITTED;

        final FileAttachment attachment = FileAttachment.builder()
                .uuid("uuid")
                .status(FileStatus.PENDING_MIGRATION)
                .build();

        when(ccaFileAttachmentRepository.findByFileNameAndStatus(fileName, FileStatus.PENDING_MIGRATION))
                .thenReturn(Optional.of(attachment));

        // Invoke
        ccaFileAttachmentService.updateMigrationFileStatusByName(fileName, status);

        // Verify
        assertThat(attachment.getStatus()).isEqualTo(status);
        verify(ccaFileAttachmentRepository, times(1))
                .findByFileNameAndStatus(fileName, FileStatus.PENDING_MIGRATION);
    }

    @Test
    void updateMigrationFileStatusByName_not_found() {
        final String fileName = "name";
        final FileStatus status = FileStatus.SUBMITTED;

        when(ccaFileAttachmentRepository.findByFileNameAndStatus(fileName, FileStatus.PENDING_MIGRATION))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                ccaFileAttachmentService.updateMigrationFileStatusByName(fileName, status));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(ccaFileAttachmentRepository, times(1))
                .findByFileNameAndStatus(fileName, FileStatus.PENDING_MIGRATION);
    }

    @Test
    void updateNameAndStatus() {
        final FileStatus status = FileStatus.SUBMITTED;
        final List<FileInfoDTO> files = List.of(
                FileInfoDTO.builder().uuid("uuid").name("changed").build()
        );

        final FileAttachment attachment = FileAttachment.builder()
                .uuid("uuid")
                .fileName("name")
                .status(FileStatus.PENDING_MIGRATION)
                .build();
        final List<FileAttachment> attachments = List.of(attachment);

        when(ccaFileAttachmentRepository.findFileAttachmentByUuidIn(List.of("uuid")))
                .thenReturn(attachments);

        // Invoke
        ccaFileAttachmentService.updateNameAndStatus(files, status);

        // Verify
        assertThat(attachment.getFileName()).isEqualTo("changed");
        assertThat(attachment.getStatus()).isEqualTo(status);
        verify(ccaFileAttachmentRepository, times(1))
                .findFileAttachmentByUuidIn(List.of("uuid"));
        verifyNoMoreInteractions(ccaFileAttachmentRepository);
    }

    @Test
    void updateNameAndStatus_with_more_params() {
        final int limit = 999;
        final FileStatus status = FileStatus.SUBMITTED;

        List<String> uuids = new ArrayList<>();
        List<FileInfoDTO> files = new ArrayList<>();
        List<FileAttachment> attachments = new ArrayList<>();

        IntStream.range(0, 2200).forEach(i -> {
            String uuid = UUID.randomUUID().toString();

            uuids.add(uuid);
            files.add(FileInfoDTO.builder().uuid(uuid).name("changed" + i).build());
            attachments.add(FileAttachment.builder().uuid(uuid).fileName("name" + i).status(FileStatus.PENDING_MIGRATION).build());
        });

        List<List<String>> uuidSublists = ListUtils.partition(uuids, limit);
        List<List<FileAttachment>> attachmentsSublists = ListUtils.partition(attachments, limit);

        when(ccaFileAttachmentRepository.findFileAttachmentByUuidIn(uuidSublists.getFirst()))
                .thenReturn(attachmentsSublists.getFirst());
        when(ccaFileAttachmentRepository.findFileAttachmentByUuidIn(uuidSublists.get(1)))
                .thenReturn(attachmentsSublists.get(1));
        when(ccaFileAttachmentRepository.findFileAttachmentByUuidIn(uuidSublists.get(2)))
                .thenReturn(attachmentsSublists.get(2));

        // Invoke
        ccaFileAttachmentService.updateNameAndStatus(files, status);

        // Verify
        assertThat(attachments.stream().allMatch(a -> a.getStatus().equals(FileStatus.SUBMITTED))).isTrue();
        assertThat(attachments.stream().noneMatch(a -> a.getFileName().contains("name"))).isTrue();
        verify(ccaFileAttachmentRepository, times(3))
                .findFileAttachmentByUuidIn(anyList());
        verifyNoMoreInteractions(ccaFileAttachmentRepository);
    }

    @Test
    void updateNameAndStatus_not_found() {
        final FileStatus status = FileStatus.SUBMITTED;
        final List<FileInfoDTO> files = List.of(
                FileInfoDTO.builder().uuid("uuid").name("changed").build()
        );

        when(ccaFileAttachmentRepository.findFileAttachmentByUuidIn(List.of("uuid")))
                .thenReturn(List.of(FileAttachment.builder().uuid("uuid2").build()));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                ccaFileAttachmentService.updateNameAndStatus(files, status));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(ccaFileAttachmentRepository, times(1))
                .findFileAttachmentByUuidIn(List.of("uuid"));
        verifyNoMoreInteractions(ccaFileAttachmentRepository);
    }
}
