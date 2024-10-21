package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountIdentifierService;
import uk.gov.cca.api.account.service.TargetUnitAccountService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.netz.api.account.service.AccountIdentifierService;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountCreationServiceTest {

    @InjectMocks
    private TargetUnitAccountCreationService targetUnitAccountCreationService;

    @Mock
    private TargetUnitAccountService targetUnitAccountService;

    @Mock
    private TargetUnitAccountIdentifierService targetUnitAccountIdentifierService;

    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;

    @Mock
    private TargetUnitAccountCreationValidationService targetUnitAccountCreationValidationService;

    @Mock
    private AccountIdentifierService accountIdentifierService;

    @Test
    void createAccount() {
        final Long identifierId = 1L;
        final Long sectorAssociationId = 1L;
        final Long accountId = 1L;
        final String businessId = "SA-T00001";

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
                .id(identifierId)
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

        final String acronym = "SA";

        when(targetUnitAccountIdentifierService.incrementAndGet(sectorAssociationId)).thenReturn(identifierId);
        when(sectorAssociationQueryService.getSectorAssociationAcronymById(sectorAssociationId))
                .thenReturn(acronym);
        when(targetUnitAccountService.createTargetUnitAccount(accountDTO, accountId, businessId)).thenReturn(accountDTOSaved);
        when(accountIdentifierService.incrementAndGet()).thenReturn(accountId);

        TargetUnitAccountDTO result = targetUnitAccountCreationService.createAccount(accountDTO);

        assertThat(result).isEqualTo(accountDTOSaved);
        assertThat(result.getStatus()).isEqualTo(TargetUnitAccountStatus.NEW);

        verify(targetUnitAccountIdentifierService, times(1)).incrementAndGet(sectorAssociationId);
        verify(targetUnitAccountService, times(1)).createTargetUnitAccount(accountDTO, accountId, businessId);
        verify(targetUnitAccountCreationValidationService, times(1)).validate(accountDTO);
        verify(sectorAssociationQueryService, times(1)).getSectorAssociationAcronymById(sectorAssociationId);
        verify(accountIdentifierService, times(1)).incrementAndGet();
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
}