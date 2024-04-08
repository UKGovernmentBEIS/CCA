package uk.gov.cca.api.mireport.common.outstandingrequesttasks;

import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportParams;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OutstandingRegulatorRequestTasksMiReportParams extends MiReportParams {

    @Default
    private Set<RequestTaskType> requestTaskTypes = new HashSet<>();

    @Default
    private Set<String> userIds = new HashSet<>();
}
