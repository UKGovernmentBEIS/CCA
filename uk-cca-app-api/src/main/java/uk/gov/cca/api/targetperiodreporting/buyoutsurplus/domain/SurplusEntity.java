package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tpr_surplus", uniqueConstraints = @UniqueConstraint(columnNames = {"resource_id", "scheme_version"}))
public class SurplusEntity {

	@Id
	@SequenceGenerator(name = "tpr_surplus_id_generator", sequenceName = "tpr_surplus_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tpr_surplus_id_generator")
	private Long id;

	@NotNull
	@EqualsAndHashCode.Include
	@Column(name = "resource_id")
	private Long resourceId;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "performance_data_resource_type", updatable = false)
	private PerformanceDataResourceType performanceDataResourceType;

	@NotNull
	@Digits(integer = 18, fraction = 0)
	@PositiveOrZero
	@Column(name = "surplus_gained")
	private BigDecimal surplusGained;

	@NotNull
	@EqualsAndHashCode.Include
	@Enumerated(EnumType.STRING)
	@Column(name = "scheme_version", updatable = false)
	private SchemeVersion schemeVersion;

	@OneToMany(mappedBy = "surplus", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("submissionDate desc")
	@Builder.Default
	private List<SurplusHistory> surplusHistoryList = new ArrayList<>();

	public void addHistory(SurplusHistory history) {
		history.setSurplus(this);
		this.surplusHistoryList.add(history);
	}
}
