package uk.gov.cca.api.subsistencefees.repository;

import java.math.BigDecimal;

import org.apache.commons.lang3.ObjectUtils;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.QSubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.QSubsistenceFeesMoaFacility;

@UtilityClass
public class SubsistenceFeesRepositoryUtils {

	public NumberExpression<BigDecimal> facilityOutstandingAmount(QSubsistenceFeesMoaFacility facility) {
		return new CaseBuilder().when(facility.paymentStatus.eq(FacilityPaymentStatus.IN_PROGRESS))
				.then(facility.initialAmount).otherwise(BigDecimal.ZERO).sum();
	}

	public NumberExpression<BigDecimal> currentTotalAmount(QSubsistenceFeesMoaFacility facility) {
		return new CaseBuilder().when(facility.paymentStatus.ne(FacilityPaymentStatus.CANCELLED))
				.then(facility.initialAmount).otherwise(BigDecimal.ZERO).sum();
	}
	
	public <T> JPAQuery<T> constructPaymentStatusQuery(PaymentStatus paymentStatus,
			QSubsistenceFeesMoaFacility facility, QSubsistenceFeesMoa moa,
			JPAQuery<T> jpaQuery) {
		if (ObjectUtils.isNotEmpty(paymentStatus)) {
			switch(paymentStatus) {
			case PaymentStatus.CANCELLED: 
				jpaQuery = jpaQuery.having(currentTotalAmount(facility).eq(BigDecimal.ZERO));
				break;
			case PaymentStatus.OVERPAID:
				jpaQuery = jpaQuery.having(currentTotalAmount(facility).lt(moa.regulatorReceivedAmount));
				break;
			case PaymentStatus.PAID:
				jpaQuery = jpaQuery.having(currentTotalAmount(facility).eq(moa.regulatorReceivedAmount));
				break;
			case PaymentStatus.AWAITING_PAYMENT:
				jpaQuery = jpaQuery.having(currentTotalAmount(facility).gt(moa.regulatorReceivedAmount));
				break;
			}
		}
		return jpaQuery;
	}

	public <T> JPAQuery<T> constructMarkFacilitiesStatusQuery(
			FacilityPaymentStatus markFacilitiesStatus, QSubsistenceFeesMoaFacility facility,
			JPAQuery<T> jpaQuery) {
		if (ObjectUtils.isNotEmpty(markFacilitiesStatus)) {
			switch(markFacilitiesStatus) {
			case FacilityPaymentStatus.CANCELLED: 
				jpaQuery = jpaQuery.having(currentTotalAmount(facility).eq(BigDecimal.ZERO));
				break;
			case FacilityPaymentStatus.COMPLETED:
				jpaQuery = jpaQuery.having(currentTotalAmount(facility).gt(BigDecimal.ZERO)
						.and(facilityOutstandingAmount(facility).eq(BigDecimal.ZERO)));
				break;
			case FacilityPaymentStatus.IN_PROGRESS:
				jpaQuery = jpaQuery.having(currentTotalAmount(facility).gt(BigDecimal.ZERO)
						.and(facilityOutstandingAmount(facility).gt(BigDecimal.ZERO)));
				break;
			}
		}
		return jpaQuery;
	}
}
