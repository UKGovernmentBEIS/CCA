package uk.gov.cca.api.sectorassociation.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.netz.api.files.common.repository.FileEntityRepository;

import java.util.Optional;

@Repository
public interface SectorAssociationSchemeDocumentRepository extends FileEntityRepository<SectorAssociationSchemeDocument, Long> {

    @Transactional(readOnly = true)
    boolean existsByUuid(String uuid);

    @Override
    @Transactional(readOnly = true)
    @Query(value = "SELECT doc.id, doc.uuid, doc.file_name, doc.file_content, doc.file_size, doc.file_type, doc.status, doc.created_by, doc.last_updated_on FROM sector_association_scheme_document doc WHERE doc.uuid = ?1", nativeQuery = true)
    Optional<SectorAssociationSchemeDocument> findByUuid(String uuid);
}
