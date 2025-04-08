package uk.gov.cca.api.migration.underlyingagreement.attachments;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.netz.api.files.common.domain.FileStatus;

@Repository
public interface FileAttachmentMigrationRepository extends FileAttachmentRepository {
    
    @Transactional(readOnly = true)
    long countAllByStatus(FileStatus status);
    
    @Transactional(readOnly = true)
    @Query("select fa from FileAttachment fa where fileName like :name%")
    List<FileAttachment> searchByNameLike(String name);
}
