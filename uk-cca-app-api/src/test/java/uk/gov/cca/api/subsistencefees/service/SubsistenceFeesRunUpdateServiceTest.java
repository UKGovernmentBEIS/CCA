package uk.gov.cca.api.subsistencefees.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.subsistencefees.config.SubsistenceFeesConfig;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesRun;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaRepository;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesRunRepository;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubsistenceFeesRunUpdateServiceTest {

    @InjectMocks
    private SubsistenceFeesRunUpdateService subsistenceFeesRunUpdateService;

    @Mock
    private SubsistenceFeesRunRepository subsistenceFeesRunRepository;

    @Mock
    private SubsistenceFeesMoaRepository subsistenceFeesMoaRepository;

    @Mock
    private SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;

    @Mock
    private SubsistenceFeesConfig subsistenceFeesConfig;

    @Test
    void createSubsistenceFeesRun() {
        final Long runId = 1L;
        final String businessId = "businessId";
        final Year chargingYear = Year.of(2025);
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final SubsistenceFeesRun subsistenceFeesRun = SubsistenceFeesRun.builder()
                .id(runId)
                .competentAuthority(competentAuthority)
                .businessId(businessId)
                .chargingYear(chargingYear)
                .initialTotalAmount(BigDecimal.ZERO)
                .build();

        when(subsistenceFeesRunRepository.save(subsistenceFeesRun)).thenReturn(subsistenceFeesRun);

        long result = subsistenceFeesRunUpdateService.createSubsistenceFeesRun(businessId, competentAuthority, chargingYear);

        // verify
        verify(subsistenceFeesRunRepository, times(1)).save(subsistenceFeesRun);
        assertThat(result).isEqualTo(runId);
        assertThat(subsistenceFeesRun.getInitialTotalAmount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void finalizeSubsistenceFeesRun() {
        long runId = 1L;
        SubsistenceFeesMoa subsistenceFeesMoa1 = SubsistenceFeesMoa.builder()
                .id(1L)
                .initialTotalAmount(BigDecimal.valueOf(555.00))
                .build();

        SubsistenceFeesMoa subsistenceFeesMoa2 = SubsistenceFeesMoa.builder()
                .id(2L)
                .initialTotalAmount(BigDecimal.valueOf(185.00))
                .build();

        SubsistenceFeesRun subsistenceFeesRun = SubsistenceFeesRun.builder()
                .id(runId)
                .chargingYear(Year.of(2025))
                .subsistenceFeesMoas(List.of(subsistenceFeesMoa1, subsistenceFeesMoa2))
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();

        when(subsistenceFeesRunQueryService.getSubsistenceFeesRunById(runId)).thenReturn(subsistenceFeesRun);

        // invoke
        subsistenceFeesRunUpdateService.finalizeSubsistenceFeesRun(runId);

        // verify
        subsistenceFeesRun.setInitialTotalAmount(BigDecimal.valueOf(740.00));
        verify(subsistenceFeesRunRepository, times(1)).save(subsistenceFeesRun);
    }

    @Test
    void deleteSubsistenceFeesRun() {
        final Long runId = 1L;

        // invoke
        subsistenceFeesRunUpdateService.deleteSubsistenceFeesRun(runId);

        // verify
        verify(subsistenceFeesRunRepository, times(1)).deleteById(runId);
    }

    @Test
    void persistMoa() {
        final Long sectorId = 1L;
        final long runId = 1L;
        final String documentUuid = "documentUuid";
        final String transactionId = "transactionId";
        final MoaType moaType = MoaType.SECTOR_MOA;
        final BigDecimal facilityFee = BigDecimal.valueOf(185.00);
        final List<EligibleFacilityDTO> facilities = List.of(EligibleFacilityDTO.builder()
                        .facilityId("facilityId1")
                        .targetUnitBusinessId("businessId")
                        .accountId(1L)
                        .build(),
                EligibleFacilityDTO.builder()
                        .facilityId("facilityId2")
                        .targetUnitBusinessId("businessId")
                        .accountId(1L)
                        .build());

        final SubsistenceFeesRun subsistenceFeesRun = SubsistenceFeesRun.builder().id(runId).build();

        final SubsistenceFeesMoa subsistenceFeesMoa = SubsistenceFeesMoa.builder()
                .moaType(moaType)
                .subsistenceFeesRun(subsistenceFeesRun)
                .fileDocumentUuid(documentUuid)
                .resourceId(sectorId)
                .transactionId(transactionId)
                .initialTotalAmount(facilityFee.multiply(BigDecimal.valueOf(facilities.size())))
                .regulatorReceivedAmount(BigDecimal.ZERO)
                .submissionDate(LocalDateTime.now())
                .build();

        when(subsistenceFeesConfig.getFacilityFee()).thenReturn(facilityFee);
        when(subsistenceFeesRunQueryService.getSubsistenceFeesRunById(runId)).thenReturn(subsistenceFeesRun);

        // invoke
        subsistenceFeesRunUpdateService.persistMoa(sectorId, transactionId, runId, moaType, facilities, documentUuid);

        // verify
        verify(subsistenceFeesConfig, times(2)).getFacilityFee();
        verify(subsistenceFeesRunQueryService, times(1)).getSubsistenceFeesRunById(runId);
        verify(subsistenceFeesMoaRepository, times(1)).save(subsistenceFeesMoa);
    }
}
