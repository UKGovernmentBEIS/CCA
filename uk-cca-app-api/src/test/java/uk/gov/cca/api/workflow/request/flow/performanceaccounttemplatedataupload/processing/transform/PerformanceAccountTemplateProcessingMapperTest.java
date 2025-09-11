package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Year;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.TargetUnitIdentityAndPerformance;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingSubmittedRequestActionPayload;


@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateProcessingMapperTest {

	private PerformanceAccountTemplateProcessingMapper cut;

	@BeforeEach
	void init() {
		cut = Mappers.getMapper(PerformanceAccountTemplateProcessingMapper.class);
	}

	@Test
	void toSubmittedAction() {
		PerformanceAccountTemplateProcessingRequestMetadata metadata = PerformanceAccountTemplateProcessingRequestMetadata.builder()
				.accountId(1L)
				.accountBusinessId("AccBId1")
				.sectorUserAssignee("secUserAs")
				.targetPeriodType(TargetPeriodType.TP5)
				.targetPeriodYear(Year.of(2024))
				.build();
		
		TargetUnitIdentityAndPerformance targetUnitIdentityAndPerformance = TargetUnitIdentityAndPerformance.builder()
				.performanceImpactedByAnyImplementedMeasures("Yes").build();
		PerformanceAccountTemplateDataContainer data = PerformanceAccountTemplateDataContainer.builder()
				.targetUnitIdentityAndPerformance(targetUnitIdentityAndPerformance)
				.build();
		
		var result = cut.toSubmittedAction(metadata, data);
		
		assertThat(result).isEqualTo(PerformanceAccountTemplateProcessingSubmittedRequestActionPayload.builder()
				.businessId(metadata.getAccountBusinessId())
				.data(data)
				.payloadType(CcaRequestActionPayloadType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED_PAYLOAD)
				.targetPeriodType(TargetPeriodType.TP5)
				.targetPeriodYear(Year.of(2024))
				.build());
	}
}
