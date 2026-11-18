package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "tpr_buy_out_surplus_transaction")
@NamedQuery(
        name = BuyOutSurplusTransaction.NAMED_QUERY_FIND_ALL_BY_ACCOUNT_ID_AND_TARGET_PERIOD_TYPE,
        query = "select tr "
                + "from BuyOutSurplusTransaction tr "
                + "join PerformanceDataEntity pf on pf.id = tr.performanceDataId "
                + "where pf.accountId = :accountId "
                + "and pf.targetPeriod.businessId = :targetPeriodType "
                + "order by tr.id asc"
)
@NamedQuery(
        name = BuyOutSurplusTransaction.NAMED_QUERY_UPDATE_STATUS_BY_ID_IN,
        query = "update BuyOutSurplusTransaction tr set tr.paymentStatus = :status where tr.id in(:ids)"
)
public class BuyOutSurplusTransaction {

    public static final String NAMED_QUERY_FIND_ALL_BY_ACCOUNT_ID_AND_TARGET_PERIOD_TYPE = "BuyOutSurplusTransaction.findAllByAccountIdAndTargetPeriodType";
    public static final String NAMED_QUERY_UPDATE_STATUS_BY_ID_IN = "BuyOutSurplusTransaction.updateStatusByIdIn";

    @Id
    @SequenceGenerator(name = "tpr_buy_out_surplus_transaction_id_generator", sequenceName = "tpr_buy_out_surplus_transaction_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tpr_buy_out_surplus_transaction_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @NotNull
    @Column(name = "performance_data_id", unique = true)
    private Long performanceDataId;

    @EqualsAndHashCode.Include()
    @NotBlank
    @Column(name = "transaction_code", unique = true)
    private String transactionCode;

    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
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

    @NotBlank
    @Column(name = "file_document_uuid")
    private String fileDocumentUuid;

    @CreatedDate
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @PastOrPresent
    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("submissionDate desc")
    @Builder.Default
    private List<BuyOutSurplusTransactionHistory> transactionHistoryList = new ArrayList<>();

    public void addHistory(BuyOutSurplusTransactionHistory history) {
        history.setTransaction(this);
        this.transactionHistoryList.add(history);
    }
}
