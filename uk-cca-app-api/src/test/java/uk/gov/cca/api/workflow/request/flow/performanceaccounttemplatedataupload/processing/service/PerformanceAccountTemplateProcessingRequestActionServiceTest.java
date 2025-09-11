package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.Year;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.TargetType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.TargetUnitIdentityAndPerformance;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingSubmittedRequestActionPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateProcessingRequestActionServiceTest {

	@InjectMocks
    private PerformanceAccountTemplateProcessingRequestActionService cut;
	
	@Mock
	private RequestService requestService;
	
	@Test
	void addSubmittedAction() {
		Request request = Request.builder()
				.creationDate(LocalDateTime.now())
				.build();
		
		PerformanceAccountTemplateProcessingRequestMetadata metadata = PerformanceAccountTemplateProcessingRequestMetadata.builder()
				.accountId(1L)
				.accountBusinessId("AccBId1")
				.sectorUserAssignee("secUserAs")
				.targetPeriodType(TargetPeriodType.TP5)
				.targetPeriodYear(Year.of(2024))
				.build();
		
		TargetUnitIdentityAndPerformance targetUnitIdentityAndPerformance = TargetUnitIdentityAndPerformance.builder()
				.targetType(TargetType.ABSOLUTE).build();
		PerformanceAccountTemplateDataContainer data = PerformanceAccountTemplateDataContainer.builder()
				.targetUnitIdentityAndPerformance(targetUnitIdentityAndPerformance)
				.build();
		
		cut.addSubmittedAction(request, metadata, data);
		
		ArgumentCaptor<PerformanceAccountTemplateProcessingSubmittedRequestActionPayload> requestActionpayloadCaptor = ArgumentCaptor
				.forClass(PerformanceAccountTemplateProcessingSubmittedRequestActionPayload.class);
		
		verify(requestService, times(1)).addActionToRequest(Mockito.eq(request), requestActionpayloadCaptor.capture(),
				Mockito.eq(CcaRequestActionType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED),
				Mockito.eq(metadata.getSectorUserAssignee()));
		
		PerformanceAccountTemplateProcessingSubmittedRequestActionPayload result = requestActionpayloadCaptor.getValue();
		
		assertThat(result).isEqualTo(PerformanceAccountTemplateProcessingSubmittedRequestActionPayload.builder()
				.businessId(metadata.getAccountBusinessId())
				.data(data)
				.payloadType(CcaRequestActionPayloadType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED_PAYLOAD)
				.targetPeriodType(TargetPeriodType.TP5)
				.targetPeriodYear(Year.of(2024))
				.build());
	}
}
