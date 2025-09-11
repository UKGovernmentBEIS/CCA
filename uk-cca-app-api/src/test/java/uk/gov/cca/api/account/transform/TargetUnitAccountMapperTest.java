package uk.gov.cca.api.account.transform;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountContact;
import uk.gov.cca.api.account.domain.TargetUnitAccountContactType;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountHeaderInfoDTO;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TargetUnitAccountMapperTest {

    private final TargetUnitAccountMapper mapper = Mappers.getMapper(TargetUnitAccountMapper.class);
    private final AccountAddressMapper accountAddressMapper = Mappers.getMapper(AccountAddressMapper.class);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(mapper, "accountAddressMapper", accountAddressMapper);
    }

    @Test
    void toTargetUnitAccount() {
        Long accountId = 1L;
        Long sectorAssociationId = 1L;

        TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
                .name("name")
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCodes(List.of("sicCode"))
                .sectorAssociationId(sectorAssociationId)
                .address(AccountAddressDTO.builder().build())
                .administrativeContactDetails(getAdministrativeContactDetailsDTO())
                .responsiblePerson(getResponsiblePersonDTO())
                .build();

        TargetUnitAccount result = mapper.toTargetUnitAccount(accountDTO, accountId);

        TargetUnitAccount account = TargetUnitAccount.builder()
                .name("name")
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCodes(List.of("sicCode"))
                .sectorAssociationId(sectorAssociationId)
                .address(AccountAddress.builder().build())
                .targetUnitAccountContacts(getTargetUnitAccountContacts())
                .build();

        assertThat(result).isEqualTo(account);
    }

    @Test
    void toTargetUnitAccountDTO() {
        Long sectorAssociationId = 1L;
        TargetUnitAccount account = TargetUnitAccount.builder()
                .name("name")
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCodes(List.of("sicCode"))
                .sectorAssociationId(sectorAssociationId)
                .address(AccountAddress.builder().build())
                .targetUnitAccountContacts(getTargetUnitAccountContacts())
                .build();

        TargetUnitAccountDTO result = mapper.toTargetUnitAccountDTO(account);

        TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
                .name("name")
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCodes(List.of("sicCode"))
                .sectorAssociationId(sectorAssociationId)
                .address(AccountAddressDTO.builder().build())
                .administrativeContactDetails(getAdministrativeContactDetailsDTO())
                .responsiblePerson(getResponsiblePersonDTO())
                .build();

        assertThat(result.getName()).isEqualTo(accountDTO.getName());
        assertThat(result.getEmissionTradingScheme()).isEqualTo(accountDTO.getEmissionTradingScheme());
        assertThat(result.getCompetentAuthority()).isEqualTo(accountDTO.getCompetentAuthority());
        assertThat(result.getOperatorType()).isEqualTo(accountDTO.getOperatorType());
        assertThat(result.getCompanyRegistrationNumber()).isEqualTo(accountDTO.getCompanyRegistrationNumber());
        assertThat(result.getSectorAssociationId()).isEqualTo(accountDTO.getSectorAssociationId());
    }

    @Test
    void toTargetUnitHeaderInfoDTO() {
        Long sectorAssociationId = 1L;
        TargetUnitAccount account = TargetUnitAccount.builder()
                .name("name")
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCodes(List.of("sicCode"))
                .sectorAssociationId(sectorAssociationId)
                .address(AccountAddress.builder().build())
                .targetUnitAccountContacts(getTargetUnitAccountContacts())
                .build();

        TargetUnitAccountHeaderInfoDTO result = mapper.toTargetUnitAccountHeaderInfoDTO(account);

        assertThat(result.getName()).isEqualTo(account.getName());
        assertThat(result.getBusinessId()).isEqualTo(account.getBusinessId());
        assertThat(result.getStatus()).isEqualTo(account.getStatus());
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
}
