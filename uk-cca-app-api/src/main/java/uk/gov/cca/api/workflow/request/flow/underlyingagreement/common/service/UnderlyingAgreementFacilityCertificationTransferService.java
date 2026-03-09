package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementFacilityCertificationTransferService {

    private final FacilityCertificationService facilityCertificationService;
    private final FacilityDataQueryService facilityDataQueryService;
    private final CertificationPeriodService certificationPeriodService;

    @Transactional
    public void processFacilityCertificationsForNewFacilities(Set<FacilityBaseInfoDTO> createdFacilities,
                                                              Set<FacilityItem> facilities) {

        // Map the new facilityId with the previous facilityId for change of ownership
        Map<String, String> changeOfOwnershipMap = new HashMap<>();
        List<String> newEntrantFacilityBusinessIds = new ArrayList<>();

        for (FacilityItem facility : facilities) {
            ApplicationReasonType reason = facility.getFacilityDetails().getApplicationReason();
            if (reason == ApplicationReasonType.CHANGE_OF_OWNERSHIP) {
                changeOfOwnershipMap.put(facility.getFacilityId(), facility.getFacilityDetails().getPreviousFacilityId());
            } else {
                newEntrantFacilityBusinessIds.add(facility.getFacilityId());
            }
        }

        List<FacilityCertificationDTO> allCertifications =
                this.copyFacilityCertifications(createdFacilities, changeOfOwnershipMap);

        allCertifications.addAll(
        this.certifyNewEntrantFacilitiesForActiveCertificationPeriod(createdFacilities, newEntrantFacilityBusinessIds)
        );

        if (!allCertifications.isEmpty()) {
            facilityCertificationService.createFacilityCertifications(allCertifications);
        }
    }

    private List<FacilityCertificationDTO> copyFacilityCertifications(Set<FacilityBaseInfoDTO> createdFacilities,
                                                                      Map<String, String> changeOfOwnershipMap) {

        // Map created facility businessId with the actual id only for those with change of ownership
        Map<String, Long> createdFacilitiesWithChangeOfOwnershipMap = createdFacilities.stream()
                .filter(facility -> changeOfOwnershipMap.containsKey(facility.getFacilityBusinessId()))
                .collect(Collectors.toMap(FacilityBaseInfoDTO::getFacilityBusinessId, FacilityBaseInfoDTO::getId));

        // Collect the previous facility certifications to be copied and group by the 'facilityId'
        Set<String> previousFacilityIds = new HashSet<>(changeOfOwnershipMap.values());
        Map<String, List<FacilityCertificationDTO>> certificationsByPreviousFacilityId = getCertificationsByFacilityBusinessIds(previousFacilityIds);

        List<FacilityCertificationDTO> copiedCertifications = new ArrayList<>();
        changeOfOwnershipMap.forEach((createdFacilityId, previousFacilityId) -> {
            List<FacilityCertificationDTO> certifications = certificationsByPreviousFacilityId.getOrDefault(previousFacilityId, Collections.emptyList());
            certifications.forEach(fc -> {
                FacilityCertificationDTO copiedCertification = FacilityCertificationDTO.builder()
                        .facilityId(createdFacilitiesWithChangeOfOwnershipMap.get(createdFacilityId))
                        .certificationPeriodId(fc.getCertificationPeriodId())
                        .certificationStatus(fc.getCertificationStatus())
                        .startDate(fc.getStartDate())
                        .build();
                copiedCertifications.add(copiedCertification);
            });
        });

        return copiedCertifications;
    }

    private List<FacilityCertificationDTO> certifyNewEntrantFacilitiesForActiveCertificationPeriod(Set<FacilityBaseInfoDTO> createdFacilities,
                                                                                                   List<String> newEntrantFacilityBusinessIds) {
        LocalDate currentDate = LocalDate.now();
	    Optional<CertificationPeriodInfoDTO> optionalCP = certificationPeriodService.getCurrentCertificationPeriodOptional();
	    
		if(optionalCP.isEmpty()) {
			return Collections.emptyList();
		}
		Long cpId = optionalCP.get().getId();

        // Map created facility businessId with the actual id
        Map<String, Long> facilityIds = createdFacilities.stream()
                .collect(Collectors.toMap(FacilityBaseInfoDTO::getFacilityBusinessId, FacilityBaseInfoDTO::getId));

	    return newEntrantFacilityBusinessIds.stream()
			    .map(f -> FacilityCertificationDTO.builder()
					    .facilityId(facilityIds.get(f))
					    .certificationPeriodId(cpId)
					    .certificationStatus(FacilityCertificationStatus.CERTIFIED)
					    .startDate(currentDate)
					    .build())
			    .toList();
	}

    private Map<String, List<FacilityCertificationDTO>> getCertificationsByFacilityBusinessIds(Set<String> facilityBusinessIds) {
        Map<Long, String> facilityBaseInfoById = facilityDataQueryService.getFacilityBaseInfoListByFacilityBusinessIds(facilityBusinessIds)
                .stream()
                .collect(Collectors.toMap(FacilityBaseInfoDTO::getId, FacilityBaseInfoDTO::getFacilityBusinessId));

        return facilityCertificationService.getFacilityCertifications(facilityBaseInfoById.keySet()).stream()
                .collect(Collectors.groupingBy(FacilityCertificationDTO::getFacilityId, Collectors.toList()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> facilityBaseInfoById.get(entry.getKey()), Map.Entry::getValue));
    }
}
