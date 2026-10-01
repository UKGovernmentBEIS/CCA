package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "target_period_type")
    @NotNull
    private TargetPeriodType targetPeriodType;

    @Column(name = "facility_business_id")
    @NotNull
    private String facilityBusinessId;

    @EqualsAndHashCode.Include()
    @NotNull
    @Column(name = "performance_data_id", updatable = false, unique = true)
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
