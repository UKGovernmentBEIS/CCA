package uk.gov.cca.api.facility.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "facility_identifier")
@NamedQuery(
        name = FacilityIdentifier.NAMED_QUERY_FIND_FACILITY_IDENTIFIER_BY_SECTOR_ASSOCIATION_ID,
        query = "SELECT fi "
                + "FROM FacilityIdentifier fi "
                + "WHERE fi.sectorAssociationId = :sectorAssociationId")
public class FacilityIdentifier {

    public static final String NAMED_QUERY_FIND_FACILITY_IDENTIFIER_BY_SECTOR_ASSOCIATION_ID = "FacilityIdentifier.findFacilityIdentifierBySectorAssociationId";

    @Id
    @SequenceGenerator(name = "facility_identifier_id_generator", sequenceName = "facility_identifier_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "facility_identifier_id_generator")
    private Long id;

    @Column(name = "facility_id")
    @NotNull
    private Long facilityId;

    @Column(name = "sector_association_id")
    @NotNull
    private Long sectorAssociationId;
}
