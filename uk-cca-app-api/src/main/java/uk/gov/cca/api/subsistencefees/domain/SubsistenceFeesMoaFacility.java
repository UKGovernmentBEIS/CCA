package uk.gov.cca.api.subsistencefees.domain;

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
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Table(name = "sfr_moa_facility",
        uniqueConstraints = @UniqueConstraint(columnNames = {"moa_target_unit_id", "facility_id"}))
@NamedEntityGraph(
        name = "moa-facility-moa-target-unit-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "subsistenceFeesMoaTargetUnit"),
        })
@NamedEntityGraph(
        name = "moa-facility-status-history-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "markingStatusHistoryList")
        })
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

    @OneToMany(mappedBy = "subsistenceFeesMoaFacility", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("submissionDate desc")
    @Builder.Default
    private List<SubsistenceFeesMoaFacilityMarkingStatusHistory> markingStatusHistoryList = new ArrayList<>();

    public void addHistory(SubsistenceFeesMoaFacilityMarkingStatusHistory history) {
        history.setSubsistenceFeesMoaFacility(this);
        this.markingStatusHistoryList.add(history);
    }
}
