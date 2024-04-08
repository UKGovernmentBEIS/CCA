package uk.gov.cca.api.account.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.account.domain.enumeration.AccountContactType;
import uk.gov.cca.api.account.domain.enumeration.AccountStatus;
import uk.gov.cca.api.common.EmissionTradingScheme;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

/**
 * The Account Entity.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "account")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_ACCOUNT_IDS_AND_CONTACT_TYPE,
        query = "select new uk.gov.cca.api.account.domain.dto.AccountContactInfoDTO(acc.id, acc.name, VALUE(contacts)) "
                + "from Account acc "
                + "join acc.contacts contacts on KEY(contacts) = :contactType "
                + "where acc.id in (:accountIds)")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNTS_BY_CONTACT_TYPE_AND_USER_ID,
        query = "select acc "
                + "from Account acc "
                + "inner join acc.contacts contacts "
                + "where KEY(contacts) = :contactType "
                + "and contacts = :userId")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_IDS_BY_VB,
        query = "select acc.id "
                + "from Account acc "
                + "where acc.verificationBodyId = :vbId ")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNTS_WITH_CONTACTS_BY_VB_IN_LIST,
        query = "select ac "
                + "from Account ac "
                + "left join fetch ac.contacts "
                + "where ac.verificationBodyId in (:vbIds)")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNTS_BY_VB_AND_EMISSION_TRADING_SCHEME_WITH_CONTACTS_IN_LIST,
        query = "select ac "
                + "from Account ac "
                + "left join fetch ac.contacts "
                + "where ac.verificationBodyId = :vbId "
                + "and ac.emissionTradingScheme in (:emissionTradingSchemes)")
public abstract class Account {

    public static final String NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_ACCOUNT_IDS_AND_CONTACT_TYPE = "Account.findAccountContactsByAccountIdsAndContactType";
    public static final String NAMED_QUERY_FIND_ACCOUNTS_BY_CONTACT_TYPE_AND_USER_ID = "Account.findAccountsByContactTypeAndUserId";
    public static final String NAMED_QUERY_FIND_ACCOUNTS_WITH_CONTACTS_BY_VB_IN_LIST = "Account.findAccountsWithContactsByVBInList";
    public static final String NAMED_QUERY_FIND_ACCOUNTS_BY_VB_AND_EMISSION_TRADING_SCHEME_WITH_CONTACTS_IN_LIST = "Account.findAccountsByVBAndEmissionTradingSchemeWithContactsinList";
    public static final String NAMED_QUERY_FIND_IDS_BY_VB = "Account.findIdsByVB";

    @Id
    private Long id;

    @EqualsAndHashCode.Include()
    @Column(name = "emitter_id", unique = true)
    @NotBlank
    private String emitterId;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    @NotNull
    private CompetentAuthorityEnum competentAuthority;

    @Enumerated(EnumType.STRING)
    @Column(name = "emission_trading_scheme")
    private EmissionTradingScheme emissionTradingScheme;

    @Column(name = "accepted_date")
    private LocalDateTime acceptedDate;

    @Column(name = "verification_body_id")
    private Long verificationBodyId;

    @Builder.Default
    @ElementCollection
    @MapKeyColumn(name = "contact_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "user_id")
    @CollectionTable(name = "account_contact", joinColumns = @JoinColumn(name = "account_id"))
    private Map<AccountContactType, String> contacts = new EnumMap<>(AccountContactType.class);

    public abstract AccountStatus getStatus();
}
