package uk.gov.cca.api.sectorassociation.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import uk.gov.cca.api.common.domain.SchemeVersion;

import java.time.LocalDate;

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
                @NamedAttributeNode("targetSet")
        }
 )
public class SectorAssociationScheme {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_association_id")
    @NotNull
    private SectorAssociation sectorAssociation;

    @Column(name = "uma_date")
    private LocalDate umaDate;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "sector_definition", length = Length.LOB_DEFAULT)
    private String sectorDefinition;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "scheme_version")
    @NotNull
    private SchemeVersion schemeVersion;
}
