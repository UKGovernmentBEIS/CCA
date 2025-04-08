package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestIdGenerator;

import java.util.List;

@Service
public class PerformanceDataSpreadsheetGenerateRequestIdGenerator implements RequestIdGenerator {

	@Override
	public String generate(RequestParams params) {
		final PerformanceDataSpreadsheetGenerateRequestMetadata metadata =
				(PerformanceDataSpreadsheetGenerateRequestMetadata) params.getRequestMetadata();

		final String accountAcronym = metadata.getAccountBusinessId();
		final String tp = metadata.getTargetPeriodType().name();

		return String.format("%s-%s-%s", accountAcronym, getPrefix(), tp);
	}

	@Override
	public List<String> getTypes() {
		return List.of(CcaRequestType.PERFORMANCE_DATA_SPREADSHEET_GENERATE);
	}

	@Override
	public String getPrefix() {
		return "TPRGN";
	}
}
