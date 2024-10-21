package uk.gov.cca.api.account.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.netz.api.account.domain.enumeration.AccountStatus;
import uk.gov.netz.api.account.repository.AccountBaseRepository;
import uk.gov.netz.api.account.service.ApprovedAccountQueryAbstractService;

import java.util.Set;

@Service
public class ApprovedTargetUnitAccountQueryService extends ApprovedAccountQueryAbstractService<TargetUnitAccount> {

    public ApprovedTargetUnitAccountQueryService(AccountBaseRepository<TargetUnitAccount> accountBaseRepository) {
        super(accountBaseRepository);
    }

    @Override
    public Set<AccountStatus> getStatusesConsideredNotApproved() { return null; }

}
