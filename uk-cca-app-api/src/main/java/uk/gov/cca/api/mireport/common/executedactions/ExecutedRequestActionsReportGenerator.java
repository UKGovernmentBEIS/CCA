package uk.gov.cca.api.mireport.common.executedactions;

import jakarta.persistence.EntityManager;
import uk.gov.cca.api.mireport.common.MiReportType;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportResult;

import java.util.List;

public abstract class ExecutedRequestActionsReportGenerator {

    public abstract List<ExecutedRequestAction> findExecutedRequestActions(EntityManager entityManager,
                                                                           ExecutedRequestActionsMiReportParams reportParams);

    public MiReportType getReportType() {
        return MiReportType.COMPLETED_WORK;
    }

    public MiReportResult generateMiReport(EntityManager entityManager, ExecutedRequestActionsMiReportParams reportParams) {
        return ExecutedRequestActionsMiReportResult.builder()
            .reportType(getReportType())
            .columnNames(ExecutedRequestAction.getColumnNames())
            .results(findExecutedRequestActions(entityManager, reportParams))
            .build();
    }
}
