package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataEntity;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.repository.PerformanceAccountTemplateDataRepository;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Optional;

import static uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataSubmissionType.FINAL;
import static uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataSubmissionType.INTERIM;

@Validated
@RequiredArgsConstructor
@Service
public class PerformanceAccountTemplateDataService {
    
    private final PerformanceAccountTemplateDataRepository performanceAccountTemplateDataRepository;
    private final TargetPeriodService targetPeriodService;
    
    @Transactional
    public void submitPerformanceAccountTemplate(PerformanceAccountTemplateDataContainer container,
                                                 Long accountId,
                                                 TargetPeriodType targetPeriodType,
                                                 Year targetPeriodYear,
                                                 int reportVersion) {
        
        TargetPeriod targetPeriod = targetPeriodService.findByTargetPeriodType(targetPeriodType);
        PerformanceAccountTemplateDataSubmissionType submissionType =
                targetPeriodYear.equals(Year.of(targetPeriod.getEndDate().getYear())) ? FINAL : INTERIM;
        
        Optional<PerformanceAccountTemplateDataEntity> entity = performanceAccountTemplateDataRepository.findByTargetPeriodYearAndAccountId(targetPeriodYear, accountId);
        
        if(entity.isPresent()) {
            PerformanceAccountTemplateDataEntity existingEntity = entity.get();
            existingEntity.setSubmissionType(submissionType);
            existingEntity.setSubmissionDate(LocalDateTime.now());
            existingEntity.setReportVersion(reportVersion);
            existingEntity.setData(container);
        } else {
            
            PerformanceAccountTemplateDataEntity newEntity = PerformanceAccountTemplateDataEntity.builder()
                    .data(container)
                    .accountId(accountId)
                    .targetPeriod(targetPeriod)
                    .targetPeriodYear(targetPeriodYear)
                    .submissionType(submissionType)
                    .reportVersion(reportVersion)
                    .build();
            
            performanceAccountTemplateDataRepository.save(newEntity);
        }
    }
}
