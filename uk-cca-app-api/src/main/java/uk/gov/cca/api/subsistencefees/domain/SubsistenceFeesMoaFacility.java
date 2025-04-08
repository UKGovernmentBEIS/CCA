package uk.gov.cca.api.subsistencefees.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Table(name = "sfr_moa_facility",
		uniqueConstraints = @UniqueConstraint(columnNames = {"moa_target_unit_id", "facility_id"}))
public class SubsistenceFeesMoaFacility {

	@Id
    @SequenceGenerator(name = "sfr_moa_facility_id_generator", sequenceName = "sfr_moa_facility_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sfr_moa_facility_id_generator")
    private Long id;
	
	@EqualsAndHashCode.Include
	@NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moa_target_unit_id")
    private SubsistenceFeesMoaTargetUnit subsistenceFeesMoaTargetUnit;
	
	@EqualsAndHashCode.Include
	@Column(name = "facility_id")
	@NotNull
    private Long facilityId;
	
	@Column(name = "initial_amount")
    @NotNull
    @Positive
    private BigDecimal initialAmount;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    @NotNull
    private FacilityPaymentStatus paymentStatus;
	
	@Column(name = "payment_date")
    private LocalDate paymentDate;
}
