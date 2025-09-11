package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.common.domain.HistoryEntity;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Entity
@SequenceGenerator(name = "default_history_id_generator", sequenceName = "tpr_surplus_history_seq", allocationSize = 1)
@Table(name = "tpr_surplus_history")
public class SurplusHistory extends HistoryEntity {

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "surplus_id", referencedColumnName = "id")
	private SurplusEntity surplus;

	@NotNull
	@Digits(integer = Integer.MAX_VALUE, fraction = 0)
	@PositiveOrZero
	@Column(name = "new_surplus_gained")
	private BigDecimal newSurplusGained;

	@Column(name = "comments")
	@Size(max = 10000)
	private String comments;
}
