package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tpr_buy_out_surplus_transaction_detail",
        uniqueConstraints = @UniqueConstraint(columnNames = {"buy_out_surplus_transaction_id", "performance_data_id"}))
public class BuyOutSurplusTransactionDetail {

    @Id
    @SequenceGenerator(name = "tpr_buy_out_surplus_transaction_detail_id_generator", sequenceName = "tpr_buy_out_surplus_transaction_detail_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tpr_buy_out_surplus_transaction_detail_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buy_out_surplus_transaction_id", referencedColumnName = "id")
    private BuyOutSurplusTransaction transaction;

    @EqualsAndHashCode.Include()
    @NotNull
    @Column(name = "performance_data_id")
    private Long performanceDataId;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 18, fraction = 2)
    @Column(name = "buy_out_fee")
    private BigDecimal buyOutFee;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private BuyOutSurplusPaymentStatus paymentStatus;

    @Type(JsonType.class)
    @Column(name = "data", columnDefinition = "jsonb")
    @NotNull
    @Valid
    private BuyOutSurplusContainer buyOutSurplusContainer;

    @PastOrPresent
    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "calculation_id")
    private Long calculationId;

    @OneToMany(mappedBy = "transactionDetail", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("submissionDate desc")
    @Builder.Default
    private List<BuyOutSurplusTransactionDetailHistory> transactionHistoryList = new ArrayList<>();

    public void addHistory(BuyOutSurplusTransactionDetailHistory history) {
        history.setTransactionDetail(this);
        this.transactionHistoryList.add(history);
    }
}
