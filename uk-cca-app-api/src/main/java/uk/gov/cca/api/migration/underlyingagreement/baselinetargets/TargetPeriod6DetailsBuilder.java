package uk.gov.cca.api.migration.underlyingagreement.baselinetargets;

import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.MigrationUtil;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.BaselineData;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.Targets;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class TargetPeriod6DetailsBuilder {
    
    
    public TargetPeriod6Details constructTargetPeriod6Details(TargetPeriod6DetailsVO targetPeriod6DetailsVO) {
        
        AgreementCompositionType agreementCompositionType = BaselineTargetsUtil
                .getAgreementCompositionType(targetPeriod6DetailsVO.getAgreementCompositionType());
        
        MeasurementType measurementType = MigrationUtil.getMeasurementType(targetPeriod6DetailsVO.getMeasurementType());
        
        Boolean isTwelveMonths = null;
        String explanation = null;
        if(targetPeriod6DetailsVO.getEstimatedData() != null) {
            isTwelveMonths = !Boolean.TRUE.equals(targetPeriod6DetailsVO.getEstimatedData());
            if(Boolean.FALSE.equals(isTwelveMonths)) {
                explanation = "This placeholder text should be replaced with an explanation of how the agreement holder meets the greenfield criteria for facilities in this agreement.";
            } else if(!LocalDate.of(2018, 01, 01).equals(targetPeriod6DetailsVO.getBaselineDate())) {
                explanation = "This placeholder text should be replaced with an explanation of why the agreement holder couldn’t use the default TP6 base year start date (01/01/2018).";
            }
        }
        
        BigDecimal throughput = targetPeriod6DetailsVO.getThroughput() != null
                ? targetPeriod6DetailsVO.getThroughput().setScale(7, RoundingMode.HALF_UP)
                : null;
        
        BigDecimal energy = targetPeriod6DetailsVO.getEnergy() != null
                ? targetPeriod6DetailsVO.getEnergy().setScale(7, RoundingMode.HALF_UP)
                : null;
        
        BigDecimal performance = AgreementCompositionType.RELATIVE.equals(agreementCompositionType) 
                ? BaselineTargetsUtil.calcPerformance(throughput, energy)
                : null;
        
        BigDecimal improvement = targetPeriod6DetailsVO.getImprovement() != null
                ? MigrationUtil.toPercentage(targetPeriod6DetailsVO.getImprovement()).setScale(7, HALF_UP)
                : null;
        
        BigDecimal energyCarbonFactor = targetPeriod6DetailsVO.getEnergyCarbonFactor() != null
                ? targetPeriod6DetailsVO.getEnergyCarbonFactor().setScale(7, RoundingMode.HALF_UP)
                : null;
        
        BigDecimal target = BaselineTargetsUtil.calcTarget(agreementCompositionType, targetPeriod6DetailsVO.isTP6(), throughput, energy, improvement);
        
        TargetComposition targetComposition = TargetComposition.builder()
                .measurementType(measurementType)
                .agreementCompositionType(agreementCompositionType)
                .build();
        
        if(!AgreementCompositionType.NOVEM.equals(agreementCompositionType)) {
            Boolean isTargetUnitThroughputMeasured = BaselineTargetsUtil.isTargetUnitThroughputMeasured(targetPeriod6DetailsVO);
            targetComposition.setIsTargetUnitThroughputMeasured(isTargetUnitThroughputMeasured);
            targetComposition.setThroughputUnit(targetPeriod6DetailsVO.getThroughputUnit());
        }
        
        BaselineData baselineData = BaselineData.builder()
                .isTwelveMonths(isTwelveMonths)
                .baselineDate(targetPeriod6DetailsVO.getBaselineDate())
                .explanation(explanation)
                .energy(energy)
                .usedReportingMechanism(targetPeriod6DetailsVO.getUsedReportingMechanism())
                .throughput(throughput)
                .performance(performance)
                .energyCarbonFactor(energyCarbonFactor)
                .build();
        
        Targets targets = Targets.builder()
                .improvement(improvement)
                .target(target)
                .build();
        
         return TargetPeriod6Details.builder()
                 .targetComposition(targetComposition)
                 .baselineData(baselineData)
                 .targets(targets)
                 .build();
    }
}
