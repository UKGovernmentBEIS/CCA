package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import org.hibernate.annotations.Type;

import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.netz.api.common.config.YearAttributeConverter;

import java.time.LocalDateTime;
import java.time.Year;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Builder
@Table(name = "tpr_performance_data_facility",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "target_period_year", "facility_id", "report_version"
        }))
public class PerformanceDataFacilityEntity {

    @Id
    @SequenceGenerator(name = "performance_data_facility_id_generator", sequenceName = "tpr_performance_data_facility_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "performance_data_facility_id_generator")
    private Long id;

    @Type(JsonType.class)
    @Valid
    @NotNull
    @Column(name = "data", columnDefinition = "jsonb")
    private PerformanceDataFacilityContainer data;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_period_id")
    private TargetPeriod targetPeriod;

    @EqualsAndHashCode.Include
    @Convert(converter = YearAttributeConverter.class)
    @NotNull
    @Column(name = "target_period_year")
    private Year targetPeriodYear;

    @EqualsAndHashCode.Include
    @NotNull
    @Column(name = "facility_id")
    private Long facilityId;

    @EqualsAndHashCode.Include
    @Positive
    @Column(name = "report_version")
    private int reportVersion;

    @Column(name = "submission_type")
    @Enumerated(EnumType.STRING)
    private PerformanceDataSubmissionType submissionType;

    @NotNull
    @Builder.Default
    @Column(name = "submission_date")
    private LocalDateTime submissionDate = LocalDateTime.now();

    @Column(name = "performance_outcome", updatable = false, insertable = false)
    @Enumerated(EnumType.STRING)
    private PerformanceDataFacilityTargetPeriodResultType performanceOutcome;
}
