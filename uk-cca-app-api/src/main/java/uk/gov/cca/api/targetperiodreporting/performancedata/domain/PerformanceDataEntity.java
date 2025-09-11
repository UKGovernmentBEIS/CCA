package uk.gov.cca.api.targetperiodreporting.performancedata.domain;


import java.time.LocalDateTime;

import org.hibernate.annotations.Type;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Builder
@Table(name = "tpr_performance_data",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "target_period_id", "account_id", "report_version"
        }))
public class PerformanceDataEntity {

    @Id
    @SequenceGenerator(name = "performance_data_id_generator", sequenceName = "tpr_performance_data_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "performance_data_id_generator")
    private Long id;

    @Type(JsonType.class)
    @Valid
    @NotNull
    @Column(name = "data", columnDefinition = "jsonb")
    private PerformanceDataContainer data;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_period_id")
    private TargetPeriod targetPeriod;

    @NotNull
    @Column(name = "account_id")
    private Long accountId;

    @Positive
    @NotNull
    @Column(name = "report_version", columnDefinition = "int default 1")
    private int reportVersion;

    @NotNull
    @Column(name = "submission_type")
    @Enumerated(EnumType.STRING)
    private PerformanceDataSubmissionType submissionType;

    @Column(name = "performance_outcome", updatable = false, insertable = false)
    @Enumerated(EnumType.STRING)
    private TargetPeriodResultType performanceOutcome;

    @NotNull
    @Builder.Default
    @Column(name = "submission_date")
    private LocalDateTime submissionDate = LocalDateTime.now();
}
