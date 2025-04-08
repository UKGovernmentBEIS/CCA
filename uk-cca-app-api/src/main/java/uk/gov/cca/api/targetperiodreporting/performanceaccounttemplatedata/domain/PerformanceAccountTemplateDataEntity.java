package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain;

import java.time.LocalDateTime;
import java.time.Year;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cca.api.targetperiod.domain.TargetPeriod;
import uk.gov.netz.api.common.config.YearAttributeConverter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "tpr_performance_account_template_data")
public class PerformanceAccountTemplateDataEntity {

	@Id
    @SequenceGenerator(name = "performance_account_template_data_id_generator", sequenceName = "tpr_performance_account_template_data_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "performance_account_template_data_id_generator")
    private Long id;
	
	@Type(JsonType.class)
    @Valid
    @NotNull
    @Column(name = "data", columnDefinition = "jsonb")
    private PerformanceAccountTemplateDataContainer data;
	
	@EqualsAndHashCode.Include
	@NotNull
	@Column(name = "account_id")
	private Long accountId;
	
	@NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_period_id")
    private TargetPeriod targetPeriod;
	
	@EqualsAndHashCode.Include
	@Convert(converter = YearAttributeConverter.class)
	@NotNull
    @Column(name = "target_period_year")
    private Year targetPeriodYear;
	
	@NotNull
    @Column(name = "submission_date")
    @CreatedDate
    private LocalDateTime submissionDate;
	
	@NotNull
    @Column(name = "submission_type")
    @Enumerated(EnumType.STRING)
    private PerformanceAccountTemplateDataSubmissionType submissionType;
	
	@Positive
    @Column(name = "report_version")
    private int reportVersion;
}
