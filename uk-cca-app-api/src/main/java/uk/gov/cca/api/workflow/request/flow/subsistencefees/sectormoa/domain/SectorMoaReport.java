package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaReport;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class SectorMoaReport extends MoaReport {

}
