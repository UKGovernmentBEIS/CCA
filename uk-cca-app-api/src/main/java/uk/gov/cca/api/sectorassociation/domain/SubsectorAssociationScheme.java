package uk.gov.cca.api.sectorassociation.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "subsector_association_scheme")
@NamedEntityGraph(
        name = "subsector-association-scheme-graph",
        attributeNodes = {
                @NamedAttributeNode("subsectorAssociation"),
                @NamedAttributeNode(value = "targetSet", subgraph = "commitment-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "commitment-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("targetCommitments"),
                                @NamedAttributeNode("energyOrCarbonUnit")
                        })
        })
public class SubsectorAssociationScheme {

    @Id
    @SequenceGenerator(name = "subsector_association_scheme_id_generator", sequenceName = "subsector_association_scheme_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subsector_association_scheme_id_generator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_association_scheme_id")
    private SectorAssociationScheme sectorAssociationScheme;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "subsector_association_id")
    @NotNull
    private SubsectorAssociation subsectorAssociation;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "target_set_id")
    @NotNull
    private TargetSet targetSet;
}
