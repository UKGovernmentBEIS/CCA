package uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.validation.UnderlyingAgreementPayloadValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.transform.UnderlyingAgreementSubmitMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementSubmitServiceTest {

	@InjectMocks
    private UnderlyingAgreementSubmitService service;
	
	@Mock
    private RequestService requestService;

	@Mock
	private UnderlyingAgreementPayloadValidatorService underlyingAgreementPayloadValidatorService;
	
	@Test
    void submitUnderlyingAgreement() {
    	AppUser authUser = AppUser.builder().userId("user1").build();
    	UUID att1UUID = UUID.randomUUID();
    	Request request = Request.builder()
    			.id("1")
    			.payload(UnderlyingAgreementRequestPayload.builder().build())
    			.build();


		UnderlyingAgreementPayload una = UnderlyingAgreementPayload
    			.builder()
				.underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().operatorName("name").build())
				.underlyingAgreement(UnderlyingAgreement.builder()
						.targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
						.build())
				.build();
        UnderlyingAgreementSubmitRequestTaskPayload requestTaskPayload = UnderlyingAgreementSubmitRequestTaskPayload.builder()
        		.underlyingAgreement(una)
        		.sectionsCompleted(Map.of("section1", "completed"))
        		.underlyingAgreementAttachments(Map.of(att1UUID, "att1"))
        		.build();
    	RequestTask requestTask = RequestTask.builder()
    			.request(request)
    			.payload(requestTaskPayload)
    			.build();

		// Invoke
    	service.submitUnderlyingAgreement(requestTask, authUser);

		// Verify
    	verify(underlyingAgreementPayloadValidatorService, times(1)).validate(requestTask);
    	UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();
        assertThat(requestPayload.getUnderlyingAgreement()).isEqualTo(una);
        assertThat(requestPayload.getSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(Map.of("section1", "completed"));
        assertThat(requestPayload.getUnderlyingAgreementAttachments()).containsExactlyInAnyOrderEntriesOf(Map.of(att1UUID, "att1"));

        
        UnderlyingAgreementSubmittedRequestActionPayload actionPayload = Mappers
				.getMapper(UnderlyingAgreementSubmitMapper.class).toUnderlyingAgreementSubmittedRequestActionPayload(
						(UnderlyingAgreementSubmitRequestTaskPayload) requestTask.getPayload(),
						CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(request, actionPayload, CcaRequestActionType.UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED, authUser.getUserId());
    }
}
