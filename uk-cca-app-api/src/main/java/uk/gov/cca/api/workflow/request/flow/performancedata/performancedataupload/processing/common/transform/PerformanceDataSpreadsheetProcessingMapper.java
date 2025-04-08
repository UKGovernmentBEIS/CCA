package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PerformanceDataSpreadsheetProcessingMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED_PAYLOAD)")
    PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload toPerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload(
            PerformanceDataSpreadsheetProcessingRequestMetadata metadata, PerformanceDataSpreadsheetProcessingRequestPayload requestPayload);

    @Mapping(target = "sectorAcronym", source = "metadata.sectorAssociationInfo.acronym")
    PerformanceDataReferenceDetails toPerformanceDataReferenceDetails(TargetUnitAccountDetailsDTO accountDetails,
                                                                      UnderlyingAgreementDTO underlyingAgreement,
                                                                      PerformanceDataCalculatedMetrics performanceDataCalculatedMetrics,
                                                                      PerformanceDataSpreadsheetProcessingRequestMetadata metadata,
                                                                      FileDTO excelFile,
                                                                      PerformanceDataContainer lastUploadedReport);
}
