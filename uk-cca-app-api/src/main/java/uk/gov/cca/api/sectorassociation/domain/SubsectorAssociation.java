package uk.gov.cca.api.sectorassociation.domain;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "subsector_association")
public class SubsectorAssociation {

    @Id
    @SequenceGenerator(name = "subsector_association_id_generator", sequenceName = "subsector_association_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subsector_association_id_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @NotBlank
    private String name;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_association_id")
    private SectorAssociation sectorAssociation;
    
    @Builder.Default
    @OneToMany(mappedBy = "subsectorAssociation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubsectorAssociationScheme> subsectorAssociationScheme = new ArrayList<>();
}
