package uk.gov.cca.api.workflow.request.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.netz.api.workflow.request.core.domain.RequestPayload;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CcaRequestPayload extends RequestPayload {

    private String sectorUserAssignee;
    private String businessId;
    private Long sectorAssociationId;
}
