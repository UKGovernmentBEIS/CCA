package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {CcaRequestActionPayloadType.class})
public interface PerformanceDataFacilityDataUploadCompletedMapper {

	@Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.PERFORMANCE_DATA_FACILITY_UPLOAD_COMPLETED_PAYLOAD)")
	@Mapping(target = "attachments", ignore = true)
	@Mapping(target = "uploadAttachments", ignore = true)
	PerformanceDataFacilityDataUploadCompletedRequestActionPayload toPerformanceDataFacilityDataUploadCompletedRequestActionPayload(
			PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload);
	
	@AfterMapping
	default void setAttachments(
			@MappingTarget PerformanceDataFacilityDataUploadCompletedRequestActionPayload requestActionPayload,
			PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload) {
		requestActionPayload.setUploadAttachments(taskPayload.getUploadAttachments());
	}

}
