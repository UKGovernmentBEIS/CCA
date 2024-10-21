package uk.gov.cca.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountContact;
import uk.gov.cca.api.account.domain.TargetUnitAccountContactType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountResponsiblePersonDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountSicCodeDTO;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.account.transform.AccountAddressMapper;
import uk.gov.cca.api.account.transform.TargetUnitAccountMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.netz.api.account.service.validator.AccountStatus;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Optional;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TargetUnitAccountUpdateService {

    private final TargetUnitAccountRepository targetUnitAccountRepository;
    private final TargetUnitAccountMapper targetUnitAccountMapper;
    private final AccountAddressMapper accountAddressMapper;

    @Transactional
    @AccountStatus(expression = "{#status == 'NEW' || #status == 'LIVE'}")
    public void updateTargetUnitAccountSicCode(Long accountId, UpdateTargetUnitAccountSicCodeDTO updateTargetUnitAccountDetails) {
        TargetUnitAccount account = targetUnitAccountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        account.setSicCode(updateTargetUnitAccountDetails.getSicCode());
    }

    @Transactional
    @AccountStatus(expression = "{#status == 'NEW' || #status == 'LIVE'}")
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
    public void updateTargetUnitAccountUponUnderlyingAgreementActivated(Long accountId,
			UnderlyingAgreementTargetUnitDetails underlyingAgreementTargetUnitDetails) {
    	TargetUnitAccount account = targetUnitAccountRepository.findTargetUnitAccountById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

    	// Update account details
    	updateAccountDetailsFromUnderlyingAgreement(underlyingAgreementTargetUnitDetails, account);

    	// Update account status
    	handleTargetUnitAccountActivated(accountId);
	}
    
    @AccountStatus(expression = "{#status == 'NEW'}")
    public void handleTargetUnitAccountActivated(Long accountId) {
        updateTargetUnitAccountStatus(accountId, TargetUnitAccountStatus.LIVE);
    }

    @Transactional
    @AccountStatus(expression = "{#status == 'NEW'}")
    public void handleTargetUnitAccountCancelled(Long accountId) {
        updateTargetUnitAccountStatus(accountId, TargetUnitAccountStatus.CANCELLED);
    }

    @Transactional
    @AccountStatus(expression = "{#status == 'LIVE'}")
    public void handleTargetUnitAccountTerminated(Long accountId) {
        updateTargetUnitAccountStatus(accountId, TargetUnitAccountStatus.TERMINATED);
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
			UnderlyingAgreementTargetUnitDetails underlyingAgreementTargetUnitDetails, TargetUnitAccount account) {
		
    	account.setOperatorType(underlyingAgreementTargetUnitDetails.getOperatorType());
    	account.setName(underlyingAgreementTargetUnitDetails.getOperatorName());
    	account.setCompanyRegistrationNumber(underlyingAgreementTargetUnitDetails.getCompanyRegistrationNumber());
    	account.setRegistrationNumberMissingReason(underlyingAgreementTargetUnitDetails.getRegistrationNumberMissingReason());
        accountAddressMapper.setAddress(account.getAddress(), underlyingAgreementTargetUnitDetails.getOperatorAddress());

    	account.getTargetUnitAccountContacts().stream()
        	.filter(contact -> contact.getContactType().equals(TargetUnitAccountContactType.RESPONSIBLE_PERSON))
        	.findFirst().ifPresent(contact -> {
        		contact.setFirstName(underlyingAgreementTargetUnitDetails.getResponsiblePersonDetails().getFirstName());
        		contact.setLastName(underlyingAgreementTargetUnitDetails.getResponsiblePersonDetails().getLastName());
        		contact.setEmail(underlyingAgreementTargetUnitDetails.getResponsiblePersonDetails().getEmail());
                accountAddressMapper.setAddress(contact.getAddress(), underlyingAgreementTargetUnitDetails.getResponsiblePersonDetails().getAddress());
        	});
	}
}
