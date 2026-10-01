package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataEntity;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.repository.FacilityPerformanceAccountTemplateDataEntityRepository;

import java.time.LocalDateTime;
import java.time.Year;

@Validated
@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataService {

    private final FacilityPerformanceAccountTemplateDataEntityRepository facilityPerformanceAccountTemplateDataRepository;

    @Transactional
    public int submitFacilityPerformanceAccountTemplateData(@Valid FacilityPerformanceAccountTemplateDataContainer container, Long facilityId, Year targetPeriodYear) {
        FacilityPerformanceAccountTemplateDataEntity dataEntity = facilityPerformanceAccountTemplateDataRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear).
                map(existingEntity -> { // if entity exists
                    int latestReportVersion = existingEntity.getReportVersion();
                    existingEntity.setData(container);
                    existingEntity.setReportVersion(latestReportVersion + 1);
                    existingEntity.setSubmissionDate(LocalDateTime.now());
                    return existingEntity;
                }) // or new entity
                .orElse(FacilityPerformanceAccountTemplateDataEntity.builder()
                        .facilityId(facilityId)
                        .targetPeriodYear(targetPeriodYear)
                        .data(container)
                        .reportVersion(1)
                        .build());

        FacilityPerformanceAccountTemplateDataEntity persistedEntity = facilityPerformanceAccountTemplateDataRepository.save(dataEntity);
        return persistedEntity.getReportVersion();
    }
}