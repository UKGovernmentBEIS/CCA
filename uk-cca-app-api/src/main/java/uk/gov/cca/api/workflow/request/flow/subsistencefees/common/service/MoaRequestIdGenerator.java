package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SectorMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.TargetUnitMoaRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestIdGenerator;

@Service
public class MoaRequestIdGenerator implements RequestIdGenerator {

	private static final String REQUEST_ID_FORMATTER = "%s-%s";

    @Override
    public String generate(RequestParams params) {
        final MoaRequestMetadata metaData = (MoaRequestMetadata) params.getRequestMetadata();
		return String.format(REQUEST_ID_FORMATTER, getMoaPrefix(params), metaData.getParentRequestId());
    }

	@Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.SECTOR_MOA, CcaRequestType.TARGET_UNIT_MOA);
    }

	private String getMoaPrefix(RequestParams params) {
		return CcaRequestType.SECTOR_MOA.equals(params.getType()) 
				? ((SectorMoaRequestMetadata) params.getRequestMetadata()).getSectorAcronym()
						: ((TargetUnitMoaRequestMetadata) params.getRequestMetadata()).getBusinessId();
	}
	
    @Override
    public String getPrefix() {
        return null;
    }
}
