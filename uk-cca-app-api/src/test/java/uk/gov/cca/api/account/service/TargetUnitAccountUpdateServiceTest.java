package uk.gov.cca.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountContact;
import uk.gov.cca.api.account.domain.TargetUnitAccountContactType;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountUpdateDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountResponsiblePersonDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountSicCodeDTO;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.account.transform.AccountAddressMapper;
import uk.gov.cca.api.account.transform.TargetUnitAccountMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountUpdateServiceTest {

    @InjectMocks
    private TargetUnitAccountUpdateService targetUnitAccountUpdateService;

    @Mock
    private TargetUnitAccountRepository targetUnitAccountRepository;

    @Mock
    private TargetUnitAccountMapper targetUnitAccountMapper;

    @Mock
    private AccountAddressMapper accountAddressMapper;

    @Test
    void updateTargetUnitAccountSicCode() {
        final long accountId = 1L;
        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(accountId)
                .status(TargetUnitAccountStatus.NEW)
                .sicCodes(List.of("000"))
                .build();

        final UpdateTargetUnitAccountSicCodeDTO sicCodeDTO = UpdateTargetUnitAccountSicCodeDTO.builder()
                .sicCodes(List.of("111"))
                .build();

        when(targetUnitAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        // Invoke
        targetUnitAccountUpdateService.updateTargetUnitAccountSicCodes(accountId, sicCodeDTO);

        // Verify
        assertThat(account.getSicCodes()).isEqualTo(List.of("111"));
        verify(targetUnitAccountRepository).findById(accountId);
    }

    @Test
    void updateTargetUnitAccountSicCode_not_found() {
        final long accountId = 1L;

        final UpdateTargetUnitAccountSicCodeDTO sicCodeDTO = UpdateTargetUnitAccountSicCodeDTO.builder()
                .sicCodes(List.of("111"))
                .build();

        when(targetUnitAccountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> targetUnitAccountUpdateService.updateTargetUnitAccountSicCodes(accountId, sicCodeDTO));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(targetUnitAccountRepository).findById(accountId);
    }

    @Test
    void updateTargetUnitAccountFinancialIndependenceStatusCode() {
        final long accountId = 1L;
        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(accountId)
                .status(TargetUnitAccountStatus.NEW)
                .financialIndependenceStatus(FinancialIndependenceStatus.FINANCIALLY_INDEPENDENT)
                .build();

        final UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO financialCodeDTO =
                UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO.builder()
                        .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                        .build();

        when(targetUnitAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        // Invoke
        targetUnitAccountUpdateService.updateTargetUnitAccountFinancialIndependenceStatusCode(accountId, financialCodeDTO);

        // Verify
        assertThat(account.getFinancialIndependenceStatus()).isEqualTo(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT);
        verify(targetUnitAccountRepository).findById(accountId);
    }

    @Test
    void updateTargetUnitAccountFinancialIndependenceStatusCode_not_found() {
        final long accountId = 1L;

        final UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO financialCodeDTO =
                UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO.builder()
                        .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                        .build();

        when(targetUnitAccountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> targetUnitAccountUpdateService.updateTargetUnitAccountFinancialIndependenceStatusCode(accountId, financialCodeDTO));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(targetUnitAccountRepository).findById(accountId);
    }

    @Test
    void updateTargetUnitAccountResponsiblePerson() {
        final long accountId = 1L;

        TargetUnitAccountContact responsibleContact = TargetUnitAccountContact.builder()
                .contactType(TargetUnitAccountContactType.RESPONSIBLE_PERSON)
                .jobTitle("Job1")
                .build();
        TargetUnitAccountContact administrativeContact = TargetUnitAccountContact.builder()
                .contactType(TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS)
                .jobTitle("Job2")
                .build();
        List<TargetUnitAccountContact> targetUnitAccountContacts = new ArrayList<>();
        targetUnitAccountContacts.add(responsibleContact);
        targetUnitAccountContacts.add(administrativeContact);

        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(accountId)
                .status(TargetUnitAccountStatus.LIVE)
                .targetUnitAccountContacts(targetUnitAccountContacts)
                .build();

        final UpdateTargetUnitAccountResponsiblePersonDTO updateResponsiblePersonDTO =
                UpdateTargetUnitAccountResponsiblePersonDTO.builder()
                        .jobTitle("Job change")
                        .build();

        when(targetUnitAccountRepository.findTargetUnitAccountById(accountId))
                .thenReturn(Optional.of(account));

        // Invoke
        targetUnitAccountUpdateService.updateTargetUnitAccountResponsiblePerson(accountId, updateResponsiblePersonDTO);

        // Verify
        assertThat(account.getTargetUnitAccountContacts().get(0).getJobTitle()).isEqualTo("Job change");
        assertThat(account.getTargetUnitAccountContacts().get(1).getJobTitle()).isEqualTo("Job2");
        verify(targetUnitAccountRepository).findTargetUnitAccountById(accountId);
    }

    @Test
    void updateTargetUnitAccountResponsiblePerson_not_found() {
        final long accountId = 1L;

        final UpdateTargetUnitAccountResponsiblePersonDTO updateResponsiblePersonDTO =
                UpdateTargetUnitAccountResponsiblePersonDTO.builder()
                        .jobTitle("Job")
                        .build();

        when(targetUnitAccountRepository.findTargetUnitAccountById(accountId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> targetUnitAccountUpdateService.updateTargetUnitAccountResponsiblePerson(accountId, updateResponsiblePersonDTO));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(targetUnitAccountRepository).findTargetUnitAccountById(accountId);
    }

    @Test
    void updateTargetUnitAccountAdministrativePerson() {
        final long accountId = 1L;

        TargetUnitAccountContact responsibleContact = TargetUnitAccountContact.builder()
                .id(1L)
                .contactType(TargetUnitAccountContactType.RESPONSIBLE_PERSON)
                .email("xx1@xx.gr")
                .build();
        TargetUnitAccountContact administrativeContact = TargetUnitAccountContact.builder()
                .id(2L)
                .contactType(TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS)
                .email("xx2@xx.gr")
                .build();
        List<TargetUnitAccountContact> targetUnitAccountContacts = new ArrayList<>();
        targetUnitAccountContacts.add(responsibleContact);
        targetUnitAccountContacts.add(administrativeContact);

        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(accountId)
                .status(TargetUnitAccountStatus.NEW)
                .targetUnitAccountContacts(targetUnitAccountContacts)
                .build();

        final TargetUnitAccountContactDTO updateAdministrativePersonDTO =
                TargetUnitAccountContactDTO.builder()
                        .email("change@xx.gr")
                        .build();
        TargetUnitAccountContact updated = TargetUnitAccountContact.builder()
                .contactType(TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS)
                .email("change@xx.gr")
                .build();

        when(targetUnitAccountRepository.findTargetUnitAccountById(accountId))
                .thenReturn(Optional.of(account));
        when(targetUnitAccountMapper.toTargetUnitAccountContact(updateAdministrativePersonDTO, TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS))
                .thenReturn(updated);

        // Invoke
        targetUnitAccountUpdateService.updateTargetUnitAccountAdministrativePerson(accountId, updateAdministrativePersonDTO);

        // Verify
        assertThat(account.getTargetUnitAccountContacts().get(0).getEmail()).isEqualTo("xx1@xx.gr");
        assertThat(account.getTargetUnitAccountContacts().get(1).getEmail()).isEqualTo("change@xx.gr");
        verify(targetUnitAccountMapper)
                .toTargetUnitAccountContact(updateAdministrativePersonDTO, TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS);
        verify(targetUnitAccountRepository).findTargetUnitAccountById(accountId);
    }

    @Test
    void updateTargetUnitAccountAdministrativePerson_not_found() {
        final long accountId = 1L;

        final TargetUnitAccountContactDTO updateAdministrativePersonDTO =
                TargetUnitAccountContactDTO.builder()
                        .email("change@xx.gr")
                        .build();

        when(targetUnitAccountRepository.findTargetUnitAccountById(accountId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> targetUnitAccountUpdateService.updateTargetUnitAccountAdministrativePerson(accountId, updateAdministrativePersonDTO));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(targetUnitAccountRepository).findTargetUnitAccountById(accountId);
    }

    @Test
    void handleTargetUnitAccountCancelled() {
        Long accountId = 1L;
        String businessId = "ADS_53-T00004";

        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(accountId)
                .status(TargetUnitAccountStatus.NEW)
                .businessId(businessId)
                .build();

        when(targetUnitAccountRepository.findById(accountId)).thenReturn(Optional.ofNullable(account));

        // invoke
        targetUnitAccountUpdateService.handleTargetUnitAccountCancelled(accountId);

        // assert
        assertThat(account.getStatus()).isEqualTo(TargetUnitAccountStatus.CANCELLED);
    }

    @Test
    void handleTargetUnitAccountTerminated() {
        Long accountId = 1L;
        String businessId = "ADS_53-T00004";

        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(accountId)
                .status(TargetUnitAccountStatus.LIVE)
                .businessId(businessId)
                .build();
        final LocalDateTime terminationDate = LocalDateTime.now();


        when(targetUnitAccountRepository.findById(accountId)).thenReturn(Optional.ofNullable(account));

        // invoke
        targetUnitAccountUpdateService.handleTargetUnitAccountTerminated(accountId, terminationDate);

        // assert
        assertThat(Objects.requireNonNull(account).getStatus()).isEqualTo(TargetUnitAccountStatus.TERMINATED);
    }

    @Test
    void handleTargetUnitAccountRejected(){
        Long accountId = 1L;
        String businessId = "ADS_53-T00004";

        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(accountId)
                .status(TargetUnitAccountStatus.LIVE)
                .businessId(businessId)
                .build();

        when(targetUnitAccountRepository.findById(accountId)).thenReturn(Optional.ofNullable(account));

        // invoke
        targetUnitAccountUpdateService.handleTargetUnitAccountRejected(accountId);

        // assert
        assertThat(Objects.requireNonNull(account).getStatus())
                .isEqualTo(TargetUnitAccountStatus.REJECTED);
    }

    @Test
    void activateTargetUnitAccount() {
        final long accountId = 1L;

        TargetUnitAccountContact responsibleContact = TargetUnitAccountContact.builder()
                .contactType(TargetUnitAccountContactType.RESPONSIBLE_PERSON)
                .firstName("fname")
                .lastName("lname")
                .email("email")
                .build();
        TargetUnitAccountContact administrativeContact = TargetUnitAccountContact.builder()
                .contactType(TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS)
                .firstName("fname")
                .lastName("lname")
                .email("email")
                .build();
        List<TargetUnitAccountContact> targetUnitAccountContacts = new ArrayList<>();
        targetUnitAccountContacts.add(responsibleContact);
        targetUnitAccountContacts.add(administrativeContact);

        AccountAddress address = AccountAddress.builder().city("city").build();
        AccountAddress newAddress = AccountAddress.builder().city("city_new").build();
        AccountAddressDTO updatedAddress = AccountAddressDTO.builder()
                .city("city_new")
                .build();

        LocalDateTime acceptedDate = LocalDateTime.now();
        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(accountId)
                .status(TargetUnitAccountStatus.NEW)
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .name("name")
                .companyRegistrationNumber("number")
                .address(address)
                .targetUnitAccountContacts(targetUnitAccountContacts)
                .acceptedDate(acceptedDate)
                .subsectorAssociationId(888L)
                .build();

        TargetUnitAccountUpdateDTO targetUnitDetails =
                TargetUnitAccountUpdateDTO.builder()
                        .companyRegistrationNumber("number_new")
                        .operatorName("name_new")
                        .operatorType(TargetUnitAccountOperatorType.SOLE_TRADER)
                        .operatorAddress(updatedAddress)
                        .responsiblePerson(TargetUnitAccountContactDTO.builder()
                                .email("email_new")
                                .firstName("fname_new")
                                .lastName("lname_new")
                                .build())
                        .subsectorAssociationId(999L)
                        .build();

        when(targetUnitAccountRepository.findTargetUnitAccountById(accountId)).thenReturn(Optional.of(account));

        doAnswer(invocation -> {
            account.setAddress(newAddress);
            return null;
        }).when(accountAddressMapper).setAddress(any(AccountAddress.class),any(AccountAddressDTO.class));

        // Invoke
        targetUnitAccountUpdateService.activateTargetUnitAccount(accountId, targetUnitDetails, acceptedDate);

        // Verify
        assertThat(Objects.requireNonNull(account).getStatus()).isEqualTo(TargetUnitAccountStatus.LIVE);
        assertThat(account.getName()).isEqualTo("name_new");
        assertThat(account.getOperatorType()).isEqualTo(TargetUnitAccountOperatorType.SOLE_TRADER);
        assertThat(account.getAddress().getCity()).isEqualTo("city_new");
        assertThat(account.getCompanyRegistrationNumber()).isEqualTo("number_new");
        assertThat(account.getTargetUnitAccountContacts().get(0).getFirstName()).isEqualTo("fname_new");
        assertThat(account.getTargetUnitAccountContacts().get(0).getLastName()).isEqualTo("lname_new");
        assertThat(account.getTargetUnitAccountContacts().get(0).getEmail()).isEqualTo("email_new");
        assertThat(account.getTargetUnitAccountContacts().get(1).getFirstName()).isEqualTo("fname");
        assertThat(account.getTargetUnitAccountContacts().get(1).getLastName()).isEqualTo("lname");
        assertThat(account.getTargetUnitAccountContacts().get(1).getEmail()).isEqualTo("email");
        assertThat(account.getSubsectorAssociationId()).isEqualTo(targetUnitDetails.getSubsectorAssociationId());
        verify(targetUnitAccountRepository).findTargetUnitAccountById(accountId);
    }


    @Test
    void updateTargetUnitAccountUponUnderlyingAgreementVariation() {


        final long accountId = 1L;

        TargetUnitAccountContact responsibleContact = TargetUnitAccountContact.builder()
                .contactType(TargetUnitAccountContactType.RESPONSIBLE_PERSON)
                .firstName("fname")
                .lastName("lname")
                .email("email")
                .build();
        TargetUnitAccountContact administrativeContact = TargetUnitAccountContact.builder()
                .contactType(TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS)
                .firstName("fname")
                .lastName("lname")
                .email("email")
                .build();
        List<TargetUnitAccountContact> targetUnitAccountContacts = new ArrayList<>();
        targetUnitAccountContacts.add(responsibleContact);
        targetUnitAccountContacts.add(administrativeContact);

        AccountAddress address = AccountAddress.builder().city("city").build();
        AccountAddress newAddress = AccountAddress.builder().city("city_new").build();
        AccountAddressDTO updatedAddress = AccountAddressDTO.builder()
                .city("city_new")
                .build();

        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(accountId)
                .status(TargetUnitAccountStatus.LIVE)
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .name("name")
                .companyRegistrationNumber("number")
                .address(address)
                .targetUnitAccountContacts(targetUnitAccountContacts)
                .acceptedDate(LocalDateTime.now())
                .build();

        TargetUnitAccountUpdateDTO targetUnitDetails =
                TargetUnitAccountUpdateDTO.builder()
                        .companyRegistrationNumber("number_new")
                        .operatorName("name_new")
                        .operatorType(TargetUnitAccountOperatorType.SOLE_TRADER)
                        .operatorAddress(updatedAddress)
                        .responsiblePerson(TargetUnitAccountContactDTO.builder()
                                .email("email_new")
                                .firstName("fname_new")
                                .lastName("lname_new")
                                .build())
                        .build();

        when(targetUnitAccountRepository.findTargetUnitAccountById(accountId)).thenReturn(Optional.of(account));

        doAnswer(invocation -> {
            account.setAddress(newAddress);
            return null;
        }).when(accountAddressMapper).setAddress(any(AccountAddress.class),any(AccountAddressDTO.class));

        // Invoke
        targetUnitAccountUpdateService.updateTargetUnitAccountUponUnderlyingAgreementVariation(accountId, targetUnitDetails);

        // Verify
        assertThat(account.getName()).isEqualTo("name_new");
        assertThat(account.getOperatorType()).isEqualTo(TargetUnitAccountOperatorType.SOLE_TRADER);
        assertThat(account.getAddress().getCity()).isEqualTo("city_new");
        assertThat(account.getCompanyRegistrationNumber()).isEqualTo("number_new");
        assertThat(account.getTargetUnitAccountContacts().get(0).getFirstName()).isEqualTo("fname_new");
        assertThat(account.getTargetUnitAccountContacts().get(0).getLastName()).isEqualTo("lname_new");
        assertThat(account.getTargetUnitAccountContacts().get(0).getEmail()).isEqualTo("email_new");
        assertThat(account.getTargetUnitAccountContacts().get(1).getFirstName()).isEqualTo("fname");
        assertThat(account.getTargetUnitAccountContacts().get(1).getLastName()).isEqualTo("lname");
        assertThat(account.getTargetUnitAccountContacts().get(1).getEmail()).isEqualTo("email");
        verify(targetUnitAccountRepository).findTargetUnitAccountById(accountId);
    }
}
