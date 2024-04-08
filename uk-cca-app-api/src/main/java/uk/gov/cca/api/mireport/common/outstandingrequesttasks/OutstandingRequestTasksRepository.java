package uk.gov.cca.api.mireport.common.outstandingrequesttasks;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OutstandingRequestTasksRepository {

    @Transactional(readOnly = true)
    List<OutstandingRequestTask> findOutstandingRequestTaskParams(EntityManager entityManager,
                                                                         OutstandingRegulatorRequestTasksMiReportParams params);

}
