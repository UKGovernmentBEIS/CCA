package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface SubsistenceFeesRunMapper {
	
	@Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.SUBSISTENCE_FEES_RUN_COMPLETED_PAYLOAD)")
	SubsistenceFeesRunCompletedRequestActionPayload toCompletedActionPayload(
			SubsistenceFeesRunRequestPayload requestPayload, SubsistenceFeesRunRequestMetadata metadata, 
			String paymentRequestId, String status);
}
