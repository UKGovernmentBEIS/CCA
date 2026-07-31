package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.netz.api.common.config.YearAttributeConverter;

import java.time.LocalDateTime;
import java.time.Year;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "tpr_pat_data_facility", uniqueConstraints = @UniqueConstraint(columnNames = {
        "facility_id", "target_period_year" }))
public class FacilityPerformanceAccountTemplateDataEntity {

    @Id
    @SequenceGenerator(name = "tpr_pat_data_facility_id_generator", sequenceName = "tpr_pat_data_facility_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tpr_pat_data_facility_id_generator")
    private Long id;

    @Type(JsonType.class)
    @Valid
    @NotNull
    @Column(name = "data", columnDefinition = "jsonb")
    private FacilityPerformanceAccountTemplateDataContainer data;

    @EqualsAndHashCode.Include
    @NotNull
    @Column(name = "facility_id")
    private Long facilityId;

    @EqualsAndHashCode.Include
    @Convert(converter = YearAttributeConverter.class)
    @NotNull
    @Column(name = "target_period_year")
    private Year targetPeriodYear;

    @Positive
    @Column(name = "report_version")
    private int reportVersion;

    @NotNull
    @Column(name = "submission_date")
    @CreatedDate
    private LocalDateTime submissionDate;
}
