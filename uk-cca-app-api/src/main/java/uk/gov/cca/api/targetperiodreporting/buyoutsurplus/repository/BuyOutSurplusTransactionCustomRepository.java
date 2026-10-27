package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.account.domain.QTargetUnitAccount;
import uk.gov.cca.api.facility.domain.QFacilityData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.QBuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.QBuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionListItemDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.QPerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.QTargetPeriod;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Arrays;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class BuyOutSurplusTransactionCustomRepository {
    
    @PersistenceContext
    private EntityManager em;
    
    public BuyOutSurplusTransactionsListDTO findBuyOutSurplusTransactions(CompetentAuthorityEnum competentAuthority,
                                                                          BuyOutSurplusTransactionsListSearchCriteria criteria) {
        QBuyOutSurplusTransaction transaction = QBuyOutSurplusTransaction.buyOutSurplusTransaction;
        QBuyOutSurplusTransactionDetail transactionDetail = QBuyOutSurplusTransactionDetail.buyOutSurplusTransactionDetail;
        QTargetUnitAccount account = QTargetUnitAccount.targetUnitAccount;
        QTargetPeriod targetPeriod = QTargetPeriod.targetPeriod;

        BooleanBuilder where = constructWhereClause(competentAuthority, criteria, targetPeriod, account, transaction);

        NumberExpression<Integer> underAppealCount = underAppealCount(transactionDetail);
        NumberExpression<Integer> awaitingPaymentCount = awaitingPaymentCount(transactionDetail);
        NumberExpression<Integer> awaitingRefundCount = awaitingRefundCount(transactionDetail);
        NumberExpression<Integer> paidCount = paidCount(transactionDetail);
        NumberExpression<Integer> refundCount = refundCount(transactionDetail);
        NumberExpression<Integer> notRequiredCount = notRequiredCount(transactionDetail);

        JPAQuery<BuyOutSurplusTransactionListItemDTO> query = new JPAQuery<>(em)
                .select(Projections.constructor(
                        BuyOutSurplusTransactionListItemDTO.class,
                        transaction.id,
                        account.businessId,
                        account.name,
                        transaction.transactionCode,
                        transaction.creationDate,
                        targetPeriod.schemeVersion,
                        transaction.performanceDataResourceType,
                        new CaseBuilder()
                                .when(underAppealCount.gt(0)).then(BuyOutSurplusPaymentStatus.UNDER_APPEAL.name())
                                .when(awaitingPaymentCount.gt(0)).then(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT.name())
                                .when(awaitingRefundCount.gt(0)).then(BuyOutSurplusPaymentStatus.AWAITING_REFUND.name())
                                .when(paidCount.gt(0)).then(BuyOutSurplusPaymentStatus.PAID.name())
                                .when(refundCount.gt(0)).then(BuyOutSurplusPaymentStatus.REFUNDED.name())
                                .when(notRequiredCount.gt(0)).then(BuyOutSurplusPaymentStatus.NOT_REQUIRED.name())
                                .otherwise(BuyOutSurplusPaymentStatus.TERMINATED.name()),
                        transactionDetail.buyOutFee.sum())
                )
                .from(transaction)
                .innerJoin(transactionDetail).on(transactionDetail.transaction.id.eq(transaction.id))
                .innerJoin(account).on(account.id.eq(transaction.accountId))
                .innerJoin(targetPeriod).on(targetPeriod.id.eq(transaction.targetPeriod.id))
                .where(where)
                .groupBy(
                        transaction.id,
                        account.businessId,
                        account.name,
                        transaction.transactionCode,
                        transaction.creationDate,
                        targetPeriod.schemeVersion
                );

        // Filter payment status
        if (criteria.getPaymentStatus() != null) {
            query.having(paymentStatusHaving(
                    criteria.getPaymentStatus(),
                    transactionDetail
            ));
        }

        List<BuyOutSurplusTransactionListItemDTO> list = query
                .orderBy(
                        account.businessId.asc(),
                        transaction.creationDate.desc(),
                        transaction.transactionCode.desc()
                )
                .offset((long)criteria.getPaging().getPageNumber() * criteria.getPaging().getPageSize())
                .limit(criteria.getPaging().getPageSize()).fetch();

        return BuyOutSurplusTransactionsListDTO.builder()
                .transactions(list)
                .total(query.fetchCount())
                .build();
    }
    
    private static @NotNull BooleanBuilder constructWhereClause(CompetentAuthorityEnum competentAuthority,
                                                                BuyOutSurplusTransactionsListSearchCriteria criteria,
                                                                QTargetPeriod targetPeriod,
                                                                QTargetUnitAccount account,
                                                                QBuyOutSurplusTransaction transaction) {
        BooleanBuilder where = new BooleanBuilder()
                .and(account.competentAuthority.eq(competentAuthority))
                .and(targetPeriod.schemeVersion.eq(criteria.getSchemeVersion()));

        if(criteria.getTargetPeriodType() != null) {
            where.and(targetPeriod.businessId.eq(criteria.getTargetPeriodType()));
        }
        
        String term = StringUtils.trimToNull(criteria.getTerm());
        if (term != null) {
        	String termLike = "%" + term + "%";
            where.and((account.businessId.likeIgnoreCase(termLike))
                    .or(account.name.likeIgnoreCase(termLike))
                    .or(transaction.transactionCode.likeIgnoreCase(termLike))
                    .or(facilityMatchesTerm(transaction, termLike)));
        }
        
        return where;
    }
    
    private static BooleanExpression facilityMatchesTerm(QBuyOutSurplusTransaction transaction, String termLike) {

        QBuyOutSurplusTransactionDetail transactionDetailSub = new QBuyOutSurplusTransactionDetail("transactionDetailSub");

        QPerformanceDataFacilityEntity performanceData = new QPerformanceDataFacilityEntity("performanceData");

        QFacilityData facilityData = new QFacilityData("facilityData");

        return JPAExpressions.selectOne()
                .from(transactionDetailSub)
                .join(performanceData).on(performanceData.id.eq(transactionDetailSub.performanceDataId))
                .join(facilityData).on(facilityData.id.eq(performanceData.facilityId))
                .where(transactionDetailSub.transaction.id.eq(transaction.id)
                        .and(transaction.performanceDataResourceType.eq(PerformanceDataResourceType.FACILITY))
                        .and(facilityData.facilityBusinessId.likeIgnoreCase(termLike)
                                .or(facilityData.siteName.likeIgnoreCase(termLike))))
                .exists();
    }

    private NumberExpression<Integer> underAppealCount(QBuyOutSurplusTransactionDetail transactionDetail) {
        return new CaseBuilder()
                .when(transactionDetail.paymentStatus.eq(BuyOutSurplusPaymentStatus.UNDER_APPEAL))
                .then(1)
                .otherwise(0)
                .sum();
    }

    private NumberExpression<Integer> awaitingPaymentCount(QBuyOutSurplusTransactionDetail transactionDetail) {
        return new CaseBuilder()
                .when(transactionDetail.paymentStatus.eq(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT))
                .then(1)
                .otherwise(0)
                .sum();
    }

    private NumberExpression<Integer> awaitingRefundCount(QBuyOutSurplusTransactionDetail transactionDetail) {
        return new CaseBuilder()
                .when(transactionDetail.paymentStatus.eq(BuyOutSurplusPaymentStatus.AWAITING_REFUND))
                .then(1)
                .otherwise(0)
                .sum();
    }

    private NumberExpression<Integer> paidCount(QBuyOutSurplusTransactionDetail transactionDetail) {
        return new CaseBuilder()
                .when(transactionDetail.paymentStatus.eq(BuyOutSurplusPaymentStatus.PAID))
                .then(1)
                .otherwise(0)
                .sum();
    }

    private NumberExpression<Integer> refundCount(QBuyOutSurplusTransactionDetail transactionDetail) {
        return new CaseBuilder()
                .when(transactionDetail.paymentStatus.eq(BuyOutSurplusPaymentStatus.REFUNDED))
                .then(1)
                .otherwise(0)
                .sum();
    }

    private NumberExpression<Integer> notRequiredCount(QBuyOutSurplusTransactionDetail transactionDetail) {
        return new CaseBuilder()
                .when(transactionDetail.paymentStatus.eq(BuyOutSurplusPaymentStatus.NOT_REQUIRED))
                .then(1)
                .otherwise(0)
                .sum();
    }

    private NumberExpression<Integer> terminatedCount(QBuyOutSurplusTransactionDetail transactionDetail) {
        return new CaseBuilder()
                .when(transactionDetail.paymentStatus.eq(BuyOutSurplusPaymentStatus.TERMINATED))
                .then(1)
                .otherwise(0)
                .sum();
    }

    private NumberExpression<Integer> statusCount(BuyOutSurplusPaymentStatus status, QBuyOutSurplusTransactionDetail transactionDetail) {
        return switch (status) {
            case UNDER_APPEAL -> underAppealCount(transactionDetail);
            case AWAITING_PAYMENT -> awaitingPaymentCount(transactionDetail);
            case AWAITING_REFUND -> awaitingRefundCount(transactionDetail);
            case PAID -> paidCount(transactionDetail);
            case REFUNDED -> refundCount(transactionDetail);
            case NOT_REQUIRED -> notRequiredCount(transactionDetail);
            case TERMINATED -> terminatedCount(transactionDetail);
        };
    }

    private BooleanExpression paymentStatusHaving(BuyOutSurplusPaymentStatus status, QBuyOutSurplusTransactionDetail transactionDetail) {
        return statusCount(status, transactionDetail)
                .gt(0)
                .and(
                        Arrays.stream(BuyOutSurplusPaymentStatus.values()).toList().stream()
                                .filter(s -> s.getPriority() < status.getPriority())
                                .map(s -> statusCount(s, transactionDetail).eq(0))
                                .reduce(BooleanExpression::and)
                                .orElse(Expressions.TRUE)
                );
    }
}
