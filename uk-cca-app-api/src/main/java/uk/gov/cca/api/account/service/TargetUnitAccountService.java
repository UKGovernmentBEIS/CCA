package uk.gov.cca.api.account.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountContactType;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.account.transform.TargetUnitAccountMapper;
import uk.gov.netz.api.account.repository.AccountSearchRepository;
import uk.gov.netz.api.account.service.AccountSearchAdditionalKeywordService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Service
@AllArgsConstructor
public class TargetUnitAccountService {

    private final TargetUnitAccountRepository targetUnitAccountRepository;
    private final TargetUnitAccountMapper targetUnitAccountMapper;
    private final AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;
    private final AccountSearchRepository accountSearchRepository;

    @Transactional
    public TargetUnitAccountDTO createTargetUnitAccount(TargetUnitAccountDTO accountDTO, Long accountId, String businessId) {
        TargetUnitAccount account = targetUnitAccountMapper.toTargetUnitAccount(accountDTO, accountId);
        account.setBusinessId(businessId);

        //add account contacts
        account.addContact(targetUnitAccountMapper
                .toTargetUnitAccountContact(accountDTO.getAdministrativeContactDetails(), TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS));
        account.addContact(targetUnitAccountMapper
                .toTargetUnitAccountContact(accountDTO.getResponsiblePerson(), TargetUnitAccountContactType.RESPONSIBLE_PERSON));

        TargetUnitAccount accountSaved = targetUnitAccountRepository.save(account);

        accountSearchAdditionalKeywordService.storeKeywordsForAccount(accountSaved.getId(), accountSaved.getName(), accountSaved.getBusinessId());

        return targetUnitAccountMapper.toTargetUnitAccountDTO(accountSaved);
    }

    public TargetUnitAccountDetailsDTO getTargetUnitAccountDetailsById(Long accountId) {
        return targetUnitAccountRepository.findTargetUnitAccountById(accountId)
                .map(targetUnitAccountMapper::toTargetUnitAccountDetailsDTO)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional
    public void deleteTargetUnitAccount(Long accountId) {
        accountSearchRepository.deleteAllByAccountId(accountId);
        targetUnitAccountRepository.deleteById(accountId);
    }
}
