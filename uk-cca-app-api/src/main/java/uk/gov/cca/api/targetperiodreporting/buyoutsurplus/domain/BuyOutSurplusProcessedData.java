package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "tpr_buy_out_surplus_processed_data",
		uniqueConstraints = @UniqueConstraint(columnNames = {"performance_data_id", "performance_data_resource_type"}) )
public class BuyOutSurplusProcessedData {

    @Id
    @SequenceGenerator(name = "buy_out_surplus_processed_data_id_generator", sequenceName = "tpr_buy_out_surplus_processed_data_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "buy_out_surplus_processed_data_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @NotNull
    @Column(name = "performance_data_id", updatable = false)
    private Long performanceDataId;

    @NotNull
    @CreatedDate
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;
    
    @EqualsAndHashCode.Include()
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "performance_data_resource_type", updatable = false)
    private PerformanceDataResourceType performanceDataResourceType;
}
