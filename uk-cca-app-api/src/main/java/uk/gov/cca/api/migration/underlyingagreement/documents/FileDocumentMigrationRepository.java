package uk.gov.cca.api.migration.underlyingagreement.documents;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.documents.domain.FileDocument;
import uk.gov.netz.api.files.documents.repository.FileDocumentRepository;

@Repository
public interface FileDocumentMigrationRepository extends FileDocumentRepository {
    
    @Transactional(readOnly = true)
    long countAllByStatus(FileStatus status);
    
    @Transactional(readOnly = true)
    @Query("select fd from FileDocument fd where fileName like :name%")
    List<FileDocument> searchByNameLike(String name);
    
    @Transactional(readOnly = true)
    List<FileDocument> findByStatus(FileStatus status);
    
}
