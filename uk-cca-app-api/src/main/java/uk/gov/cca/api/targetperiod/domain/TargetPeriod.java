package uk.gov.cca.api.targetperiod.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Entity
@Table(name = "tpr_target_period")
public class TargetPeriod {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tpr_target_period_seq")
  @SequenceGenerator(
      name = "tpr_target_period_seq",
      sequenceName = "tpr_target_period_seq",
      allocationSize = 1
  )
  @Column(name = "id", nullable = false)
  @EqualsAndHashCode.Include
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "business_id", unique = true)
  @NotNull
  @Size(max = 64)
  @EqualsAndHashCode.Include
  private TargetPeriodType businessId;

  @Column(name = "name")
  @NotNull
  @Size(max = 255)
  private String name;

  @Column(name = "start_date")
  @NotNull
  private LocalDate startDate;

  @Column(name = "end_date")
  @NotNull
  private LocalDate endDate;

  @Column(name = "performance_data_template_version")
  @Size(max = 64)
  private String performanceDataTemplateVersion;

  @Column(name = "performance_data_start_date")
  @NotNull
  private LocalDate performanceDataStartDate;

  @Column(name = "performance_data_end_date")
  @NotNull
  private LocalDate performanceDataEndDate;

  @Column(name = "buy_out_start_date")
  @NotNull
  private LocalDate buyOutStartDate;

  @Column(name = "buy_out_end_date")
  @NotNull
  private LocalDate buyOutEndDate;

  @Column(name = "is_current")
  private boolean isCurrent;

  @Column(name = "secondary_reporting_start_date")
  @NotNull
  private LocalDate secondaryReportingStartDate;
}

