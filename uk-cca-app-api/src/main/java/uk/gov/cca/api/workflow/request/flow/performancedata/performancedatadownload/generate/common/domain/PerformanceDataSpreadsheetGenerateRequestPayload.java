package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
public class PerformanceDataSpreadsheetGenerateRequestPayload extends CcaRequestPayload {
}
