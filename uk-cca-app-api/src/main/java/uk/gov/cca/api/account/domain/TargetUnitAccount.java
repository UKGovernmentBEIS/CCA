package uk.gov.cca.api.account.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.account.domain.Account;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "account_target_unit")
@NamedEntityGraph(
        name = "target-unit-account-contacts-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "address"),
                @NamedAttributeNode(value = "targetUnitAccountContacts", subgraph = "account-contacts-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "account-contacts-subgraph",
                        attributeNodes = {@NamedAttributeNode(value = "address")}
                )
        }
)
@NamedQueries({
    @NamedQuery(
            name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNT_IDS_BY_SECTOR_ASSOCIATION,
            query = "select tu.id "
                + "from TargetUnitAccount tu "
                + "where tu.sectorAssociationId = :sectorAssociationId "
    ),
    @NamedQuery(
            name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNTS_BUSINESS_INFO_BY_SECTOR_ASSOCIATION_AND_STATUS,
            query = "select new uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO(tu.id, tu.businessId) "
                    + "from TargetUnitAccount tu "
                    + "where tu.sectorAssociationId = :sectorAssociationId and tu.status = :status "
    ),
    @NamedQuery(
            name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNTS_ACTIVATED_BEFORE_WITH_STATUS_ACTIVE_OR_TERMINATED_BETWEEN,
            query = "select new uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO(tu.id, tu.businessId) "
                    + "from TargetUnitAccount tu "
            		+ "where tu.sectorAssociationId = :sectorAssociationId "
                    + "and tu.acceptedDate <= :acceptedDate "
                    + "and (tu.status = uk.gov.cca.api.account.domain.TargetUnitAccountStatus.LIVE "
                    + "		or (tu.status = uk.gov.cca.api.account.domain.TargetUnitAccountStatus.TERMINATED and "
                    + "			 tu.terminatedDate >= :terminatedDateFrom and tu.terminatedDate < :terminatedDateTo "
                    + "			)"
                    + ") "
    ),
    @NamedQuery(
            name = TargetUnitAccount.NAMED_QUERY_FIND_ACCOUNTS_BY_CONTACT_TYPE_AND_USER_ID_AND_SECTOR_ASSOCIATION,
            query = "select tu "
                + "from TargetUnitAccount tu "
                + "inner join tu.contacts contacts on KEY(contacts) = :contactType "
                + "where tu.sectorAssociationId = :sectorAssociationId "
                + "and contacts = :userId "
    ),
    @NamedQuery(
            name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNTS_WITH_SITE_CONTACT_BY_SECTOR_ASSOCIATION,
            query = "select new uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO(tu.id, tu.businessId, tu.name, tu.status, VALUE(contacts)) "
                + "from TargetUnitAccount tu "
                + "left join tu.contacts contacts on KEY(contacts) = :contactType "
                + "where tu.sectorAssociationId = :sectorAssociationId "
                + "order by tu.businessId "
    ),
    @NamedQuery(
            name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNT_NOTICE_RECIPIENTS_BY_ACCOUNT_ID,
            query = "select new uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO(tuac.firstName , tuac.lastName, tuac.email, tuac.contactType) "
                + "from TargetUnitAccount atu "
                + "left join TargetUnitAccountContact tuac on atu.id = tuac.targetUnitAccount.id "
                + "where atu.id = :accountId "
    ),
    @NamedQuery(
            name = TargetUnitAccount.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNTS_WITH_SITE_CONTACT_BY_SECTOR_ASSOCIATION_AND_ACCOUNTS_IDS,
            query = "select new uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO(tu.id, tu.businessId, tu.name, tu.status, VALUE(contacts)) "
                    + "from TargetUnitAccount tu "
                    + "left join tu.contacts contacts on KEY(contacts) = :contactType "
                    + "where tu.sectorAssociationId = :sectorAssociationId "
                    + "and tu.id in (:accountsIds)"
                    + "order by tu.businessId "
    )
})
public class TargetUnitAccount extends Account {

    public static final String NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNTS_WITH_SITE_CONTACT_BY_SECTOR_ASSOCIATION = "TargetUnitAccount.findTargetUnitAccountWithSiteContactBySectorAssociationId";
    public static final String NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNT_IDS_BY_SECTOR_ASSOCIATION = "TargetUnitAccount.findAllIdsBySectorAssociationId";
    public static final String NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNTS_BUSINESS_INFO_BY_SECTOR_ASSOCIATION_AND_STATUS = "TargetUnitAccount.findAllTargetUnitAccountsBusinessInfoBySectorAssociationIdAndStatus";
    public static final String NAMED_QUERY_FIND_ACCOUNTS_BY_CONTACT_TYPE_AND_USER_ID_AND_SECTOR_ASSOCIATION = "TargetUnitAccount.findTargetUnitAccountsByContactTypeAndUserIdAndSectorAsssociationId";
    public static final String NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNT_NOTICE_RECIPIENTS_BY_ACCOUNT_ID = "TargetUnitAccount.findTargetUnitAccountNoticeRecipientsByAccountId";
    public static final String NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNTS_WITH_SITE_CONTACT_BY_SECTOR_ASSOCIATION_AND_ACCOUNTS_IDS = "TargetUnitAccount.findTargetUnitAccountWithSiteContactBySectorAssociationIdAndAccountId";
	public static final String NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNTS_ACTIVATED_BEFORE_WITH_STATUS_ACTIVE_OR_TERMINATED_BETWEEN = "TargetUnitAccount.findAllTargetUnitAccountsActivatedBeforeWithStatusActiveOrTerminatedDuringActivatedYearOrTerminatedBetween";

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    private TargetUnitAccountStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "emission_trading_scheme")
    @NotNull
    private CcaEmissionTradingScheme emissionTradingScheme;

    @Column(name = "sector_association_id")
    @NotNull
    private Long sectorAssociationId;

    @Column(name = "subsector_association_id")
    private Long subsectorAssociationId;

    @Column(name = "operator_type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private TargetUnitAccountOperatorType operatorType;

    @Column(name = "company_registration_number")
    private String companyRegistrationNumber;

    @Column(name = "registration_number_missing_reason")
    private String registrationNumberMissingReason;

    @Column(name = "sic_code")
    private String sicCode;

    /** The user id */
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "creation_date")
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    @NotNull
    private AccountAddress address;

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_independence_status")
    @NotNull
    private FinancialIndependenceStatus financialIndependenceStatus;
    
    @Column(name = "migrated")
    private boolean migrated;

    @Builder.Default
    @OneToMany(mappedBy = "targetUnitAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TargetUnitAccountContact> targetUnitAccountContacts = new ArrayList<>();

    @Column(name = "terminated_date")
    private LocalDateTime terminatedDate;

    public void addContact(TargetUnitAccountContact targetUnitAccountContact) {
        if (this.targetUnitAccountContacts == null) {
            this.targetUnitAccountContacts = new ArrayList<>();
        }

        targetUnitAccountContact.setTargetUnitAccount(this);
        targetUnitAccountContacts.add(targetUnitAccountContact);
    }

    public void removeContact(TargetUnitAccountContact targetUnitAccountContact) {
        this.getTargetUnitAccountContacts().remove(targetUnitAccountContact);
    }
}
