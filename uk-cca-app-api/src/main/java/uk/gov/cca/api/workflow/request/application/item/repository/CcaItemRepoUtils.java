package uk.gov.cca.api.workflow.request.application.item.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.workflow.request.core.domain.QRequestResource;
import uk.gov.netz.api.workflow.request.core.domain.QRequestTask;

@UtilityClass
public class CcaItemRepoUtils {

	public BooleanExpression constructSectorRequestTaskScopeWhereClause(Map<Long, Set<String>> scopedUserRequestTaskTypes,
            QRequestTask requestTask, QRequestResource requestResource) {
		
	List<BooleanExpression> orExpressions = new ArrayList<>();
	scopedUserRequestTaskTypes.forEach((sectorId, types) ->
		orExpressions.add(requestResource.resourceType.eq(CcaResourceType.SECTOR_ASSOCIATION)
				.and(requestResource.resourceId.eq(sectorId.toString()))
				.and(requestTask.type.code.in(types)))
	);
	
	return Expressions.booleanTemplate(constructMultipleOrWhereTemplate(orExpressions.size()), orExpressions);
	}
	
	private String constructMultipleOrWhereTemplate(int scopedRequestTaskTypesSize) {
        StringBuilder templateBuilder;
        if (scopedRequestTaskTypesSize == 0) {
            templateBuilder = new StringBuilder("(1 = -1)");
        } else {
            templateBuilder = new StringBuilder("(({0})");
            for (int i = 1; i < scopedRequestTaskTypesSize; i++) {
                templateBuilder.append(" or ({").append(i).append("})");
            }
            templateBuilder.append(")");
        }
        return templateBuilder.toString();
    }
}
