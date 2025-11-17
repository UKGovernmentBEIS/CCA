package uk.gov.cca.api.migration.cca3inprogressfacilities;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

@Repository
public interface Cca3InProgressFacilitiesMigrationRepository extends JpaRepository<RequestTask, Long> {

    @Transactional(readOnly = true)
    @Query(value = """
        select rt.*
        from request_task rt
                 inner join request_task_type rtt on rtt.id = rt.type_id
                 inner join request_resource rr on rr.request_id = rt.request_id
                 inner join account acc on acc.id = rr.resource_id::bigint
        where rtt.code = 'UNDERLYING_AGREEMENT_APPLICATION_REVIEW'
          and rr.resource_type = 'ACCOUNT'
          and acc.business_id in :accountBusinessIds
        """,
            nativeQuery = true)
    List<RequestTask> findUnderlyingAgreementReviewRequestTasksByAccountBusinessIds(@Param("accountBusinessIds") List<String> accountBusinessIds);
}
