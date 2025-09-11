package uk.gov.cca.api.subsistencefees.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.files.evidences.service.FileEvidenceService;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaReceivedAmountDetailsDTO;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaReceivedAmountHistoryRepository;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesMoaUpdateServiceTest {

    @InjectMocks
    private SubsistenceFeesMoaUpdateService subsistenceFeesMoaUpdateService;

    @Mock
    private SubsistenceFeesMoaReceivedAmountHistoryRepository receivedAmountHistoryRepository;

    @Mock
    private SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;

    @Mock
    private FileEvidenceService fileEvidenceService;

    @Test
    void updateSubsistenceFeesMoaReceivedAmount() {
        Long moaId = 1L;
        BigDecimal transactionAmount = BigDecimal.valueOf(185);
        BigDecimal previousReceivedAmount = BigDecimal.valueOf(5000);
        UUID uuid = UUID.randomUUID();
        AppUser submitter = AppUser.builder()
                .roleType("REGULATOR")
                .firstName("FirstName")
                .lastName("LastName")
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();
        SubsistenceFeesMoaReceivedAmountDetailsDTO detailsDto = SubsistenceFeesMoaReceivedAmountDetailsDTO.builder()
                .transactionAmount(transactionAmount)
                .evidenceFiles(Map.of(uuid, "EvidenceFile.txt"))
                .build();

        SubsistenceFeesMoa subsistenceFeesMoa = SubsistenceFeesMoa.builder()
                .id(moaId)
                .moaType(MoaType.SECTOR_MOA)
                .regulatorReceivedAmount(previousReceivedAmount)
                .build();

        when(subsistenceFeesMoaQueryService.getSubsistenceFeesMoaByIdPessimistic(moaId)).thenReturn(subsistenceFeesMoa);

        // invoke
        subsistenceFeesMoaUpdateService.updateSubsistenceFeesMoaReceivedAmount(moaId, detailsDto, submitter);

        // verify
        verify(fileEvidenceService, times(1)).submitFileEvidence(uuid.toString());
        verify(subsistenceFeesMoaQueryService, times(1)).getSubsistenceFeesMoaByIdPessimistic(moaId);
        verify(receivedAmountHistoryRepository, times(1)).save(any());
    }

    @Test
    void updateSubsistenceFeesMoaReceivedAmount_throw_exception() {
        Long moaId = 1L;
        BigDecimal transactionAmount = BigDecimal.valueOf(-5185);
        BigDecimal previousReceivedAmount = BigDecimal.valueOf(5000);
        AppUser submitter = AppUser.builder()
                .roleType("REGULATOR")
                .firstName("FirstName")
                .lastName("LastName")
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();
        SubsistenceFeesMoaReceivedAmountDetailsDTO detailsDto = SubsistenceFeesMoaReceivedAmountDetailsDTO.builder()
                .transactionAmount(transactionAmount)
                .build();

        SubsistenceFeesMoa subsistenceFeesMoa = SubsistenceFeesMoa.builder()
                .id(moaId)
                .moaType(MoaType.SECTOR_MOA)
                .regulatorReceivedAmount(previousReceivedAmount)
                .build();

        when(subsistenceFeesMoaQueryService.getSubsistenceFeesMoaByIdPessimistic(moaId)).thenReturn(subsistenceFeesMoa);

        // invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> subsistenceFeesMoaUpdateService.updateSubsistenceFeesMoaReceivedAmount(moaId, detailsDto, submitter));

        // verify
        assertThat(businessException.getErrorCode()).isEqualTo(CcaErrorCode.NEGATIVE_SUBSISTENCE_FEES_MOA_RECEIVED_AMOUNT);
        verify(subsistenceFeesMoaQueryService, times(1)).getSubsistenceFeesMoaByIdPessimistic(moaId);
        verify(receivedAmountHistoryRepository, never()).save(any());
    }

    @Test
    void updateSubsistenceFeesMoaReceivedAmount_throw_exception_zero_amount() {
        Long moaId = 1L;
        BigDecimal transactionAmount = BigDecimal.ZERO;
        AppUser submitter = AppUser.builder()
                .roleType("REGULATOR")
                .firstName("FirstName")
                .lastName("LastName")
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();

        SubsistenceFeesMoaReceivedAmountDetailsDTO detailsDto = SubsistenceFeesMoaReceivedAmountDetailsDTO.builder()
                .transactionAmount(transactionAmount)
                .build();

        // invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> subsistenceFeesMoaUpdateService.updateSubsistenceFeesMoaReceivedAmount(moaId, detailsDto, submitter));

        // verify
        assertThat(businessException.getErrorCode()).isEqualTo(CcaErrorCode.ZERO_SUBSISTENCE_FEES_MOA_TRANSACTION_AMOUNT);
        verify(subsistenceFeesMoaQueryService, never()).getSubsistenceFeesMoaByIdPessimistic(moaId);
        verify(receivedAmountHistoryRepository, never()).save(any());
    }
}
