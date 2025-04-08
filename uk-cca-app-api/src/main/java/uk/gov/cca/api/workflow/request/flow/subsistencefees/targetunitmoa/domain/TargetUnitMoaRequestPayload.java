package uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
public class TargetUnitMoaRequestPayload extends CcaRequestPayload {

    private FileInfoDTO targetUnitMoaDocument;
}
