package uk.gov.cca.api.workflow.request.flow.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class CcaRequestParams extends RequestParams {

	public Long getSectorId() {
        return getRequestResources().get(CcaResourceType.SECTOR_ASSOCIATION) != null 
        		? Long.parseLong(getRequestResources().get(CcaResourceType.SECTOR_ASSOCIATION))
        				: null;
	}
}
