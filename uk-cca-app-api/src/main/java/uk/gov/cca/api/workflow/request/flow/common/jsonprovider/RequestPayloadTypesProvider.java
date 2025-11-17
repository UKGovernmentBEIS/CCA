package uk.gov.cca.api.workflow.request.flow.common.jsonprovider;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.ADMIN_TERMINATION_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.BUY_OUT_SURPLUS_ACCOUNT_PROCESSING_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.BUY_OUT_SURPLUS_RUN_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.CCA2_EXTENSION_NOTICE_RUN_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.FACILITY_AUDIT_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.FACILITY_CERTIFICATION_RUN_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.PERFORMANCE_DATA_DOWNLOAD_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.PERFORMANCE_DATA_GENERATE_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.PERFORMANCE_DATA_PROCESSING_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.PERFORMANCE_DATA_SPREADSHEET_GENERATE_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.PERFORMANCE_DATA_SPREADSHEET_PROCESSING_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.PERFORMANCE_DATA_UPLOAD_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.SECTOR_MOA_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.SUBSISTENCE_FEES_RUN_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.TARGET_UNIT_ACCOUNT_CREATION_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.TARGET_UNIT_MOA_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.UNDERLYING_AGREEMENT_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.UNDERLYING_AGREEMENT_VARIATION_REQUEST_PAYLOAD;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3ExistingFacilitiesMigrationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.PerformanceDataGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataDownloadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.PerformanceDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;

@Component
public class RequestPayloadTypesProvider implements JsonSubTypesProvider {

	@Override
	public List<NamedType> getTypes() {
		return List.of(
				new NamedType(TargetUnitAccountCreationRequestPayload.class, TARGET_UNIT_ACCOUNT_CREATION_REQUEST_PAYLOAD),
				new NamedType(UnderlyingAgreementRequestPayload.class, UNDERLYING_AGREEMENT_REQUEST_PAYLOAD),
				new NamedType(UnderlyingAgreementVariationRequestPayload.class, UNDERLYING_AGREEMENT_VARIATION_REQUEST_PAYLOAD),
				new NamedType(AdminTerminationRequestPayload.class, ADMIN_TERMINATION_REQUEST_PAYLOAD),
				new NamedType(PerformanceDataDownloadRequestPayload.class, PERFORMANCE_DATA_DOWNLOAD_PAYLOAD),
				new NamedType(PerformanceDataGenerateRequestPayload.class, PERFORMANCE_DATA_GENERATE_PAYLOAD),
				new NamedType(PerformanceDataSpreadsheetGenerateRequestPayload.class, PERFORMANCE_DATA_SPREADSHEET_GENERATE_PAYLOAD),
				new NamedType(PerformanceDataUploadRequestPayload.class, PERFORMANCE_DATA_UPLOAD_PAYLOAD),
				new NamedType(PerformanceDataProcessingRequestPayload.class, PERFORMANCE_DATA_PROCESSING_PAYLOAD),
				new NamedType(PerformanceDataSpreadsheetProcessingRequestPayload.class, PERFORMANCE_DATA_SPREADSHEET_PROCESSING_PAYLOAD),

				new NamedType(PerformanceAccountTemplateDataUploadRequestPayload.class,
						CcaRequestPayloadType.PERFORMANCE_ACCCOUNT_TEMPLATE_DATA_UPLOAD_PAYLOAD),
				new NamedType(PerformanceAccountTemplateDataProcessingRequestPayload.class,
						CcaRequestPayloadType.PERFORMANCE_ACCCOUNT_TEMPLATE_DATA_PROCESSING_PAYLOAD),

				new NamedType(SubsistenceFeesRunRequestPayload.class, SUBSISTENCE_FEES_RUN_REQUEST_PAYLOAD),
				new NamedType(SectorMoaRequestPayload.class, SECTOR_MOA_REQUEST_PAYLOAD),
				new NamedType(TargetUnitMoaRequestPayload.class, TARGET_UNIT_MOA_REQUEST_PAYLOAD),

				new NamedType(BuyOutSurplusRunRequestPayload.class, BUY_OUT_SURPLUS_RUN_REQUEST_PAYLOAD),
				new NamedType(BuyOutSurplusAccountProcessingRequestPayload.class, BUY_OUT_SURPLUS_ACCOUNT_PROCESSING_PAYLOAD),

				new NamedType(FacilityCertificationRunRequestPayload.class, FACILITY_CERTIFICATION_RUN_REQUEST_PAYLOAD),

				new NamedType(Cca3ExistingFacilitiesMigrationRunRequestPayload.class, CCA3_EXISTING_FACILITIES_MIGRATION_RUN_REQUEST_PAYLOAD),
				new NamedType(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.class, CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_PAYLOAD),

        		new NamedType(FacilityAuditRequestPayload.class, FACILITY_AUDIT_REQUEST_PAYLOAD),

				new NamedType(Cca2ExtensionNoticeRunRequestPayload.class, CCA2_EXTENSION_NOTICE_RUN_REQUEST_PAYLOAD),
				new NamedType(Cca2ExtensionNoticeAccountProcessingRequestPayload.class, CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_PAYLOAD)
		);
	}

}
