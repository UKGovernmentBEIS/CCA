package uk.gov.cca.api.web.orchestrator.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountService;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationService;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDetailsDTO;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.web.orchestrator.account.dto.TargetUnitAccountDetailsResponseDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.time.LocalDate;

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
                .activationDate(LocalDate.of(2023, 11, 23))
                .fileDocument(FileInfoDTO.builder().uuid(UUID).name(unaFilename).build())
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

}
