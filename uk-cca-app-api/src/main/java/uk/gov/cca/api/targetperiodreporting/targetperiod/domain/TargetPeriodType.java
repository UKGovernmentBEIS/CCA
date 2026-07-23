package uk.gov.cca.api.targetperiodreporting.targetperiod.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TargetPeriodType {
  TP5(5),
  TP6(6),
  TP7(7),
  TP8(8),
  TP9(9);

  private final int number;
}
