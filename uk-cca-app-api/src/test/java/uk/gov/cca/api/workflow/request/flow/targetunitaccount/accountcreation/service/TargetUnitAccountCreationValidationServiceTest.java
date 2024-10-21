package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeService;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.exception.CcaErrorCode.TARGET_UNIT_ACCOUNT_SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountCreationValidationServiceTest {

    @InjectMocks
    private TargetUnitAccountCreationValidationService targetUnitAccountCreationValidationService;

    @Mock
    private SectorAssociationSchemeService sectorAssociationSchemeService;

    @Test
    void validate() {
        final long subsectorAssociationId = 2L;
        final long sectorAssociationId = 1L;
        final List<Long> subsectorAssociationIds = List.of(2L);
        final TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
                .sectorAssociationId(sectorAssociationId)
                .subsectorAssociationId(subsectorAssociationId)
                .build();

        when(sectorAssociationSchemeService.getSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId))
                .thenReturn(subsectorAssociationIds);

        // Invoke
        targetUnitAccountCreationValidationService.validate(accountDTO);

        // Verify
        verify(sectorAssociationSchemeService, times(1))
                .getSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId);
    }

    @Test
    void validate_empty_subsector_valid() {
        final long sectorAssociationId = 1L;
        final List<Long> subsectorAssociationIds = List.of();
        final TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
                .sectorAssociationId(sectorAssociationId)
                .build();

        when(sectorAssociationSchemeService.getSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId))
                .thenReturn(subsectorAssociationIds);

        // Invoke
        targetUnitAccountCreationValidationService.validate(accountDTO);

        // Verify
        verify(sectorAssociationSchemeService, times(1))
                .getSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId);
    }

    @Test
    void validate_false_subsector_invalid() {
        final long subsectorAssociationId = 2L;
        final long sectorAssociationId = 1L;
        final List<Long> subsectorAssociationIds = List.of(3L);
        final TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
                .sectorAssociationId(sectorAssociationId)
                .subsectorAssociationId(subsectorAssociationId)
                .build();

        when(sectorAssociationSchemeService.getSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId))
                .thenReturn(subsectorAssociationIds);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> targetUnitAccountCreationValidationService.validate(accountDTO));

        // Verify
        assertThat(businessException.getErrorCode())
                .isEqualTo(TARGET_UNIT_ACCOUNT_SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION);
        verify(sectorAssociationSchemeService, times(1))
                .getSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId);
    }

    @Test
    void validate_no_subsector_declared_invalid() {
        final long sectorAssociationId = 1L;
        final List<Long> subsectorAssociationIds = List.of(2L);
        final TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
                .sectorAssociationId(sectorAssociationId)
                .build();

        when(sectorAssociationSchemeService.getSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId))
                .thenReturn(subsectorAssociationIds);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> targetUnitAccountCreationValidationService.validate(accountDTO));

        // Verify
        assertThat(businessException.getErrorCode())
                .isEqualTo(TARGET_UNIT_ACCOUNT_SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION);
        verify(sectorAssociationSchemeService, times(1))
                .getSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId);
    }

    @Test
    void validate_no_subsector_invalid() {
        final long subsectorAssociationId = 2L;
        final long sectorAssociationId = 1L;
        final List<Long> subsectorAssociationIds = List.of();
        final TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
                .sectorAssociationId(sectorAssociationId)
                .subsectorAssociationId(subsectorAssociationId)
                .build();

        when(sectorAssociationSchemeService.getSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId))
                .thenReturn(subsectorAssociationIds);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> targetUnitAccountCreationValidationService.validate(accountDTO));

        // Verify
        assertThat(businessException.getErrorCode())
                .isEqualTo(TARGET_UNIT_ACCOUNT_SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION);
        verify(sectorAssociationSchemeService, times(1))
                .getSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId);
    }
}
