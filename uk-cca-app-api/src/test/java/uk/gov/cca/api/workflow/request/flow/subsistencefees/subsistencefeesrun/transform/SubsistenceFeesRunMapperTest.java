package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Year;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaReport;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;


class SubsistenceFeesRunMapperTest {

	private final SubsistenceFeesRunMapper mapper = Mappers.getMapper(SubsistenceFeesRunMapper.class);
	
	@Test
    void toCompletedActionPayload() {
		String requestId = "S2501";
		String status = "COMPLETED_WITH_FAILURES";
		SubsistenceFeesRunRequestPayload payload = SubsistenceFeesRunRequestPayload.builder().build();
    	SubsistenceFeesRunRequestMetadata metadata = SubsistenceFeesRunRequestMetadata.builder()
    			.chargingYear(Year.of(2025))
    			.accountsReports(Map.of(1L, MoaReport.builder().succeeded(false).build()))
    			.sectorsReports(Map.of(1L, MoaReport.builder().succeeded(true).build()))
    			.build();
    	
		SubsistenceFeesRunCompletedRequestActionPayload result = mapper.toCompletedActionPayload(
				payload, metadata, requestId, status);
    	
    	assertThat(result).isEqualTo(SubsistenceFeesRunCompletedRequestActionPayload.builder()
    			.payloadType(CcaRequestActionPayloadType.SUBSISTENCE_FEES_RUN_COMPLETED_PAYLOAD)
        		.paymentRequestId(requestId)
        		.chargingYear(Year.of(2025))
				.sentInvoices(1L)
				.failedInvoices(1L)
				.status("COMPLETED_WITH_FAILURES")
				.build());
    }
}
