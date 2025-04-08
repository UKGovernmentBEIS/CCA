package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaReport;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SectorMoaReport extends MoaReport {

}
