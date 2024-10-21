package uk.gov.cca.api.authorization.ccaauth.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import uk.gov.netz.api.authorization.core.domain.Authority;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Getter
@Setter
@Table(name = "au_authority_details")
public class CcaAuthorityDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type")
    @NotNull
    private ContactType contactType;

    @Column(name = "organisation_name")
    private String organisationName;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "authority_id")
    @NotNull
    @MapsId
    private Authority authority;
}
