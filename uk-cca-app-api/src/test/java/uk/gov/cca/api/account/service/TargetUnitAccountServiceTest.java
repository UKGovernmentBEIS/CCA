package uk.gov.cca.api.account.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountContact;
import uk.gov.cca.api.account.domain.TargetUnitAccountContactType;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.account.transform.TargetUnitAccountMapper;
import uk.gov.netz.api.account.service.AccountSearchAdditionalKeywordService;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TargetUnitAccountServiceTest {

    @InjectMocks
    private TargetUnitAccountService targetUnitAccountService;

    @Mock
    private TargetUnitAccountRepository targetUnitAccountRepository;

    @Mock
    private TargetUnitAccountMapper targetUnitAccountMapper;

    @Mock
    private AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;

    @Test
    void createTargetUnitAccount() {
        Long accountId = 1L;
        Long sectorAssociationId = 1L;
        String businessId = "SA-T00001";

        TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
                .name("name")
                .subsectorAssociationId(1L)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCode("sicCode")
                .sectorAssociationId(sectorAssociationId)
                .address(AccountAddressDTO.builder().build())
                .administrativeContactDetails(getAdministrativeContactDetailsDTO())
                .responsiblePerson(getResponsiblePersonDTO())
                .status(TargetUnitAccountStatus.NEW)
                .businessId(businessId)
                .build();

        TargetUnitAccountDTO accountDTOSaved = TargetUnitAccountDTO.builder()
                .id(accountId)
                .name("name")
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCode("sicCode")
                .sectorAssociationId(sectorAssociationId)
                .address(AccountAddressDTO.builder().build())
                .administrativeContactDetails(getPersistedAdministrativeContactDetailsDTO())
                .responsiblePerson(getPersistedResponsiblePersonDTO())
                .status(TargetUnitAccountStatus.NEW)
                .build();

        TargetUnitAccount account = TargetUnitAccount.builder()
                .name("name")
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCode("sicCode")
                .sectorAssociationId(sectorAssociationId)
                .address(AccountAddress.builder().build())
                .targetUnitAccountContacts(getTargetUnitAccountContacts())
                .status(TargetUnitAccountStatus.NEW)
                .build();

        TargetUnitAccount accountSaved = TargetUnitAccount.builder()
                .id(accountId)
                .name("name")
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCode("sicCode")
                .sectorAssociationId(sectorAssociationId)
                .address(AccountAddress.builder().build())
                .targetUnitAccountContacts(getPersistedTargetUnitAccountContacts())
                .status(TargetUnitAccountStatus.NEW)
                .build();

        when(targetUnitAccountMapper.toTargetUnitAccount(accountDTO, accountId)).thenReturn(account);
        when(targetUnitAccountMapper.toTargetUnitAccountContact(accountDTO.getAdministrativeContactDetails(), TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS))
                .thenReturn(getAdministrativeContactDetails());
        when(targetUnitAccountMapper.toTargetUnitAccountContact(accountDTO.getResponsiblePerson(), TargetUnitAccountContactType.RESPONSIBLE_PERSON))
                .thenReturn(getResponsiblePerson());
        when(targetUnitAccountMapper.toTargetUnitAccountDTO(accountSaved)).thenReturn(accountDTOSaved);
        when(targetUnitAccountRepository.save(account)).thenReturn(accountSaved);

        // Invoke
        TargetUnitAccountDTO result = targetUnitAccountService.createTargetUnitAccount(accountDTO, accountId, businessId);

        // Verify
        assertThat(result).isEqualTo(accountDTOSaved);
        verify(targetUnitAccountMapper, times(1)).toTargetUnitAccount(accountDTO, accountId);
        verify(targetUnitAccountMapper, times(1))
                .toTargetUnitAccountContact(accountDTO.getAdministrativeContactDetails(), TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS);
        verify(targetUnitAccountMapper, times(1))
                .toTargetUnitAccountContact(accountDTO.getResponsiblePerson(), TargetUnitAccountContactType.RESPONSIBLE_PERSON);
        verify(targetUnitAccountMapper, times(1)).toTargetUnitAccountDTO(accountSaved);
        verify(targetUnitAccountRepository, times(1)).save(account);
        verify(accountSearchAdditionalKeywordService, times(1)).storeKeywordsForAccount(accountDTOSaved.getId(),
                accountDTOSaved.getName(), accountDTOSaved.getBusinessId());
    }

    @Test
    void getTargetUnitAccountDetailsById() {
        final long accountId = 1L;
        final TargetUnitAccount targetUnitAccount = TargetUnitAccount.builder()
                .id(accountId)
                .businessId("businessId")
                .status(TargetUnitAccountStatus.NEW)
                .name("Name")
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .build();
        final TargetUnitAccountDetailsDTO targetUnitAccountDTO = TargetUnitAccountDetailsDTO.builder()
                .id(accountId)
                .businessId("businessId")
                .status(TargetUnitAccountStatus.NEW)
                .name("Name")
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .build();

        when(targetUnitAccountRepository.findTargetUnitAccountById(accountId))
                .thenReturn(Optional.of(targetUnitAccount));
        when(targetUnitAccountMapper.toTargetUnitAccountDetailsDTO(targetUnitAccount))
                .thenReturn(targetUnitAccountDTO);

        // Invoke
        targetUnitAccountService.getTargetUnitAccountDetailsById(accountId);

        // Verify
        verify(targetUnitAccountRepository, times(1)).findTargetUnitAccountById(accountId);
        verify(targetUnitAccountMapper, times(1)).toTargetUnitAccountDetailsDTO(targetUnitAccount);
    }

    @Test
    void getTargetUnitAccountDetailsById_not_exist() {
        final long accountId = 1L;

        when(targetUnitAccountRepository.findTargetUnitAccountById(accountId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                targetUnitAccountService.getTargetUnitAccountDetailsById(accountId));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(targetUnitAccountRepository, times(1)).findTargetUnitAccountById(accountId);
    }

    @NotNull
    private static ArrayList<TargetUnitAccountContact> getTargetUnitAccountContacts() {
        return new ArrayList<>(List.of(TargetUnitAccountContact.builder()
                        .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                        .contactType(TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS)
                        .address(AccountAddress.builder().build()).email("email").build(),
                TargetUnitAccountContact.builder().jobTitle("jobTitle").firstName("firstName")
                        .lastName("lastName").contactType(TargetUnitAccountContactType.RESPONSIBLE_PERSON)
                        .address(AccountAddress.builder().build()).email("email").build()));
    }

    @NotNull
    private static ArrayList<TargetUnitAccountContact> getPersistedTargetUnitAccountContacts() {
        return new ArrayList<>(List.of(TargetUnitAccountContact.builder()
                        .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                        .contactType(TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS)
                        .address(AccountAddress.builder().build()).email("email")
                        .targetUnitAccount(TargetUnitAccount.builder().id(1L).build()).build(),
                TargetUnitAccountContact.builder().jobTitle("jobTitle").firstName("firstName")
                        .lastName("lastName").contactType(TargetUnitAccountContactType.RESPONSIBLE_PERSON)
                        .address(AccountAddress.builder().build()).email("email")
                        .targetUnitAccount(TargetUnitAccount.builder().id(1L).build()).build()));
    }

    @NotNull
    private static TargetUnitAccountContactDTO getAdministrativeContactDetailsDTO() {
        return TargetUnitAccountContactDTO.builder()
                .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                .address(AccountAddressDTO.builder().build()).email("email").build();
    }

    @NotNull
    private static TargetUnitAccountContactDTO getResponsiblePersonDTO() {
        return TargetUnitAccountContactDTO.builder()
                .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                .phoneNumber(PhoneNumberDTO.builder().countryCode("code").number("number").build()).build();
    }


    @NotNull
    private static TargetUnitAccountContactDTO getPersistedAdministrativeContactDetailsDTO() {
        return TargetUnitAccountContactDTO.builder()
                .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                .address(AccountAddressDTO.builder().build()).email("email")
                .build();
    }

    @NotNull
    private static TargetUnitAccountContactDTO getPersistedResponsiblePersonDTO() {
        return TargetUnitAccountContactDTO.builder()
                .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                .phoneNumber(PhoneNumberDTO.builder().countryCode("code").number("number").build())
                .address(AccountAddressDTO.builder().build()).email("email")
                .build();
    }

    @NotNull
    private static TargetUnitAccountContact getAdministrativeContactDetails() {
        return TargetUnitAccountContact.builder()
                .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                .contactType(TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS)
                .address(AccountAddress.builder().build()).email("email").build();
    }

    @NotNull
    private static TargetUnitAccountContact getResponsiblePerson() {
        return TargetUnitAccountContact.builder()
                .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                .contactType(TargetUnitAccountContactType.RESPONSIBLE_PERSON)
                .address(AccountAddress.builder().build()).email("email")
                .phoneNumber("123456789").build();
    }
}
