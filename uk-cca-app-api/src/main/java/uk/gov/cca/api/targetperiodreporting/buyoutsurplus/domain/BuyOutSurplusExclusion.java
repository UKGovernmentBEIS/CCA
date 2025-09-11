package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tpr_buy_out_surplus_exclusion")
public class BuyOutSurplusExclusion {

	@Id
	@SequenceGenerator(name = "buy_out_surplus_exclusion_id_generator", sequenceName = "tpr_buy_out_surplus_exclusion_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "buy_out_surplus_exclusion_id_generator")
	private Long id;

	@NotNull
	@EqualsAndHashCode.Include
	@Column(name = "account_id",updatable = false, unique = true)
	private Long accountId;
}
