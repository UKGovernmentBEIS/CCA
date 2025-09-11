package uk.gov.cca.api.account.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountContact;
import uk.gov.cca.api.account.domain.TargetUnitAccountContactType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountUpdateDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountResponsiblePersonDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountSicCodeDTO;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.account.transform.AccountAddressMapper;
import uk.gov.cca.api.account.transform.TargetUnitAccountMapper;
import uk.gov.netz.api.account.service.validator.AccountStatus;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.Optional;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@Validated
@RequiredArgsConstructor
public class TargetUnitAccountUpdateService {

    private final TargetUnitAccountRepository targetUnitAccountRepository;
    private final TargetUnitAccountMapper targetUnitAccountMapper;
    private final AccountAddressMapper accountAddressMapper;

    @Transactional
    @AccountStatus(expression = "{#status == 'NEW' || #status == 'LIVE'}")
    public void updateTargetUnitAccountSicCodes(Long accountId, UpdateTargetUnitAccountSicCodeDTO updateTargetUnitAccountDetails) {
        TargetUnitAccount account = targetUnitAccountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        account.setSicCodes(updateTargetUnitAccountDetails.getSicCodes());
    }

    @Transactional
    @AccountStatus(expression = "{#status == 'NEW' || #status == 'LIVE' || #status == 'TERMINATED'}")
    public void updateTargetUnitAccountFinancialIndependenceStatusCode(Long accountId,
                                                                       UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO updateFinancialIndependenceStatusCode) {
        TargetUnitAccount account = targetUnitAccountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        account.setFinancialIndependenceStatus(updateFinancialIndependenceStatusCode.getFinancialIndependenceStatus());
    }

    @Transactional
    @AccountStatus(expression = "{#status == 'NEW' || #status == 'LIVE'}")
    public void updateTargetUnitAccountResponsiblePerson(Long accountId, UpdateTargetUnitAccountResponsiblePersonDTO responsiblePerson) {
        TargetUnitAccount account = targetUnitAccountRepository.findTargetUnitAccountById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        account.getTargetUnitAccountContacts().stream()
                .filter(contact -> contact.getContactType().equals(TargetUnitAccountContactType.RESPONSIBLE_PERSON))
                .findFirst().ifPresent(contact -> {
                    contact.setJobTitle(responsiblePerson.getJobTitle());

                    // Update Phone
                    Optional.ofNullable(responsiblePerson.getPhoneNumber())
                            .ifPresentOrElse(phone -> {
                                        contact.setPhoneCode(phone.getCountryCode());
                                        contact.setPhoneNumber(phone.getNumber());
                                    },
                                    () -> {
                                        contact.setPhoneCode(null);
                                        contact.setPhoneNumber(null);
                                    });
                });
    }

    @Transactional
    @AccountStatus(expression = "{#status == 'NEW' || #status == 'LIVE'}")
    public void updateTargetUnitAccountAdministrativePerson(Long accountId, TargetUnitAccountContactDTO accountContact) {
        TargetUnitAccount account = targetUnitAccountRepository.findTargetUnitAccountById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        Optional<TargetUnitAccountContact> administrativeContact = account.getTargetUnitAccountContacts().stream()
                .filter(contact -> contact.getContactType().equals(TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS))
                .findFirst();

        administrativeContact.ifPresent(contact -> {
            account.removeContact(contact);

            TargetUnitAccountContact updated = targetUnitAccountMapper
                    .toTargetUnitAccountContact(accountContact, TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS);
            account.addContact(updated);
        });
    }
    
    @Transactional
    @AccountStatus(expression = "{#status == 'NEW'}")
    public void activateTargetUnitAccount(Long accountId,
            @Valid TargetUnitAccountUpdateDTO targetUnitAccountUpdateDTO,
            @PastOrPresent LocalDateTime activationDate) {

        TargetUnitAccount account = targetUnitAccountRepository.findTargetUnitAccountById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

    	// Update account details
    	updateAccountDetailsFromUnderlyingAgreement(targetUnitAccountUpdateDTO, account);

    	// Update account status
        account.setStatus(TargetUnitAccountStatus.LIVE);

        // Set Accepted Date
        account.setAcceptedDate(activationDate);
	}

    @Transactional
    public void updateTargetUnitAccountUponUnderlyingAgreementVariation(Long accountId, @Valid TargetUnitAccountUpdateDTO targetUnitAccountUpdateDTO) {

        TargetUnitAccount account = targetUnitAccountRepository.findTargetUnitAccountById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        // Update account details
        updateAccountDetailsFromUnderlyingAgreement(targetUnitAccountUpdateDTO, account);
    }

    @Transactional
    @AccountStatus(expression = "{#status == 'LIVE'}")
    public void handleTargetUnitAccountTerminated(Long accountId, LocalDateTime terminatedDate) {
        final TargetUnitAccount targetUnitAccount = targetUnitAccountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        // Update account status
        targetUnitAccount.setStatus(TargetUnitAccountStatus.TERMINATED);

        // Set termination date
        targetUnitAccount.setTerminatedDate(terminatedDate);
    }

    @Transactional
    @AccountStatus(expression = "{#status == 'NEW'}")
    public void handleTargetUnitAccountCancelled(Long accountId) {
        updateTargetUnitAccountStatus(accountId, TargetUnitAccountStatus.CANCELLED);
    }

    @Transactional
    @AccountStatus(expression = "{#status == 'NEW'}")
    public void handleTargetUnitAccountRejected(Long accountId) {
        updateTargetUnitAccountStatus(accountId, TargetUnitAccountStatus.REJECTED);
    }

    private void updateTargetUnitAccountStatus(Long accountId, TargetUnitAccountStatus status) {
        final TargetUnitAccount targetUnitAccount = targetUnitAccountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        targetUnitAccount.setStatus(status);
    }
    
    private void updateAccountDetailsFromUnderlyingAgreement(
            TargetUnitAccountUpdateDTO targetUnitAccountUpdateDTO, TargetUnitAccount account) {
		
    	account.setOperatorType(targetUnitAccountUpdateDTO.getOperatorType());
    	account.setName(targetUnitAccountUpdateDTO.getOperatorName());
    	account.setCompanyRegistrationNumber(targetUnitAccountUpdateDTO.getCompanyRegistrationNumber());
    	account.setRegistrationNumberMissingReason(targetUnitAccountUpdateDTO.getRegistrationNumberMissingReason());
    	if(targetUnitAccountUpdateDTO.getSubsectorAssociationId() != null) {
    	    account.setSubsectorAssociationId(targetUnitAccountUpdateDTO.getSubsectorAssociationId());
    	}
        accountAddressMapper.setAddress(account.getAddress(), targetUnitAccountUpdateDTO.getOperatorAddress());

    	account.getTargetUnitAccountContacts().stream()
        	.filter(contact -> contact.getContactType().equals(TargetUnitAccountContactType.RESPONSIBLE_PERSON))
        	.findFirst().ifPresent(contact -> {
        		contact.setFirstName(targetUnitAccountUpdateDTO.getResponsiblePerson().getFirstName());
        		contact.setLastName(targetUnitAccountUpdateDTO.getResponsiblePerson().getLastName());
        		contact.setEmail(targetUnitAccountUpdateDTO.getResponsiblePerson().getEmail());
                accountAddressMapper.setAddress(contact.getAddress(), targetUnitAccountUpdateDTO.getResponsiblePerson().getAddress());
        	});
	}
}
