package uk.gov.cca.api.account.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO;
import uk.gov.netz.api.account.repository.AccountBaseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface TargetUnitAccountRepository extends AccountBaseRepository<TargetUnitAccount> {

    @EntityGraph(value = "target-unit-account-contacts-graph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<TargetUnitAccount> findTargetUnitAccountById(Long id);

    List<TargetUnitAccount> findAllByIdIn(List<Long> ids);

    @Query(name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNT_IDS_BY_SECTOR_ASSOCIATION)
    List<Long> findAllIdsBySectorAssociationId(Long sectorAssociationId);

    @Query(name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNTS_BUSINESS_INFO_BY_SECTOR_ASSOCIATION_AND_STATUS)
    List<TargetUnitAccountBusinessInfoDTO> findAllTargetUnitAccountsBusinessInfoBySectorAssociationIdAndStatus(Long sectorAssociationId, TargetUnitAccountStatus status);

    List<TargetUnitAccount> findAllBySectorAssociationId(Long sectorAssociationId);
    
    @Query(name = TargetUnitAccount.NAMED_QUERY_FIND_ACCOUNTS_BY_CONTACT_TYPE_AND_USER_ID_AND_SECTOR_ASSOCIATION)
    List<TargetUnitAccount> findTargetUnitAccountsByContactTypeAndUserIdAndSectorAsssociationId(String contactType, String userId, Long sectorAssociationId);

    @Query(name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNTS_WITH_SITE_CONTACT_BY_SECTOR_ASSOCIATION)
    Page<TargetUnitAccountInfoDTO> findTargetUnitAccountsWithSiteContact(Pageable pageable,
    																     Long sectorAssociationId,
                                                                         String contactType);


    @Query(name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNTS_WITH_SITE_CONTACT_BY_SECTOR_ASSOCIATION_AND_ACCOUNTS_IDS)
    Page<TargetUnitAccountInfoDTO> findTargetUnitAccountWithSiteContactAndAccountsIds(
            Pageable pageable,
            Long sectorAssociationId,
            Set<Long> accountsIds,
            String contactType
    );

    @Query(value = "select distinct ask.accountId "
            + "from AccountSearchAdditionalKeyword ask "
            + "inner join TargetUnitAccount acc on ask.accountId = acc.id "
            + "where acc.sectorAssociationId in (:sectorIds) "
            + "and LOWER(ask.value) like CONCAT('%',:term,'%') "
            + "order by ask.accountId asc")
    Page<Long> searchDistinctAccountIdsByValue(Pageable pageable, List<Long> sectorIds, String term);


    @Query(name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNT_NOTICE_RECIPIENTS_BY_ACCOUNT_ID)
    List<NoticeRecipientDTO> findTargetUnitAccountNoticeRecipientsByAccountId(Long accountId);
    
    boolean existsByBusinessId(String businessId);

    @Transactional
    @Query("SELECT acc FROM TargetUnitAccount acc WHERE migrated = true AND acc.status = :status and acc.sectorAssociationId in (:sectorIds)")
    List<TargetUnitAccount> findMigratedTargetUnitAccountsByStatusAndSectorIdIn(TargetUnitAccountStatus status, List<Long> sectorIds);

    @Transactional
    @Query("SELECT acc FROM TargetUnitAccount acc WHERE migrated = true AND acc.status = :status")
    List<TargetUnitAccount> findMigratedTargetUnitAccountsByStatus(TargetUnitAccountStatus status);
    
    @Transactional(readOnly = true)
    @Query(name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNTS_ACTIVATED_BEFORE_WITH_STATUS_ACTIVE_OR_TERMINATED_BETWEEN)
	List<TargetUnitAccountBusinessInfoDTO> findAllTargetUnitAccountsActivatedBeforeWithStatusActiveOrTerminatedBetween(
			Long sectorAssociationId, LocalDateTime acceptedDate, LocalDateTime terminatedDateFrom,
			LocalDateTime terminatedDateTo);
}
