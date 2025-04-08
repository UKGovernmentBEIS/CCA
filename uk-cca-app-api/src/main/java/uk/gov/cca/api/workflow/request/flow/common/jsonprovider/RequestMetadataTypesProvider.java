package uk.gov.cca.api.workflow.request.flow.common.jsonprovider;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SectorMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.TargetUnitMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestMetadata;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.BUY_OUT_SURPLUS_ACCOUNT_PROCESSING;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.BUY_OUT_SURPLUS_RUN;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.PERFORMANCE_DATA_GENERATE;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.PERFORMANCE_DATA_PROCESSING;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.SECTOR_MOA;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.SUBSISTENCE_FEES_RUN;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.TARGET_UNIT_ACCOUNT_CREATION;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.TARGET_UNIT_MOA;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.UNDERLYING_AGREEMENT;

@Component
public class RequestMetadataTypesProvider implements JsonSubTypesProvider {

	@Override
	public List<NamedType> getTypes() {
		return List.of(
				new NamedType(TargetUnitAccountCreationRequestPayload.class, TARGET_UNIT_ACCOUNT_CREATION),
				new NamedType(UnderlyingAgreementRequestMetadata.class, UNDERLYING_AGREEMENT),
				new NamedType(PerformanceDataSpreadsheetGenerateRequestMetadata.class, PERFORMANCE_DATA_GENERATE),
				new NamedType(PerformanceDataSpreadsheetProcessingRequestMetadata.class, PERFORMANCE_DATA_PROCESSING),
				new NamedType(PerformanceAccountTemplateProcessingRequestMetadata.class, CcaRequestMetadataType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING),
				new NamedType(SubsistenceFeesRunRequestMetadata.class, SUBSISTENCE_FEES_RUN),
				new NamedType(SectorMoaRequestMetadata.class, SECTOR_MOA),
				new NamedType(TargetUnitMoaRequestMetadata.class, TARGET_UNIT_MOA),
				new NamedType(BuyOutSurplusRunRequestMetadata.class, BUY_OUT_SURPLUS_RUN),
				new NamedType(BuyOutSurplusAccountProcessingRequestMetadata.class, BUY_OUT_SURPLUS_ACCOUNT_PROCESSING)
		);
	}

}
