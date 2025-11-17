package uk.gov.cca.api.migration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.AgreementType;

@UtilityClass
public class MigrationUtil {
    
    public String cleanString(String input) {
        if(input != null) {
            return input.replaceAll("\\s+", "");
        }
        return input;
    }
    
    public String convertLegacyToCcaBusinessId(String legacyBusinessId) {
        if (legacyBusinessId == null) {
            return null;
        }
        return legacyBusinessId.replace("/", "-");
    }
    
    public String convertCcaToLegacyBusinessId(String businessId) {
        if (businessId == null) {
            return null;
        }
        return businessId.replace("-", "/");
    }
    
    public BigDecimal toPercentage(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.multiply(BigDecimal.valueOf(100L));
    }

	public BigDecimal toDecimal(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.divide(BigDecimal.valueOf(100L), 9, RoundingMode.HALF_UP)
		        .stripTrailingZeros();
    }
    
    public AgreementType getAgreementType(String agreementType) {
        if (agreementType == null) {
            return null;
        }
        return switch (agreementType.toUpperCase().trim()) {
        case "EI", "ENERGY INTENSIVE" -> AgreementType.ENERGY_INTENSIVE;
        case "EPR", "ENVIRONMENTAL PERMITTING REGULATIONS (EPR)" -> AgreementType.ENVIRONMENTAL_PERMITTING_REGULATIONS;
        default -> null;
        };
    }
    
    public MeasurementType getMeasurementType(String energyCarbonUnit) {
        if(energyCarbonUnit == null) {
            return null;
        }
        
        MeasurementType measurementType = MigrationUtil.getMeasurementTypeByDescription(energyCarbonUnit);
        if(measurementType == null) {
            measurementType = MigrationUtil.getMeasurementTypeByUnit(energyCarbonUnit);
        }
        return measurementType;
    }

    public MeasurementType getMeasurementTypeByDescription(String description) {
        return Arrays.stream(MeasurementType.values())
                .filter(targetUnit -> targetUnit.getDescription().equalsIgnoreCase(description))
                .findFirst()
                .orElse(null);
    }

    private MeasurementType getMeasurementTypeByUnit(String unit) {
        return Arrays.stream(MeasurementType.values())
                .filter(targetUnit -> targetUnit.getUnit().equalsIgnoreCase(unit))
                .findFirst()
                .orElse(null);
    }
}
