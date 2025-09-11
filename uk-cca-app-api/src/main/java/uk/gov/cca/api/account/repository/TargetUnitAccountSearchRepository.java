package uk.gov.cca.api.account.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO;

@Repository
@Transactional(readOnly = true)
public interface TargetUnitAccountSearchRepository extends JpaRepository<TargetUnitAccount, Long> {

    @Query(value = "select distinct acc "
            + "from TargetUnitAccount acc "
            + "inner join AccountSearchAdditionalKeyword ask on ask.accountId = acc.id "
            + "where acc.sectorAssociationId in (:sectorIds) "
            + "and LOWER(ask.value) like CONCAT('%',:term,'%') ")
    Page<TargetUnitAccount> searchAccounts(PageRequest pageRequest, List<Long> sectorIds, String term);
    
    @Query(value = "select new uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO(tu.id, tu.businessId, tu.name, tu.status, VALUE(contacts)) "
            + "from TargetUnitAccount tu "
            + "left join tu.contacts contacts on KEY(contacts) = :contactType "
            + "where tu.sectorAssociationId = :sectorAssociationId ")
	Page<TargetUnitAccountInfoDTO> searchAccountsWithSiteContact(PageRequest pageRequest,
			Long sectorAssociationId, String contactType);

	@Query(value = "select new uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO(tu.id, tu.businessId, tu.name, tu.status, VALUE(contacts)) "
            + "from TargetUnitAccount tu "
            + "left join tu.contacts contacts on KEY(contacts) = :contactType "
            + "where tu.sectorAssociationId = :sectorAssociationId "
            + "and tu.id in (:accountsIds) ")
	Page<TargetUnitAccountInfoDTO> searchAccountsWithSiteContactAndAccountsIds(PageRequest pageRequest,
			Long sectorAssociationId, Set<Long> accountsIds, String contactType);

}
