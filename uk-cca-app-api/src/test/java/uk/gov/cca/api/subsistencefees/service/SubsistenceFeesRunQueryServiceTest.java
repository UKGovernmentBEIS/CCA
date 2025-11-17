package uk.gov.cca.api.subsistencefees.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesRun;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunSearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunDetailsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunMoaDetailsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunSearchResultInfo;
import uk.gov.cca.api.subsistencefees.repository.FacilityProcessStatusRepository;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesRunRepository;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunQueryServiceTest {

    @InjectMocks
    private SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;

    @Mock
    private FacilityProcessStatusRepository facilityProcessStatusRepository;

    @Mock
    private SubsistenceFeesRunRepository subsistenceFeesRunRepository;

    @Test
    void isSectorEligibleForSubsistenceFeesRun() {
        final long sectorAssociationId = 1L;
        final Year chargingYear = Year.of(2025);
        final LocalDate firstDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 1, 1);
        final LocalDate endDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 12, 31);

        EligibleFacilityDTO eligibleFacilityDTO = EligibleFacilityDTO.builder()
                .id(1L)
                .facilityBusinessId("ADS_1-F00014")
                .siteName("site name 1")
                .targetUnitBusinessId("business id 1")
                .operatorName("Operator name 1")
                .build();

        when(facilityProcessStatusRepository.findSectorFacilitiesForSubsistenceFeesRun(sectorAssociationId, chargingYear, firstDateOfChargingYear, endDateOfChargingYear))
                .thenReturn(List.of(eligibleFacilityDTO));

        // invoke
        boolean isSectorEligibleForSubsistenceFeesRun = subsistenceFeesRunQueryService.isSectorEligibleForSubsistenceFeesRun(sectorAssociationId, chargingYear);

        // verify
        verify(facilityProcessStatusRepository, times(1)).findSectorFacilitiesForSubsistenceFeesRun(sectorAssociationId, chargingYear, firstDateOfChargingYear, endDateOfChargingYear);
        assertThat(isSectorEligibleForSubsistenceFeesRun).isTrue();
    }

    @Test
    void getTargetAccountIdsForSubsistenceFeesRun() {
        final long accountId = 1L;

        final Year chargingYear = Year.of(2025);
        final LocalDate firstDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 1, 1);
        final LocalDate endDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 12, 31);

        when(facilityProcessStatusRepository.findTargetUnitAccountsForSubsistenceFeesRun(chargingYear, firstDateOfChargingYear, endDateOfChargingYear))
                .thenReturn(Set.of(accountId));

        // invoke
        Set<Long> targetUnitIdsForSubsistenceFeesRun = subsistenceFeesRunQueryService.getTargetAccountIdsForSubsistenceFeesRun(chargingYear);

        // verify
        verify(facilityProcessStatusRepository, times(1)).findTargetUnitAccountsForSubsistenceFeesRun(chargingYear, firstDateOfChargingYear, endDateOfChargingYear);
        assertThat(targetUnitIdsForSubsistenceFeesRun).hasSize(1);
    }

    @Test
    void getSectorEligibleFacilitiesForSubsistenceFeesRunTest() {
        final long sectorAssociationId = 1L;
        final Year chargingYear = Year.of(2025);
        final LocalDate firstDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 1, 1);
        final LocalDate endDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 12, 31);

        EligibleFacilityDTO eligibleFacilityDTO = EligibleFacilityDTO.builder()
                .id(1L)
                .facilityBusinessId("ADS_1-F00014")
                .siteName("site name 1")
                .targetUnitBusinessId("business id 1")
                .operatorName("Operator name 1")
                .build();

        when(facilityProcessStatusRepository.findSectorFacilitiesForSubsistenceFeesRun(sectorAssociationId, chargingYear, firstDateOfChargingYear, endDateOfChargingYear))
                .thenReturn(List.of(eligibleFacilityDTO));

        // invoke
        List<EligibleFacilityDTO> sectorEligibleFacilitiesForSubsistenceFeesRun =
                subsistenceFeesRunQueryService.getSectorEligibleFacilitiesForSubsistenceFeesRun(sectorAssociationId, chargingYear);

        // verify
        verify(facilityProcessStatusRepository, times(1)).findSectorFacilitiesForSubsistenceFeesRun(sectorAssociationId, chargingYear, firstDateOfChargingYear, endDateOfChargingYear);
        assertThat(sectorEligibleFacilitiesForSubsistenceFeesRun).contains(eligibleFacilityDTO);
    }

    @Test
    void getAccountEligibleFacilitiesForSubsistenceFeesRun() {
        final long accountId = 1L;
        final Year chargingYear = Year.of(2025);
        final LocalDate firstDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 1, 1);
        final LocalDate endDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 12, 31);

        EligibleFacilityDTO eligibleFacilityDTO = EligibleFacilityDTO.builder()
                .facilityBusinessId("ADS_1-F00015")
                .siteName("site name 2")
                .targetUnitBusinessId("business id 2")
                .operatorName("Operator name 2")
                .build();

        when(facilityProcessStatusRepository.findAccountFacilitiesForSubsistenceFeesRun(accountId, chargingYear, firstDateOfChargingYear, endDateOfChargingYear))
                .thenReturn(List.of(eligibleFacilityDTO));

        // invoke
        List<EligibleFacilityDTO> accountEligibleFacilitiesForSubsistenceFeesRun =
                subsistenceFeesRunQueryService.getAccountEligibleFacilitiesForSubsistenceFeesRun(accountId, chargingYear);

        // verify
        verify(facilityProcessStatusRepository, times(1)).findAccountFacilitiesForSubsistenceFeesRun(accountId, chargingYear, firstDateOfChargingYear, endDateOfChargingYear);
        assertThat(accountEligibleFacilitiesForSubsistenceFeesRun).contains(eligibleFacilityDTO);
    }

    @Test
    void getSubsistenceFeesRuns() {
        final AppUser user = AppUser.builder()
                .roleType(REGULATOR)
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()))
                .build();
        final PagingRequest pagingRequest = PagingRequest.builder().pageNumber(0).pageSize(50).build();
        final Pageable pageable = getPageable();
        final SubsistenceFeesRun run = SubsistenceFeesRun.builder().id(1L).businessId("S2501").build();
        final Page<SubsistenceFeesRun> page = new PageImpl<>(List.of(run));
        final BigDecimal amount = BigDecimal.valueOf(1000L);
        final LocalDateTime date = LocalDateTime.now();
        final SubsistenceFeesRunSearchResultInfo resultInfo = new SubsistenceFeesRunSearchResultInfo(1L, "S2501", date, amount, amount, amount);
        final SubsistenceFeesRunSearchResultInfoDTO resultInfoDto =
                new SubsistenceFeesRunSearchResultInfoDTO(1L, "S2501", date, PaymentStatus.PAID, FacilityPaymentStatus.IN_PROGRESS, amount, BigDecimal.ZERO);
        final SubsistenceFeesRunSearchResults expectedResults = SubsistenceFeesRunSearchResults.builder()
                .subsistenceFeesRuns(List.of(resultInfoDto))
                .total(1L)
                .build();

        when(subsistenceFeesRunRepository.findSubsistenceFeesRunsByCompetentAuthorityAndSubmissionDateNotNull(pageable, user.getCompetentAuthority())).thenReturn(page);
        when(subsistenceFeesRunRepository.findSubsistenceFeesRunsWithAmountsByIds(Set.of(1L))).thenReturn(List.of(resultInfo));

        // invoke
        final SubsistenceFeesRunSearchResults results = subsistenceFeesRunQueryService.getSubsistenceFeesRuns(user, pagingRequest);

        // verify
        verify(subsistenceFeesRunRepository, times(1)).findSubsistenceFeesRunsByCompetentAuthorityAndSubmissionDateNotNull(pageable, user.getCompetentAuthority());
        verify(subsistenceFeesRunRepository, times(1)).findSubsistenceFeesRunsWithAmountsByIds(Set.of(1L));
        assertThat(results).isEqualTo(expectedResults);
    }
    
    @Test
    void getSubsistenceFeesRunDetailsById() {
        final long runId = 1L;
        final LocalDateTime date = LocalDateTime.now();
        
        SubsistenceFeesRunDetailsInfo sfrDetailsInfo = new SubsistenceFeesRunDetailsInfo(1L, "S2501", date, 
        		BigDecimal.valueOf(100L), BigDecimal.valueOf(100L));
        SubsistenceFeesRunMoaDetailsInfo sfrMoaDetailsInfo = new SubsistenceFeesRunMoaDetailsInfo(
        		BigDecimal.valueOf(100L), 1L, 1L);
        
        SubsistenceFeesRunDetailsDTO expectedDTO = new SubsistenceFeesRunDetailsDTO(1L, "S2501", date, PaymentStatus.PAID, 
        		BigDecimal.valueOf(100L), BigDecimal.valueOf(100L), BigDecimal.ZERO, 1L, 1L);

        when(subsistenceFeesRunRepository.findSubsistenceFeesRunDetailsById(runId))
                .thenReturn(Optional.of(sfrDetailsInfo));
        when(subsistenceFeesRunRepository.findSubsistenceFeesRunMoaDetailsById(runId))
        		.thenReturn(Optional.of(sfrMoaDetailsInfo));

        // invoke
        SubsistenceFeesRunDetailsDTO subsistenceFeesRunDetailsDTO =
                subsistenceFeesRunQueryService.getSubsistenceFeesRunDetailsById(runId);

        // verify
        verify(subsistenceFeesRunRepository, times(1)).findSubsistenceFeesRunDetailsById(runId);
        verify(subsistenceFeesRunRepository, times(1)).findSubsistenceFeesRunMoaDetailsById(runId);
        assertThat(subsistenceFeesRunDetailsDTO).isEqualTo(expectedDTO);
    }
    
    @Test
    void getSubsistenceFeesRunCaById() {
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final long runId = 1L;
        
        SubsistenceFeesRun run = SubsistenceFeesRun.builder().id(null).competentAuthority(ca).build();

        when(subsistenceFeesRunRepository.findById(runId)).thenReturn(Optional.of(run));

        // invoke
        CompetentAuthorityEnum result = subsistenceFeesRunQueryService.getSubsistenceFeesRunCaById(runId);

        // verify
        verify(subsistenceFeesRunRepository, times(1)).findById(runId);
        assertThat(result).isEqualTo(ca);
    }

    @Test
    void getSubsistenceFeesRunById() {
        final Long runId = 1L;

        final SubsistenceFeesRun subsistenceFeesRun = SubsistenceFeesRun.builder()
                .chargingYear(Year.of(2025))
                .id(runId)
                .subsistenceFeesMoas(List.of())
                .build();

        when(subsistenceFeesRunRepository.findById(runId)).thenReturn(Optional.of(subsistenceFeesRun));

        // invoke
        SubsistenceFeesRun result = subsistenceFeesRunQueryService.getSubsistenceFeesRunById(runId);

        // verify
        verify(subsistenceFeesRunRepository, times(1)).findById(runId);
        assertThat(result).isEqualTo(subsistenceFeesRun);
    }

    private Pageable getPageable() {
        return PageRequest.of(0, 50, Sort.by(Direction.DESC, "submissionDate"));
    }
}