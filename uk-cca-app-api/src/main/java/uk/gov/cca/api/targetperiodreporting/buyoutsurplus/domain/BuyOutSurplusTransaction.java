package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;

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
                + "where tr.accountId = :accountId "
                + "and tr.targetPeriod.businessId = :targetPeriodType "
                + "order by tr.id asc"
)
@NamedQuery(
        name = BuyOutSurplusTransaction.NAMED_QUERY_FIND_TRANSACTION_DETAILS_SUMMARY_BY_ACCOUNT_AND_TARGET_PERIODS,
        query = " select new uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary( "
                + "                td.id, "
                + "                fd.facilityBusinessId, "
                + "                tr.targetPeriod.businessId, "
                + "                tr.chargeType, "
                + "                tr.creationDate, "
                + "                td.performanceDataId, "
                + "                td.paymentStatus, "
                + "                td.buyOutFee "
                + "            ) "
                + "            from BuyOutSurplusTransaction tr "
                + "            join tr.transactionDetailList td "
                + "            join PerformanceDataFacilityEntity pdf on pdf.id = td.performanceDataId "
                + "            join FacilityData fd on fd.id = pdf.facilityId "
                + "            where tr.accountId = :accountId "
                + "            and pdf.targetPeriod.id IN :targetPeriodIds "
                + "            order by tr.id asc"
)
public class BuyOutSurplusTransaction {

    public static final String NAMED_QUERY_FIND_ALL_BY_ACCOUNT_ID_AND_TARGET_PERIOD_TYPE = "BuyOutSurplusTransaction.findAllByAccountIdAndTargetPeriodType";
    public static final String NAMED_QUERY_FIND_TRANSACTION_DETAILS_SUMMARY_BY_ACCOUNT_AND_TARGET_PERIODS = "BuyOutSurplusTransaction.findTransactionDetailsSummaryByAccountIdAndTargetPeriodIds";

    @Id
    @SequenceGenerator(name = "tpr_buy_out_surplus_transaction_id_generator", sequenceName = "tpr_buy_out_surplus_transaction_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tpr_buy_out_surplus_transaction_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @NotBlank
    @Column(name = "transaction_code", unique = true)
    private String transactionCode;

    @NotNull
    @Column(name = "account_id")
    private Long accountId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_period_id")
    private TargetPeriod targetPeriod;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "charge_type")
    private BuyOutSurplusChargeType chargeType;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 18, fraction = 2)
    @Column(name = "invoiced_buy_out_fee")
    private BigDecimal invoicedBuyOutFee;

    @Column(name = "invoiced_payment_deadline")
    private LocalDate invoicedPaymentDeadline;

    @NotBlank
    @Column(name = "file_document_uuid")
    private String fileDocumentUuid;

    @PositiveOrZero
    @Digits(integer = 18, fraction = 2)
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "performance_data_resource_type", updatable = false)
    private PerformanceDataResourceType performanceDataResourceType;

    @CreatedDate
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BuyOutSurplusTransactionDetail> transactionDetailList = new ArrayList<>();

    public void addDetail(BuyOutSurplusTransactionDetail detail) {
        detail.setTransaction(this);
        this.transactionDetailList.add(detail);
    }

    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("submissionDate desc")
    @Builder.Default
    private List<BuyOutSurplusTransactionHistory> transactionHistoryList = new ArrayList<>();

    public void addHistory(BuyOutSurplusTransactionHistory history) {
        history.setTransaction(this);
        this.transactionHistoryList.add(history);
    }
}
