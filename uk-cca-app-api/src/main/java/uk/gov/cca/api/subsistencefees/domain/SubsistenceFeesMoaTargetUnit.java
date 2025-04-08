package uk.gov.cca.api.subsistencefees.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "sfr_moa_target_unit",
		uniqueConstraints = @UniqueConstraint(columnNames = {"moa_id", "account_id"}))
public class SubsistenceFeesMoaTargetUnit {

	@Id
    @SequenceGenerator(name = "sfr_moa_target_unit_id_generator", sequenceName = "sfr_moa_target_unit_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sfr_moa_target_unit_id_generator")
    private Long id;
	
	@NotNull
	@EqualsAndHashCode.Include
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moa_id")
    private SubsistenceFeesMoa subsistenceFeesMoa;
	
	@EqualsAndHashCode.Include
	@Column(name = "account_id")
    @NotNull
    private Long accountId;
	
	@Column(name = "initial_total_amount")
    @NotNull
    @Positive
    private BigDecimal initialTotalAmount;
	
	@Builder.Default
    @OneToMany(mappedBy = "subsistenceFeesMoaTargetUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SubsistenceFeesMoaFacility> subsistenceFeesMoaFacilities = new ArrayList<>();

    public void addSubsistenceFeesMoaFacility(SubsistenceFeesMoaFacility facility) {
        facility.setSubsistenceFeesMoaTargetUnit(this);
        subsistenceFeesMoaFacilities.add(facility);
    }
}
