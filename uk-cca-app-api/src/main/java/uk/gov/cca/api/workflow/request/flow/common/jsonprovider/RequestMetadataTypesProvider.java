package uk.gov.cca.api.workflow.request.flow.common.jsonprovider;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2termination.processing.domain.Cca2TerminationAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3ExistingFacilitiesMigrationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain.FacilityCertificationAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SectorMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.TargetUnitMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestMetadata;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.BUY_OUT_SURPLUS_ACCOUNT_PROCESSING;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.BUY_OUT_SURPLUS_RUN;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.CCA2_EXTENSION_NOTICE_RUN;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.CCA2_TERMINATION_ACCOUNT_PROCESSING;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.CCA2_TERMINATION_RUN;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.FACILITY_CERTIFICATION_ACCOUNT_PROCESSING;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.FACILITY_CERTIFICATION_RUN;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.PERFORMANCE_DATA_GENERATE;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.PERFORMANCE_DATA_PROCESSING;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.SECTOR_MOA;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.SUBSISTENCE_FEES_RUN;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.TARGET_UNIT_ACCOUNT_CREATION;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.TARGET_UNIT_MOA;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.UNDERLYING_AGREEMENT;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.UNDERLYING_AGREEMENT_VARIATION;

@Component
public class RequestMetadataTypesProvider implements JsonSubTypesProvider {

	@Override
	public List<NamedType> getTypes() {
		return List.of(
				new NamedType(TargetUnitAccountCreationRequestPayload.class, TARGET_UNIT_ACCOUNT_CREATION),
				new NamedType(UnderlyingAgreementRequestMetadata.class, UNDERLYING_AGREEMENT),
				new NamedType(UnderlyingAgreementVariationRequestMetadata.class, UNDERLYING_AGREEMENT_VARIATION),
				new NamedType(PerformanceDataSpreadsheetGenerateRequestMetadata.class, PERFORMANCE_DATA_GENERATE),
				new NamedType(PerformanceDataSpreadsheetProcessingRequestMetadata.class, PERFORMANCE_DATA_PROCESSING),
				new NamedType(PerformanceAccountTemplateProcessingRequestMetadata.class, CcaRequestMetadataType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING),
				new NamedType(SubsistenceFeesRunRequestMetadata.class, SUBSISTENCE_FEES_RUN),
				new NamedType(SectorMoaRequestMetadata.class, SECTOR_MOA),
				new NamedType(TargetUnitMoaRequestMetadata.class, TARGET_UNIT_MOA),
				new NamedType(BuyOutSurplusRunRequestMetadata.class, BUY_OUT_SURPLUS_RUN),
				new NamedType(BuyOutSurplusAccountProcessingRequestMetadata.class, BUY_OUT_SURPLUS_ACCOUNT_PROCESSING),
				new NamedType(FacilityCertificationRunRequestMetadata.class, FACILITY_CERTIFICATION_RUN),
				new NamedType(FacilityCertificationAccountProcessingRequestMetadata.class, FACILITY_CERTIFICATION_ACCOUNT_PROCESSING),
				new NamedType(Cca3ExistingFacilitiesMigrationRunRequestMetadata.class, CCA3_EXISTING_FACILITIES_MIGRATION_RUN),
				new NamedType(Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata.class, CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING),
				new NamedType(Cca2ExtensionNoticeRunRequestMetadata.class, CCA2_EXTENSION_NOTICE_RUN),
				new NamedType(Cca2ExtensionNoticeAccountProcessingRequestMetadata.class, CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING),
				new NamedType(Cca2TerminationRunRequestMetadata.class, CCA2_TERMINATION_RUN),
				new NamedType(Cca2TerminationAccountProcessingRequestMetadata.class, CCA2_TERMINATION_ACCOUNT_PROCESSING)
		);
	}

}
