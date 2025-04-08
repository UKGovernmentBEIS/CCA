package uk.gov.cca.api.targetperiod.domain.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TargetPeriodDTO {

  private Long id;
  private TargetPeriodType businessId;
  private String name;
  private LocalDate startDate;
  private LocalDate endDate;
  private String performanceDataTemplateVersion;
  private LocalDate performanceDataStartDate;
  private LocalDate performanceDataEndDate;
  private LocalDate buyOutStartDate;
  private LocalDate buyOutEndDate;
  private boolean isCurrent;
  private LocalDate secondaryReportingStartDate;
}

