package uk.gov.cca.api.account.repository;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;

import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.netz.api.account.search.paths.AccountSearchEntityPaths;
import uk.gov.netz.api.account.search.query.AccountSearchProjectionMapper;
import uk.gov.netz.api.account.search.query.AccountSearchResultRow;

public class CcaAccountSearchProjectionMapper implements AccountSearchProjectionMapper<TargetUnitAccount, AccountSearchResultRow> {

    @Override
    public ConstructorExpression<AccountSearchResultRow> constructorProjection(AccountSearchEntityPaths<TargetUnitAccount> paths) {
        return Projections.constructor(
                AccountSearchResultRow.class,
                paths.idPath(),
                paths.namePath(),
                paths.businessIdPath(),
                paths.statusPath());
    }
}
