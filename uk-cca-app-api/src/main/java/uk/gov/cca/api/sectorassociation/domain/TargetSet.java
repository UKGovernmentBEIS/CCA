package uk.gov.cca.api.sectorassociation.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "target_set")
public class TargetSet {

    @Id
    @SequenceGenerator(name = "target_set_id_generator", sequenceName = "target_set_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "target_set_id_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "target_currency_type")
    private String targetCurrencyType;

    @Column(name = "throughput_unit")
    private String throughputUnit;

    @Column(name = "energy_or_carbon_unit")
    private String energyOrCarbonUnit;

    @Builder.Default
    @OneToMany(mappedBy = "targetSet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TargetCommitment> targetCommitments = new ArrayList<>();
}
