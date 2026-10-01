package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.TargetUnitAccountTemplateParams;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaDocumentTemplateAccountDataCollectFromAccountServiceTest {

    @InjectMocks
    private CcaDocumentTemplateAccountDataCollectFromAccountService ccaDocumentTemplateAccountDataCollectFromAccountService;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Mock
    private DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    @Test
    void collect() {
        final Long accountId = 1L;

        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder()
                .name("name")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .companyRegistrationNumber("registrationNumber")
                .businessId("Business Id")
                .address(AccountAddressDTO.builder()
                        .line1("Acc Line 1")
                        .line2("Acc Line 2")
                        .city("Acc City")
                        .county("Acc County")
                        .postcode("Acc code")
                        .country("GR")
                        .build())
                .responsiblePerson(TargetUnitAccountContactDTO.builder()
                        .firstName("First")
                        .lastName("Last")
                        .email("responsible@example.com")
                        .address(AccountAddressDTO.builder()
                                .line1("Res Line 1")
                                .line2("Res Line 2")
                                .city("Res City")
                                .county("Res County")
                                .postcode("Res code")
                                .country("GR")
                                .build())
                        .build())
                .build();
        final TargetUnitAccountTemplateParams expected = TargetUnitAccountTemplateParams.builder()
                .name("name")
                .companyRegistrationNumber("registrationNumber")
                .targetUnitIdentifier("Business Id")
                .targetUnitAddress("Acc Line 1\nAcc Line 2\nAcc City\nAcc code\nAcc County\nGreece")
                .primaryContact("First Last")
                .primaryContactEmail("responsible@example.com")
                .location("Res Line 1\nRes Line 2\nRes City\nRes code\nRes County\nGreece")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();

        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(documentTemplateTransformationMapper.constructAccountAddressDTO(accountDetails.getAddress()))
                .thenReturn("Acc Line 1\nAcc Line 2\nAcc City\nAcc code\nAcc County\nGreece");
        when(documentTemplateTransformationMapper.constructAccountAddressDTO(accountDetails.getResponsiblePerson().getAddress()))
                .thenReturn("Res Line 1\nRes Line 2\nRes City\nRes code\nRes County\nGreece");

        // Invoke
        final TargetUnitAccountTemplateParams result = ccaDocumentTemplateAccountDataCollectFromAccountService
                .collect(accountId);

        // Verify
        assertThat(result).isEqualTo(expected);
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(documentTemplateTransformationMapper, times(1))
                .constructAccountAddressDTO(accountDetails.getAddress());
        verify(documentTemplateTransformationMapper, times(1))
                .constructAccountAddressDTO(accountDetails.getResponsiblePerson().getAddress());
    }
}
