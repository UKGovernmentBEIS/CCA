package uk.gov.cca.api.web.orchestrator.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.CcaAccountSearchResultInfoDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountSearchCriteria;
import uk.gov.cca.api.account.service.TargetUnitAccountService;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationService;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDocumentDetailsDTO;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.web.orchestrator.account.dto.TargetUnitAccountDetailsResponseDTO;
import uk.gov.netz.api.account.domain.dto.AccountSearchResults;
import uk.gov.netz.api.account.search.criteria.AccountSearchCommonSortField;
import uk.gov.netz.api.account.search.criteria.AccountSearchContactFilter;
import uk.gov.netz.api.account.search.criteria.AccountSearchFilterCriteria;
import uk.gov.netz.api.account.search.service.AccountSearchQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountQueryServiceOrchestratorTest {

    @InjectMocks
    private TargetUnitAccountQueryServiceOrchestrator serviceOrchestrator;

    @Mock
    private TargetUnitAccountService targetUnitAccountService;

    @Mock
    private AccountSearchQueryService<TargetUnitAccount, CcaAccountSearchResultInfoDTO> ccaAccountSearchQueryService;

    @Mock
    private SubsectorAssociationService subsectorAssociationService;
    
    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Test
    void getTargetUnitAccountDetailsById() {
        final long accountId = 1L;
        final long subsectorId = 2L;
        final TargetUnitAccountDetailsDTO targetUnitAccountDetails = TargetUnitAccountDetailsDTO.builder()
                .id(accountId)
                .subsectorAssociationId(subsectorId)
                .build();
        final SubsectorAssociationDTO subsectorAssociation = SubsectorAssociationDTO.builder()
                .name("Name")
                .build();

        final TargetUnitAccountDetailsResponseDTO expected = TargetUnitAccountDetailsResponseDTO.builder()
                .targetUnitAccountDetails(targetUnitAccountDetails)
                .subsectorAssociation(subsectorAssociation)
                .build();

        when(targetUnitAccountService.getTargetUnitAccountDetailsById(accountId))
                .thenReturn(targetUnitAccountDetails);
        when(subsectorAssociationService.getSubsectorById(subsectorId))
                .thenReturn(subsectorAssociation);

        // Invoke
        TargetUnitAccountDetailsResponseDTO actual = serviceOrchestrator.getTargetUnitAccountDetailsById(accountId);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(targetUnitAccountService, times(1)).getTargetUnitAccountDetailsById(accountId);
        verify(subsectorAssociationService, times(1)).getSubsectorById(subsectorId);
    }

    @Test
    void getTargetUnitAccountDetailsById_no_subsector() {
        final long accountId = 1L;
        final TargetUnitAccountDetailsDTO targetUnitAccountDetails = TargetUnitAccountDetailsDTO.builder()
                .id(accountId)
                .build();

        final TargetUnitAccountDetailsResponseDTO expected = TargetUnitAccountDetailsResponseDTO.builder()
                .targetUnitAccountDetails(targetUnitAccountDetails)
                .subsectorAssociation(new SubsectorAssociationDTO())
                .build();

        when(targetUnitAccountService.getTargetUnitAccountDetailsById(accountId))
                .thenReturn(targetUnitAccountDetails);

        // Invoke
        TargetUnitAccountDetailsResponseDTO actual = serviceOrchestrator.getTargetUnitAccountDetailsById(accountId);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(targetUnitAccountService, times(1)).getTargetUnitAccountDetailsById(accountId);
        verifyNoInteractions(subsectorAssociationService);
    }

    @Test
    void getTargetUnitAccountDetailsById_new() {
        final long accountId = 1L;
        final TargetUnitAccountDetailsDTO targetUnitAccountDetails = TargetUnitAccountDetailsDTO.builder()
                .id(accountId)
                .status(TargetUnitAccountStatus.NEW)
                .build();

        final TargetUnitAccountDetailsResponseDTO expected = TargetUnitAccountDetailsResponseDTO.builder()
                .targetUnitAccountDetails(targetUnitAccountDetails)
                .subsectorAssociation(new SubsectorAssociationDTO())
                .build();

        when(targetUnitAccountService.getTargetUnitAccountDetailsById(accountId))
                .thenReturn(targetUnitAccountDetails);

        // Invoke
        TargetUnitAccountDetailsResponseDTO actual = serviceOrchestrator.getTargetUnitAccountDetailsById(accountId);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(targetUnitAccountService, times(1)).getTargetUnitAccountDetailsById(accountId);
        verifyNoInteractions(underlyingAgreementQueryService);
    }

    @Test
    void getTargetUnitAccountDetailsById_live() {
        final long accountId = 1L;
        final String UUID = "UUID";
        final String unaFilename = "Underlying Agreement";
        final TargetUnitAccountDetailsDTO targetUnitAccountDetails = TargetUnitAccountDetailsDTO.builder()
                .id(accountId)
                .status(TargetUnitAccountStatus.LIVE)
                .build();
        final UnderlyingAgreementDetailsDTO underlyingAgreementDetails = UnderlyingAgreementDetailsDTO.builder()
        		.underlyingAgreementDocumentMap(Map.of(SchemeVersion.CCA_2, UnderlyingAgreementDocumentDetailsDTO.builder()
        				.activationDate(LocalDate.of(2023, 11, 23))
        				.fileDocument(FileInfoDTO.builder().uuid(UUID).name(unaFilename).build())
        				.build()))
                .build();

        final TargetUnitAccountDetailsResponseDTO expected = TargetUnitAccountDetailsResponseDTO.builder()
                .targetUnitAccountDetails(targetUnitAccountDetails)
                .subsectorAssociation(new SubsectorAssociationDTO())
                .underlyingAgreementDetails(underlyingAgreementDetails)
                .build();

        when(targetUnitAccountService.getTargetUnitAccountDetailsById(accountId))
                .thenReturn(targetUnitAccountDetails);
        
        when(underlyingAgreementQueryService.getUnderlyingAgreementDetailsByAccountId(accountId))
        		.thenReturn(underlyingAgreementDetails);

        // Invoke
        TargetUnitAccountDetailsResponseDTO actual = serviceOrchestrator.getTargetUnitAccountDetailsById(accountId);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(targetUnitAccountService, times(1)).getTargetUnitAccountDetailsById(accountId);
        verify(underlyingAgreementQueryService, times(1)).getUnderlyingAgreementDetailsByAccountId(accountId);
    }

    @Test
    void searchUserAccounts() {
        final AppUser appUser = AppUser.builder().userId("id").build();
        final TargetUnitAccountSearchCriteria searchCriteria = TargetUnitAccountSearchCriteria.builder()
                .page(0).size(5).build();

        final AccountSearchFilterCriteria criteria = AccountSearchFilterCriteria.builder()
                .term(searchCriteria.getTerm())
                .paging(PagingRequest.builder()
                        .pageNumber(0)
                        .pageSize(5)
                        .build())
                .statuses(new HashSet<>())
                .sortField(AccountSearchCommonSortField.OPERATOR_NAME)
                .sortDirection(Sort.Direction.ASC)
                .build();
        final AccountSearchResults<CcaAccountSearchResultInfoDTO> accountSearchResults = AccountSearchResults.<CcaAccountSearchResultInfoDTO>builder()
                .accounts(List.of(
                        new CcaAccountSearchResultInfoDTO(1L, "Account_1", "business_id_1", TargetUnitAccountStatus.NEW.getName())
                ))
                .total(2L)
                .build();

        when(ccaAccountSearchQueryService.search(appUser, criteria, AccountSearchContactFilter.none()))
                .thenReturn(accountSearchResults);

        // Invoke
        serviceOrchestrator.searchUserAccounts(appUser, searchCriteria);

        // Verify
        verify(ccaAccountSearchQueryService, times(1)).search(appUser, criteria, AccountSearchContactFilter.none());
    }
}
