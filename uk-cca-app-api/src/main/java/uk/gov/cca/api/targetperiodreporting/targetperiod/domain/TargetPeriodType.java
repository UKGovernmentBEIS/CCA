package uk.gov.cca.api.targetperiodreporting.targetperiod.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

@AllArgsConstructor
@Getter
public enum TargetPeriodType {
  TP5(5),
  TP6(6),
  TP7(7),
  TP8(8),
  TP9(9);

  private final int number;
  
  public PerformanceDataResourceType getResourceType() {
	    return switch (this) {
	        case TP5, TP6 -> PerformanceDataResourceType.ACCOUNT;
	        case TP7, TP8, TP9 -> PerformanceDataResourceType.FACILITY;
	    };
	}
}
