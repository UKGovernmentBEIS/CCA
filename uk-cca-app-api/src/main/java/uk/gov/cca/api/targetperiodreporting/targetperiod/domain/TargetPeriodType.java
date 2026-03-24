package uk.gov.cca.api.targetperiodreporting.targetperiod.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public enum TargetPeriodType {
  TP5(5, BigDecimal.valueOf(18)),
  TP6(6, BigDecimal.valueOf(25)),
  TP7(7, BigDecimal.ZERO),
  TP8(8, BigDecimal.ZERO),
  TP9(9, BigDecimal.ZERO);

  private final int number;
  private final BigDecimal costPerCarbon;
}
