package uk.gov.cca.api.files.evidences.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.FileEntity;

@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@SequenceGenerator(name = "default_file_id_generator", sequenceName = "file_evidence_seq", allocationSize = 1)
@Table(name = "file_evidence")
@NamedQuery(
        name = FileEvidence.NAMED_QUERY_DELETE_EVIDENCE_FILES_BY_STATUS_AND_DATE_BEFORE,
        query = "delete from FileEvidence fileEvidence " +
                "where fileEvidence.status =: status " +
                "and fileEvidence.lastUpdatedOn < :expirationDate"
)
public class FileEvidence extends FileEntity {

    public static final String NAMED_QUERY_DELETE_EVIDENCE_FILES_BY_STATUS_AND_DATE_BEFORE = "FileEvidence.deleteEvidenceFilesByStatusAndDateBefore";
}
