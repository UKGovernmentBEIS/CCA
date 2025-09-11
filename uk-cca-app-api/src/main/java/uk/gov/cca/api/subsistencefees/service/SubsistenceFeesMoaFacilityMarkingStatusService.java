package uk.gov.cca.api.subsistencefees.service;

import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.FacilityProcessStatus;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaFacility;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaFacilityMarkingStatusHistory;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaTargetUnit;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilityMarkingStatusDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO;
import uk.gov.cca.api.subsistencefees.repository.FacilityProcessStatusRepository;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaFacilityRepository;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaRepository;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaTargetUnitRepository;
import uk.gov.cca.api.subsistencefees.transform.SubsistenceFeesMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus.CANCELLED;
import static uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus.COMPLETED;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@AllArgsConstructor
@Service
public class SubsistenceFeesMoaFacilityMarkingStatusService {

    private final SubsistenceFeesMoaRepository moaRepository;
    private final SubsistenceFeesMoaTargetUnitRepository moaTargetUnitRepository;
    private final SubsistenceFeesMoaFacilityRepository moaFacilityRepository;
    private final FacilityDataQueryService facilityDataQueryService;
    private static final SubsistenceFeesMapper SUBSISTENCE_FEES_MAPPER = Mappers.getMapper(SubsistenceFeesMapper.class);
    private final FacilityProcessStatusRepository facilityProcessStatusRepository;

