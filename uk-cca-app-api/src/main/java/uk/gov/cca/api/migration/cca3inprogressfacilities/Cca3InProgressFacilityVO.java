package uk.gov.cca.api.migration.cca3inprogressfacilities;

import java.math.BigDecimal;
import java.util.Set;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#applicationReason eq 'CHANGE_OF_OWNERSHIP') == (#participatingInCca3Question != null)}", 
message = "cca3InProgressFacilityMigrationData.applicationReason")
@SpELExpression(expression = "{(T(java.lang.Boolean).FALSE.equals(#participatingInCca3SchemeIndicator) == (" +
        "#tp7Improvement == null && #tp8Improvement == null && #tp9Improvement == null  " +
        "&& #totalFixedEnergy == null && #baselineVariableEnergy == null && #totalThroughput == null && #throughputUnit == null)) " +
        "&& (T(java.lang.Boolean).TRUE.equals(#participatingInCca3SchemeIndicator) == (" +
        "#tp7Improvement != null && #tp8Improvement != null && #tp9Improvement != null  " +
        "&& #totalFixedEnergy != null && #baselineVariableEnergy != null && #totalThroughput != null && #throughputUnit != null)) " +
        "}",
        message = "cca3InProgressFacilityMigrationData.participatingInCca3SchemeIndicator")
public class Cca3InProgressFacilityVO {
	
	@NotNull
	private int rowNumber;
	
	@NotBlank
	@Size(max = 255)
    private String targetUnitId;
	
	@NotBlank
	@Size(max = 255)
    private String facilityId;
	
	@NotBlank
	@Size(max = 255)
    private String facilityName;
	
	@NotNull
    private ApplicationReasonType applicationReason;
	
	private Boolean participatingInCca3Question;
	
    private boolean participatingInCca3SchemeIndicator;
    
    @NotEmpty
    private Set<SchemeVersion> participatingSchemeVersions;
    
    // Improvement targets
    @DecimalMax(value = "100")
    @Digits(integer = 3, fraction = 7)
    private BigDecimal tp7Improvement;
    
    @DecimalMax(value = "100")
    @Digits(integer = 3, fraction = 7)
    private BigDecimal tp8Improvement;
    
    @DecimalMax(value = "100")
    @Digits(integer = 3, fraction = 7)
    private BigDecimal tp9Improvement;
    
    // Baseline fields
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal totalFixedEnergy;
    
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal baselineVariableEnergy;
    
    @Positive
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal totalThroughput;
    
    private String throughputUnit;
}
