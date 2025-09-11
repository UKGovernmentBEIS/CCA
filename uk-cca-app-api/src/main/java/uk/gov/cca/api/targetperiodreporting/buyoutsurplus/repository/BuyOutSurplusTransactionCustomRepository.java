package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.QBuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionListItemDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.QTargetPeriod;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.QPerformanceDataEntity;
import uk.gov.netz.api.account.domain.QAccount;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class BuyOutSurplusTransactionCustomRepository {
    
    @PersistenceContext
    private EntityManager em;
    
    
    public BuyOutSurplusTransactionsListDTO findBuyOutSurplusTransactions(CompetentAuthorityEnum competentAuthority, BuyOutSurplusTransactionsListSearchCriteria criteria) {
        
        QBuyOutSurplusTransaction buyOutSurplusTransaction = QBuyOutSurplusTransaction.buyOutSurplusTransaction;
        QPerformanceDataEntity performanceData = QPerformanceDataEntity.performanceDataEntity;
        QAccount account = QAccount.account;
        QTargetPeriod targetPeriod = QTargetPeriod.targetPeriod;
        
        BooleanBuilder where = constructWhereClause(competentAuthority, criteria, targetPeriod, account, buyOutSurplusTransaction);
        
        JPAQuery<BuyOutSurplusTransactionListItemDTO> query = new JPAQuery<>(em)
                .select(Projections.constructor(
                        BuyOutSurplusTransactionListItemDTO.class,
                        buyOutSurplusTransaction.id,
                        account.businessId,
                        account.name,
                        buyOutSurplusTransaction.transactionCode,
                        buyOutSurplusTransaction.creationDate,
                        buyOutSurplusTransaction.paymentStatus,
                        buyOutSurplusTransaction.buyOutFee))
                .from(buyOutSurplusTransaction)
                .innerJoin(performanceData).on(performanceData.id.eq(buyOutSurplusTransaction.performanceDataId))
                .innerJoin(account).on(account.id.eq(performanceData.accountId))
                .innerJoin(targetPeriod).on(targetPeriod.id.eq(performanceData.targetPeriod.id))
                .where(where);
        
        List<BuyOutSurplusTransactionListItemDTO> list = query.orderBy(
                        account.businessId.asc(),
                        buyOutSurplusTransaction.creationDate.desc()
                )
                .offset((long)criteria.getPaging().getPageNumber() * criteria.getPaging().getPageSize())
                .limit(criteria.getPaging().getPageSize())
                .fetch();
        
        return BuyOutSurplusTransactionsListDTO.builder()
                .transactions(list)
                .total(query.fetchCount())
                .build();
        
    }
    
    private static @NotNull BooleanBuilder constructWhereClause(CompetentAuthorityEnum competentAuthority, BuyOutSurplusTransactionsListSearchCriteria criteria, QTargetPeriod targetPeriod, QAccount account, QBuyOutSurplusTransaction buyOutSurplusTransaction) {
        BooleanBuilder where = new BooleanBuilder()
                .and(account.competentAuthority.eq(competentAuthority))
                .and(targetPeriod.businessId.eq(criteria.getTargetPeriodType()));
        
        String term = StringUtils.trimToNull(criteria.getTerm());
        if (term != null) {
        	String termLike = "%" + term + "%";
            where.and((account.businessId.likeIgnoreCase(termLike))
                    .or(account.name.likeIgnoreCase(termLike))
                    .or(buyOutSurplusTransaction.transactionCode.likeIgnoreCase(termLike)));
        }
        
        if (criteria.getBuyOutSurplusPaymentStatus() != null) {
            where.and(buyOutSurplusTransaction.paymentStatus.eq(criteria.getBuyOutSurplusPaymentStatus()));
        }
        
        return where;
    }
}
