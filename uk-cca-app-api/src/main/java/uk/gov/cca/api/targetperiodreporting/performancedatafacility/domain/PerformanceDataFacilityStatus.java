package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.netz.api.common.config.YearAttributeConverter;

import java.time.Year;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Table(name = "tpr_performance_data_facility_status",
        uniqueConstraints = @UniqueConstraint(columnNames = {"target_period_year", "facility_id"}))
public class PerformanceDataFacilityStatus {

    @Id
    @SequenceGenerator(name = "performance_data_facility_status_id_generator", sequenceName = "tpr_performance_data_facility_status_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "performance_data_facility_status_id_generator")
    private Long id;

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

    @Column(name = "locked")
    private boolean locked;

    @Column(name = "variation_indicator")
    private boolean variationIndicator;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_performance_data_id", referencedColumnName = "id")
    private PerformanceDataFacilityEntity lastPerformanceData;
}
