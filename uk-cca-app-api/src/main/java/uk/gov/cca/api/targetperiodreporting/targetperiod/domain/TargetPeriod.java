package uk.gov.cca.api.targetperiodreporting.targetperiod.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.hibernate.annotations.Type;

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
  @Column(name = "business_id", unique = true, length = 64)
  @NotNull
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

  @Type(JsonType.class)
  @Column(name = "target_period_years", columnDefinition = "jsonb")
  @NotNull
  @Valid
  private TargetPeriodYearsContainer targetPeriodYearsContainer;

  @Column(name = "buy_out_start_date")
  @NotNull
  private LocalDate buyOutStartDate;

  @Column(name = "buy_out_primary_payment_deadline")
  @NotNull
  private LocalDate buyOutPrimaryPaymentDeadline;

  @Column(name = "secondary_reporting_start_date")
  @NotNull
  private LocalDate secondaryReportingStartDate;
}

