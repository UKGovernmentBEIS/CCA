package uk.gov.cca.api.subsistencefees.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.FacilityProcessStatus;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaFacility;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaFacilityMarkingStatusHistory;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesRun;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilityMarkingStatusDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO;
import uk.gov.cca.api.subsistencefees.repository.FacilityProcessStatusRepository;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaFacilityRepository;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaRepository;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaTargetUnitRepository;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesMoaFacilityMarkingStatusServiceTest {

    @InjectMocks
    private SubsistenceFeesMoaFacilityMarkingStatusService service;

    @Mock
    private SubsistenceFeesMoaTargetUnitRepository moaTargetUnitRepository;

    @Mock
    private SubsistenceFeesMoaFacilityRepository moaFacilityRepository;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private SubsistenceFeesMoaRepository moaRepository;

    @Mock
    private FacilityProcessStatusRepository facilityProcessStatusRepository;

    @Test
    void markMoaFacilitiesStatusByMoaId() {
        Long moaId = 1L;
        Set<Long> resourceIds = Set.of(1L, 2L);
        AppUser submitter = AppUser.builder()
                .roleType(REGULATOR)
                .firstName("FirstName")
                .lastName("LastName")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()))
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusDTO statusDTO = SubsistenceFeesMoaFacilityMarkingStatusDTO.builder()
                .filterResourceIds(resourceIds)
                .status(FacilityPaymentStatus.COMPLETED)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility1 = SubsistenceFeesMoaFacility.builder()
                .facilityId(1L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility2 = SubsistenceFeesMoaFacility.builder()
                .facilityId(2L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();


        when(moaTargetUnitRepository.findMoaTargetUnitIdsByMoaId(moaId)).thenReturn(Set.of(1L, 2L, 3L, 4L));
        when(moaFacilityRepository.findBySubsistenceFeesMoaTargetUnitIdInAndPaymentStatusNotInOrderById(statusDTO.getFilterResourceIds(), List.of(FacilityPaymentStatus.CANCELLED, statusDTO.getStatus())))
                .thenReturn(List.of(subsistenceFeesMoaFacility1, subsistenceFeesMoaFacility2));

        // invoke
        service.markMoaFacilitiesStatusByMoaId(submitter, moaId, statusDTO);

        // verify
        verify(moaTargetUnitRepository, times(1)).findMoaTargetUnitIdsByMoaId(moaId);
        verify(moaRepository, never()).findById(moaId);
        assertThat(subsistenceFeesMoaFacility1.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.COMPLETED);
        assertThat(subsistenceFeesMoaFacility2.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.COMPLETED);
    }

    @Test
    void markMoaFacilitiesStatusByMoaId_cancelled() {
        Long moaId = 1L;
        Set<Long> resourceIds = Set.of(1L, 2L);
        AppUser submitter = AppUser.builder()
                .roleType(REGULATOR)
                .firstName("FirstName")
                .lastName("LastName")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()))
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusDTO statusDTO = SubsistenceFeesMoaFacilityMarkingStatusDTO.builder()
                .filterResourceIds(resourceIds)
                .status(FacilityPaymentStatus.CANCELLED)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility1 = SubsistenceFeesMoaFacility.builder()
                .facilityId(1L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility2 = SubsistenceFeesMoaFacility.builder()
                .facilityId(2L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();

        SubsistenceFeesMoa subsistenceFeesMoa = SubsistenceFeesMoa.builder()
                .id(moaId)
                .moaType(MoaType.SECTOR_MOA)
                .subsistenceFeesRun(SubsistenceFeesRun.builder()
                        .id(1L)
                        .build())
                .build();

        List<FacilityProcessStatus> facilityProcessStatusList = List.of(
                FacilityProcessStatus.builder().facilityId(1L).runId(1L).build(),
                FacilityProcessStatus.builder().facilityId(2L).runId(1L).build());

        when(moaRepository.findById(moaId)).thenReturn(Optional.ofNullable(subsistenceFeesMoa));
        when(facilityProcessStatusRepository.findAllByRunIdAndFacilityIdIn(1L, Set.of(1L, 2L))).thenReturn(facilityProcessStatusList);
        when(moaTargetUnitRepository.findMoaTargetUnitIdsByMoaId(moaId)).thenReturn(Set.of(1L, 2L, 3L, 4L));
        when(moaFacilityRepository.findBySubsistenceFeesMoaTargetUnitIdInAndPaymentStatusNotInOrderById(statusDTO.getFilterResourceIds(), List.of(FacilityPaymentStatus.CANCELLED, statusDTO.getStatus())))
                .thenReturn(List.of(subsistenceFeesMoaFacility1, subsistenceFeesMoaFacility2));

        // invoke
        service.markMoaFacilitiesStatusByMoaId(submitter, moaId, statusDTO);

        // verify
        verify(moaTargetUnitRepository, times(1)).findMoaTargetUnitIdsByMoaId(moaId);
        verify(moaRepository, times(1)).findById(moaId);
        verify(facilityProcessStatusRepository, times(1)).findAllByRunIdAndFacilityIdIn(1L, Set.of(1L, 2L));
        verify(facilityProcessStatusRepository, times(1)).deleteAll(facilityProcessStatusList);
        assertThat(subsistenceFeesMoaFacility1.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.CANCELLED);
        assertThat(subsistenceFeesMoaFacility2.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.CANCELLED);
    }

    @Test
    void markMoaFacilitiesStatusByMoaId_forbidden() {
        Long moaId = 1L;
        Set<Long> resourceIds = Set.of(1L, 2L);
        AppUser submitter = AppUser.builder()
                .roleType(SECTOR_USER)
                .firstName("FirstName")
                .lastName("LastName")
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusDTO statusDTO = SubsistenceFeesMoaFacilityMarkingStatusDTO.builder()
                .filterResourceIds(resourceIds)
                .status(FacilityPaymentStatus.CANCELLED)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility1 = SubsistenceFeesMoaFacility.builder()
                .facilityId(1L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility2 = SubsistenceFeesMoaFacility.builder()
                .facilityId(2L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();

        // invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.markMoaFacilitiesStatusByMoaId(submitter, moaId, statusDTO));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);

        // verify
        verify(moaTargetUnitRepository, never()).findMoaTargetUnitIdsByMoaId(moaId);
        assertThat(subsistenceFeesMoaFacility1.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.IN_PROGRESS);
        assertThat(subsistenceFeesMoaFacility2.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.IN_PROGRESS);
    }

    @Test
    void markMoaFacilitiesStatusByMoaId_throws_error() {
        Long moaId = 1L;
        Set<Long> resourceIds = Set.of(1L, 5L);
        AppUser submitter = AppUser.builder()
                .roleType(REGULATOR)
                .firstName("FirstName")
                .lastName("LastName")
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusDTO statusDTO = SubsistenceFeesMoaFacilityMarkingStatusDTO.builder()
                .filterResourceIds(resourceIds)
                .status(FacilityPaymentStatus.CANCELLED)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility1 = SubsistenceFeesMoaFacility.builder()
                .facilityId(1L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility2 = SubsistenceFeesMoaFacility.builder()
                .facilityId(2L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();

        when(moaTargetUnitRepository.findMoaTargetUnitIdsByMoaId(moaId)).thenReturn(Set.of(1L, 2L, 3L, 4L));

        // invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.markMoaFacilitiesStatusByMoaId(submitter, moaId, statusDTO));
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.SUBSISTENCE_FEES_MOA_TARGET_UNIT_ID_DOES_NOT_EXIST);

        // verify
        verify(moaTargetUnitRepository, times(1)).findMoaTargetUnitIdsByMoaId(moaId);
        assertThat(subsistenceFeesMoaFacility1.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.IN_PROGRESS);
        assertThat(subsistenceFeesMoaFacility2.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.IN_PROGRESS);
    }

    @Test
    void markMoaFacilitiesStatusByTargetUnitId() {
        Long moaTargetUnitId = 1L;
        Set<Long> resourceIds = Set.of(1L, 2L);
        AppUser submitter = AppUser.builder()
                .roleType(REGULATOR)
                .firstName("FirstName")
                .lastName("LastName")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()))
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusDTO statusDTO = SubsistenceFeesMoaFacilityMarkingStatusDTO.builder()
                .filterResourceIds(resourceIds)
                .status(FacilityPaymentStatus.COMPLETED)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility1 = SubsistenceFeesMoaFacility.builder()
                .facilityId(1L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility2 = SubsistenceFeesMoaFacility.builder()
                .facilityId(2L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();

        when(moaFacilityRepository.findByIdInAndPaymentStatusNotInOrderById(statusDTO.getFilterResourceIds(), List.of(FacilityPaymentStatus.CANCELLED, statusDTO.getStatus())))
                .thenReturn(List.of(subsistenceFeesMoaFacility1, subsistenceFeesMoaFacility2));
        when(moaFacilityRepository.findMoaFacilityIdsByMoaTargetUnitId(moaTargetUnitId))
                .thenReturn(Set.of(subsistenceFeesMoaFacility1.getFacilityId(), subsistenceFeesMoaFacility2.getFacilityId()));

        // invoke
        service.markMoaFacilitiesStatusByTargetUnitId(submitter, moaTargetUnitId, statusDTO);

        // verify
        verify(moaFacilityRepository, times(1)).findByIdInAndPaymentStatusNotInOrderById(statusDTO.getFilterResourceIds(), List.of(FacilityPaymentStatus.CANCELLED, statusDTO.getStatus()));
        verify(moaFacilityRepository, times(1)).findMoaFacilityIdsByMoaTargetUnitId(moaTargetUnitId);
        assertThat(subsistenceFeesMoaFacility1.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.COMPLETED);
        assertThat(subsistenceFeesMoaFacility2.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.COMPLETED);
    }

    @Test
    void markMoaFacilitiesStatusByTargetUnitId_empty_resources() {
        Long moaTargetUnitId = 1L;
        Set<Long> resourceIds = Set.of();
        AppUser submitter = AppUser.builder()
                .roleType(REGULATOR)
                .firstName("FirstName")
                .lastName("LastName")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()))
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusDTO statusDTO = SubsistenceFeesMoaFacilityMarkingStatusDTO.builder()
                .filterResourceIds(resourceIds)
                .status(FacilityPaymentStatus.COMPLETED)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility1 = SubsistenceFeesMoaFacility.builder()
                .facilityId(1L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility2 = SubsistenceFeesMoaFacility.builder()
                .facilityId(2L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();

        when(moaFacilityRepository.findTargetUnitMoaFacilitiesEligibleForStatusUpdate(moaTargetUnitId, statusDTO.getStatus()))
                .thenReturn(List.of(subsistenceFeesMoaFacility1, subsistenceFeesMoaFacility2));

        // invoke
        service.markMoaFacilitiesStatusByTargetUnitId(submitter, moaTargetUnitId, statusDTO);

        // verify
        verify(moaFacilityRepository, times(1)).findTargetUnitMoaFacilitiesEligibleForStatusUpdate(moaTargetUnitId, statusDTO.getStatus());
        assertThat(subsistenceFeesMoaFacility1.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.COMPLETED);
        assertThat(subsistenceFeesMoaFacility2.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.COMPLETED);
    }

    @Test
    void markMoaFacilitiesStatusByTargetUnitId_forbidden() {
        Long moaTargetUnitId = 1L;
        Set<Long> resourceIds = Set.of(1L, 2L);
        AppUser submitter = AppUser.builder()
                .roleType(SECTOR_USER)
                .firstName("FirstName")
                .lastName("LastName")
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusDTO statusDTO = SubsistenceFeesMoaFacilityMarkingStatusDTO.builder()
                .filterResourceIds(resourceIds)
                .status(FacilityPaymentStatus.CANCELLED)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility1 = SubsistenceFeesMoaFacility.builder()
                .facilityId(1L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility2 = SubsistenceFeesMoaFacility.builder()
                .facilityId(2L)
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();

        // invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> service
                .markMoaFacilitiesStatusByTargetUnitId(submitter, moaTargetUnitId, statusDTO));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);

        // verify
        verify(moaFacilityRepository, never()).findTargetUnitMoaFacilitiesEligibleForStatusUpdate(moaTargetUnitId, FacilityPaymentStatus.CANCELLED);
        assertThat(subsistenceFeesMoaFacility1.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.IN_PROGRESS);
        assertThat(subsistenceFeesMoaFacility2.getPaymentStatus()).isEqualTo(FacilityPaymentStatus.IN_PROGRESS);
    }

    @Test
    void getMoaFacilityMarkingStatusHistoryInfo() {
        Long moaFacilityId = 1L;
        Long facilityId = 2L;
        String submitter = "Test User";
        LocalDateTime submissionDate = LocalDateTime.of(2025, 4, 4, 15, 0);

        FacilityBaseInfoDTO facilityBaseInfo = FacilityBaseInfoDTO.builder()
                .siteName("Facility name")
                .facilityId("Facility Id")
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusHistory facilityMarkingStatusHistory = SubsistenceFeesMoaFacilityMarkingStatusHistory.builder()
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .submitter(submitter)
                .submissionDate(submissionDate)
                .build();

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility = SubsistenceFeesMoaFacility.builder()
                .facilityId(facilityId)
                .markingStatusHistoryList(List.of(facilityMarkingStatusHistory))
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO subsistenceFeesMoaFacilityMarkingStatusHistoryDTO = SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO.builder()
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .submitter(submitter)
                .submissionDate(submissionDate)
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO expectedResult = SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO.builder()
                .facilityId("Facility Id")
                .markingStatusHistoryList(List.of(subsistenceFeesMoaFacilityMarkingStatusHistoryDTO))
                .siteName("Facility name")
                .build();

        when(facilityDataQueryService.getFacilityBaseInfo(facilityId)).thenReturn(facilityBaseInfo);
        when(moaFacilityRepository.findWithMarkingStatusHistory(moaFacilityId)).thenReturn(Optional.ofNullable(subsistenceFeesMoaFacility));

        SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO result = service.getMoaFacilityMarkingStatusHistoryInfo(moaFacilityId);

        assertThat(result).isEqualTo(expectedResult);
    }
}
