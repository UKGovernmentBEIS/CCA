package uk.gov.cca.api.files.attachments.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.netz.api.files.common.domain.FileStatus;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface CcaFileAttachmentRepository extends FileAttachmentRepository {

    @Transactional
    Optional<FileAttachment> findByFileNameAndStatus(String fileName, FileStatus status);

    @Transactional
    List<FileAttachment> findFileAttachmentByUuidIn(List<String> uuids);

    @Query("select fa from FileAttachment fa where fa.status = :status and fa.fileName like %:name%")
    List<FileAttachment> findAllByFileNameLikeAndStatus(String name, FileStatus status);
}
