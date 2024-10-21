package uk.gov.cca.api.sectorassociation.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
}
