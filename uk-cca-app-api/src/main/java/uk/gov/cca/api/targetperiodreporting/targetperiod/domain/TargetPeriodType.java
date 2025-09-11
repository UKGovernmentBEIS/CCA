package uk.gov.cca.api.targetperiodreporting.targetperiod.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public enum TargetPeriodType {
  TP5(5, BigDecimal.valueOf(18)),
  TP6(6, BigDecimal.valueOf(25));

  private final int number;
  private final BigDecimal costPerCarbon;
}
