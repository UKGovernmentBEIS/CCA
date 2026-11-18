package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import uk.gov.cca.api.common.domain.HistoryEntity;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Entity
@SequenceGenerator(name = "default_history_id_generator", sequenceName = "tpr_buy_out_surplus_transaction_history_seq", allocationSize = 1)
@Table(name = "tpr_buy_out_surplus_transaction_history")
public class BuyOutSurplusTransactionHistory extends HistoryEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buy_out_surplus_transaction_id", referencedColumnName = "id")
    private BuyOutSurplusTransaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", updatable = false, insertable = false)
    private BuyOutSurplusTransactionChangeType changeType;

    @Valid
    @NotNull
    @Type(JsonType.class)
    @Column(name = "payload", columnDefinition = "jsonb")
    private BuyOutSurplusTransactionHistoryPayload payload;
}
