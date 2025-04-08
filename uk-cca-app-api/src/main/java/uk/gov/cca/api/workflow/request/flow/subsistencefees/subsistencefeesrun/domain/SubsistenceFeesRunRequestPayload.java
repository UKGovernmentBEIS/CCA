package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestPayload;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SubsistenceFeesRunRequestPayload extends RequestPayload {

    private String submitterId;

    private Long runId;

    private FileInfoDTO report;
}
