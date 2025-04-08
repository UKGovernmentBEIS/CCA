package uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmittedRequestActionPayload;

class UnderlyingAgreementSubmitMapperTest {

	private final UnderlyingAgreementSubmitMapper mapper = Mappers.getMapper(UnderlyingAgreementSubmitMapper.class);
    
    @Test
    void toUnderlyingAgreementSubmittedRequestActionPayload() {
    	UUID uuid = UUID.randomUUID();

		UnderlyingAgreementPayload unaPayload = UnderlyingAgreementPayload.builder()
				.underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder()
						.operatorName("test")
						.build())
				.underlyingAgreement(UnderlyingAgreement.builder()
						.targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
						.targetPeriod6Details(TargetPeriod6Details.builder()
								.targetComposition(TargetComposition.builder()
										.agreementCompositionType(AgreementCompositionType.ABSOLUTE)
										.calculatorFile(uuid)
										.build())
								.build())
						.build())
				.build();
    	
    	AccountReferenceData accountData = AccountReferenceData.builder()
    			.sectorAssociationDetails(SectorAssociationDetails.builder()
    					.measurementType(MeasurementType.ENERGY_KWH)
    					.throughputUnit("tonne")
    					.build())
    			.build();
    	
    	UnderlyingAgreementSubmitRequestTaskPayload taskPayload = UnderlyingAgreementSubmitRequestTaskPayload.builder()
				.underlyingAgreement(unaPayload)
    			.accountReferenceData(accountData)
    			.underlyingAgreementAttachments(Map.of(uuid, "uuid.pdf"))
    			.build();
    	
    	UnderlyingAgreementSubmittedRequestActionPayload result = 
    			mapper.toUnderlyingAgreementSubmittedRequestActionPayload(taskPayload, CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_SUBMITTED_PAYLOAD);
    	
    	assertThat(result).isEqualTo(UnderlyingAgreementSubmittedRequestActionPayload.builder()
    			.payloadType(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_SUBMITTED_PAYLOAD)
				.underlyingAgreement(unaPayload)
    			.accountReferenceData(accountData)
    			.underlyingAgreementAttachments(Map.of(uuid, "uuid.pdf"))
    			.build());
    }
}
