package uk.gov.cca.api.account.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "account_address")
public class AccountAddress {

    @Id
    @SequenceGenerator(name = "account_address_id_generator", sequenceName = "account_address_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_address_id_generator")
    private Long id;

    @NotBlank
    @Column(name = "line1")
    private String line1;

    @Column(name = "line2")
    private String line2;

    @NotBlank
    @Column(name = "city")
    private String city;

    @NotBlank
    @Column(name = "postcode")
    private String postcode;

    @Column(name = "county")
    private String county;

    @NotBlank
    @Column(name = "country")
    private String country;
}
