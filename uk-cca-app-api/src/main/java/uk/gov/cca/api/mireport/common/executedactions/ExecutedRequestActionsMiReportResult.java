package uk.gov.cca.api.mireport.common.executedactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportResult;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExecutedRequestActionsMiReportResult extends MiReportResult {

    private List<ExecutedRequestAction> results;
}
