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
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Table(name = "sfr_moa")
public class SubsistenceFeesMoa {

	@Id
    @SequenceGenerator(name = "sfr_moa_id_generator", sequenceName = "sfr_moa_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sfr_moa_id_generator")
    private Long id;
	
	@EqualsAndHashCode.Include()
	@Column(name = "transaction_id", unique = true)
    @NotNull
	private String transactionId;
	
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id")
    private SubsistenceFeesRun subsistenceFeesRun;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "moa_type")
    @NotNull
    private MoaType moaType;
    
    @Column(name = "resource_id")
    @NotNull
    private Long resourceId;
	
	@Column(name = "initial_total_amount")
    @NotNull
    @Positive
    private BigDecimal initialTotalAmount;
	
	@Column(name = "reg_received_amount")
    @NotNull
    @PositiveOrZero
    private BigDecimal regulatorReceivedAmount;
	
	@Column(name = "file_document_uuid")
	@NotNull
    private String fileDocumentUuid;

	@Column(name = "submission_date")
    @Builder.Default
    @NotNull
    private LocalDateTime submissionDate = LocalDateTime.now();
	
	@Builder.Default
    @OneToMany(mappedBy = "subsistenceFeesMoa", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SubsistenceFeesMoaTargetUnit> subsistenceFeesMoaTargetUnits = new ArrayList<>();

    public void addSubsistenceFeesMoaTargetUnit(SubsistenceFeesMoaTargetUnit subsistenceFeesMoaTargetUnit) {
        subsistenceFeesMoaTargetUnit.setSubsistenceFeesMoa(this);
        subsistenceFeesMoaTargetUnits.add(subsistenceFeesMoaTargetUnit);
    }
}
