package uk.gov.cca.api.files.attachments.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.netz.api.files.attachments.transform.FileAttachmentMapper;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.io.IOException;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class CcaFileAttachmentService {

    private final FileAttachmentRepository fileAttachmentRepository;
    private static final FileAttachmentMapper FILE_ATTACHMENT_MAPPER = Mappers.getMapper(FileAttachmentMapper.class);

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

        this.fileAttachmentRepository.save(attachment);

        return attachment.getUuid();
    }
}
