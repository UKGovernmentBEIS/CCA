package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;

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
@Table(name = "tpr_surplus", uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "target_period_id"}))
public class SurplusEntity {

	@Id
	@SequenceGenerator(name = "surplus_id_generator", sequenceName = "tpr_surplus_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "surplus_id_generator")
	private Long id;

	@NotNull
	@EqualsAndHashCode.Include
	@Column(name = "account_id")
	private Long accountId;

	@NotNull
	@EqualsAndHashCode.Include
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_period_id")
	private TargetPeriod targetPeriod;

	@NotNull
	@Digits(integer = Integer.MAX_VALUE, fraction = 0)
	@PositiveOrZero
	@Column(name = "surplus_gained")
	private BigDecimal surplusGained;

	@OneToMany(mappedBy = "surplus", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("submissionDate desc")
	@Builder.Default
	private List<SurplusHistory> surplusHistoryList = new ArrayList<>();

	public void addHistory(SurplusHistory history) {
		history.setSurplus(this);
		this.surplusHistoryList.add(history);
	}
}
