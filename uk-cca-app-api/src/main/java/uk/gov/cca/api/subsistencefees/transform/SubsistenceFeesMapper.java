package uk.gov.cca.api.subsistencefees.transform;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.FacilityProcessStatus;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.dto.FacilityProcessStatusCreationDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaSearchResultInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaTargetUnitSearchResultInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunDetailsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaDetails;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunMoaDetailsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunSearchResultInfo;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface SubsistenceFeesMapper {

    FacilityProcessStatus toFacilityProcessStatus(FacilityProcessStatusCreationDTO facilityProcessStatusCreationDTO);

	@Mapping(target = "outstandingTotalAmount", expression = "java(BigDecimal.ZERO.max(subsistenceFeesRunSearchResultInfo.getCurrentTotalAmount().subtract(subsistenceFeesRunSearchResultInfo.getReceivedAmount())))")
	@Mapping(target = "paymentStatus", expression = "java(updatePaymentStatus(subsistenceFeesRunSearchResultInfo.getCurrentTotalAmount(), subsistenceFeesRunSearchResultInfo.getReceivedAmount()))")
	@Mapping(target = "markFacilitiesStatus", expression = "java(updateMarkFacilitiesStatus(subsistenceFeesRunSearchResultInfo.getCurrentTotalAmount(), subsistenceFeesRunSearchResultInfo.getFacilityOutstandingAmount()))")
	SubsistenceFeesRunSearchResultInfoDTO toSubsistenceFeesRunSearchResultInfoDTO(SubsistenceFeesRunSearchResultInfo subsistenceFeesRunSearchResultInfo);

	@Mapping(target = "outstandingTotalAmount", expression = "java(BigDecimal.ZERO.max(subsistenceFeesRunDetailsInfo.getCurrentTotalAmount().subtract(subsistenceFeesRunMoaDetailsInfo.getReceivedAmount())))")
	@Mapping(target = "paymentStatus", expression = "java(updatePaymentStatus(subsistenceFeesRunDetailsInfo.getCurrentTotalAmount(), subsistenceFeesRunMoaDetailsInfo.getReceivedAmount()))")
	SubsistenceFeesRunDetailsDTO toSubsistenceFeesRunDetailsDTO(SubsistenceFeesRunDetailsInfo subsistenceFeesRunDetailsInfo, SubsistenceFeesRunMoaDetailsInfo subsistenceFeesRunMoaDetailsInfo);

	@Mapping(target = "outstandingTotalAmount", expression = "java(BigDecimal.ZERO.max(subsistenceFeesMoaSearchResultInfo.getCurrentTotalAmount().subtract(subsistenceFeesMoaSearchResultInfo.getReceivedAmount())))")
	@Mapping(target = "paymentStatus", expression = "java(updatePaymentStatus(subsistenceFeesMoaSearchResultInfo.getCurrentTotalAmount(), subsistenceFeesMoaSearchResultInfo.getReceivedAmount()))")
	@Mapping(target = "markFacilitiesStatus", expression = "java(updateMarkFacilitiesStatus(subsistenceFeesMoaSearchResultInfo.getCurrentTotalAmount(), subsistenceFeesMoaSearchResultInfo.getFacilityOutstandingAmount()))")
	SubsistenceFeesMoaSearchResultInfoDTO toSubsistenceFeesMoaSearchResultInfoDTO(SubsistenceFeesMoaSearchResultInfo subsistenceFeesMoaSearchResultInfo);
	
	@Mapping(target = "paymentStatus", expression = "java(updatePaymentStatus(moaDetails.getCurrentTotalAmount(), moaDetails.getReceivedAmount()))")
	@Mapping(target = "businessId", source = "businessId")
	@Mapping(target = "name", source = "name")
	@Mapping(target = "moaDocument", source = "moaDocument")
	SubsistenceFeesMoaDetailsDTO toSubsistenceFeesMoaDetailsDTO(
			SubsistenceFeesMoaDetails moaDetails, String businessId, String name, FileInfoDTO moaDocument);
	
	@Mapping(target = "markFacilitiesStatus", expression = "java(updateMarkFacilitiesStatus(subsistenceFeesMoaTargetUnitSearchResultInfo.getCurrentTotalAmount(), subsistenceFeesMoaTargetUnitSearchResultInfo.getFacilityOutstandingAmount()))")
	SubsistenceFeesMoaTargetUnitSearchResultInfoDTO toSubsistenceFeesMoaTargetUnitSearchResultInfoDTO(SubsistenceFeesMoaTargetUnitSearchResultInfo subsistenceFeesMoaTargetUnitSearchResultInfo);
	
	@Named("paymentStatus")
    default PaymentStatus updatePaymentStatus(BigDecimal currentTotalAmount, BigDecimal receivedAmount) {
		if (BigDecimal.ZERO.equals(currentTotalAmount)) {
			return PaymentStatus.CANCELLED;
		} else {
			return mapPaymentStatus(currentTotalAmount, receivedAmount);
		}
	}

	@Named("markFacilitiesStatus")
	default FacilityPaymentStatus updateMarkFacilitiesStatus(BigDecimal currentTotalAmount, BigDecimal facilityOutstandingAmount) {
		if (BigDecimal.ZERO.equals(currentTotalAmount)) {
			return FacilityPaymentStatus.CANCELLED;
		} else if (BigDecimal.ZERO.equals(facilityOutstandingAmount)) {
			return FacilityPaymentStatus.COMPLETED;
		} else {
			return FacilityPaymentStatus.IN_PROGRESS;
		}
	}

	private PaymentStatus mapPaymentStatus(BigDecimal currentTotalAmount, BigDecimal receivedAmount) {
		switch (currentTotalAmount.compareTo(receivedAmount)) {
		case -1: return PaymentStatus.OVERPAID;
		case 0: return PaymentStatus.PAID;
		default: return PaymentStatus.AWAITING_PAYMENT;
		}
	}
}
