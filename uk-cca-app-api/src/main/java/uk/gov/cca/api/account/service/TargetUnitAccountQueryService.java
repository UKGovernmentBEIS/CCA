package uk.gov.cca.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountHeaderInfoDTO;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.account.transform.TargetUnitAccountMapper;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.TargetUnitAuthorityInfoProvider;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TargetUnitAccountQueryService implements TargetUnitAuthorityInfoProvider {

	private final TargetUnitAccountRepository repository;
	private final TargetUnitAccountMapper targetUnitAccountMapper;
	
	public List<TargetUnitAccountDTO> getAccountsByIds(List<Long> accountIds) {
        return repository.findAllByIdIn(accountIds)
            .stream()
            .map(targetUnitAccountMapper::toNoContactsTargetUnitAccountDTO)
            .collect(Collectors.toList());
    }

    public Set<Long> getSectorAssociationIdsByAccountIds(List<Long> accountIds) {
        return getAccountsByIds(accountIds).stream()
                .map(TargetUnitAccountDTO::getSectorAssociationId)
                .collect(Collectors.toSet());
    }

    public List<TargetUnitAccount> getAccounts(List<Long> accountIds) {
        return repository.findAllByIdIn(accountIds);
    }

    @Override
    public Long getAccountSectorAssociationId(Long accountId) {
        return getAccountById(accountId).getSectorAssociationId();
    }
    
    public String getAccountName(Long accountId) {
        final TargetUnitAccount targetUnitAccount = getAccountById(accountId);
        return targetUnitAccount.getName();
    }

    public String getAccountBusinessIdAndName(Long accountId) {
        final TargetUnitAccount targetUnitAccount = getAccountById(accountId);
        return targetUnitAccount.getBusinessId() + " - " + targetUnitAccount.getName();
    }

    @Override
    public List<Long> getAllTargetUnitAccountIdsBySectorAssociationId(Long sectorAssociationId) {
        return repository.findAllIdsBySectorAssociationId(sectorAssociationId);
    }

    public TargetUnitAccountHeaderInfoDTO getTargetUnitAccountHeaderInfo(Long accountId) {
        return targetUnitAccountMapper.toTargetUnitAccountHeaderInfoDTO(getAccountById(accountId));
    }
    
    public TargetUnitAccountBusinessInfoDTO getTargetUnitAccountBusinessInfoDTO(Long accountId) {
        return targetUnitAccountMapper.toTargetUnitAccountBusinessInfoDTO(getAccountById(accountId));
    }

    public TargetUnitAccount getAccountById(Long accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public List<NoticeRecipientDTO> getTargetUnitAccountNoticeRecipientsByAccountId(Long accountId) {
         return repository.findTargetUnitAccountNoticeRecipientsByAccountId(accountId);
    }
    
    public boolean isExistingTargetUnitAccount(String businessId) {
        return repository.existsByBusinessId(businessId);
    }
    
	public List<TargetUnitAccountBusinessInfoDTO> findAllTargetUnitAccountsActivatedBeforeWithStatusActiveOrTerminatedDuringActivatedYearOrTerminatedBetween(
			Long sectorAssociationId, LocalDateTime acceptedDate, LocalDateTime terminatedDateFrom,
			LocalDateTime terminatedDateTo) {
		return repository.findAllTargetUnitAccountsActivatedBeforeWithStatusActiveOrTerminatedBetween(
				sectorAssociationId, acceptedDate, terminatedDateFrom, terminatedDateTo);
	}
    		
}
