package uk.gov.cca.api.mireport.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportParams;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportResult;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportSearchResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MiReportService {

    private final MiReportGeneratorService miReportGeneratorService;
    private final MiReportRepository miReportRepository;

    public List<MiReportSearchResult> findByCompetentAuthority(CompetentAuthorityEnum competentAuthority) {
        return miReportRepository.findByCompetentAuthority(competentAuthority);
    }

    @Transactional(readOnly = true)
    public MiReportResult generateReport(CompetentAuthorityEnum competentAuthority, MiReportParams reportParams) {
        return miReportGeneratorService.generateReport(competentAuthority, reportParams);
    }
}
