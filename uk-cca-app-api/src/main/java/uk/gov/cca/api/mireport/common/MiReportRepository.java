package uk.gov.cca.api.mireport.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.mireport.common.domain.MiReportEntity;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportSearchResult;

import java.util.List;

@Repository
public interface MiReportRepository extends JpaRepository<MiReportEntity, Integer> {

    @Transactional(readOnly = true)
    List<MiReportSearchResult> findByCompetentAuthority(CompetentAuthorityEnum competentAuthority);
}
