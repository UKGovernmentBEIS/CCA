package uk.gov.cca.api.buyoutsurplus.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tpr_buy_out_transaction_identifier")
public class BuyOutTransactionIdentifier {

    @Id
    private int id;

    @EqualsAndHashCode.Include()
    @Enumerated(EnumType.STRING)
    @Column(name = "target_period_type", unique = true)
    @NotNull
    private TargetPeriodType targetPeriodType;

    @Column(name = "transaction_id")
    @NotNull
    private Long transactionId;
}
