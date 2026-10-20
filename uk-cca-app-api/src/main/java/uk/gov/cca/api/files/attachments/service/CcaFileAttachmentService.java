package uk.gov.cca.api.files.attachments.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import uk.gov.cca.api.files.attachments.repository.CcaFileAttachmentRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.attachments.transform.FileAttachmentMapper;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@Validated
@RequiredArgsConstructor
public class CcaFileAttachmentService {

    private static final int PARAMETER_LIMIT = 999;

    private final CcaFileAttachmentRepository ccaFileAttachmentRepository;
    private static final FileAttachmentMapper FILE_ATTACHMENT_MAPPER = Mappers.getMapper(FileAttachmentMapper.class);

    /**
     * Get files with status PENDING_MIGRATION with name prefix.
     *
     * @param prefixName Prefix name
     * @return Files
     */
    public List<FileInfoDTO> getAllByFileNameLikeAndStatus(String prefixName, FileStatus status) {
        return ccaFileAttachmentRepository.findAllByFileNameLikeAndStatus(prefixName, status)
                .stream().map(a -> FileInfoDTO.builder().uuid(a.getUuid()).name(a.getFileName()).build())
                .toList();
    }

    /**
     * Creates a file attachment in DB. To be used only for files created by the system, as we are skipping validations on the files.
     *
     * @param fileDTO File to be saved
     * @param status Status
     * @param createdBy Created by
     * @return UUID
     * @throws IOException in case of an error
     */
    @Transactional
    public String createSystemFileAttachment(@Valid FileDTO fileDTO, FileStatus status, String createdBy) throws IOException {
        FileAttachment attachment = FILE_ATTACHMENT_MAPPER.toFileAttachment(fileDTO);
        attachment.setUuid(UUID.randomUUID().toString());
        attachment.setStatus(status);
        attachment.setCreatedBy(createdBy);

        this.ccaFileAttachmentRepository.save(attachment);

        return attachment.getUuid();
    }

    /**
     * Creates multiple file attachments in DB. To be used only for files created by the system, as we are skipping validations on the files.
     *
     * @param files Files to insert
     * @param placeholderFile The placeholder file with the content
     * @param status Status
     * @throws IOException In case of an error
     */
    @Transactional
    public void createSystemFileAttachments(List<FileInfoDTO> files, @Valid FileDTO placeholderFile, @NotNull FileStatus status) throws IOException {
        List<FileAttachment> attachments = new ArrayList<>();

        for (FileInfoDTO fileInfo : files) {
            FileAttachment attachment = FILE_ATTACHMENT_MAPPER.toFileAttachment(placeholderFile);
            attachment.setUuid(fileInfo.getUuid());
            attachment.setStatus(status);
            attachment.setCreatedBy("System");

            attachments.add(attachment);
        }

        this.ccaFileAttachmentRepository.saveAll(attachments);
    }

    /**
     * Updates only a migration file.
     *
     * @param fileName File name
     * @param status status to be updated
     * @return UUID
     */
    @Transactional
    public String updateMigrationFileStatusByName(@NotBlank String fileName, @NotNull FileStatus status) {
        FileAttachment attachment = this.ccaFileAttachmentRepository
                .findByFileNameAndStatus(fileName, FileStatus.PENDING_MIGRATION)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        attachment.setStatus(status);

        return attachment.getUuid();
    }

    @Transactional
    public void updateNameAndStatus(List<FileInfoDTO> files, @NotNull FileStatus status) {
        List<String> uuids = files.stream().map(FileInfoDTO::getUuid).toList();

        // Sublist Uuids for max Hibernate IN params
        List<List<String>> uuidSublists = ListUtils.partition(uuids, PARAMETER_LIMIT);
        List<FileAttachment> attachments = new ArrayList<>();

        uuidSublists.forEach(list ->
                attachments.addAll(this.ccaFileAttachmentRepository.findFileAttachmentByUuidIn(list)));

        files.forEach(f ->
                attachments.stream().filter(a -> a.getUuid().equals(f.getUuid()))
                        .findFirst().ifPresentOrElse(attachment -> {
                            attachment.setFileName(f.getName());
                            attachment.setStatus(status);
                        },
                        () -> {
                            throw new BusinessException(RESOURCE_NOT_FOUND);
                        })
        );
    }
}
