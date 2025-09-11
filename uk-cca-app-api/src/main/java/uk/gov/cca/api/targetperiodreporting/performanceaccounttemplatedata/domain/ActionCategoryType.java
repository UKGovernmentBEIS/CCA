package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum ActionCategoryType {
    ENERGY_MANAGEMENT("Energy Management"),
    PROCESS_OPTIMISATION("Process Optimisation"),
    NEW_TECHNOLOGY_UPTAKE("New Technology Uptake"),
    INSULATION_IMPROVEMENT("Insulation Improvement"),
    INFRASTRUCTURE_IMPROVEMENT("Infrastructure Improvement"),
    SWITCHING_NON_RENEWABLE_FUELS("Switching Non-renewable fuels"),
    HEAT_RECOVERY("Heat Recovery"),
    SWITCHING_TO_CHP("Switching to CHP"),
    SWITCHING_TO_BIOMASS_HEATING("Switching to Biomass Heating"),
    SWITCHING_TO_RENEWABLE_POWER("Switching to Renewable Power"),
    OTHER_CONTRIBUTIONS("Other Contributions");
    
    private final String description;
    
    public static ActionCategoryType fromDescription(String descr) {
        return StringUtils.isBlank(descr) ? null : Arrays.stream(values())
                .filter(ct -> ct.description.equalsIgnoreCase(descr.trim()))
                .findFirst()
                .orElse(null);
    }
}
