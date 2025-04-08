package uk.gov.cca.api.underlyingagreement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;

@Repository
public interface UnderlyingAgreementRepository extends JpaRepository<UnderlyingAgreementEntity, Long> {

    @Transactional(readOnly = true)
    Optional<UnderlyingAgreementEntity> findByAccountId(Long accountId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(name = UnderlyingAgreementEntity.NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID)
    void updateFileDocumentUuid(@Param("id") Long id, @Param("fileDocumentUUid") String fileDocumentUUid);
    
    @Transactional(readOnly = true)
    Optional<UnderlyingAgreementEntity> findUnderlyingAgreementByIdAndFileDocumentUuid(Long id, String fileDocumentUuid);
    
    @Transactional(readOnly = true)
    @Query(name = UnderlyingAgreementEntity.NAMED_QUERY_FIND_UNDERLYING_AGREEMENT_ACCOUNT_BY_ID)
    Optional<Long> findUnderlyingAgreementAccountById(Long id);
    
    @Query("SELECT una FROM UnderlyingAgreementEntity una WHERE una.accountId = :accountId and exists (SELECT 1 FROM TargetUnitAccount acc WHERE una.accountId = acc.id AND acc.status = 'LIVE' AND acc.migrated = true) AND not exists (select 1 from Request req inner join RequestResource rr on req.id = rr.request.id where cast(una.accountId as string) = rr.resourceId AND req.type.id = 2 AND rr.resourceType = 'ACCOUNT')")
    Optional<UnderlyingAgreementEntity> findUnderlyingAgreementToMigrateRequestByAccountId(Long accountId);
}
