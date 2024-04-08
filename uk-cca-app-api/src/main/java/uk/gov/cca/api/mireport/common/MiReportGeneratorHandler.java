package uk.gov.cca.api.mireport.common;

import jakarta.persistence.EntityManager;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportParams;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportResult;

public interface MiReportGeneratorHandler<T extends MiReportParams> {

    MiReportResult generateMiReport(EntityManager entityManager, T reportParams);

    MiReportType getReportType();
}
