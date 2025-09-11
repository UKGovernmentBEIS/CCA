package uk.gov.cca.api.files.evidences.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.files.evidences.domain.FileEvidence;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.repository.FileEntityRepository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface FileEvidenceRepository extends FileEntityRepository<FileEvidence, Long> {

    @Transactional
    @Modifying
    @Query(name = FileEvidence.NAMED_QUERY_DELETE_EVIDENCE_FILES_BY_STATUS_AND_DATE_BEFORE)
    void deleteEvidenceFilesByStatusAndDateBefore(FileStatus status, LocalDateTime expirationDate);

    @Transactional(readOnly = true)
    long countAllByUuidIn(Set<String> uuids);
}
