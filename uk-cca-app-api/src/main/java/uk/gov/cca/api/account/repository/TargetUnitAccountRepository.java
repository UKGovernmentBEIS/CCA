package uk.gov.cca.api.account.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO;
import uk.gov.netz.api.account.repository.AccountBaseRepository;

@Repository
public interface TargetUnitAccountRepository extends AccountBaseRepository<TargetUnitAccount> {

    @Transactional(readOnly = true)
    @EntityGraph(value = "target-unit-account-contacts-graph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<TargetUnitAccount> findTargetUnitAccountById(Long id);

    @Transactional(readOnly = true)
    List<TargetUnitAccount> findAllByIdIn(List<Long> ids);

    @Transactional(readOnly = true)
    @Query(name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNT_IDS_BY_SECTOR_ASSOCIATION)
    List<Long> findAllIdsBySectorAssociationId(Long sectorAssociationId);

    @Transactional(readOnly = true)
    List<TargetUnitAccount> findAllBySectorAssociationId(Long sectorAssociationId);
    
    @Transactional(readOnly = true)
    @Query(name = TargetUnitAccount.NAMED_QUERY_FIND_ACCOUNTS_BY_CONTACT_TYPE_AND_USER_ID_AND_SECTOR_ASSOCIATION)
    List<TargetUnitAccount> findTargetUnitAccountsByContactTypeAndUserIdAndSectorAsssociationId(String contactType, String userId, Long sectorAssociationId);

    @Transactional(readOnly = true)
    @Query(name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNTS_WITH_SITE_CONTACT_BY_SECTOR_ASSOCIATION)
    Page<TargetUnitAccountInfoDTO> findTargetUnitAccountsWithSiteContact(Pageable pageable,
    																     Long sectorAssociationId,
                                                                         String contactType);

    @Transactional(readOnly = true)
    @Query(value = "select distinct ask.accountId "
            + "from AccountSearchAdditionalKeyword ask "
            + "inner join TargetUnitAccount acc on ask.accountId = acc.id "
            + "where acc.sectorAssociationId in (:sectorIds) "
            + "and LOWER(ask.value) like CONCAT('%',:term,'%') "
            + "order by ask.accountId asc")
    Page<Long> searchDistinctAccountIdsByValue(Pageable pageable, List<Long> sectorIds, String term);


    @Transactional(readOnly = true)
    @Query(name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNT_NOTICE_RECIPIENTS_BY_ACCOUNT_ID)
    List<NoticeRecipientDTO> findTargetUnitAccountNoticeRecipientsByAccountId(Long accountId);
    
    @Transactional(readOnly = true)
    boolean existsByBusinessId(String businessId);
}
