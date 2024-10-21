package uk.gov.cca.api.sectorassociation.domain;

import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "target_commitment")
public class TargetCommitment {

    @Id
    @SequenceGenerator(name = "target_commitment_id_generator", sequenceName = "target_commitment_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "target_commitment_id_generator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "target_period")
    private String targetPeriod;

    @Column(name = "target_improvement")
    private BigDecimal targetImprovement;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_set_id")
    private TargetSet targetSet;
}
