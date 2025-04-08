package uk.gov.cca.api.buyoutsurplus.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
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
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "tpr_buy_out_surplus")
@NamedQuery(
        name = BuyOutSurplusEntity.NAMED_QUERY_FIND_ALL_BY_ACCOUNT_ID_AND_PAYMENT_STATUS_IN,
        query = "select bof "
                + "from BuyOutSurplusEntity bof, PerformanceDataEntity pf "
                + "where pf.accountId = :accountId "
                + "and bof.performanceDataId = pf.id "
                + "and bof.paymentStatus in (:paymentStatuses)"
)
public class BuyOutSurplusEntity {

    public static final String NAMED_QUERY_FIND_ALL_BY_ACCOUNT_ID_AND_PAYMENT_STATUS_IN = "BuyOutSurplusEntity.findAllByAccountIdAndPaymentStatusIn";

    @Id
    @SequenceGenerator(name = "tpr_buy_out_surplus_id_generator", sequenceName = "tpr_buy_out_surplus_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tpr_buy_out_surplus_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @NotNull
    @Column(name = "performance_data_id", unique = true)
    private Long performanceDataId;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    @Column(name = "buy_out_fee")
    private BigDecimal buyOutFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private BuyOutPaymentStatus paymentStatus;

    @Type(JsonType.class)
    @Column(name = "data", columnDefinition = "jsonb")
    @NotNull
    @Valid
    private BuyOutSurplusContainer buyOutSurplusContainer;

    @Column(name = "file_document_uuid")
    private String fileDocumentUuid;

    @CreatedDate
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;
}