    public SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO getMoaFacilityMarkingStatusHistoryInfo(Long moaFacilityId) {

        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility = moaFacilityRepository.findWithMarkingStatusHistory(moaFacilityId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        Long facilityId = subsistenceFeesMoaFacility.getFacilityId();
        FacilityBaseInfoDTO facilityBaseInfo = facilityDataQueryService.getFacilityBaseInfo(facilityId);

        List<SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO> markingStatusHistoryDtoList = subsistenceFeesMoaFacility.getMarkingStatusHistoryList()
                .stream()
                .map(SUBSISTENCE_FEES_MAPPER::toSubsistenceFeesMoaFacilityMarkingStatusHistoryDTO)
                .toList();

        return SUBSISTENCE_FEES_MAPPER.toSubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO(facilityBaseInfo, markingStatusHistoryDtoList);
    }

    @Transactional
    public void markMoaFacilitiesStatusByMoaId(AppUser user, Long moaId, SubsistenceFeesMoaFacilityMarkingStatusDTO statusDTO) {

        FacilityPaymentStatus newStatus = statusDTO.getStatus();

        validateMoaFacilityForPaymentStatusUpdate(user, newStatus);

        Set<Long> requestMoaTUIds = statusDTO.getFilterResourceIds();
        List<SubsistenceFeesMoaFacility> subsistenceFeesMoaFacilities;
        if (requestMoaTUIds.isEmpty()) {
            subsistenceFeesMoaFacilities = moaFacilityRepository.findMoaFacilitiesEligibleForStatusUpdate(moaId, newStatus);
        } else {
            Set<Long> allMoaTUIds = moaTargetUnitRepository.findMoaTargetUnitIdsByMoaId(moaId);
            if (!allMoaTUIds.containsAll(requestMoaTUIds)) {
                throw new BusinessException(CcaErrorCode.SUBSISTENCE_FEES_MOA_TARGET_UNIT_ID_DOES_NOT_EXIST);
            }
            subsistenceFeesMoaFacilities = moaFacilityRepository.findBySubsistenceFeesMoaTargetUnitIdInAndPaymentStatusNotInOrderById(requestMoaTUIds, List.of(CANCELLED, newStatus));
        }

        updateMoaFacilitiesStatusAndHistory(subsistenceFeesMoaFacilities, user, newStatus);

        if (statusDTO.getStatus() == CANCELLED) {
            SubsistenceFeesMoa moa = moaRepository.findById(moaId)
                    .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
            Long runId = moa.getSubsistenceFeesRun().getId();
            removeFacilitiesFromRun(runId, subsistenceFeesMoaFacilities);
        }
    }

    @Transactional
    public void markMoaFacilitiesStatusByTargetUnitId(AppUser user, Long moaTargetUnitId, SubsistenceFeesMoaFacilityMarkingStatusDTO statusDTO) {

        FacilityPaymentStatus newStatus = statusDTO.getStatus();

        validateMoaFacilityForPaymentStatusUpdate(user, newStatus);

        Set<Long> requestMoaFacilityIds = statusDTO.getFilterResourceIds();
        List<SubsistenceFeesMoaFacility> subsistenceFeesMoaFacilities;
        if (requestMoaFacilityIds.isEmpty()) {
            subsistenceFeesMoaFacilities = moaFacilityRepository.findTargetUnitMoaFacilitiesEligibleForStatusUpdate(moaTargetUnitId, newStatus);
        } else {
            Set<Long> allMoaFacilityIds = moaFacilityRepository.findMoaFacilityIdsByMoaTargetUnitId(moaTargetUnitId);
            if (!allMoaFacilityIds.containsAll(requestMoaFacilityIds)) {
                throw new BusinessException(CcaErrorCode.SUBSISTENCE_FEES_MOA_FACILITY_ID_DOES_NOT_EXIST);
            }
            subsistenceFeesMoaFacilities = moaFacilityRepository.findByIdInAndPaymentStatusNotInOrderById(requestMoaFacilityIds, List.of(CANCELLED, newStatus));
        }

        updateMoaFacilitiesStatusAndHistory(subsistenceFeesMoaFacilities, user, newStatus);

        if (statusDTO.getStatus() == CANCELLED) {
            SubsistenceFeesMoaTargetUnit subsistenceFeesMoaTargetUnit = moaTargetUnitRepository.findById(moaTargetUnitId)
                    .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
            Long runId = subsistenceFeesMoaTargetUnit.getSubsistenceFeesMoa().getSubsistenceFeesRun().getId();
            removeFacilitiesFromRun(runId, subsistenceFeesMoaFacilities);
        }
    }

    private void removeFacilitiesFromRun(Long runId, List<SubsistenceFeesMoaFacility> subsistenceFeesMoaFacilities) {
        Set<Long> facilityIds = subsistenceFeesMoaFacilities.stream()
                .map(SubsistenceFeesMoaFacility::getFacilityId)
                .collect(Collectors.toSet());

        List<FacilityProcessStatus> facilityProcessStatusList =
                facilityProcessStatusRepository.findAllByRunIdAndFacilityIdIn(runId, facilityIds);

        facilityProcessStatusRepository.deleteAll(facilityProcessStatusList);
    }

    private void validateMoaFacilityForPaymentStatusUpdate(AppUser user, FacilityPaymentStatus newStatus) {
        if (!user.getRoleType().equals(REGULATOR) && newStatus.equals(CANCELLED)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private void updateMoaFacilitiesStatusAndHistory(List<SubsistenceFeesMoaFacility> subsistenceFeesMoaFacilities, AppUser submitter, FacilityPaymentStatus paymentStatus) {
        LocalDate paymentDate = LocalDate.now();
        subsistenceFeesMoaFacilities.forEach(moaFacility -> {
            updatePaymentStatus(moaFacility, paymentStatus, paymentDate);
            SubsistenceFeesMoaFacilityMarkingStatusHistory facilityMarkingStatusHistory =
                    SubsistenceFeesMoaFacilityMarkingStatusHistory.builder()
                            .subsistenceFeesMoaFacility(moaFacility)
                            .paymentStatus(paymentStatus)
                            .submitterId(submitter.getUserId())
                            .submitter(submitter.getFullName())
                            .build();
            moaFacility.addHistory(facilityMarkingStatusHistory);
        });
    }

    private void updatePaymentStatus(SubsistenceFeesMoaFacility moaFacility, FacilityPaymentStatus paymentStatus, LocalDate paymentDate) {
        moaFacility.setPaymentStatus(paymentStatus);
        if (paymentStatus == COMPLETED) {
            moaFacility.setPaymentDate(paymentDate);
        } else {
            moaFacility.setPaymentDate(null);
        }
    }
}
