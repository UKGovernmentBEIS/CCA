package uk.gov.cca.api.underlyingagreement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementDocument;

@Repository
@Transactional(readOnly = true)
public interface UnderlyingAgreementDocumentRepository extends JpaRepository<UnderlyingAgreementDocument, Long> {

    Optional<UnderlyingAgreementDocument> findById(Long id);

	@Transactional
    @Modifying
    @Query(name = UnderlyingAgreementDocument.NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID)
    void updateFileDocumentUuid(@Param("id") Long id, @Param("fileDocumentUUid") String fileDocumentUUid);

	@Query("SELECT unaDocument FROM UnderlyingAgreementDocument unaDocument WHERE unaDocument.underlyingAgreementEntity.id = :unaId and unaDocument.fileDocumentUuid = :fileDocumentUuid")
    Optional<UnderlyingAgreementDocument> findUnderlyingAgreementDocumentByUnderlyingAgreementIdAndFileDocumentUuid(Long unaId, String fileDocumentUuid);
}
