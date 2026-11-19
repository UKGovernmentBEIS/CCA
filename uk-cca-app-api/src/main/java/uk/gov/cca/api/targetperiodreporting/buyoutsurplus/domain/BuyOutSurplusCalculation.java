package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "tpr_buy_out_surplus_calculation")
public class BuyOutSurplusCalculation {

    @Id
    @SequenceGenerator(name = "tpr_buy_out_surplus_calculation_id_generator", sequenceName = "tpr_buy_out_surplus_calculation_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tpr_buy_out_surplus_calculation_id_generator")
    private Long id;

    @NotNull
    @Column(name = "account_id")
    private Long accountId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_period_id")
    private TargetPeriod targetPeriod;

    @Column(name = "facility_id")
    @NotNull
    private Long facilityId;

    @NotNull
    @Column(name = "performance_data_id")
    private Long performanceDataId;

    @NotNull
    @CreatedDate
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @Type(JsonType.class)
    @Valid
    @NotNull
    @Column(name = "data", columnDefinition = "jsonb")
    private BuyOutSurplusCalculationDataContainer data;
}
