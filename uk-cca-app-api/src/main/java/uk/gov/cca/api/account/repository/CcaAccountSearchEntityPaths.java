package uk.gov.cca.api.account.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;

import uk.gov.cca.api.account.domain.QTargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.netz.api.account.domain.enumeration.AccountStatus;
import uk.gov.netz.api.account.search.paths.AccountSearchEntityPaths;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Set;
import java.util.stream.Collectors;

public class CcaAccountSearchEntityPaths implements AccountSearchEntityPaths<TargetUnitAccount> {

    private static final QTargetUnitAccount ACCOUNT = QTargetUnitAccount.targetUnitAccount;

    @Override
    public EntityPathBase<TargetUnitAccount> root() {
        return ACCOUNT;
    }

    @Override
    public NumberPath<Long> idPath() {
        return ACCOUNT.id;
    }

    @Override
    public StringPath namePath() {
        return ACCOUNT.name;
    }

    @Override
    public StringPath businessIdPath() {
        return ACCOUNT.businessId;
    }

    @Override
    public EnumPath<CompetentAuthorityEnum> competentAuthorityPath() {
        return ACCOUNT.competentAuthority;
    }

    @Override
    public StringExpression statusPath() {
        return ACCOUNT.status.stringValue();
    }

    @Override
    public BooleanExpression statusIn(Set<? extends AccountStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        Set<TargetUnitAccountStatus> mappedStatuses = statuses.stream()
                .map(status -> TargetUnitAccountStatus.valueOf(status.getName()))
                .collect(Collectors.toSet());
        return ACCOUNT.status.in(mappedStatuses);
    }
}
