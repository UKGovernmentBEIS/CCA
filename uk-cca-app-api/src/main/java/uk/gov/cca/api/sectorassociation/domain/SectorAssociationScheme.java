package uk.gov.cca.api.sectorassociation.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Length;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "sector_association_scheme")
@NamedEntityGraph(
        name = "sector-association-scheme-graph",
        attributeNodes = {
                @NamedAttributeNode("umbrellaAgreement"),
                @NamedAttributeNode("targetSet"),
                @NamedAttributeNode(value = "subsectorAssociationSchemes", subgraph = "subsector-subgraph")
        },
        subgraphs = {
            @NamedSubgraph(
                    name = "subsector-subgraph",
                    attributeNodes = {@NamedAttributeNode(value = "subsectorAssociation", subgraph = "subsector-name-subgraph")}),
            @NamedSubgraph(
                    name = "subsector-name-subgraph",
                    attributeNodes = {@NamedAttributeNode("name")})
    })
@NamedQuery(
        name = SectorAssociationScheme.NAMED_QUERY_FIND_SUBSECTOR_ASSOCIATIONS_IDS_BY_SECTOR_ASSOCIATIONS_ID,
        query = "SELECT ssas.subsectorAssociation.id " +
                "FROM SectorAssociationScheme sas, SubsectorAssociationScheme ssas " +
                "WHERE sas.id = ssas.sectorAssociationScheme.id AND sas.sectorAssociation.id = :sectorAssociationId")
@NamedQuery(
        name = SectorAssociationScheme.NAMED_QUERY_FIND_SUBSECTOR_ASSOCIATIONS_BY_SECTOR_ASSOCIATION_ID,
        query = "SELECT NEW uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationInfoDTO(ssas.subsectorAssociation.id, ssas.subsectorAssociation.name) " +
                "FROM SectorAssociationScheme sas, SubsectorAssociationScheme ssas " +
                "WHERE sas.id = ssas.sectorAssociationScheme.id AND sas.sectorAssociation.id = :sectorAssociationId")
public class SectorAssociationScheme {

    public static final String NAMED_QUERY_FIND_SUBSECTOR_ASSOCIATIONS_IDS_BY_SECTOR_ASSOCIATIONS_ID = "SectorAssociationScheme.findSubsectorAssociationIdsBySectorAssociationId";
    public static final String NAMED_QUERY_FIND_SUBSECTOR_ASSOCIATIONS_BY_SECTOR_ASSOCIATION_ID = "SectorAssociationScheme.findSubsectorAssociationsBySectorAssociationId";

    @Id
    @SequenceGenerator(name = "sector_association_scheme_id_generator", sequenceName = "sector_association_scheme_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sector_association_scheme_id_generator")
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "umbrella_agreement_doc_id")
    @NotNull
    private SectorAssociationSchemeDocument umbrellaAgreement;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "target_set_id")
    private TargetSet targetSet;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sector_association_id")
    @NotNull
    private SectorAssociation sectorAssociation;

    @Column(name = "uma_date")
    private LocalDate umaDate;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "sector_definition", length = Length.LOB_DEFAULT)
    private String sectorDefinition;

    @Builder.Default
    @OneToMany(mappedBy = "sectorAssociationScheme", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SubsectorAssociationScheme> subsectorAssociationSchemes = new ArrayList<>();
}
