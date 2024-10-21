package uk.gov.cca.api.sectorassociation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Location Entity.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "location")
public class Location {

    /** The id. */
    @Id
    @SequenceGenerator(name = "location_id_generator", sequenceName = "location_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_id_generator")
    private Long id;

    /** Location. */
    @Column(name = "postcode")
    @NotBlank
    private String postcode;

    @Column(name = "line1")
    @NotBlank
    private String line1;

    /** The line 2 address. */
    @Column(name = "line2")
    private String line2;

    /** The city. */
    @Column(name = "city")
    @NotBlank
    private String city;

    /** The county. */
    @Column(name = "county")
    private String county;
}
