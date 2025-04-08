package uk.gov.cca.api.targetperiod.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TargetPeriodType {
  TP5(5),
  TP6(6);

  private final int number;
}
