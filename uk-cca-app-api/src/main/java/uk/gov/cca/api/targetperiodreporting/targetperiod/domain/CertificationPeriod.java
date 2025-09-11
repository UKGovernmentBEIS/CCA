package uk.gov.cca.api.targetperiodreporting.targetperiod.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Entity
@Table(name = "tpr_certification_period")
public class CertificationPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tpr_certification_period_seq")
    @SequenceGenerator(name = "tpr_certification_period_seq", sequenceName = "tpr_certification_period_seq", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne
    @JoinColumn(name = "target_period_id")
    @NotNull
    private TargetPeriod targetPeriod;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_id", unique = true, length = 64)
    @NotNull
    @EqualsAndHashCode.Include
    private CertificationPeriodType businessId;

    @Column(name = "name")
    @NotNull
    @Size(max = 255)
    private String name;

    @Column(name = "certification_batch_trigger_date")
    @NotNull
    private LocalDate certificationBatchTriggerDate;

    @Column(name = "start_date")
    @NotNull
    private LocalDate startDate;

    @Column(name = "end_date")
    @NotNull
    private LocalDate endDate;
}
