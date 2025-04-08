package uk.gov.cca.api.migration.underlyingagreement.baselinetargets;

import static java.math.BigDecimal.TWO;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationHelper;

@UtilityClass
public class BaselineTargetsUtil {
    
    private static final Map<String, String> throughputsMapper;
    
    static {
        throughputsMapper = new HashMap<>();
        throughputsMapper.put("tonne", "tonnes");
        throughputsMapper.put("hectolitre", "hectolitres");
        throughputsMapper.put("kg", "Kilogram");
    }
    
    public AgreementCompositionType getAgreementCompositionType(String str) {
        if(str == null) {
            return null;
        }
        return Arrays.stream(AgreementCompositionType.values())
                .filter(agreementComposition -> agreementComposition.getDescription().equalsIgnoreCase(str))
                .findFirst()
                .orElse(null);
    }
    
    public Boolean isTargetUnitThroughputMeasured(TargetPeriod6DetailsVO targetPeriod6Details) {
        AgreementCompositionType sectorAgreementCompositionType = getAgreementCompositionType(targetPeriod6Details.getSectorAgreementCompositionType());
        AgreementCompositionType targetUnitAgreementCompositionType = getAgreementCompositionType(targetPeriod6Details.getAgreementCompositionType());
        if(AgreementCompositionType.NOVEM.equals(sectorAgreementCompositionType) 
                || AgreementCompositionType.NOVEM.equals(targetUnitAgreementCompositionType)
                || targetPeriod6Details.getThroughputUnit() == null) {
            return null;
        }
        Boolean result = !targetPeriod6Details.getThroughputUnit().equalsIgnoreCase(targetPeriod6Details.getSectorThroughputUnit());
        if(Boolean.TRUE.equals(result) && throughputsMapper.get(targetPeriod6Details.getSectorThroughputUnit()) != null) {
            result = !throughputsMapper.get(targetPeriod6Details.getSectorThroughputUnit()).equalsIgnoreCase(targetPeriod6Details.getThroughputUnit());
        }
        return result;
    }

    public BigDecimal calcPerformance(BigDecimal throughput, BigDecimal energy) {
        if (throughput == null || energy == null) {
            return null;
        }
        return throughput.signum() == 0 ? ZERO : energy.divide(throughput, 7, HALF_UP);
    }

    public BigDecimal calcTarget(AgreementCompositionType agreementCompositionType, boolean isTP6, BigDecimal throughput, BigDecimal energy, BigDecimal improvement) {
        if (AgreementCompositionType.RELATIVE.equals(agreementCompositionType)) {
            return calcTargetOfRelativeAgreementCompositionType(throughput, energy, improvement);
        } else if (AgreementCompositionType.ABSOLUTE.equals(agreementCompositionType)) {
            return calcTargetOfAbsoluteAgreementCompositionType(isTP6, energy, improvement);
        }

        return null;
    }
    
    private BigDecimal calcTargetOfRelativeAgreementCompositionType(BigDecimal throughput, BigDecimal energy, BigDecimal improvement) {
        if (throughput == null || energy == null || improvement == null) {
            return null;
        }
        BigDecimal baseline = throughput.signum() == 0 ? ZERO : energy.divide(throughput, DECIMAL128);
        return UnderlyingAgreementValidationHelper.calculateTarget(baseline, improvement);
    }
    
    private BigDecimal calcTargetOfAbsoluteAgreementCompositionType(boolean isTP6, BigDecimal energy, BigDecimal improvement) {
        if (energy == null || improvement == null) {
            return null;
        }
        return isTP6 ? UnderlyingAgreementValidationHelper.calculateTarget(energy, improvement)
                : UnderlyingAgreementValidationHelper.calculateTarget(energy.multiply(TWO), improvement);
    }
}
