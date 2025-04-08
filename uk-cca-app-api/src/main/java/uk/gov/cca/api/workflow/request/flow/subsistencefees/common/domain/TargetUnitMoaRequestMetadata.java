package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TargetUnitMoaRequestMetadata extends MoaRequestMetadata {

    private String businessId;
}
