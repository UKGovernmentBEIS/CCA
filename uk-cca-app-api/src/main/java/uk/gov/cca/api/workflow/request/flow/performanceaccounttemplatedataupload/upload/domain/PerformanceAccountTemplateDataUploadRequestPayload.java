package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceAccountTemplateDataUploadRequestPayload extends CcaRequestPayload {

	private SectorAssociationInfo sectorAssociationInfo;

}
