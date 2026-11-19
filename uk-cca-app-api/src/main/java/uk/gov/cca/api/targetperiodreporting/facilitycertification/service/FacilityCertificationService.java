package uk.gov.cca.api.targetperiodreporting.facilitycertification.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertification;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.repository.FacilityCertificationRepository;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.transform.FacilityCertificationMapper;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.validation.FacilityCertificationValidationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusDTO;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Validated
@Service
@RequiredArgsConstructor
public class FacilityCertificationService {

    private final CertificationPeriodService certificationPeriodService;
    private final FacilityCertificationRepository facilityCertificationRepository;
    private final FacilityCertificationValidationService validatorService;

    private static final FacilityCertificationMapper FACILITY_CERTIFICATION_MAPPER = Mappers.getMapper(FacilityCertificationMapper.class);

    @Transactional
    public void certifyFacilities(@NotEmpty Set<Long> facilityIds,
                                  @NotNull Long certificationPeriodId,
                                  @PastOrPresent LocalDate startDate) {
        List<FacilityCertification> facilities = facilityCertificationRepository
                .findAllByFacilityIdInAndCertificationPeriodId(facilityIds, certificationPeriodId);

        // Update facilities
        facilities.forEach(facility -> {
            facility.setCertificationStatus(FacilityCertificationStatus.CERTIFIED);
            facility.setStartDate(startDate);
        });

        // Insert facilities
        Set<Long> existFacilityIds = facilities.stream().map(FacilityCertification::getFacilityId).collect(Collectors.toSet());
        Set<Long> newFacilityIds = SetUtils.difference(facilityIds, existFacilityIds);

        if (!newFacilityIds.isEmpty()) {
            List<FacilityCertification> newFacilities = newFacilityIds.stream().map(facilityId ->
                    FacilityCertification.builder()
                            .facilityId(facilityId)
                            .certificationPeriodId(certificationPeriodId)
                            .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                            .startDate(startDate)
                            .build()
            ).toList();

            facilityCertificationRepository.saveAll(newFacilities);
        }
    }
    
    public void saveFacilityCertification(FacilityCertificationDTO dto) {
        facilityCertificationRepository.save(FACILITY_CERTIFICATION_MAPPER.toFacilityCertification(dto));
    }
    
    public boolean existsFacilityCertificationByFacilityIdAndCertificationPeriodId(Long facilityId, Long certificationPeriodId) {
        return facilityCertificationRepository.existsFacilityCertificationByFacilityIdAndCertificationPeriodId(facilityId, certificationPeriodId);
    }

    public Map<Long, FacilityCertificationStatus> getFacilityCertifications(Set<Long> facilityIds, @NotNull Long certificationPeriodId) {

        Map<Long, FacilityCertificationStatus> facilityCertificationById =
                facilityCertificationRepository.findAllByFacilityIdInAndCertificationPeriodId(facilityIds, certificationPeriodId)
                        .stream()
                        .collect(Collectors.toMap(FacilityCertification::getFacilityId, FacilityCertification::getCertificationStatus));

        facilityIds.forEach(facilityId -> {
            if (!facilityCertificationById.containsKey(facilityId)) {
                facilityCertificationById.put(facilityId, FacilityCertificationStatus.NOT_YET_DEFINED);
            }
        });

        return facilityCertificationById;
    }

    public List<FacilityCertificationDTO> getFacilityCertifications(Set<Long> facilityIds) {
        return facilityCertificationRepository.findAllByFacilityIdIn(facilityIds).stream()
                .map(FACILITY_CERTIFICATION_MAPPER::toFacilityCertificationDto)
                .toList();
    }

    public List<FacilityCertificationStatusDTO> getFacilityCertifications(Long facilityId) {
        Map<Long, FacilityCertificationDTO> facilityCertificationByPeriod = facilityCertificationRepository
                .findAllByFacilityId(facilityId).stream()
                .map(FACILITY_CERTIFICATION_MAPPER::toFacilityCertificationDto)
                .collect(Collectors.toMap(FacilityCertificationDTO::getCertificationPeriodId, Function.identity()));

        LocalDate currentDate = LocalDate.now();
        FacilityCertificationDTO facilityNotDefined = FacilityCertificationDTO.builder()
                .certificationStatus(FacilityCertificationStatus.NOT_YET_DEFINED).build();

        return certificationPeriodService.getAllCertificationPeriods().stream()
                .sorted(Comparator.comparing(CertificationPeriodDTO::getStartDate).reversed())
                .filter(cp -> cp.getStartDate().isBefore(currentDate) || cp.getStartDate().isEqual(currentDate))
                .map(cp -> FACILITY_CERTIFICATION_MAPPER.toFacilityCertificationStatusDTO(facilityCertificationByPeriod.getOrDefault(cp.getId(), facilityNotDefined), cp))
                .toList();
    }

    @Transactional
    public void createFacilityCertifications(@Valid @NotEmpty List<FacilityCertificationDTO> facilityCertifications) {
        List<FacilityCertification> facilityCertificationList = facilityCertifications.stream()
                .map(FACILITY_CERTIFICATION_MAPPER::toFacilityCertification)
                .toList();
        facilityCertificationRepository.saveAll(facilityCertificationList);
    }

    @Transactional
    public void updateOrCreateFacilityCertificationStatus(Long facilityId,
                                                          FacilityCertificationStatusUpdateDTO statusUpdateDTO) {

        validatorService.validateFacilityCertificationByCertificationPeriod(
                statusUpdateDTO, LocalDate.now());

        facilityCertificationRepository
                .findByFacilityIdAndCertificationPeriodId(facilityId, statusUpdateDTO.getCertificationPeriodId())
                .ifPresentOrElse(fc -> {
                            fc.setCertificationStatus(statusUpdateDTO.getCertificationStatus());
                            fc.setStartDate(statusUpdateDTO.getStartDate());
                        },
                        () -> createFacilityCertification(facilityId, statusUpdateDTO));
    }

    private void createFacilityCertification(Long facilityId, FacilityCertificationStatusUpdateDTO updateDTO) {

        facilityCertificationRepository
                .save(FACILITY_CERTIFICATION_MAPPER
                        .toFacilityCertification(facilityId, updateDTO));
    }

}
