package uk.gov.cca.api.targetperiodreporting.performancedata.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tpr_performance_data_account_status",
		uniqueConstraints = @UniqueConstraint(columnNames = {"target_period_id", "account_id"}))
@NamedQuery(
		name = AccountPerformanceDataStatus.NAMED_QUERY_FIND_ELIGIBLE_ACCOUNTS_FOR_PERFORMANCE_DATA_REPORTING_BY_SECTOR,
		query =
				"select new uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO(tu.id, tu.businessId, tu.name) "
						+ "from TargetUnitAccount tu, TargetPeriod tp "
						+ "where ("
						+ "(tu.status = uk.gov.cca.api.account.domain.TargetUnitAccountStatus.LIVE) "
						+ "or (tu.status = uk.gov.cca.api.account.domain.TargetUnitAccountStatus.TERMINATED "
						+ "and tp.endDate < CAST(tu.terminatedDate AS date)) "
						+ ") "
						+ "and tp.endDate >= CAST(tu.acceptedDate AS date) "
						+ "and tp.id = :targetPeriodId "
						+ "and tu.sectorAssociationId = :sectorAssociationId "
						+ "and not exists "
						+ "(select 1 from AccountPerformanceDataStatus pds where tu.id = pds.accountId and pds.targetPeriod = tp and pds.locked = true)"
)
public class AccountPerformanceDataStatus {

	public static final String NAMED_QUERY_FIND_ELIGIBLE_ACCOUNTS_FOR_PERFORMANCE_DATA_REPORTING_BY_SECTOR = "AccountPerformanceDataStatus.findEligibleAccountsForPerformanceDataReportingBySector";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tpr_performance_data_account_status_seq")
	@SequenceGenerator(name = "tpr_performance_data_account_status_seq", sequenceName = "tpr_performance_data_account_status_seq", allocationSize = 1)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_period_id")
	private TargetPeriod targetPeriod;

	@NotNull
	@Column(name = "account_id")
	private Long accountId;

	@Column(name = "locked")
	private boolean locked;

	@NotNull
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "last_performance_data_id", referencedColumnName = "id")
	private PerformanceDataEntity lastPerformanceData;
}
