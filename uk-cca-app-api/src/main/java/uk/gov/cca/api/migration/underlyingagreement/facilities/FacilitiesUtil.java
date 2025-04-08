package uk.gov.cca.api.migration.underlyingagreement.facilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.RegulatorNameType;

@UtilityClass
public class FacilitiesUtil {

    private static final String NEW_ENTRANT = "new entrant";
    private static final String CHANGE_OF_OWNERSHIP = "change of ownership";
    
    private static final Map<String, RegulatorNameType> regulatorNameTypeMapper;
    
    static {
        regulatorNameTypeMapper = new HashMap<>();
        regulatorNameTypeMapper.put("Environment Agency (England)", RegulatorNameType.ENVIRONMENT_AGENCY);
        regulatorNameTypeMapper.put("Scottish Environment Protection Agency (Scotland)", RegulatorNameType.SCOTTISH_ENVIRONMENT_PROTECTION_AGENCY);
        regulatorNameTypeMapper.put("Natural Resources Wales (Wales)", RegulatorNameType.NATURAL_RESOURCES_WALES);
        regulatorNameTypeMapper.put("Department of Agriculture, Environment and Rural Affairs (Northern Ireland)", RegulatorNameType.DEPARTMENT_OF_AGRICULTURE_ENVIRONMENT_AND_RURAL_AFFAIRS);
        regulatorNameTypeMapper.put("Other", RegulatorNameType.OTHER);
    }
    
    public ApplicationReasonType getApplicationReasonType(String appReasonType) {
        if (appReasonType == null) {
            return null;
        }
        return switch (appReasonType.toLowerCase()) {
        case NEW_ENTRANT -> ApplicationReasonType.NEW_AGREEMENT;
        case CHANGE_OF_OWNERSHIP -> ApplicationReasonType.CHANGE_OF_OWNERSHIP;
        default -> null;
        };
    }
    
    public RegulatorNameType getRegulatorNameType(String regulatorNameType) {
        if (regulatorNameType == null) {
            return null;
        }
        return regulatorNameTypeMapper.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(regulatorNameType))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(RegulatorNameType.OTHER);
    }
    
    public BigDecimal toPercentage(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.multiply(BigDecimal.valueOf(100L));
    }
    
    public BigDecimal getEnergyConsumed(FacilityItemVO facilityItem) {
        return facilityItem.getEnergyConsumed() != null
                ? toPercentage(facilityItem.getEnergyConsumed()).setScale(7, RoundingMode.HALF_UP)
                : null;
    }
    
    public BigDecimal getEnergyConsumedProvision(FacilityItemVO facilityItem) {
        if(facilityItem.getEnergyConsumedProvision() == null) {
            return null;
        }
        BigDecimal energyConsumedPercentage = getEnergyConsumed(facilityItem);
        return facilityItem.getEnergyConsumedProvision().compareTo(BigDecimal.ZERO) == 0
                && energyConsumedPercentage != null
                && energyConsumedPercentage.compareTo(BigDecimal.valueOf(70L)) >= 0 ? null
                        : toPercentage(facilityItem.getEnergyConsumedProvision()).setScale(7, RoundingMode.HALF_UP);
    }
    
    public BigDecimal calcEnergyConsumedEligible(FacilityItemVO facilityItem) {
        BigDecimal energyConsumedPercentage = getEnergyConsumed(facilityItem);
        BigDecimal energyConsumedProvisionPercentage = getEnergyConsumedProvision(facilityItem);
        if ((energyConsumedPercentage == null) 
                || (energyConsumedPercentage.compareTo(BigDecimal.valueOf(70L)) < 0 && energyConsumedProvisionPercentage == null)) {
            return null;
        }
        return energyConsumedPercentage.compareTo(BigDecimal.valueOf(70L)) >= 0 ? BigDecimal.valueOf(100L)
                : energyConsumedPercentage.multiply(energyConsumedProvisionPercentage).divide(BigDecimal.valueOf(100L)).add(energyConsumedPercentage).setScale(7, RoundingMode.HALF_UP);
    }
}
