package uk.gov.cca.api.sectorassociation.domain;

import org.hibernate.annotations.NamedQuery;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Getter
@Setter
@Table(name = "sector_association")
@NamedQuery(
        name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_INFO_BY_COMPETENT_AUTHORITY,
        query = "select new uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoDTO(sa.id, concat(sa.acronym,' - ', sa.name), concat(sac.firstName,' ', sac.lastName)) "
                + "from SectorAssociation sa "
                + "inner join sa.sectorAssociationContact sac "
                + "on sa.sectorAssociationContact.id = sac.id "
                + "where sa.competentAuthority = :competentAuthority")
@NamedQuery(
        name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_INFO_BY_SECTORS,
        query = "select new uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoDTO(sa.id, concat(sa.acronym,' - ', sa.name), concat(sac.firstName,' ', sac.lastName)) "
                + "from SectorAssociation sa "
                + "inner join sa.sectorAssociationContact sac "
                + "on sa.sectorAssociationContact.id = sac.id "
                + "where sa.id in (:sectorIds)")
@NamedQuery(
        name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_SITE_CONTACTS,
        query = "SELECT new uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactInfoDTO(sa.id, concat(sa.acronym,' - ', sa.name), sa.facilitatorUserId) "
                + "FROM SectorAssociation sa "
                + "WHERE sa.competentAuthority = :competentAuthority")
@NamedQuery(
        name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_IDS_BY_COMPETENT_AUTHORITY,
        query = "SELECT sa.id FROM SectorAssociation sa WHERE sa.competentAuthority = :ca")
@NamedQuery(
        name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATION_ACRONYM_BY_ID,
        query = "SELECT sa.acronym FROM SectorAssociation sa WHERE sa.id = :id")
@NamedQuery(
        name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATION_ID_BY_ACRONYM,
        query = "SELECT sa.ID FROM SectorAssociation sa WHERE sa.acronym = :acronym")

public class SectorAssociation {

    public static final String NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_INFO_BY_COMPETENT_AUTHORITY = "SectorAssociation.findSectorAssociationsByCompetentAuthority";
    public static final String NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_INFO_BY_SECTORS = "SectorAssociation.findSectorAssociationsBySectors";
    public static final String NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_SITE_CONTACTS = "SectorAssociation.findSectorAssociationSiteContacts";
    public static final String NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_IDS_BY_COMPETENT_AUTHORITY = "SectorAssociation.findSectorAssociationsIdsByCompetentAuthority";
    public static final String NAMED_QUERY_FIND_SECTOR_ASSOCIATION_ACRONYM_BY_ID = "SectorAssociation.findSectorAssociationAcronymById";
    public static final String NAMED_QUERY_FIND_SECTOR_ASSOCIATION_ID_BY_ACRONYM = "SectorAssociation.findSectorAssociationIdByAcronym";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sector_association_id_generator")
    @SequenceGenerator(name = "sector_association_id_generator", sequenceName = "sector_association_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    @NotNull
    private CompetentAuthorityEnum competentAuthority;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "acronym", unique = true)
    @NotBlank
    private String acronym;

    @Column(name = "legal_name")
    @NotBlank
    private String legalName;

    @Column(name = "facilitator_user_id")
    private String facilitatorUserId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "location_id")
    @NotNull
    private Location location;

    @Column(name = "energy_epr_factor")
    @NotBlank
    private String energyEprFactor;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sector_association_contact_id")
    @NotNull
    private SectorAssociationContact sectorAssociationContact;
}
